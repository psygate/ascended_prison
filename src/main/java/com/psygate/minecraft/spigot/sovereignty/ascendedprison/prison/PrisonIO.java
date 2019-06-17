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

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalNotification;
import com.psygate.minecraft.spigot.sovereignty.ascendedprison.AscendedPrison;
import com.psygate.minecraft.spigot.sovereignty.ascendedprison.db.model.tables.records.AscendedprisonFeedingCyclesRecord;
import com.psygate.minecraft.spigot.sovereignty.ascendedprison.db.model.tables.records.AscendedprisonPrisonHistoryRecord;
import com.psygate.minecraft.spigot.sovereignty.ascendedprison.db.model.tables.records.AscendedprisonPrisonRecord;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.impl.DSL;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.psygate.minecraft.spigot.sovereignty.ascendedprison.db.model.Tables.*;

/**
 * Created by psygate on 30.06.2016.
 */
class PrisonIO {
    private final static Logger LOG = AscendedPrison.getLogger(PrisonIO.class.getName());
    private final LoadingCache<UUID, Optional<Prison>> prisonByPlayerUUIDCache = CacheBuilder.newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .initialCapacity(100)
            .maximumSize(1000)
            .removalListener(this::unloadPrison)
            .build(new CacheLoader<UUID, Optional<Prison>>() {
                @Override
                public Optional<Prison> load(UUID uuid) throws Exception {
                    final Optional<Prison> prisonopt = AscendedPrison.DBI().submit((conf) -> {
                        Optional<Prison> popt = DSL.using(conf).selectFrom(ASCENDEDPRISON_PRISON)
                                .where(ASCENDEDPRISON_PRISON.PRISONER_UUID.eq(uuid))
                                .fetchOptional()
                                .map(v -> new Prison(v));

                        return Objects.requireNonNull(popt, () -> "Database didn't return a valid optional.");
                    });

                    Objects.requireNonNull(prisonopt, () -> "Database didn't return a valid optional.");

                    if (!prisonopt.isPresent()) {
                        LOG.fine("No prison mapping recorded for player " + uuid);
                    }

                    return prisonopt;
                }
            });

    private final LoadingCache<PrisonID, Optional<Prison>> prisonUUIDtoPrisonerUUIDCache = CacheBuilder.newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .initialCapacity(100)
            .maximumSize(1000)
            .build(new CacheLoader<PrisonID, Optional<Prison>>() {
                @Override
                public Optional<Prison> load(PrisonID prisonid) throws Exception {
                    final Optional<UUID> prisonopt = AscendedPrison.DBI().submit((conf) -> {
                        return DSL.using(conf)
                                .select(ASCENDEDPRISON_PRISON.PRISONER_UUID)
                                .from(ASCENDEDPRISON_PRISON)
                                .where(ASCENDEDPRISON_PRISON.PRISONID.eq(prisonid))
                                .fetchOptional()
                                .map(Record1<UUID>::value1);
                    });
                    if (prisonopt.isPresent()) {
                        return prisonByPlayerUUIDCache.getUnchecked(prisonopt.get());
                    } else {
                        return Optional.empty();
                    }
                }
            });

    public Optional<Prison> getPrisonByID(PrisonID prisonID) {
        return prisonUUIDtoPrisonerUUIDCache.getUnchecked(prisonID);
    }

    public Optional<Prison> getPrisonByPlayerUUID(UUID player) {
        return prisonByPlayerUUIDCache.getUnchecked(player);
    }

    public List<Prison> getSummonedPrisons() {
        List<UUID> prisonerIDS = AscendedPrison.DBI().submit(conf -> {
            DSLContext ctx = DSL.using(conf);
            return ctx.select(ASCENDEDPRISON_PRISON.PRISONER_UUID)
                    .from(ASCENDEDPRISON_PRISON)
                    .where(ASCENDEDPRISON_PRISON.SUMMONED_STATE.eq(true))
                    .fetch(Record1<UUID>::value1);
        });

        List<Prison> prisons = prisonerIDS.stream()
                .map(prisonByPlayerUUIDCache::getUnchecked)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        return prisons;
    }

    private void unloadPrison(RemovalNotification<UUID, Optional<Prison>> rn) {
        rn.getValue().ifPresent(prison -> {
                    LOG.info("Unloading prison " + prison.getPrisonID());

                    if (prison.getLocation() == null) {
                        LOG.warning("Unable to unload prison, location is null. " + prison);
                        return;
                    } else if (prison.getLocation().getLocation() == null) {
                        LOG.warning("Unable to unload prison, location is unmappable. " + prison);
                        return;
                    } else if (prison.getLocation().getLocation().getWorld() == null) {
                        LOG.warning("Unable to unload prison, world is unmappable. " + prison);
                        return;
                    }

                    AscendedPrison.DBI().asyncSubmit(conf -> {
                        DSLContext ctx = DSL.using(conf);
                        ctx.update(ASCENDEDPRISON_PRISON)
                                .set(ASCENDEDPRISON_PRISON.SUMMONED_STATE, prison.isSummoned())
                                .set(ASCENDEDPRISON_PRISON.PRISON_LOCATION_TYPE, prison.getLocation().getLocationName())
                                .set(ASCENDEDPRISON_PRISON.LEASH_RANGE, prison.getLeashRange())
                                .set(ASCENDEDPRISON_PRISON.X, prison.getLocation().getLocation().getX())
                                .set(ASCENDEDPRISON_PRISON.Y, prison.getLocation().getLocation().getY())
                                .set(ASCENDEDPRISON_PRISON.Z, prison.getLocation().getLocation().getZ())
                                .set(ASCENDEDPRISON_PRISON.WORLD_UUID, prison.getLocation().getLocation().getWorld().getUID())
                                .execute();
                        LOG.info("Updated prison: " + prison);
                    });
                }
        );
    }

    public void persist(Prison prison) {
        AscendedPrison.DBI().submit((conf) -> {
            DSLContext ctx = DSL.using(conf);
            AscendedprisonPrisonRecord rec = new AscendedprisonPrisonRecord(
                    prison.getPrisonID(),
                    prison.getImprisonedPlayer(),
                    prison.getImprisoner(),
                    new Timestamp(prison.getImprisonTime()),
                    prison.isSummoned(),
                    prison.getLocation().getLocationName(),
                    prison.getLeashRange(),
                    prison.getLocation().getLocation().getX(),
                    prison.getLocation().getLocation().getY(),
                    prison.getLocation().getLocation().getZ(),
                    prison.getLocation().getLocation().getWorld().getUID(),
                    prison.getLocation().getLocationUUID()
            );

            ctx.insertInto(ASCENDEDPRISON_PRISON).set(rec).onDuplicateKeyUpdate().set(rec).execute();

            Optional<AscendedprisonPrisonHistoryRecord> histopt = ctx.selectFrom(ASCENDEDPRISON_PRISON_HISTORY)
                    .where(ASCENDEDPRISON_PRISON_HISTORY.PRISONER_UUID.eq(prison.getImprisonedPlayer()))
                    .and(ASCENDEDPRISON_PRISON_HISTORY.RECORDED.ge(new Timestamp(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7))))
                    .fetchOptional();

            histopt.ifPresent(histrec -> {
                AscendedprisonFeedingCyclesRecord cycleRec = new AscendedprisonFeedingCyclesRecord(
                        new Timestamp(System.currentTimeMillis()),
                        prison.getPrisonID(),
                        histrec.getCost()
                );

                ctx.insertInto(ASCENDEDPRISON_FEEDING_CYCLES).set(cycleRec).onDuplicateKeyUpdate().set(cycleRec).execute();
            });

        });

        prisonByPlayerUUIDCache.put(prison.getImprisonedPlayer(), Optional.of(prison));
        prisonUUIDtoPrisonerUUIDCache.put(prison.getPrisonID(), Optional.of(prison));
    }

    public void delete(Prison prison) {

        AscendedPrison.DBI().submit((conf) -> {
            DSLContext ctx = DSL.using(conf);

            Optional<AscendedprisonFeedingCyclesRecord> recopt = ctx.selectFrom(ASCENDEDPRISON_FEEDING_CYCLES)
                    .where(ASCENDEDPRISON_FEEDING_CYCLES.PRISONID.eq(prison.getPrisonID()))
                    .fetchOptional();

            recopt.ifPresent(rec -> {

                AscendedprisonPrisonHistoryRecord histrec = new AscendedprisonPrisonHistoryRecord(
                        prison.getImprisonedPlayer(),
                        prison.getImprisoner(),
                        new Timestamp(prison.getImprisonTime()),
                        rec.getCost(),
                        new Timestamp(System.currentTimeMillis())
                );
                ctx.insertInto(ASCENDEDPRISON_PRISON_HISTORY).set(histrec).onDuplicateKeyUpdate().set(histrec).execute();
            });

            prisonUUIDtoPrisonerUUIDCache.put(prison.getPrisonID(), Optional.empty());
            prisonByPlayerUUIDCache.put(prison.getImprisonedPlayer(), Optional.empty());

            ctx.deleteFrom(ASCENDEDPRISON_FEEDING_CYCLES)
                    .where(ASCENDEDPRISON_FEEDING_CYCLES.PRISONID.eq(prison.getPrisonID()))
                    .execute();
            int del = ctx
                    .deleteFrom(ASCENDEDPRISON_PRISON)
                    .where(ASCENDEDPRISON_PRISON.PRISONID.eq(prison.getPrisonID()))
                    .execute();

            if (del <= 0) {
                LOG.severe("Unable to delete prison: " + prison);
            }
        });
        LOG.info("Deleted prison: " + prison);
    }


    public void flush() {
        prisonUUIDtoPrisonerUUIDCache.invalidateAll();
        prisonByPlayerUUIDCache.invalidateAll();
    }
}
