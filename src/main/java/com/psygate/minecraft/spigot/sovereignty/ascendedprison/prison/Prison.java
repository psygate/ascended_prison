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
import com.psygate.minecraft.spigot.sovereignty.ascendedprison.db.model.tables.records.AscendedprisonPrisonRecord;
import org.bukkit.block.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

import java.util.UUID;
import java.util.logging.Logger;

/**
 * Created by psygate on 27.06.2016.
 */
public class Prison {
    private final static Logger LOG = AscendedPrison.getLogger(Prison.class.getName());
    private final UUID imprisonedPlayer;
    private final long imprisonTime;
    private final PrisonID prisonID;
    private final UUID imprisoner;
    private PrisonLocation location;
    private boolean isSummoned = false;
    private double leashRange;

    public Prison(UUID imprisonedPlayer, long imprisonTime, PrisonID prisonID, UUID imprisoner) {
        this.imprisonedPlayer = imprisonedPlayer;
        this.imprisonTime = imprisonTime;
        this.prisonID = prisonID;
        this.imprisoner = imprisoner;
        this.leashRange = 10;
    }

    public Prison(AscendedprisonPrisonRecord v) {
        this.imprisonedPlayer = v.getPrisonerUuid();
        this.imprisonTime = v.getImprisontime().getTime();
        this.prisonID = v.getPrisonid();
        this.imprisoner = v.getImprisonerUuid();
        this.location = PrisonLocation.map(v, this);
        this.leashRange = v.getLeashRange();
        this.isSummoned = v.getSummonedState();
    }

    public UUID getImprisonedPlayer() {
        return imprisonedPlayer;
    }

    public long getImprisonTime() {
        return imprisonTime;
    }

    public PrisonID getPrisonID() {
        return prisonID;
    }

    public UUID getImprisoner() {
        return imprisoner;
    }

    public void bind(Player player) {
        location = new PlayerPrisonLocation(player.getUniqueId(), this);
    }

    public void setLocation(PrisonLocation location) {
        this.location = location;
    }

    public boolean isSummoned() {
        return isSummoned;
    }

    public void setSummoned(boolean summoned) {
        isSummoned = summoned;
    }

    public PrisonLocation getLocation() {
        return location;
    }

    public void setLocation(Player player) {
        this.location = new PlayerPrisonLocation(player.getUniqueId(), this);
    }

    public void setLocation(Item item) {
        this.location = new ItemPrisonLocation(item, this);
    }

    public double getLeashRange() {
        return leashRange;
    }

    public void setLeashRange(double leashRange) {
        this.leashRange = leashRange;
    }

    @Override
    public String toString() {
        return "Prison{" +
                "prisonID=" + prisonID +
                '}';
    }

    public void setLocation(Block block) {
        this.location = new BlockPrisonLocation(block.getLocation(), this);
    }

    public boolean setLocation(InventoryHolder holder) {
        //Beacon, BrewingStand, Chest, Dispenser, Dropper, Furnace, Hopper, HopperMinecart, Horse, HumanEntity, Player, StorageMinecart, StorageMinecart, Villager
        LOG.info("Attempting to move prison " + prisonID + " to " + holder);
        if (holder instanceof Chest) {
            setLocation(((Chest) holder).getBlock());
            return true;
        } else if (holder instanceof Dispenser) {
            setLocation(((Dispenser) holder).getBlock());
            return true;
        } else if (holder instanceof Dropper) {
            setLocation(((Dropper) holder).getBlock());
            return true;
        } else if (holder instanceof Furnace) {
            setLocation(((Furnace) holder).getBlock());
            return true;
        } else if (holder instanceof Hopper) {
            setLocation(((Hopper) holder).getBlock());
            return true;
        } else if (holder instanceof Player) {
            setLocation((Player) holder);
            return true;
        }
        return false;
    }
}
