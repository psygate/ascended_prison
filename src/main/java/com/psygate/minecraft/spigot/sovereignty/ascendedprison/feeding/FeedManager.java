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

package com.psygate.minecraft.spigot.sovereignty.ascendedprison.feeding;

import com.psygate.minecraft.spigot.sovereignty.ascendedprison.AscendedPrison;
import com.psygate.minecraft.spigot.sovereignty.ascendedprison.prison.Prison;
import com.psygate.minecraft.spigot.sovereignty.ascendedprison.prison.PrisonID;
import com.psygate.minecraft.spigot.sovereignty.ascendedprison.prison.PrisonManager;
import org.bukkit.Bukkit;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Result;
import org.jooq.impl.DSL;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.psygate.minecraft.spigot.sovereignty.ascendedprison.db.model.Tables.*;

/**
 * Created by psygate on 29.06.2016.
 */
public class FeedManager {
    private final static Logger LOG = AscendedPrison.getLogger(FeedManager.class.getName());
    private static FeedManager instance;
    private static Timer timer = new Timer("AP-Feed-Timer", true);

    private FeedManager() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                checkFeed();
            }
        }, 0L, AscendedPrison.getConf().getPrisonConf().getFeedingCycleTime());
    }

    private void checkFeed() {
        LOG.info("Checking feed cycle.");
        AscendedPrison.DBI().asyncSubmit((conf) -> {
            DSLContext ctx = DSL.using(conf);

            Set<PrisonID> prisonuuids = ctx
                    .selectDistinct(ASCENDEDPRISON_PRISON.PRISONID)
                    .from(ASCENDEDPRISON_PRISON)
                    .fetchSet(ASCENDEDPRISON_PRISON.PRISONID);

            Result<Record3<PrisonID, Integer, Timestamp>> list =
                    ctx.select(ASCENDEDPRISON_FEEDING_CYCLES.PRISONID, ASCENDEDPRISON_FEEDING_CYCLES.COST, ASCENDEDPRISON_FEEDING_CYCLES.CYCLE_TIME)
                            .from(ASCENDEDPRISON_FEEDING_CYCLES)
                            .join(ASCENDEDPRISON_PRISON)
                            .using(ASCENDEDPRISON_PRISON.PRISONID)
                            .fetch();

            for (Record3<PrisonID, Integer, Timestamp> rec : list) {
                if (rec.value3().getTime() < AscendedPrison.getConf().getPrisonConf().getFeedingCycleTime()) {
                    Bukkit.getScheduler().runTask(AscendedPrison.getInstance(), () -> feedPrison(rec.value1(), rec.value2()));
                }
            }

            prisonuuids.removeAll(list.stream().map(rec -> rec.value1()).collect(Collectors.toList()));

            for (PrisonID id : prisonuuids) {
                Bukkit.getScheduler().runTask(AscendedPrison.getInstance(), () -> feedPrison(id, 0));
            }
        });
    }

    private void feedPrison(PrisonID prisonID, Integer oldcost) {
        int totalcost = 0;
        LOG.info("Feeding prison: " + prisonID + " (Previous Cost: " + oldcost + ")");
        Optional<Prison> prisonopt = PrisonManager.getInstance().getPrisonByPrisonUUID(prisonID);
        if (!prisonopt.isPresent()) {
            LOG.warning("Prison: " + prisonID + " not found for feeding.");
        } else {
            Prison prison = prisonopt.get();
            long day = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis());
            long timeplayed = AscendedPrison.DBI().submit((conf) -> {
                DSLContext ctx = DSL.using(conf);
                BigDecimal val = ctx.select(DSL.sum(ASCENDEDPRISON_PLAYER_LOG.PLAYTIME))
                        .from(ASCENDEDPRISON_PLAYER_LOG)
                        .where(ASCENDEDPRISON_PLAYER_LOG.PUUID.eq(prison.getImprisonedPlayer()))
                        .and(ASCENDEDPRISON_PLAYER_LOG.DAY.eq(day - 1))
                        .fetchOne(Record1::value1);

                if (val == null) {
                    return 0L;
                } else {
                    return val.longValue();
                }
            });

            if (TimeUnit.MILLISECONDS.toHours(timeplayed) > 1) {
                totalcost = oldcost + 1;
                LOG.info("Prison: " + prisonID + " new cost: " + totalcost + ".");
            } else {
                long timeplayedprevious = AscendedPrison.DBI().submit((conf) -> {
                    DSLContext ctx = DSL.using(conf);
                    BigDecimal val = ctx.select(DSL.sum(ASCENDEDPRISON_PLAYER_LOG.PLAYTIME))
                            .from(ASCENDEDPRISON_PLAYER_LOG)
                            .where(ASCENDEDPRISON_PLAYER_LOG.PUUID.eq(prison.getImprisonedPlayer()))
                            .and(ASCENDEDPRISON_PLAYER_LOG.DAY.eq(day - 1))
                            .fetchOne(Record1::value1);
                    if (val == null) {
                        return 0L;
                    } else {
                        return val.longValue();
                    }
                });
                if (TimeUnit.MILLISECONDS.toHours(timeplayedprevious) > 1) {
                    LOG.info("Prison: " + prisonID + " prisoner did not play enough yesterday, but the previous day Cost stays the same.");
                } else {
                    LOG.info("Prison: " + prisonID + " prisoner did not play enough yesterday or the day before, zeroing cost.");
                    totalcost = 0;
                }
            }

            int finalTotalcost = totalcost;
            AscendedPrison.DBI().asyncSubmit((conf) -> {
                DSL.using(conf).update(ASCENDEDPRISON_FEEDING_CYCLES)
                        .set(ASCENDEDPRISON_FEEDING_CYCLES.COST, finalTotalcost)
                        .set(ASCENDEDPRISON_FEEDING_CYCLES.CYCLE_TIME, new Timestamp(System.currentTimeMillis()))
                        .where(ASCENDEDPRISON_FEEDING_CYCLES.PRISONID.eq(prison.getPrisonID()))
                        .execute();
            });
            if (totalcost > 0) {
                prison.getLocation().feedOrFree(totalcost);
            }
        }
    }

    public static FeedManager getInstance() {
        if (instance == null) {
            instance = new FeedManager();
        }

        return instance;
    }


    public void start() {

    }

    public void stop() {
        timer.cancel();
        timer.purge();
    }
}
