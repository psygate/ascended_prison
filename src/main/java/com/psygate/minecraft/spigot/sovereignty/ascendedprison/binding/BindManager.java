/*
 *     Copyright (C) 2016 psygate (https://github.com/psygate)
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 */

package com.psygate.minecraft.spigot.sovereignty.ascendedprison.binding;

import co.sovereigntymc.tagtonics.tagging.PvPTag;
import co.sovereigntymc.tagtonics.tagging.Tag;
import com.psygate.minecraft.spigot.sovereignty.ascendedprison.AscendedPrison;
import com.psygate.minecraft.spigot.sovereignty.ascendedprison.prison.Prison;
import com.psygate.minecraft.spigot.sovereignty.ascendedprison.prison.PrisonID;
import com.psygate.minecraft.spigot.sovereignty.ascendedprison.prison.PrisonManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.stringtemplate.v4.ST;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by psygate on 26.06.2016.
 */
public class BindManager {
    private final static Logger LOG = AscendedPrison.getLogger(BindManager.class.getName());
    private static BindManager instance;

    private BindManager() {

    }

    public static BindManager getInstance() {
        if (instance == null) {
            instance = new BindManager();
        }

        return instance;
    }

    public void imprisonPlayerByCombatTags(UUID uuid, List<Tag> tags) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        boolean bound;
        if (tags.isEmpty()) {
            LOG.info("Player " + player.getName() + "(" + player.getUniqueId() + ") died without tags.");
        } else {
            switch (AscendedPrison.getConf().getBindingConf().getBindStrategy()) {
                case DAMAGE:
                    LOG.info("Player " + player.getName() + "(" + player.getUniqueId() + ") died with tags, binding by damage.");

                    Map<UUID, DamageTag> tagmap = new HashMap<>();

                    for (Tag t : tags) {
                        if (t instanceof PvPTag) {
                            tagmap.computeIfAbsent(t.getTagger(), (uuid1) -> new DamageTag(uuid1, 0))
                                    .add(((PvPTag) t).getDamage());
                        }
                    }

                    List<DamageTag> taglist = new ArrayList<>(tagmap.values());
                    Collections.sort(taglist, (o1, o2) -> Double.compare(o2.getDamage(), o1.getDamage()));

                    bound = bindPlayerByDamage(player, taglist);
                    break;
                case LAST_TAG:
                    LOG.info("Player " + player.getName() + "(" + player.getUniqueId() + ") died with tags, binding by last tag.");
                    List<TimeTag> timetags = new ArrayList<>(tags.stream().filter(t -> t instanceof PvPTag).map(t -> (PvPTag) t).map(TimeTag::new).sorted().collect(Collectors.toList()));
                    bound = bindPlayer(player, timetags);
                    break;
                default:
                    throw new IllegalStateException("Unknown bind strategy: " + AscendedPrison.getConf().getBindingConf().getBindStrategy());
            }

            if (bound) {
                LOG.info("Player " + player.getName() + "(" + player.getUniqueId() + ") bound.");
            }
        }
    }

    private boolean bindPlayerByDamage(OfflinePlayer player, List<? extends ITag> tags) {
        return bindPlayer(player, tags);
    }

    private boolean bindPlayer(OfflinePlayer player, List<? extends ITag> damageTags) {
//    public void imprisonPlayerByCombatTags(UUID playerUUID, List<Tag> tags) {
//        }
        LOG.finer("Tags used for binding in sorted order (iteration / search order): " + damageTags);
        Material searchfor = AscendedPrison.getConf().getBindingConf().getBindType();
        LOG.fine("Searching for " + searchfor + " material for binding.");
        int searchSpace = (AscendedPrison.getConf().getBindingConf().isRequireHotBar()) ? 9 : 27 + 9;
        LOG.fine("Search space: " + searchSpace + " slots.");

        tag_loop:
        for (ITag tag : damageTags) {
            Player binder = tag.getPlayer();

            if (binder == null) {
                LOG.fine("Unable to bind by tag " + tag + ", player " + binder + " is offline.");
                continue;
            } else {
                for (int i = 0; i < searchSpace; i++) {
                    if (isBindableItem(binder, i)) {
                        LOG.info(binder.getName() + "(" + binder.getUniqueId() + ") has valid bind item in slot " + i + " (" + binder.getInventory().getItem(i) + ")");
                        removeOneItem(binder, i);

                        long now = System.currentTimeMillis();
                        UUID apid = UUID.randomUUID();

                        ItemStack boundItem = createBoundItem(binder, player, apid, now);
                        LOG.info("Bound item created: " + boundItem);
                        int emptySlot = binder.getInventory().firstEmpty();
                        if (emptySlot < 0) {
                            LOG.info("No empty slots on " + binder.getName() + "(" + binder.getUniqueId() + "), dropping prison item.");
                            binder.getLocation().getWorld().dropItemNaturally(binder.getLocation(), boundItem);
                        } else {
                            LOG.info("Empty slot on " + binder.getName() + "(" + binder.getUniqueId() + "): " + emptySlot + ", added prison item.");
                            binder.getInventory().setItem(emptySlot, boundItem);
                        }

                        Prison prison = new Prison(player.getUniqueId(), now, new PrisonID(apid), binder.getUniqueId());
                        prison.bind(binder);
                        PrisonManager.getInstance().imprison(prison);
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private ItemStack createBoundItem(Player binder, OfflinePlayer player, UUID apid, long now) {
        ItemStack boundItem = new ItemStack(AscendedPrison.getConf().getBindingConf().getBindType(), 1);
        ItemMeta meta = boundItem.getItemMeta();
        Date date = new Date(now);
        ArrayList<String> lore = new ArrayList<>(10);
        lore.add(AscendedPrison.generateSignature(new PrisonID(apid)));
        ST nametmp = new ST(AscendedPrison.getConf().getBindingConf().getNameTemplate());
        bindValues(nametmp, binder, player, date);
        meta.setDisplayName(nametmp.render());
        AscendedPrison.getConf().getBindingConf().getEnchants().forEach(v -> meta.addEnchant(v, v.getStartLevel(), true));
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        ST loretmp = new ST(AscendedPrison.getConf().getBindingConf().getLoreTemplate());
        bindValues(loretmp, binder, player, date);
        Arrays.stream(loretmp.render().replaceAll("\n\r", "\n").replaceAll("\r\n", "\n").replaceAll("\r", "\n").split("\n")).forEach(lore::add);
        meta.setLore(lore);
        boundItem.setItemMeta(meta);
        return boundItem;
    }

    private void bindValues(ST nametmp, Player binder, OfflinePlayer player, Date date) {
        for (ChatColor color : ChatColor.values()) {
            nametmp.add(color.name().toUpperCase(), color.toString());
            nametmp.add(color.name().toLowerCase(), color.toString());
            nametmp.add(color.name(), color.toString());
        }
        //  lore: "Bound: <bound_name>\nUUID: <bound_uuid>\nBinder: <binder_name>\nUUID: <binder_uuid>\nDate: <date>"
        nametmp.add("bound_name", player.getName());
        nametmp.add("bound_uuid", player.getUniqueId());
        nametmp.add("binder_name", binder.getName());
        nametmp.add("binder_uuid", binder.getUniqueId());
        nametmp.add("date", date);
    }


    private void removeOneItem(Player binder, int index) {
        PlayerInventory inv = binder.getInventory();
        ItemStack stack = inv.getItem(index);

        if (stack.getAmount() == 1) {
            LOG.info("Removing bind stack, only one item in stack. " + stack);
            inv.setItem(index, new ItemStack(Material.AIR));
        } else {
            LOG.info("Cutting bind stack. " + stack);
            stack.setAmount(stack.getAmount() - 1);
            LOG.info("After cut bind stack. " + stack);
            inv.setItem(index, stack);
        }
    }

    private boolean isBindableItem(Player binder, int i) {
        PlayerInventory inv = binder.getInventory();
        ItemStack stack = inv.getItem(i);
        if (stack != null && stack.getType() == AscendedPrison.getConf().getBindingConf().getBindType() && !hasMeta(stack)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean hasMeta(ItemStack stack) {
        return stack.hasItemMeta() && (stack.getItemMeta().hasEnchants() || stack.getItemMeta().hasLore());
    }

    private class ITag {
        private UUID damager;

        public ITag(UUID damager) {
            this.damager = damager;
        }

        public UUID getDamager() {
            return damager;
        }

        public void setDamager(UUID damager) {
            this.damager = damager;
        }

        public Player getPlayer() {
            return Bukkit.getPlayer(damager);
        }
    }

    private class DamageTag extends ITag {
        private double damage;

        public DamageTag(UUID damager, double damage) {
            super(damager);
            this.damage = damage;
        }

        public double getDamage() {
            return damage;
        }

        public void setDamage(double damage) {
            this.damage = damage;
        }

        public void add(double damage) {
            this.damage = this.damage + damage;
        }
    }

    private class TimeTag extends ITag implements Comparator<TimeTag> {
        private long time;

        public TimeTag(PvPTag tag) {
            super(tag.getTagger());
            this.time = tag.getTagtime();
        }


        @Override
        public int compare(TimeTag o1, TimeTag o2) {
            return Long.compare(o1.time, o2.time);
        }
    }
}
