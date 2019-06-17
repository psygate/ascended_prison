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

import org.bukkit.Location;

import java.util.Objects;
import java.util.UUID;

/**
 * Created by psygate on 29.06.2016.
 */
public abstract class DelayedLocationAction extends DelayedAction {
    private Location loc;

    public DelayedLocationAction(UUID player, Location loc) {
        super(player);
        this.loc = Objects.requireNonNull(loc, () -> "Location cannot be null.");
    }

    public DelayedLocationAction(UUID puuid) {
        super(puuid);
        loc = null;
    }

    public Location getLoc() {
        return loc;
    }

    public void setLoc(Location loc) {
        this.loc = loc;
    }
}
