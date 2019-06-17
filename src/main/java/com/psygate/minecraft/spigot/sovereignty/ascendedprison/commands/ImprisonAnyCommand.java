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

package com.psygate.minecraft.spigot.sovereignty.ascendedprison.commands;

import com.psygate.minecraft.spigot.sovereignty.ascendedprison.AscendedPrison;
import com.psygate.minecraft.spigot.sovereignty.ascendedprison.prison.Prison;
import com.psygate.minecraft.spigot.sovereignty.ascendedprison.prison.PrisonID;
import com.psygate.minecraft.spigot.sovereignty.ascendedprison.prison.PrisonManager;
import com.psygate.minecraft.spigot.sovereignty.nucleus.commands.util.NucleusPlayerCommand;
import com.psygate.minecraft.spigot.sovereignty.nucleus.util.player.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.stringtemplate.v4.ST;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

/**
 * Created by psygate on 30.06.2016.
 */
public class ImprisonAnyCommand extends NucleusPlayerCommand {
    public ImprisonAnyCommand() {
        super(1, 1);
    }

    @Override
    protected void subOnCommand(Player player, Command command, String s, String[] strings) throws Exception {
        UUID bound = PlayerManager.getInstance().toUUID(strings[0]);

        UUID prisonid = UUID.randomUUID();
        long now = System.currentTimeMillis();
        ItemStack stack = createBoundItem(player, bound, strings[0], prisonid, now);
        Prison prison = new Prison(bound, now, new PrisonID(prisonid), player.getUniqueId());
        prison.setLocation(player);
        PrisonManager.getInstance().imprison(prison);

        if (player.getInventory().firstEmpty() < 0) {
            player.getWorld().dropItemNaturally(player.getLocation(), stack);
        } else {
            player.getInventory().addItem(stack);
        }

        Player pris = Bukkit.getPlayer(bound);
        if (pris != null) {
            pris.setHealth(0);
            pris.damage(1);
        }
    }

    private ItemStack createBoundItem(Player binder, UUID player, String playername, UUID apid, long now) {
        ItemStack boundItem = new ItemStack(AscendedPrison.getConf().getBindingConf().getBindType(), 1);
        ItemMeta meta = boundItem.getItemMeta();
        Date date = new Date(now);
        ArrayList<String> lore = new ArrayList<>(10);
        lore.add(AscendedPrison.generateSignature(new PrisonID(apid)));
        ST nametmp = new ST(AscendedPrison.getConf().getBindingConf().getNameTemplate());
        bindValues(nametmp, binder, player, playername, date);
        meta.setDisplayName(nametmp.render());
        AscendedPrison.getConf().getBindingConf().getEnchants().forEach(v -> meta.addEnchant(v, v.getStartLevel(), true));
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        ST loretmp = new ST(AscendedPrison.getConf().getBindingConf().getLoreTemplate());
        bindValues(loretmp, binder, player, playername, date);
        Arrays.stream(loretmp.render().replaceAll("\n\r", "\n").replaceAll("\r\n", "\n").replaceAll("\r", "\n").split("\n")).forEach(lore::add);
        meta.setLore(lore);
        boundItem.setItemMeta(meta);
        return boundItem;
    }

    private void bindValues(ST nametmp, Player binder, UUID player, String playername, Date date) {
        for (ChatColor color : ChatColor.values()) {
            nametmp.add(color.name().toUpperCase(), color.toString());
            nametmp.add(color.name().toLowerCase(), color.toString());
            nametmp.add(color.name(), color.toString());
        }
        //  lore: "Bound: <bound_name>\nUUID: <bound_uuid>\nBinder: <binder_name>\nUUID: <binder_uuid>\nDate: <date>"
        nametmp.add("bound_name", playername);
        nametmp.add("bound_uuid", player);
        nametmp.add("binder_name", binder.getName());
        nametmp.add("binder_uuid", binder.getUniqueId());
        nametmp.add("date", date);
    }

    @Override
    protected String[] getName() {
        return new String[]{"imprisonany"};
    }
}
