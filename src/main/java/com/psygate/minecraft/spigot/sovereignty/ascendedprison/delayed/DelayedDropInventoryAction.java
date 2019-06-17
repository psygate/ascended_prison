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

package com.psygate.minecraft.spigot.sovereignty.ascendedprison.delayed;

import com.psygate.minecraft.spigot.sovereignty.ascendedprison.AscendedPrison;
import com.psygate.minecraft.spigot.sovereignty.ascendedprison.db.model.tables.records.AscendedprisonDelayedActionsRecord;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Created by psygate on 26.06.2016.
 */
public class DelayedDropInventoryAction extends DelayedAction {
    private final static Logger LOG = AscendedPrison.getLogger(DelayedDropInventoryAction.class.getName());
    private String reason;

    public DelayedDropInventoryAction(UUID player, String reason) {
        super(player);
        this.reason = reason;
    }

    public DelayedDropInventoryAction(AscendedprisonDelayedActionsRecord rec) {
        super(rec.getPuuid());
        super.setActionID(rec.getActionId());
        reason = rec.getReason();
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public void process(Event ev) {
        if (ev instanceof PlayerJoinEvent) {
            Player p = Bukkit.getPlayer(getPlayer());
            if (p != null && !p.isDead()) {
                LOG.info("Dropping inventory of " + p.getName() + "(" + p.getUniqueId() + ")");

                for (ItemStack stack : p.getInventory().getArmorContents()) {
                    if (stack != null && stack.getType() != Material.AIR) {
                        p.getWorld().dropItemNaturally(p.getLocation(), stack);
                    }
                }
                for (ItemStack stack : p.getInventory().getContents()) {
                    if (stack != null && stack.getType() != Material.AIR) {
                        p.getWorld().dropItemNaturally(p.getLocation(), stack);
                    }
                }

                p.getInventory().clear();
                ItemStack[] armor = p.getInventory().getArmorContents();
                Arrays.fill(armor, new ItemStack(Material.AIR));
                p.getInventory().setArmorContents(armor);
                if (reason != null) p.sendMessage(ChatColor.RED + reason);
                setProcessed(true);
            }
        }
    }

    @Override
    public int getActionTypeID() {
        return 6;
    }

    @Override
    public String toString() {
        return DelayedDropInventoryAction.class.getSimpleName() + "[" + getPlayer() + "]";
    }
}
