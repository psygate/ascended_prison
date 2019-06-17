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

package com.psygate.minecraft.spigot.sovereignty.ascendedprison.prison;

import com.psygate.minecraft.spigot.sovereignty.ascendedprison.db.model.tables.records.AscendedprisonPrisonRecord;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.UUID;

/**
 * Created by psygate on 27.06.2016.
 */
public interface PrisonLocation {
    Location getLocation();

    boolean isValid();

    static PrisonLocation map(AscendedprisonPrisonRecord v, Prison prison) {
        switch (v.getPrisonLocationType()) {
            case "PLAYER":
                return new PlayerPrisonLocation(v.getLocationUuid(), prison);
            case "ITEM":
                Location loc = new Location(
                        Bukkit.getWorld(v.getWorldUuid()), v.getX(), v.getY(), v.getZ()
                );
                return new ItemPrisonLocation(v.getLocationUuid(), loc, prison);
            case "BLOCK":
                Location bloc = new Location(
                        Bukkit.getWorld(v.getWorldUuid()), v.getX(), v.getY(), v.getZ()
                );
                return new BlockPrisonLocation(bloc, prison);
            default:
                throw new IllegalStateException();
        }
    }

    UUID getLocationUUID();

    String getLocationName();

    String toPlayerString();

    void feedOrFree(int totalcost);

    default double distanceSqr(Location location) {
        if (getLocation().getWorld().getUID().equals(location.getWorld().getUID())) {
            return getLocation().distanceSquared(location);
        } else {
            return Double.MAX_VALUE;
        }
    }
}
