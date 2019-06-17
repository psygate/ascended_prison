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

package com.psygate.minecraft.spigot.sovereignty.ascendedprison.feeding;

import com.psygate.minecraft.spigot.sovereignty.ascendedprison.AscendedPrison;
import com.psygate.minecraft.spigot.sovereignty.ascendedprison.Flushable;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jooq.impl.DSL;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.psygate.minecraft.spigot.sovereignty.ascendedprison.db.model.Tables.ASCENDEDPRISON_PLAYER_LOG;

/**
 * Created by psygate on 29.06.2016.
 */
public class PlayerTrackerListener implements Listener, Flushable {
    private HashMap<UUID, PlayerContainer> playTime = new HashMap<>();

    public PlayerTrackerListener() {
        Bukkit.getOnlinePlayers().forEach(v -> playTime.put(v.getUniqueId(), new PlayerContainer(v.getUniqueId(), System.currentTimeMillis())));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onPlayerLogin(PlayerJoinEvent ev) {
        long now = System.currentTimeMillis();
        long day = TimeUnit.MILLISECONDS.toDays(now);
        PlayerContainer con = playTime.computeIfAbsent(ev.getPlayer().getUniqueId(), uuid -> new PlayerContainer(uuid, now));

        if (con.getCreationDay() != day) {
            persistPlayTime(con);
            playTime.put(ev.getPlayer().getUniqueId(), new PlayerContainer(ev.getPlayer().getUniqueId(), now));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onPlayerQuit(PlayerQuitEvent ev) {
        persistPlayTime(playTime.get(ev.getPlayer().getUniqueId()));
        playTime.remove(ev.getPlayer().getUniqueId());
    }

    private void persistPlayTime(PlayerContainer con) {
        AscendedPrison.DBI().asyncSubmit((conf) -> {
            DSL.using(conf).insertInto(ASCENDEDPRISON_PLAYER_LOG)
                    .set(ASCENDEDPRISON_PLAYER_LOG.PUUID, con.getPlayer())
                    .set(ASCENDEDPRISON_PLAYER_LOG.DAY, con.getCreationDay())
                    .set(ASCENDEDPRISON_PLAYER_LOG.PLAYTIME, System.currentTimeMillis() - con.getLoginTime())
                    .execute();
        });
    }

    @Override
    public void flush() {
        long now = System.currentTimeMillis();
        long day = TimeUnit.MILLISECONDS.toDays(now);
        Iterator<Map.Entry<UUID, PlayerContainer>> it = playTime.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<UUID, PlayerContainer> en = it.next();
            if (en.getValue().getCreationDay() != day) {
                persistPlayTime(en.getValue());
                en.setValue(new PlayerContainer(en.getValue().getPlayer(), now));
            }
        }
    }

    private class PlayerContainer {
        private final UUID player;
        private long creation = System.currentTimeMillis();
        long loginTime;

        public PlayerContainer(UUID player, long loginTime) {
            this.player = player;
            this.loginTime = loginTime;
        }

        public long getCreationDay() {
            return TimeUnit.MILLISECONDS.toDays(creation);
        }

        public long getLoginTime() {
            return loginTime;
        }

        public UUID getPlayer() {
            return player;
        }

        public void setLoginTime(long loginTime) {
            this.loginTime = loginTime;
        }
    }
}
