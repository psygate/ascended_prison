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
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;
import java.util.logging.Logger;

/**
 * Created by psygate on 28.06.2016.
 */
public class DelayedRandomSpawnPlayer extends DelayedLocationAction {
    private final static Logger LOG = AscendedPrison.getLogger(DelayedRandomSpawnPlayer.class.getName());
    private String reason;

    public DelayedRandomSpawnPlayer(UUID player, String reason, Location world) {
        super(player, world);
        this.reason = reason;
    }

    public DelayedRandomSpawnPlayer(AscendedprisonDelayedActionsRecord rec) {
        super(rec.getPuuid());
        this.reason = rec.getReason();
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
                LOG.info("Random spawning " + p.getName() + "(" + p.getUniqueId() + ")");
                randomSpawn(p);
                if (reason != null) p.sendMessage(ChatColor.RED + reason);
                setProcessed(true);
            }
        }
    }

    private void randomSpawn(Player p) {
        AscendedPrison.randomSpawn(p, getLoc());
    }

    @Override
    public int getActionTypeID() {
        return 5;
    }

    @Override
    public String toString() {
        return DelayedRandomSpawnPlayer.class.getSimpleName() + "[" + getPlayer() + "]";
    }
}
