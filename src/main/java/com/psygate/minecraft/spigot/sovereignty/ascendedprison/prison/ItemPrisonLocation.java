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

import com.psygate.minecraft.spigot.sovereignty.ascendedprison.AscendedPrison;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Created by psygate on 27.06.2016.
 */
class ItemPrisonLocation implements PrisonLocation {
    private final static Logger LOG = AscendedPrison.getLogger(ItemPrisonLocation.class.getName());
    private final UUID locationUUID;
    private Location location;
    private Prison prison;

    public ItemPrisonLocation(Item item, Prison prison) {
        this.location = item.getLocation();
        this.prison = prison;
        this.locationUUID = item.getUniqueId();
    }

    public ItemPrisonLocation(UUID locationUuid, Location loc, Prison prison) {
        this.locationUUID = locationUuid;
        this.location = loc;
        this.prison = prison;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public boolean isValid() {
        return location.getChunk().isLoaded();
    }

    @Override
    public UUID getLocationUUID() {
        return locationUUID;
    }

    @Override
    public String getLocationName() {
        return "ITEM";
    }

    @Override
    public String toPlayerString() {
        if (location == null) {
            return ChatColor.RED + "Internal error, unable to locate prison item.";
        } else {
            return "Item[" + getLocation().getBlockX() + ", " + getLocation().getBlockY() + ", " + getLocation().getBlockZ() + "]";
        }
    }

    @Override
    public void feedOrFree(int totalcost) {
        Optional<Entity> item = Arrays.stream(location.getChunk().getEntities()).filter(en -> en.getUniqueId().equals(locationUUID)).findAny();

        if (!item.isPresent()) {
            PrisonManager.getInstance().free(prison, location);
        } else {
            PrisonManager.getInstance().free(((Item) item.get()).getItemStack(), location);
        }
    }
}
