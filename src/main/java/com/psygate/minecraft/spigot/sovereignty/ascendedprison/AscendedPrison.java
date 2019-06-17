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
package com.psygate.minecraft.spigot.sovereignty.ascendedprison;

import com.psygate.minecraft.spigot.sovereignty.ascendedprison.binding.BindListener;
import com.psygate.minecraft.spigot.sovereignty.ascendedprison.configuration.Configuration;
import com.psygate.minecraft.spigot.sovereignty.ascendedprison.delayed.DelayedListener;
import com.psygate.minecraft.spigot.sovereignty.ascendedprison.feeding.FeedManager;
import com.psygate.minecraft.spigot.sovereignty.ascendedprison.feeding.PlayerTrackerListener;
import com.psygate.minecraft.spigot.sovereignty.ascendedprison.prison.PrisonID;
import com.psygate.minecraft.spigot.sovereignty.ascendedprison.prison.PrisonListener;
import com.psygate.minecraft.spigot.sovereignty.ascendedprison.prison.PrisonManager;
import com.psygate.minecraft.spigot.sovereignty.nucleus.Nucleus;
import com.psygate.minecraft.spigot.sovereignty.nucleus.managment.NucleusPlugin;
import com.psygate.minecraft.spigot.sovereignty.nucleus.sql.DatabaseInterface;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.*;

/**
 * @author psygate (https://github.com/psygate)
 */
public class AscendedPrison extends JavaPlugin implements NucleusPlugin {

    private final static Logger LOG = Logger.getLogger(AscendedPrison.class.getName());

    private static AscendedPrison instance;

    public static Logger logger() {
        return LOG;
    }

    private Configuration conf;
    private DatabaseInterface dbi;
    private Flusher flusher;

    static {
        LOG.setUseParentHandlers(false);
        LOG.setLevel(Level.ALL);
        List<Handler> handlers = Arrays.asList(LOG.getHandlers());

        if (handlers.stream().noneMatch(h -> h instanceof FileHandler)) {
            try {
                File logdir = new File("logs/nucleus_logs/ascendedprison/");
                if (!logdir.exists()) {
                    logdir.mkdirs();
                }
                FileHandler fh = new FileHandler(
                        "logs/nucleus_logs/ascendedprison/ascendedprison.%u.%g.log",
                        8 * 1024 * 1024,
                        12,
                        true
                );
                fh.setLevel(Level.ALL);
                fh.setEncoding("UTF-8");
                SimpleFormatter formatter = new SimpleFormatter();
                fh.setFormatter(formatter);
                LOG.addHandler(fh);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Logger getLogger(String name) {
        Logger log = Logger.getLogger(name);
        log.setParent(LOG);
        log.setUseParentHandlers(true);
        log.setLevel(Level.ALL);
        return log;
    }

    public static Configuration getConf() {
        return getInstance().conf;
    }

    @Override
    public void onEnable() {
        try {
            instance = this;
            saveDefaultConfig();
            conf = new Configuration(getConfig());
            Nucleus.getInstance().register(this);
            flusher.add(PrisonManager.getInstance());
            FeedManager.getInstance().start();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Override
    public void onDisable() {
        if (flusher != null) {
            flusher.run();
        }
        for (Handler handler : LOG.getHandlers()) {
            handler.flush();
        }
        FeedManager.getInstance().stop();

    }

    @Override
    public int getWantedDBVersion() {
        return 1;
    }

    @Override
    public void fail() {
        System.err.println("Ascended Prison failed to load.");
        Bukkit.shutdown();
    }

    @Override
    public void setLogger(Logger logger) {

    }

    @Override
    public Logger getSubLogger(String logname) {
        return Logger.getLogger(logname);
    }

    @Override
    public Logger getPluginLogger() {
        return LOG;
    }

    @Override
    public List<Listener> getListeners() {
        return getAndRegisterListeners();
    }

    @Override
    public void setDatabaseInterface(DatabaseInterface databaseInterface) {
        this.dbi = databaseInterface;
    }

    public static AscendedPrison getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Plugin not enabled.");
        }
        return instance;
    }

    public static String generateSignature(PrisonID apid) {
        return "AP-ID: " + apid;
    }

    public static Optional<PrisonID> getPrisonUUID(String apstr) {
        try {
            return Optional.of(new PrisonID(UUID.fromString(apstr.replace("AP-ID: ", ""))));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static DatabaseInterface DBI() {
        return getInstance().dbi;
    }

    private List<Listener> getAndRegisterListeners() {
        List<Listener> li = Arrays.asList(
                new DelayedListener(),
                new Listener() {
                    @EventHandler
                    public void debug(AsyncPlayerChatEvent ev) {
                        if ("fill".equals(ev.getMessage()) && ev.getPlayer().isOp()) {
                            ev.getPlayer().getInventory().setArmorContents(new ItemStack[]{
                                    new ItemStack(Material.DIAMOND_BOOTS),
                                    new ItemStack(Material.DIAMOND_LEGGINGS),
                                    new ItemStack(Material.DIAMOND_CHESTPLATE),
                                    new ItemStack(Material.DIAMOND_HELMET)
                            });

                            for (ItemStack stack : ev.getPlayer().getInventory().getArmorContents()) {
                                stack.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
                            }

                            ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
                            sword.addEnchantment(Enchantment.DAMAGE_ALL, 5);
                            sword.addEnchantment(Enchantment.FIRE_ASPECT, 2);

                            ev.getPlayer().getInventory().addItem(sword);
                            Material type = AscendedPrison.getConf().getBindingConf().getBindType();
                            ev.getPlayer().getInventory().addItem(new ItemStack(type, type.getMaxStackSize()));
                            ev.getPlayer().getInventory().addItem(new ItemStack(Material.BREAD, 64));
                        }
                    }
                },
                new BindListener(),
                new PrisonListener(),
                new PlayerTrackerListener()
        );

        li.stream().filter(v -> v instanceof Flushable).map(v -> (Flushable) v).forEach(v -> getFlusher().add(v));
        return li;
    }

    public Flusher getFlusher() {
        if (flusher == null) {
            flusher = new Flusher();
            Bukkit.getScheduler().scheduleSyncRepeatingTask(
                    this,
                    flusher,
                    20 * 60 * 5,
                    20 * 60 * 5
            );
        }
        return flusher;
    }

    public static void randomSpawn(Player p, Location loc) {
        Plugin sp = Bukkit.getPluginManager().getPlugin("Random Spawn");
        Plugin wb = Bukkit.getPluginManager().getPlugin("WorldBorder");
        if (sp != null) {
            Location spawn = ((me.josvth.randomspawn.RandomSpawn) sp).chooseSpawn(loc.getWorld());
            LOG.info("Random spawning " + p.getName() + "(" + p.getUniqueId() + ") at " + spawn + " using random spawn plugin.");
            p.teleport(spawn);
        } else if (wb != null) {
            Random rand = new Random();
            com.wimbli.WorldBorder.BorderData data = ((com.wimbli.WorldBorder.WorldBorder) wb).getWorldBorder(loc.getWorld().getName());
            Location spawn = loc.clone();
            double angle = rand.nextDouble() * 2 * Math.PI;
            double radx = rand.nextDouble() * data.getRadiusX(), radz = rand.nextDouble() * data.getRadiusZ();
            double x = Math.cos(angle) * radx, z = Math.sin(angle) * radz;
            spawn.setX(x);
            spawn.setZ(z);
            spawn.setY(spawn.getWorld().getHighestBlockYAt((int) x, (int) z) + 1);
            LOG.info("Random spawning " + p.getName() + "(" + p.getUniqueId() + ") at " + spawn + " using world border.");
            p.teleport(spawn);
        } else {
            Location spawn = loc.getWorld().getSpawnLocation();
            LOG.info("Spawning " + p.getName() + "(" + p.getUniqueId() + ") at " + spawn + " using natural spawn point.");
            p.teleport(spawn);
        }
    }

    private class Flusher implements Runnable {
        private List<Flushable> flush = new LinkedList<>();

        public boolean add(Flushable flushable) {
            return flush.add(flushable);
        }

        @Override
        public void run() {
            for (Flushable fl : flush) {
                fl.flush();
            }
        }
    }
}
