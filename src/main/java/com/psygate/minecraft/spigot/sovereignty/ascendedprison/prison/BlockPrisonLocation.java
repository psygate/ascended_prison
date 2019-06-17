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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Created by psygate on 01.07.2016.
 */
public class BlockPrisonLocation implements PrisonLocation {
    private final static Logger LOG = AscendedPrison.getLogger(BlockPrisonLocation.class.getName());
    private Location block;
    private Prison prison;

    public BlockPrisonLocation(Location block, Prison prison) {
        this.block = block;
        this.prison = prison;
    }

    @Override
    public Location getLocation() {
        return block;
    }

    @Override
    public boolean isValid() {
        return isPrison(block.getBlock());
    }

    private boolean isPrison(Block block) {
        Optional<Inventory> inventory = getInventory();

        if (!inventory.isPresent()) {
            return false;
        }
        Inventory inv = inventory.get();

        String signature = AscendedPrison.generateSignature(prison.getPrisonID());
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack stack = inv.getItem(i);

            if (PrisonManager.getInstance().getPrisonByItemStack(stack).isPresent()) {
                return true;
            }
        }

        LOG.info("Unable to locate prison " + prison.getPrisonID() + " on block " + block + ")");
        return false;
    }

    @Override
    public UUID getLocationUUID() {
        long upper = getLocation().getWorld().getUID().getMostSignificantBits() ^ (block.getBlockX() << 32 | block.getBlockY());
        long lower = getLocation().getWorld().getUID().getLeastSignificantBits() ^ (block.getBlockY() << 32 | block.getBlockZ());
        return new UUID(upper, lower);
    }

    @Override
    public String getLocationName() {
        return "BLOCK";
    }

    @Override
    public String toPlayerString() {
        return getLocation().getBlock().getType() + "[" + block.getBlockX() + ", " + block.getBlockY() + ", " + block.getBlockZ() + "]";
    }

    @Override
    public void feedOrFree(int totalcost) {
        int foundCoal = 0;

        Optional<Inventory> inventory = getInventory();

        if (!inventory.isPresent()) {
            PrisonManager.getInstance().free(prison, block);
        } else {
            Inventory inv = inventory.get();

            for (int i = 0; i < inv.getSize(); i++) {
                ItemStack stack = inv.getItem(i);


                if (stack != null && stack.getType() == Material.COAL && stack.getData().getData() == 0) {
                    foundCoal += stack.getAmount();
                }
            }

            if (foundCoal < totalcost) {
                PrisonManager.getInstance().free(prison, block);
            } else {
                for (int i = 0; i < inv.getSize() && totalcost > 0; i++) {
                    ItemStack stack = inv.getItem(i);


                    if (stack != null && stack.getType() == Material.COAL && stack.getData().getData() == 0) {
                        if (stack.getAmount() > totalcost) {
                            totalcost = 0;
                            stack.setAmount(stack.getAmount() - totalcost);
                            inv.setItem(i, stack);
                        } else if (stack.getAmount() == totalcost) {
                            totalcost = 0;
                            inv.setItem(i, new ItemStack(Material.AIR));
                        } else {
                            totalcost -= stack.getAmount();
                            inv.setItem(i, new ItemStack(Material.AIR));
                        }
                    }
                }
            }
        }
    }

    @Override
    public double distanceSqr(Location location) {
        return this.getLocation().distanceSquared(location);
    }

    public Optional<Inventory> getInventory() {
        MaterialData data = block.getBlock().getState().getData();
        Inventory inv;
        if (data instanceof Chest) {
            inv = ((Chest) data).getBlockInventory();
        } else if (data instanceof Hopper) {
            inv = ((Hopper) data).getInventory();
        } else if (data instanceof Dispenser) {
            inv = ((Hopper) data).getInventory();
        } else if (data instanceof Furnace) {
            inv = ((Furnace) data).getInventory();
        } else if (data instanceof Dropper) {
            inv = ((Dropper) data).getInventory();
        } else {
            return Optional.empty();
        }

        return Optional.of(inv);
    }
}
