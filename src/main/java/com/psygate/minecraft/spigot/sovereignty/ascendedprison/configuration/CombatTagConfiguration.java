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

package com.psygate.minecraft.spigot.sovereignty.ascendedprison.configuration;

import com.psygate.minecraft.spigot.sovereignty.nucleus.sql.util.TimeUtil;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Created by psygate on 26.06.2016.
 */
public class CombatTagConfiguration {
    private long tagTimeMillis;
    private boolean killOnTaggedQuit;
    private long logoutTimer;
    private boolean npcOnQuit;
    private long npcDespawnTicks;

    public CombatTagConfiguration(ConfigurationSection section) {
        tagTimeMillis = TimeUtil.parseTimeStringToMillis(section.getString("tag_time"));
        killOnTaggedQuit = section.getBoolean("kill_on_tagged_quit");
        logoutTimer = TimeUtil.parseTimeStringToMillis(section.getString("logout_timer"));
        npcOnQuit = section.getBoolean("npc_on_quit");
        npcDespawnTicks = section.getLong("npc_despawn_ticks");
    }

    public long getTagTimeMillis() {
        return tagTimeMillis;
    }

    public boolean isKillOnTaggedQuit() {
        return killOnTaggedQuit;
    }

    public long getLogoutTimer() {
        return logoutTimer;
    }

    public boolean isNpcOnQuit() {
        return npcOnQuit;
    }

    public long getNpcDespawnTicks() {
        return npcDespawnTicks;
    }
}
