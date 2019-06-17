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
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Created by psygate on 27.06.2016.
 */
class PlayerPrisonLocation implements PrisonLocation {
    private final static Logger LOG = AscendedPrison.getLogger(PlayerPrisonLocation.class.getName());
    private UUID playerUUID;
    private Prison prison;

    public PlayerPrisonLocation(UUID player, Prison prison) {
        this.playerUUID = player;
        this.prison = prison;
    }

    @Override
    public Location getLocation() {
        Player p = Bukkit.getPlayer(playerUUID);

        if (p != null) {
            return Bukkit.getPlayer(playerUUID).getLocation();
        } else {
            return null;
        }
    }

    @Override
    public boolean isValid() {
        return Bukkit.getPlayer(playerUUID) != null && isPrison(Bukkit.getPlayer(playerUUID));
    }

    @Override
    public UUID getLocationUUID() {
        return playerUUID;
    }

    @Override
    public String getLocationName() {
        return "PLAYER";
    }

    @Override
    public String toPlayerString() {
        Player player = Bukkit.getPlayer(playerUUID);

        if (player == null) {
            return ChatColor.RED + "Internal error, unable to locate prison item.";
        } else {
            return player.getName() + "[" + player.getLocation().getBlockX() + ", " + player.getLocation().getBlockY() + ", " + player.getLocation().getBlockZ() + "]";
        }
    }

    @Override
    public void feedOrFree(int totalcost) {
        int foundCoal = 0;

        Optional<Inventory> inventory = getInventory();

        if (!inventory.isPresent()) {
            PrisonManager.getInstance().free(prison, Bukkit.getWorld(AscendedPrison.getConf().getPrisonConf().getDefaultFreeWorld()).getSpawnLocation());
        } else {
            Inventory inv = inventory.get();

            for (int i = 0; i < inv.getSize(); i++) {
                ItemStack stack = inv.getItem(i);


                if (stack != null && stack.getType() == Material.COAL && stack.getData().getData() == 0) {
                    foundCoal += stack.getAmount();
                }
            }

            if (foundCoal < totalcost) {
                PrisonManager.getInstance().free(prison, Bukkit.getWorld(AscendedPrison.getConf().getPrisonConf().getDefaultFreeWorld()).getSpawnLocation());
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

    private boolean isPrison(Player player) {
        PlayerInventory inv = player.getInventory();
        String signature = AscendedPrison.generateSignature(prison.getPrisonID());
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (stack.hasItemMeta() && stack.getItemMeta().hasLore() && !stack.getItemMeta().getLore().isEmpty()
                    && signature.equals(stack.getItemMeta().getLore().get(0))) {
                return true;
            }
        }

        LOG.info("Unable to locate prison " + prison.getPrisonID() + " on player " + player.getName() + "(" + player.getUniqueId() + ")");
        return false;
    }

    public Optional<Inventory> getInventory() {
        return Optional.ofNullable(Bukkit.getPlayer(playerUUID)).map(Player::getInventory);
    }
}
