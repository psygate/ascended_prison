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

package com.psygate.minecraft.spigot.sovereignty.ascendedprison.commands;

import com.psygate.minecraft.spigot.sovereignty.ascendedprison.AscendedPrison;
import com.psygate.minecraft.spigot.sovereignty.ascendedprison.prison.Prison;
import com.psygate.minecraft.spigot.sovereignty.ascendedprison.prison.PrisonManager;
import com.psygate.minecraft.spigot.sovereignty.nucleus.commands.util.NucleusPlayerCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.Optional;

/**
 * Created by psygate on 30.06.2016.
 */
public class LocatePrisonCommand extends NucleusPlayerCommand {
    public LocatePrisonCommand() {
        super(0, 0);
    }

    @Override
    protected void subOnCommand(Player player, Command command, String s, String[] strings) throws Exception {
        Optional<Prison> prisonopt = PrisonManager.getInstance().getPrisonByPlayer(player.getUniqueId());

        if (!prisonopt.isPresent()) {
            player.sendMessage(ChatColor.YELLOW + "You're not imprisoned.");
            if (player.getWorld().getName().equals(AscendedPrison.getConf().getPrisonConf().getPrisonWorldName())) {
                AscendedPrison.randomSpawn(player, Bukkit.getWorld(AscendedPrison.getConf().getPrisonConf().getDefaultFreeWorld()).getSpawnLocation());
            }
        } else {
            player.sendMessage(ChatColor.YELLOW + prisonopt.get().getLocation().toPlayerString());
        }
    }

    @Override
    protected String[] getName() {
        return new String[]{"locateprison"};
    }
}
