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

package com.psygate.minecraft.spigot.sovereignty.ascendedprison.binding;

import co.sovereigntymc.tagtonics.events.PlayerTaggedDeathEvent;
import co.sovereigntymc.tagtonics.events.PlayerTaggedQuitEvent;
import com.psygate.minecraft.spigot.sovereignty.ascendedprison.AscendedPrison;
import com.psygate.minecraft.spigot.sovereignty.ascendedprison.prison.Prison;
import com.psygate.minecraft.spigot.sovereignty.ascendedprison.prison.PrisonManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Optional;
import java.util.logging.Logger;

/**
 * Created by psygate on 27.06.2016.
 */
public class BindListener implements Listener {
    private final static Logger LOG = AscendedPrison.getLogger(BindListener.class.getName());

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerDeath(PlayerTaggedDeathEvent ev) {
        LOG.info(ev.getPlayerUUID() + " died tagged, checking for bind.");
        if (!AscendedPrison.getConf().getPrisonConf().isNoBindWorld(ev.getLocation().getWorld().getUID())) {
            Optional<Prison> prisonopt = PrisonManager.getInstance().getPrisonByPlayer(ev.getPlayerUUID());

            if (prisonopt.isPresent() && AscendedPrison.getConf().getPrisonConf().isNoStealWorld(ev.getLocation().getWorld().getUID())) {
                LOG.info("Skipping binding of " + ev.getPlayerUUID() + ", " + ev.getLocation().getWorld() + " is a no-steal world (Player already imprisoned.)");
            } else {
                BindManager.getInstance().imprisonPlayerByCombatTags(ev.getPlayerUUID(), ev.getTags());
            }
        } else {
            LOG.info("Skipping binding of " + ev.getPlayerUUID() + ", " + ev.getLocation().getWorld() + " is a no-bind world.");
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerDeath(PlayerTaggedQuitEvent ev) {
        LOG.info(ev.getPlayerUUID() + " quit tagged, checking for bind.");
        if (!AscendedPrison.getConf().getPrisonConf().isNoBindWorld(ev.getLocation().getWorld().getUID())) {
            Optional<Prison> prisonopt = PrisonManager.getInstance().getPrisonByPlayer(ev.getPlayerUUID());

            if (prisonopt.isPresent() && AscendedPrison.getConf().getPrisonConf().isNoStealWorld(ev.getLocation().getWorld().getUID())) {
                LOG.info("Skipping binding of " + ev.getPlayerUUID() + ", " + ev.getLocation().getWorld() + " is a no-steal world (Player already imprisoned.)");
            } else {
                BindManager.getInstance().imprisonPlayerByCombatTags(ev.getPlayerUUID(), ev.getTags());
            }
        } else {
            LOG.info("Skipping binding of " + ev.getPlayerUUID() + ", " + ev.getLocation().getWorld() + " is a no-bind world.");
        }
    }
}
