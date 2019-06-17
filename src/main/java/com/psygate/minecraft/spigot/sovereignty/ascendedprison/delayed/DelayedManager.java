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

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.psygate.minecraft.spigot.sovereignty.ascendedprison.AscendedPrison;
import com.psygate.minecraft.spigot.sovereignty.ascendedprison.db.model.tables.records.AscendedprisonDelayedActionsLocationRecord;
import com.psygate.minecraft.spigot.sovereignty.ascendedprison.db.model.tables.records.AscendedprisonDelayedActionsRecord;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.psygate.minecraft.spigot.sovereignty.ascendedprison.db.model.Tables.ASCENDEDPRISON_DELAYED_ACTIONS;
import static com.psygate.minecraft.spigot.sovereignty.ascendedprison.db.model.Tables.ASCENDEDPRISON_DELAYED_ACTIONS_LOCATION;

/**
 * Created by psygate on 26.06.2016.
 */
public class DelayedManager {
    private final static Logger LOG = AscendedPrison.getLogger(DelayedManager.class.getName());
    private static DelayedManager instance;
    private LoadingCache<UUID, List<DelayedAction>> actionCache = CacheBuilder.newBuilder()
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .initialCapacity(100)
            .maximumSize(1000)
            .build(new CacheLoader<UUID, List<DelayedAction>>() {
                @Override
                public List<DelayedAction> load(UUID uuid) throws Exception {
                    List<DelayedAction> local = loadActions(uuid);

                    return Objects.requireNonNull(local, () -> "Loader returned null.");
                }

            });

    private List<DelayedAction> loadActions(UUID uuid) {
        List<DelayedAction> local = new LinkedList<>();
        AscendedPrison.DBI().submit((conf) -> {
            DSLContext ctx = DSL.using(conf);
            List<DelayedAction> actions = ctx.selectFrom(ASCENDEDPRISON_DELAYED_ACTIONS).where(ASCENDEDPRISON_DELAYED_ACTIONS.PUUID.eq(uuid))
                    .orderBy(ASCENDEDPRISON_DELAYED_ACTIONS.ACTION_ID.asc())
                    .fetch()
                    .map(DelayedManager::mapToActions)
                    .stream()
                    .collect(Collectors.toList());

            for (DelayedAction action : actions) {
                if (action instanceof DelayedLocationAction) {
                    apply((DelayedLocationAction) action, ctx.selectFrom(ASCENDEDPRISON_DELAYED_ACTIONS_LOCATION)
                            .where(ASCENDEDPRISON_DELAYED_ACTIONS_LOCATION.ACTION_ID.eq(action.getActionID()))
                            .fetchOne());
                }
            }

            local.addAll(actions);
        });

        return Objects.requireNonNull(local, () -> "Local return is null?");
    }

    private void apply(DelayedLocationAction action, AscendedprisonDelayedActionsLocationRecord rec) {
        if (rec == null) {
            LOG.severe("Location record assembly called, but record has no attached location.");
            return;
        }
        World w = Objects.requireNonNull(Bukkit.getWorld(rec.getWorldUuid()), () -> "World of location action is null.");
        Location loc = new Location(
                w,
                rec.getX(),
                rec.getY(),
                rec.getZ()
        );
        action.setLoc(loc);
    }

    private DelayedManager() {

    }

    private static DelayedAction mapToActions(AscendedprisonDelayedActionsRecord rec) {
        switch (rec.getActionType()) {
            case 0:
                return new DelayedClearInventoryAction(rec);
            case 1:
                return new DelayedKillPlayer(rec);
            case 2:
                return new DelayedTeleportPlayer(rec);
            case 4:
                return new DelayedMessagePlayer(rec);
            case 5:
                return new DelayedRandomSpawnPlayer(rec);
            case 6:
                return new DelayedDropInventoryAction(rec);
            case 7:
                return new DelayedStoreInventoryPlayer(rec);
            case 8:
                return new DelayedRestoreInventoryPlayer(rec);
            case 9:
                return new DelayedBedSpawnRemoveAction(rec);
            default:
                throw new IllegalStateException("Unknown action type. " + rec.getActionType() + " [ID: " + rec.getActionId() + "]");
        }
    }

    public static DelayedManager getInstance() {
        if (instance == null) {
            instance = new DelayedManager();
        }

        return instance;
    }

    public void process(Player player, Event ev) {
        List<DelayedAction> proc = actionCache.getUnchecked(player.getUniqueId());
        if (!proc.isEmpty()) {
            Iterator<DelayedAction> it = proc.iterator();

            while (it.hasNext()) {
                DelayedAction ac = it.next();
                ac.process(ev);
                if (ac.isProcessed()) {
                    AscendedPrison.DBI().asyncSubmit((conf) -> {
                        DSL.using(conf).deleteFrom(ASCENDEDPRISON_DELAYED_ACTIONS_LOCATION)
                                .where(ASCENDEDPRISON_DELAYED_ACTIONS_LOCATION.ACTION_ID.eq(ac.getActionID()))
                                .execute();
                        DSL.using(conf).deleteFrom(ASCENDEDPRISON_DELAYED_ACTIONS)
                                .where(ASCENDEDPRISON_DELAYED_ACTIONS.ACTION_ID.eq(ac.getActionID()))
                                .execute();
                    });
                    it.remove();
                }
            }
        }
    }

    public void add(DelayedClearInventoryAction dkp) {
        LOG.info("Pushing action: " + dkp);
        actionCache.getUnchecked(dkp.getPlayer()).add(dkp);
        persistSimpleAction(dkp);
    }

    public void add(DelayedBedSpawnRemoveAction dkp) {
        LOG.info("Pushing action: " + dkp);
        actionCache.getUnchecked(dkp.getPlayer()).add(dkp);
        persistSimpleAction(dkp);
    }

    public void add(DelayedTeleportPlayer dkp) {
        LOG.info("Pushing action: " + dkp);
        actionCache.getUnchecked(dkp.getPlayer()).add(dkp);
        persistLocationAction(dkp);
    }

    private void persistLocationAction(DelayedLocationAction dkp) {
        AscendedPrison.DBI().submit((conf) -> {
            DSLContext ctx = DSL.using(conf);
            persistBasic(dkp, ctx);
            persistLocation(dkp, ctx);
        });
    }

    private void persistLocation(DelayedLocationAction dkp, DSLContext ctx) {
        ctx.insertInto(ASCENDEDPRISON_DELAYED_ACTIONS_LOCATION)
                .set(ASCENDEDPRISON_DELAYED_ACTIONS_LOCATION.ACTION_ID, dkp.getActionID())
                .set(ASCENDEDPRISON_DELAYED_ACTIONS_LOCATION.X, dkp.getLoc().getX())
                .set(ASCENDEDPRISON_DELAYED_ACTIONS_LOCATION.Y, dkp.getLoc().getY())
                .set(ASCENDEDPRISON_DELAYED_ACTIONS_LOCATION.Z, dkp.getLoc().getZ())
                .set(ASCENDEDPRISON_DELAYED_ACTIONS_LOCATION.WORLD_UUID, dkp.getLoc().getWorld().getUID())
                .execute();
    }


    public void add(DelayedKillPlayer dkp) {
        actionCache.getUnchecked(dkp.getPlayer()).add(dkp);
        persistSimpleAction(dkp);
    }

    private void persistSimpleAction(DelayedAction dkp) {
        AscendedPrison.DBI().submit((conf) -> {
            persistBasic(dkp, DSL.using(conf));
        });
    }

    private void persistBasic(DelayedAction dkp, DSLContext ctx) {
        long id = ctx.insertInto(ASCENDEDPRISON_DELAYED_ACTIONS)
                .set(ASCENDEDPRISON_DELAYED_ACTIONS.ACTION_TYPE, dkp.getActionTypeID())
                .set(ASCENDEDPRISON_DELAYED_ACTIONS.PUUID, dkp.getPlayer())
                .set(ASCENDEDPRISON_DELAYED_ACTIONS.REASON, dkp.getReason())
                .returning(ASCENDEDPRISON_DELAYED_ACTIONS.ACTION_ID)
                .fetchOne().value1();
        dkp.setActionID(id);
    }

    public void add(DelayedMessagePlayer dkp) {
        LOG.info("Pushing action: " + dkp);
        actionCache.getUnchecked(dkp.getPlayer()).add(dkp);
        persistSimpleAction(dkp);
    }

    public void add(DelayedRandomSpawnPlayer dkp) {
        LOG.info("Pushing action: " + dkp);
        actionCache.getUnchecked(dkp.getPlayer()).add(dkp);
        persistLocationAction(dkp);
    }

    public void add(DelayedDropInventoryAction dkp) {
        LOG.info("Pushing action: " + dkp);
        actionCache.getUnchecked(dkp.getPlayer()).add(dkp);
        persistSimpleAction(dkp);
    }

    public void clearActions(UUID imprisonedPlayer) {
        actionCache.invalidate(imprisonedPlayer);
        AscendedPrison.DBI().submit((conf) -> {
            DSLContext ctx = DSL.using(conf);
            List<Long> ids = ctx.selectFrom(ASCENDEDPRISON_DELAYED_ACTIONS)
                    .where(ASCENDEDPRISON_DELAYED_ACTIONS.PUUID.eq(imprisonedPlayer))
                    .fetch(AscendedprisonDelayedActionsRecord::getActionId);

            ctx.deleteFrom(ASCENDEDPRISON_DELAYED_ACTIONS_LOCATION).where(ASCENDEDPRISON_DELAYED_ACTIONS_LOCATION.ACTION_ID.in(ids)).execute();
            ctx.deleteFrom(ASCENDEDPRISON_DELAYED_ACTIONS).where(ASCENDEDPRISON_DELAYED_ACTIONS.ACTION_ID.in(ids)).execute();
        });

        LOG.info("Removed all pending actions for " + imprisonedPlayer);
    }

    public void add(DelayedStoreInventoryPlayer dkp) {
        LOG.info("Pushing action: " + dkp);
        actionCache.getUnchecked(dkp.getPlayer()).add(dkp);
        persistSimpleAction(dkp);
    }

    public void add(DelayedRestoreInventoryPlayer dkp) {
        LOG.info("Pushing action: " + dkp);
        actionCache.getUnchecked(dkp.getPlayer()).add(dkp);
        persistSimpleAction(dkp);
    }
}
