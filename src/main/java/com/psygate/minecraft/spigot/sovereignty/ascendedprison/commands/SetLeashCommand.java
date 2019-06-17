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

import com.psygate.minecraft.spigot.sovereignty.ascendedprison.prison.Prison;
import com.psygate.minecraft.spigot.sovereignty.ascendedprison.prison.PrisonManager;
import com.psygate.minecraft.spigot.sovereignty.nucleus.commands.util.CommandException;
import com.psygate.minecraft.spigot.sovereignty.nucleus.commands.util.NucleusPlayerCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.Optional;

/**
 * Created by psygate on 07.07.2016.
 */
public class SetLeashCommand extends NucleusPlayerCommand {
    public SetLeashCommand() {
        super(1, 1);
    }

    @Override
    protected void subOnCommand(Player player, Command command, String s, String[] strings) throws Exception {
        double leash = 10;
        try {
            leash = Double.parseDouble(strings[0]);
            if (leash < 0.1) {
                throw new IllegalArgumentException();
            }
        } catch (Exception e) {
            throw new CommandException("Invalid distance: " + strings);
        }
        Optional<Prison> prisonopt = PrisonManager.getInstance().getPrisonByItemStack(player.getItemInHand());
        if (!prisonopt.isPresent()) {
            throw new CommandException("Not a prison.");
        } else {
            prisonopt.get().setLeashRange(leash);
            player.sendMessage(ChatColor.GREEN + "Leash range set to " + leash + " units.");
        }
    }

    @Override
    protected String[] getName() {
        return new String[]{"setleash"};
    }
}
