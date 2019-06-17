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
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Optional;
import java.util.logging.Logger;

/**
 * Created by psygate on 28.06.2016.
 */
public class PrisonListener implements Listener, Flushable {
    private final static Logger LOG = AscendedPrison.getLogger(PrisonListener.class.getName());

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPortalTransit(PlayerPortalEvent ev) {
        PlayerInventory iv = ev.getPlayer().getInventory();
        dropPrisonItem(iv);
    }

    private void dropPrisonItem(PlayerInventory iv) {
        for (int i = 0; i < iv.getSize(); i++) {
            ItemStack stack = iv.getItem(i);
            Optional<Prison> prisonopt = PrisonManager.getInstance().getPrisonByItemStack(stack);
            int finalI = i;
            prisonopt.ifPresent(prison -> {
                iv.setItem(finalI, new ItemStack(Material.AIR));
                iv.getHolder().sendMessage(ChatColor.RED + "Prison item dropped. World transit not allowed for prison items.");
                Location loc = iv.getHolder().getLocation();
                loc.getWorld().dropItemNaturally(loc, stack);
            });
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPortalTransitItem(EntityPortalEvent ev) {
        if (ev.getEntity().getType() == EntityType.DROPPED_ITEM) {
            ItemStack stack = ((Item) ev.getEntity()).getItemStack();
            Optional<Prison> prisonopt = PrisonManager.getInstance().getPrisonByItemStack(stack);
            prisonopt.ifPresent(v -> ev.setCancelled(true));
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPickup(PlayerPickupItemEvent ev) {
        ItemStack stack = (ev.getItem()).getItemStack();
        Optional<Prison> prisonopt = PrisonManager.getInstance().getPrisonByItemStack(stack);
        prisonopt.ifPresent(prison -> {
            if (prison.getImprisonedPlayer().equals(ev.getPlayer().getUniqueId())) {
                ev.getItem().setPickupDelay(20);
                ev.setCancelled(true);
            } else {
                PrisonManager.getInstance().getPrisonByItemStack(stack).ifPresent(p -> p.setLocation(ev.getPlayer()));
            }
        });
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onItemDrop(PlayerDropItemEvent ev) {
        ItemStack stack = (ev.getItemDrop()).getItemStack();
        PrisonManager.getInstance().getPrisonByItemStack(stack).ifPresent(p -> p.setLocation(ev.getItemDrop()));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void playerInteractEvent(PlayerInteractEvent ev) {
//        if (ev.getAction() == Action.RIGHT_CLICK_AIR && ev.getItem() != null) {
//            ItemStack stack = ev.getItem();
//            if (PrisonManager.getInstance().getPrisonByItemStack(stack).isPresent()) {
//                ev.setCancelled(true);
//            }
//        } else
        if (ev.getItem() != null) {
            if (PrisonManager.getInstance().getPrisonByItemStack(ev.getItem()).isPresent()) {
                ev.getPlayer().sendMessage(ChatColor.RED + "Interaction cancelled, use /free to free a prisoner.");
                ev.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onItemFrame(PlayerInteractEntityEvent ev) {
        if (ev.getRightClicked() instanceof ItemFrame) {
            if (ev.getPlayer().getItemInHand() != null) {
                Optional<Prison> prisonopt = PrisonManager.getInstance().getPrisonByItemStack(ev.getPlayer().getItemInHand());
                if (prisonopt.isPresent()) {
                    ev.getPlayer().sendMessage(ChatColor.RED + "You can't put bound souls into item frames.");
                    ev.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void itemDespawnEvent(ItemDespawnEvent ev) {
        freeIfPrison(ev.getEntity());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChunkUnload(ChunkUnloadEvent ev) {
        for (Entity en : ev.getChunk().getEntities()) {
            freeIfPrison(en);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent ev) {
        freeIfPrison(ev.getEntity());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityCombustEvent ev) {
        freeIfPrison(ev.getEntity());
    }

    private boolean freeIfPrison(Entity en) {
        if (en instanceof Item) {
            return freeIfPrison(((Item) en).getItemStack(), en.getLocation());
        }

        return false;
    }

    private boolean freeIfPrison(ItemStack stack, Location loc) {
        Optional<Prison> opt = PrisonManager.getInstance().getPrisonByItemStack(stack);
        if (opt.isPresent()) {
            PrisonManager.getInstance().free(stack, loc);
        }

        return opt.isPresent();
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInventoryClose(InventoryCloseEvent ev) {
        Inventory inv = ev.getInventory();
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            Optional<Prison> prisonopt = PrisonManager.getInstance().getPrisonByItemStack(item);
            if (prisonopt.isPresent()) {
                if (!prisonopt.get().setLocation(inv.getHolder())) {
                    inv.setItem(i, new ItemStack(Material.AIR));
                    ev.getPlayer().getWorld().dropItemNaturally(ev.getPlayer().getLocation(), item);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInventoryPickupItem(InventoryPickupItemEvent ev) {
        if (PrisonManager.getInstance().getPrisonByItemStack(ev.getItem().getItemStack()).isPresent()) {
            ev.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInventoryMove(InventoryClickEvent ev) {
        if (ev.getClick() == ClickType.DOUBLE_CLICK) {
            ev.setCancelled(true);
            ev.setResult(Event.Result.DENY);
        } else if (ev.getClickedInventory() == null) {
            ev.setCancelled(true);
            ev.setResult(Event.Result.DENY);
        } else {
            ItemStack cursor = ev.getCursor();
            ItemStack currentItem = ev.getCurrentItem();

            Optional<Prison> cursorPrison = PrisonManager.getInstance().getPrisonByItemStack(cursor);
            Optional<Prison> itemPrison = PrisonManager.getInstance().getPrisonByItemStack(currentItem);

            if (itemPrison.isPresent() && itemPrison.get().getImprisonedPlayer().equals(ev.getWhoClicked().getUniqueId())) {
                ev.getWhoClicked().sendMessage(ChatColor.RED + "Cannot pickup your own prison.");
                ev.setCancelled(true);
            } else if (itemPrison.isPresent() && (ev.getClick() == ClickType.SHIFT_LEFT || ev.getClick() == ClickType.SHIFT_RIGHT)) {
                Inventory to = getOtherInventory(ev.getView(), ev.getClickedInventory());
                if (!itemPrison.get().setLocation(to.getHolder())) {
                    ev.getWhoClicked().sendMessage(ChatColor.RED + "Unable to move prison item to other inventory.");
                    ev.setCancelled(true);
                    ev.setResult(Event.Result.DENY);
                }
            } else if (cursorPrison.isPresent()) {
                if (!cursorPrison.get().setLocation(ev.getClickedInventory().getHolder())) {
                    ev.getWhoClicked().sendMessage(ChatColor.RED + "Unable to move prison item to other inventory.");
                    ev.setCancelled(true);
                    ev.setResult(Event.Result.DENY);
                }
            }
        }
    }

    private Inventory getOtherInventory(InventoryView compound, Inventory other) {
        if (compound.getTopInventory().equals(other)) {
            return compound.getBottomInventory();
        } else {
            return compound.getTopInventory();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryMove(InventoryMoveItemEvent ev) {
        Optional<Prison> prison = PrisonManager.getInstance().getPrisonByItemStack(ev.getItem());
        prison.ifPresent(p -> ev.setCancelled(!p.setLocation(ev.getDestination().getHolder())));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPrisonerPortal(PlayerPortalEvent ev) {
        PrisonManager.getInstance().getPrisonByPlayer(ev.getPlayer().getUniqueId()).ifPresent(p -> {
            ev.getPlayer().sendMessage(ChatColor.RED + "You are imprisoned, you cannot use portals.");
            ev.setCancelled(true);
        });
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemSpawn(ItemSpawnEvent ev) {
        Optional<Prison> prison = PrisonManager.getInstance().getPrisonByItemStack(ev.getEntity().getItemStack());
        prison.ifPresent(p -> p.setLocation(ev.getEntity()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent ev) {
        PlayerInventory inv = ev.getPlayer().getInventory();

        for (int i = 0; i < inv.getSize(); i++) {
            int finalI = i;
            PrisonManager.getInstance().getPrisonByItemStack(inv.getItem(i)).ifPresent(p -> {
                ItemStack stack = inv.getItem(finalI);
                inv.setItem(finalI, new ItemStack(Material.AIR));
                ev.getPlayer().getWorld().dropItemNaturally(ev.getPlayer().getLocation(), stack);
                LOG.info("Dropped prison item on logout (" + ev.getPlayer() + ") " + stack);
            });
        }
    }

    @Override
    public void flush() {
        PrisonManager.getInstance().flush();
    }
}

