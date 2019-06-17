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
import com.psygate.minecraft.spigot.sovereignty.ascendedprison.Flushable;
import com.psygate.minecraft.spigot.sovereignty.ascendedprison.delayed.*;
import com.psygate.minecraft.spigot.sovereignty.ascendedprison.summoning.InventoryManager;
import com.psygate.minecraft.spigot.sovereignty.ascendedprison.summoning.SummonManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.logging.Logger;

/**
 * Created by psygate on 26.06.2016.
 */
public class PrisonManager implements Flushable {
    private final static Logger LOG = AscendedPrison.getLogger(PrisonManager.class.getName());
    private static PrisonManager instance;
    private final PrisonIO prisonIO = new PrisonIO();

    private PrisonManager() {
        List<Prison> prisons = prisonIO.getSummonedPrisons();

        LOG.info("Loaded " + prisons.size() + " summoned prisons.");

        SummonManager.getInstance().addTracking(prisons);
    }

    public static PrisonManager getInstance() {
        if (instance == null) {
            instance = new PrisonManager();
        }

        return instance;
    }

    public Optional<Prison> getPrisonByPlayer(UUID playerUUID) {
        return prisonIO.getPrisonByPlayerUUID(playerUUID);
    }

    public Optional<Prison> getPrisonByPrisonUUID(PrisonID prisonUUID) {
        return prisonIO.getPrisonByID(prisonUUID);
    }

    public void imprison(Prison prison) {
        World w = Bukkit.getWorld(AscendedPrison.getConf().getPrisonConf().getPrisonWorldName());
        if (w.getSpawnLocation() == null) {
            w.setSpawnLocation(0, w.getHighestBlockYAt(0, 0), 0);
        }
        Player p = Bukkit.getPlayer(prison.getImprisonedPlayer());
        if (p == null) {
            DelayedManager.getInstance().add(new DelayedClearInventoryAction(prison.getImprisonedPlayer(), null));
            DelayedManager.getInstance().add(new DelayedTeleportPlayer(prison.getImprisonedPlayer(), "You were imprisoned.", w.getSpawnLocation()));
            DelayedManager.getInstance().add(new DelayedRestoreInventoryPlayer(prison.getImprisonedPlayer(), null));
        } else {
            p.setBedSpawnLocation(null, true);
            InventoryManager.getInstance().clearInventory(p);
            if (p.isDead()) {
                DelayedManager.getInstance().add(new DelayedTeleportPlayer(prison.getImprisonedPlayer(), "You were imprisoned.", w.getSpawnLocation()));
                DelayedManager.getInstance().add(new DelayedRestoreInventoryPlayer(prison.getImprisonedPlayer(), null));
                DelayedManager.getInstance().add(new DelayedBedSpawnRemoveAction(prison.getImprisonedPlayer(), null));
            } else {
                p.sendMessage(ChatColor.RED + "You were imprisoned.");
                p.teleport(w.getSpawnLocation());
                InventoryManager.getInstance().restoreInventory(p);
            }
        }
//        if (p != null && !p.isDead()) {
//            DelayedManager.getInstance().add(new DelayedClearInventoryAction(prison.getImprisonedPlayer(), null));
//        } else if (p != null) {
//            p.getInventory().clear();
//            p.getInventory().setArmorContents(new ItemStack[]{
//                    new ItemStack(Material.AIR),
//                    new ItemStack(Material.AIR),
//                    new ItemStack(Material.AIR),
//                    new ItemStack(Material.AIR)
//            });
//        } else {
//            DelayedManager.getInstance().add(new DelayedClearInventoryAction(prison.getImprisonedPlayer(), null));
//        }

        prisonIO.getPrisonByPlayerUUID(prison.getImprisonedPlayer()).ifPresent(op -> {
            LOG.info("Deleting old prison for " + prison.getImprisonedPlayer() + ": " + op);
            prisonIO.delete(op);
        });

        prisonIO.persist(prison);
    }

    public void free(ItemStack stack, Location freedAt) {

        Optional<Prison> prisonopt = getPrisonByItemStack(stack);

        prisonopt.ifPresent(prison -> {
            free(prison, freedAt);
            AscendedPrison.getConf()
                    .getBindingConf()
                    .getEnchants()
                    .forEach(stack::removeEnchantment);

            ItemMeta meta = stack.getItemMeta();
            ArrayList<String> lore = new ArrayList<>(meta.getLore());
            lore.remove(0);
            lore.add(ChatColor.GREEN + "Freed on " + (new Date()));
            meta.setLore(lore);
            stack.setItemMeta(meta);
        });
    }

    private void freeOffline(Prison prison, Location freedAt) {
        DelayedManager.getInstance().add(new DelayedStoreInventoryPlayer(prison.getImprisonedPlayer(), null));
        DelayedManager.getInstance().add(new DelayedClearInventoryAction(prison.getImprisonedPlayer(), null));
        DelayedManager.getInstance().add(new DelayedRandomSpawnPlayer(prison.getImprisonedPlayer(), ChatColor.GREEN + "You've been freed.", freedAt));
        DelayedManager.getInstance().add(new DelayedBedSpawnRemoveAction(prison.getImprisonedPlayer(), null));
    }

    private void freeOnline(Player prisoner, Prison prison, Location freedAt) {
        InventoryManager.getInstance().storeInventory(prisoner);
        InventoryManager.getInstance().clearInventory(prisoner);
        prisoner.setBedSpawnLocation(null, true);
        AscendedPrison.randomSpawn(prisoner, freedAt);
    }

    private void freeOfflineSummoned(Prison prison, Location freedAt) {
        DelayedManager.getInstance().add(new DelayedMessagePlayer(prison.getImprisonedPlayer(), ChatColor.GREEN + "You've been freed."));
    }

    private void freeOnlineSummoned(Player prisoner, Prison prison, Location freedAt) {
        prisoner.sendMessage(ChatColor.GREEN + "You've been freed.");
    }

    public Optional<Prison> getPrisonByItemStack(ItemStack stack) {
        if (stack == null) {
            return Optional.empty();
        }

        if (stack.getType() != AscendedPrison.getConf().getBindingConf().getBindType()) {
            return Optional.empty();
        }
        if (stack.hasItemMeta() && !stack.getItemMeta().getLore().isEmpty()) {
            String loretxt = stack.getItemMeta().getLore().get(0);
            Optional<PrisonID> uuidopt = AscendedPrison.getPrisonUUID(loretxt);

            LOG.finest("Prison UUID decoded: " + uuidopt);

            if (!uuidopt.isPresent()) {
                AscendedPrison.getConf()
                        .getBindingConf()
                        .getEnchants()
                        .forEach(stack::removeEnchantment);
            }
            if (uuidopt.isPresent()) {
                return prisonIO.getPrisonByID(uuidopt.get());
            }
        }

        return Optional.empty();
    }

    public boolean isValidPrison(ItemStack stack) {
        return getPrisonByItemStack(stack).isPresent();
    }

    public Optional<UUID> getPrisoner(ItemStack stack) {
        return getPrisonByItemStack(stack).map(Prison::getImprisonedPlayer);
    }

    public void flush() {
        prisonIO.flush();
    }

    public void free(Prison prison, Location freedAt) {
        LOG.info("Freeing prison " + prison);

        Player prisoner = Bukkit.getPlayer(prison.getImprisonedPlayer());
        String msg = ChatColor.GREEN + "You've been freed.";

        DelayedManager.getInstance().clearActions(prison.getImprisonedPlayer());

        if (prison.isSummoned()) {
            if (prisoner == null) {
                freeOfflineSummoned(prison, freedAt);
            } else {
                freeOnlineSummoned(prisoner, prison, freedAt);
            }
        } else {
            if (prisoner == null) {
                freeOffline(prison, freedAt);
            } else {
                freeOnline(prisoner, prison, freedAt);
            }
        }

        prisonIO.delete(prison);
    }
}
//
//        LOG.info("Freeing prison " + prison);
//
//        Player p = Bukkit.getPlayer(prison.getImprisonedPlayer());
//        String msg = ChatColor.GREEN + "You've been freed.";
//        if (prison.isSummoned()) {
//            if (p != null) {
//                LOG.info("Prisoner " + p.getName() + "(" + p.getUniqueId() + ") is online and summoned.");
//                p.sendMessage(msg);
//            } else {
//                LOG.info("Prisoner " + p.getName() + "(" + p.getUniqueId() + ") is offline and summoned.");
//                DelayedManager.getInstance().add(new DelayedMessagePlayer(prison.getImprisonedPlayer(), msg));
//            }
//        } else {
//            if (p != null) {
//                LOG.info("Prisoner " + p.getName() + "(" + p.getUniqueId() + ") is online.");
//                p.sendMessage(msg);
//                AscendedPrison.randomSpawn(p, freedAt);
//            } else {
//                LOG.info("Prisoner " + p.getName() + "(" + p.getUniqueId() + ") is offline.");
//                DelayedManager.getInstance().add(new DelayedRandomSpawnPlayer(prison.getImprisonedPlayer(), msg, freedAt));
//                DelayedManager.getInstance().add(new DelayedMessagePlayer(prison.getImprisonedPlayer(), msg));
//            }
//        }
//        prisonIO.delete(prison);
//    }

