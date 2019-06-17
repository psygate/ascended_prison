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
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;
import java.util.logging.Logger;

/**
 * Created by psygate on 26.06.2016.
 */
public class DelayedKillPlayer extends DelayedAction {
    private final static Logger LOG = AscendedPrison.getLogger(DelayedKillPlayer.class.getName());
    private String reason;

    public DelayedKillPlayer(UUID player, String reason) {
        super(player);
        this.reason = reason;
    }

    public DelayedKillPlayer(AscendedprisonDelayedActionsRecord rec) {
        super(rec.getPuuid());
        reason = rec.getReason();
    }

    @Override
    public int getActionTypeID() {
        return 1;
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
            if (p.isDead()) {
                LOG.info("Player " + p.getName() + "(" + p.getUniqueId() + ") is already dead, nothing to do.");
                if (reason != null) p.sendMessage(ChatColor.RED + reason);
                setProcessed(true);
            } else if (p != null && !p.isDead()) {
                LOG.info("Killing " + p.getName() + "(" + p.getUniqueId() + ")");
                // This is a work around since Player::damage doesn't kill the player on join.
                p.setHealth(0);
                p.damage(p.getMaxHealth());
                if (reason != null) p.sendMessage(ChatColor.RED + reason);
                setProcessed(true);
            }
        }
    }

    @Override
    public String toString() {
        return DelayedKillPlayer.class.getSimpleName() + "[" + getPlayer() + "]";
    }
}
