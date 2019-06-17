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

import org.bukkit.event.Event;

import java.util.UUID;

/**
 * Created by psygate on 26.06.2016.
 */
public abstract class DelayedAction {
    private boolean processed = false;
    private long actionID = -1;
    private UUID player;

    public DelayedAction(UUID player) {
        this.player = player;
    }

    public abstract void process(Event ev);

    protected void setProcessed(boolean proc) {
        this.processed = proc;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setActionID(long actionID) {
        this.actionID = actionID;
    }

    public long getActionID() {
        return actionID;
    }

    public abstract int getActionTypeID();

    public UUID getPlayer() {
        return player;
    }

    public abstract String getReason();
}
