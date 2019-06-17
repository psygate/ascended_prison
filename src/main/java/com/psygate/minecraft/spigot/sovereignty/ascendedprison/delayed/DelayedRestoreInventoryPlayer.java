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
import com.psygate.minecraft.spigot.sovereignty.ascendedprison.summoning.InventoryManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.util.UUID;
import java.util.logging.Logger;

/**
 * Created by psygate on 26.06.2016.
 */
public class DelayedRestoreInventoryPlayer extends DelayedAction {
    private final static Logger LOG = AscendedPrison.getLogger(DelayedRestoreInventoryPlayer.class.getName());
    private String reason;

    public DelayedRestoreInventoryPlayer(UUID player, String reason) {
        super(player);
        this.reason = reason;
    }

    public DelayedRestoreInventoryPlayer(AscendedprisonDelayedActionsRecord rec) {
        super(rec.getPuuid());
        reason = rec.getReason();
    }

    @Override
    public int getActionTypeID() {
        return 8;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public void process(Event ev) {
        if (ev instanceof PlayerJoinEvent || ev instanceof PlayerRespawnEvent || ev instanceof PlayerSpawnLocationEvent) {

            Player p = Bukkit.getPlayer(getPlayer());
            if (p != null) {
                Bukkit.getScheduler().runTask(AscendedPrison.getInstance(), () -> {
                    LOG.info("Restoring inventory " + p.getName() + "(" + p.getUniqueId() + ")");
                    // This is a work around since Player::damage doesn't kill the player on join.
                    InventoryManager.getInstance().restoreInventory(p);
                    if (reason != null) p.sendMessage(ChatColor.RED + reason);
                    setProcessed(true);
                });
            }
        }
    }

    @Override
    public String toString() {
        return DelayedRestoreInventoryPlayer.class.getSimpleName() + "[" + getPlayer() + "]";
    }
}
