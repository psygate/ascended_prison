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
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by psygate on 27.06.2016.
 */
public class ImprisonmentConfiguration {
    private String prisonWorldName;
    private World.Environment environment;
    private WorldType type;

    private UUID defaultFreeWorld;

    private Set<String> noBindWorlds;
    private Set<String> noStealWorlds;

    private Set<UUID> noBindWorldsUUID;
    private Set<UUID> noStealWorldsUUID;

    private long feedingCycleTime;
    private int summonDelay;

    public ImprisonmentConfiguration(ConfigurationSection sec) {
        prisonWorldName = sec.getString("prison_world.name");
        environment = World.Environment.valueOf(sec.getString("prison_world.environment").toUpperCase().trim());
        type = WorldType.valueOf(sec.getString("prison_world.type").toUpperCase().trim());

        if (Bukkit.getWorld(prisonWorldName) == null) {
            checkPrisonWorld(prisonWorldName, environment, type);
        }
        noBindWorlds = new HashSet<>(
                sec.getStringList("no_bind_worlds")
        );

        noStealWorlds = new HashSet<>(
                sec.getStringList("no_prison_stealing_worlds")
        );

        defaultFreeWorld = Bukkit.getWorld(sec.getString("default_free_world")).getUID();

        feedingCycleTime = TimeUtil.parseTimeStringToMillis(sec.getString("cycle_time"));

        summonDelay = sec.getInt("summon_delay");
    }

    private World checkPrisonWorld(String prisonWorldName, World.Environment environment, WorldType type) {
        World w = Bukkit.getWorld(prisonWorldName);
        if (w == null) {
            WorldCreator creator = new WorldCreator(prisonWorldName)
                    .environment(environment)
                    .type(type);
            w = Bukkit.createWorld(creator);
        }

        return w;
    }

    public String getPrisonWorldName() {
        return prisonWorldName;
    }

    public World.Environment getEnvironment() {
        return environment;
    }

    public WorldType getType() {
        return type;
    }

    public Set<UUID> getNoBindWorlds() {
        if (noBindWorldsUUID == null) {
            noBindWorldsUUID = new HashSet<>();
            noBindWorlds.stream()
                    .map(Bukkit::getWorld)
                    .map(World::getUID)
                    .forEach(noBindWorldsUUID::add);
        }
        return noBindWorldsUUID;
    }

    public boolean isNoBindWorld(UUID world) {
        return getNoBindWorlds().contains(world);
    }

    public Set<UUID> getNoStealWorlds() {
        if (noStealWorldsUUID == null) {
            noStealWorldsUUID = new HashSet<>();
            noStealWorlds.stream()
                    .map(Bukkit::getWorld)
                    .map(World::getUID)
                    .forEach(noStealWorldsUUID::add);
        }
        return noStealWorldsUUID;
    }

    public Set<UUID> getNoBindWorldsUUID() {
        return noBindWorldsUUID;
    }

    public Set<UUID> getNoStealWorldsUUID() {
        return noStealWorldsUUID;
    }

    public int getSummonDelay() {
        return summonDelay;
    }

    public boolean isNoStealWorld(UUID world) {
        return getNoStealWorlds().contains(world);
    }

    public UUID getDefaultFreeWorld() {
        return defaultFreeWorld;
    }

    public long getFeedingCycleTime() {
        return feedingCycleTime;
    }
}
