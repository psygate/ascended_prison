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

package com.psygate.minecraft.spigot.sovereignty.ascendedprison.summoning;

import com.psygate.minecraft.spigot.sovereignty.ascendedprison.AscendedPrison;
import com.psygate.minecraft.spigot.sovereignty.ascendedprison.delayed.DelayedDropInventoryAction;
import com.psygate.minecraft.spigot.sovereignty.ascendedprison.delayed.DelayedManager;
import com.psygate.minecraft.spigot.sovereignty.ascendedprison.delayed.DelayedTeleportPlayer;
import com.psygate.minecraft.spigot.sovereignty.ascendedprison.prison.Prison;
import com.psygate.minecraft.spigot.sovereignty.ascendedprison.prison.PrisonID;
import com.psygate.minecraft.spigot.sovereignty.ascendedprison.prison.PrisonManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by psygate on 26.06.2016.
 */
public class SummonManager {
    private final static Logger LOG = AscendedPrison.getLogger(SummonManager.class.getName());
    private static SummonManager instance;
    private int damageTickerID = -1;
    private Set<PrisonID> trackedPrisons = new HashSet<>();
    private Set<PrisonID> intransit = new HashSet<>();

    private SummonManager() {
    }

    public static SummonManager getInstance() {
        if (instance == null) {
            instance = new SummonManager();
            LOG.setLevel(Level.INFO);
        }

        return instance;
    }

    public void summonPlayer(Player summoner, Prison prison) {
        Player toSummon = Bukkit.getPlayer(prison.getImprisonedPlayer());
        if (toSummon.isDead()) {
            summoner.sendMessage(ChatColor.RED + "Player to summon is dead. Summoning failed.");
            return;
        }
        if (intransit.contains(prison.getPrisonID())) {
            summoner.sendMessage(ChatColor.RED + "Prisoner is already being summoned.");
            return;
        }
        LOG.info("Summoning prisoner " + prison.getImprisonedPlayer());
        if (toSummon == null) {
            summoner.sendMessage(ChatColor.RED + "Prisoner is not online.");
        } else {
            SummonJob job = new SummonJob(summoner, toSummon, summoner.getLocation(), AscendedPrison.getConf().getPrisonConf().getSummonDelay());
            job.id = Bukkit.getScheduler().scheduleSyncRepeatingTask(AscendedPrison.getInstance(), job, 1, 1);
        }
    }

    public void addTracking(List<Prison> prisons) {
        for (Prison p : prisons) {
            addTracking(p);
        }
    }

    private void addTracking(Prison prison) {
        LOG.finest("Adding tracked prison " + prison.getPrisonID() + ".");
        trackedPrisons.add(prison.getPrisonID());
        if (damageTickerID == -1 && !trackedPrisons.isEmpty()) {
            LOG.info("Starting damage ticker.");
            damageTickerID = Bukkit.getScheduler().scheduleSyncRepeatingTask(AscendedPrison.getInstance(), new DamageTicker(), 20, 20);
        }
    }

    public void removeTracking(List<Prison> prisons) {
        for (Prison p : prisons) {
            addTracking(p);
        }
    }

    private void removeTracking(Prison prison) {
        LOG.finest("Removing tracked prison " + prison.getPrisonID() + ".");
        trackedPrisons.remove(prison.getPrisonID());

        if (trackedPrisons.isEmpty()) {
            Bukkit.getScheduler().cancelTask(damageTickerID);
            damageTickerID = -1;
            LOG.info("Cancelling damage ticker.");
        }
    }

    public void returnPlayer(Player player, Prison prison) {
        if (!prison.isSummoned()) {
            LOG.info("Cannot return a player that is not summoned.");
            return;
        } else {
            removeTracking(prison);
            LOG.info("Returning prisoner " + prison.getImprisonedPlayer() + " to the prison world.");
            Player prisoner = Bukkit.getPlayer(prison.getImprisonedPlayer());
            prison.setSummoned(false);
            if (prisoner == null) {
                DelayedManager.getInstance().add(new DelayedDropInventoryAction(prison.getImprisonedPlayer(), null));
                DelayedManager.getInstance().add(new DelayedTeleportPlayer(
                        prison.getImprisonedPlayer(),
                        "You were returned.",
                        Bukkit.getWorld(AscendedPrison.getConf().getPrisonConf().getPrisonWorldName()).getSpawnLocation()));
            } else {
                LOG.info("Dropping inventory of " + prisoner.getName() + "(" + prisoner.getUniqueId() + ")");

                for (ItemStack stack : prisoner.getInventory().getArmorContents()) {
                    if (stack != null && stack.getType() != Material.AIR) {
                        prisoner.getWorld().dropItemNaturally(prisoner.getLocation(), stack);
                    }
                }
                for (ItemStack stack : prisoner.getInventory().getContents()) {
                    if (stack != null && stack.getType() != Material.AIR) {
                        prisoner.getWorld().dropItemNaturally(prisoner.getLocation(), stack);
                    }
                }

                prisoner.getInventory().clear();
                ItemStack[] armor = prisoner.getInventory().getArmorContents();
                Arrays.fill(armor, new ItemStack(Material.AIR));
                prisoner.getInventory().setArmorContents(armor);
                prisoner.teleport(Bukkit.getWorld(AscendedPrison.getConf().getPrisonConf().getPrisonWorldName()).getSpawnLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                InventoryManager.getInstance().restoreInventory(prisoner);
            }
        }
    }

    private class SummonJob implements Runnable {
        private final Location location;
        private final UUID summoner;
        private final UUID summoned;
        private int ticks = 0;
        private int id = -1;

        public SummonJob(Player summoner, Player toSummon, Location location, int ticks) {
            this.summoner = summoner.getUniqueId();
            this.summoned = toSummon.getUniqueId();
            this.location = location;
            this.ticks = ticks;
        }

        @Override
        public void run() {
            if (Bukkit.getPlayer(summoner) == null) {
                send(ChatColor.RED + "Summoner has left the game. Summoning stopped.", summoned);
                Bukkit.getScheduler().cancelTask(id);
                return;
            } else if (Bukkit.getPlayer(summoned) == null) {
                send(ChatColor.RED + "Summoned prisoner has left the game. Summoning stopped.", summoner);
                Bukkit.getScheduler().cancelTask(id);
                return;
            }

            if (ticks % 20 == 0) {
                send(ChatColor.YELLOW + "You'll be summoned in " + (ticks / 20) + " seconds.", summoned);
                send(ChatColor.YELLOW + "Prisoner will be summoned in " + (ticks / 20) + " seconds.", summoner);
            }
            ticks--;

            if (ticks <= 0) {
                teleportPrisoner();
                PrisonManager.getInstance().getPrisonByPlayer(summoned).ifPresent(p -> {
                    p.setSummoned(true);
                    addTracking(p);
                    intransit.remove(p.getPrisonID());
                });
                Bukkit.getScheduler().cancelTask(id);
            }
        }

        private void teleportPrisoner() {
            Optional.ofNullable(Bukkit.getPlayer(summoned)).ifPresent(p -> {
                InventoryManager.getInstance().storeInventory(p);
                p.getInventory().clear();
                p.getInventory().setArmorContents(new ItemStack[]{
                        new ItemStack(Material.AIR),
                        new ItemStack(Material.AIR),
                        new ItemStack(Material.AIR),
                        new ItemStack(Material.AIR)
                });
                p.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
            });
        }

        private void send(String s, UUID summoner) {
            Optional.ofNullable(Bukkit.getPlayer(summoner)).ifPresent(p -> p.sendMessage(s));
        }
    }

    private class JsonStack {
        private Material type;
        private byte data;
        private Map<Enchantment, Integer> enchantments = new HashMap<>();
        private String customName = null;
        private List<String> lore = new LinkedList<>();
        private int amount;
        private short durability;

        public JsonStack(ItemStack stack) {
            type = stack.getType();
            data = stack.getData().getData();
            amount = stack.getAmount();
            durability = stack.getDurability();
            enchantments.putAll(stack.getEnchantments());
            if (stack.hasItemMeta()) {
                if (stack.getItemMeta().hasDisplayName()) {
                    customName = stack.getItemMeta().getDisplayName();
                }

                if (stack.getItemMeta().hasLore()) {
                    lore.addAll(stack.getItemMeta().getLore());
                }
            }
        }

        public Material getType() {
            return type;
        }

        public void setType(Material type) {
            this.type = type;
        }

        public byte getData() {
            return data;
        }

        public void setData(byte data) {
            this.data = data;
        }

        public Map<Enchantment, Integer> getEnchantments() {
            return enchantments;
        }

        public void setEnchantments(Map<Enchantment, Integer> enchantments) {
            this.enchantments = enchantments;
        }

        public String getCustomName() {
            return customName;
        }

        public void setCustomName(String customName) {
            this.customName = customName;
        }

        public List<String> getLore() {
            return lore;
        }

        public void setLore(List<String> lore) {
            this.lore = lore;
        }

        public ItemStack toItemStack() {
            ItemStack stack = new ItemStack(type, amount);
            stack.getData().setData(data);
            stack.setDurability(durability);
            if (!enchantments.isEmpty()) {
                stack.addEnchantments(enchantments);
            }

            if (customName != null) {
                ItemMeta meta = stack.getItemMeta();
                meta.setDisplayName(customName);
                stack.setItemMeta(meta);
            }

            if (lore != null && !lore.isEmpty()) {
                ItemMeta meta = stack.getItemMeta();
                meta.setLore(lore);
                stack.setItemMeta(meta);
            }

            return stack;
        }
    }

    private class BoundInventory {
        private UUID player;
        private Map<Integer, JsonStack> armor = new HashMap<>();
        private Map<Integer, JsonStack> items = new HashMap<>();

        public BoundInventory(Player player) {
            this.player = player.getUniqueId();

            for (int i = 0; i < player.getInventory().getArmorContents().length; i++) {
                ItemStack stack = player.getInventory().getArmorContents()[i];

                if (stack != null && stack.getType() != Material.AIR) {
                    armor.put(i, new JsonStack(stack));
                }
            }

            for (int i = 0; i < player.getInventory().getSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);

                if (stack != null && stack.getType() != Material.AIR) {
                    items.put(i, new JsonStack(stack));
                }
            }
        }

        public UUID getPlayer() {
            return player;
        }

        public void setPlayer(UUID player) {
            this.player = player;
        }

        public Map<Integer, JsonStack> getArmor() {
            return armor;
        }

        public void setArmor(Map<Integer, JsonStack> armor) {
            this.armor = armor;
        }

        public Map<Integer, JsonStack> getItems() {
            return items;
        }

        public void setItems(Map<Integer, JsonStack> items) {
            this.items = items;
        }

        public void restore(Player prisoner) {
            prisoner.getInventory().clear();
            ItemStack[] armor = new ItemStack[4];
            Arrays.fill(armor, new ItemStack(Material.AIR));
            for (Map.Entry<Integer, JsonStack> slot : this.armor.entrySet()) {
                armor[slot.getKey()] = slot.getValue().toItemStack();
            }

            prisoner.getInventory().setArmorContents(armor);

            for (Map.Entry<Integer, JsonStack> slot : this.items.entrySet()) {
                prisoner.getInventory().setItem(slot.getKey(), slot.getValue().toItemStack());
            }

        }
    }

    private class DamageTicker implements Runnable {
        @Override
        public void run() {
            LOG.finest("Running damage ticker for " + trackedPrisons);
            for (PrisonID uuid : trackedPrisons) {
                PrisonManager.getInstance().getPrisonByPrisonUUID(uuid).ifPresent(p -> {
                    Optional.ofNullable(Bukkit.getPlayer(p.getImprisonedPlayer())).ifPresent(player -> {
                        if (p.getLocation().distanceSqr(player.getLocation()) > p.getLeashRange() * p.getLeashRange()) {
                            LOG.finer("Hurting " + p.getImprisonedPlayer());
                            player.sendMessage(ChatColor.RED + "Leash range exceeded. (" + p.getLeashRange() + ")");
                            player.damage(2);
                        }
                    });
                });
            }
        }
    }
}
