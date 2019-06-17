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

package com.psygate.minecraft.spigot.sovereignty.ascendedprison.summoning;

import com.psygate.minecraft.spigot.sovereignty.ascendedprison.AscendedPrison;
import com.psygate.minecraft.spigot.sovereignty.ascendedprison.db.model.tables.records.AscendedprisonInventoryStackEnchantmentsRecord;
import com.psygate.minecraft.spigot.sovereignty.ascendedprison.db.model.tables.records.AscendedprisonInventoryStackLoreRecord;
import com.psygate.minecraft.spigot.sovereignty.ascendedprison.db.model.tables.records.AscendedprisonInventoryStackRecord;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.impl.DSL;

import java.util.*;
import java.util.logging.Logger;

import static com.psygate.minecraft.spigot.sovereignty.ascendedprison.db.model.Tables.*;

/**
 * Created by psygate on 08.07.2016.
 */
public class InventoryManager {
    private final static Logger LOG = AscendedPrison.getLogger(InventoryManager.class.getName());
    private static InventoryManager instance;

    private InventoryManager() {
    }

    public static InventoryManager getInstance() {
        if (instance == null) {
            instance = new InventoryManager();
        }

        return instance;
    }

    public void storeInventory(Player p) {
        UUID puuid = p.getUniqueId();
        UUID wuuid = p.getWorld().getUID();
        SInventory inv = new SInventory(p);
        LOG.info("Storing player inventory: " + p.getName() + "(" + p.getUniqueId() + ")");

        AscendedPrison.DBI().asyncSubmit((conf) -> {
            DSLContext ctx = DSL.using(conf);
            clearRecords(puuid, wuuid, ctx);
            storeRecords(puuid, wuuid, inv, ctx);
        });
    }

    public void restoreInventory(Player p) {
        UUID puuid = p.getUniqueId();
        UUID wuuid = p.getWorld().getUID();
        LOG.info("Restoring player inventory: " + p.getName() + "(" + p.getUniqueId() + ") for world " + p.getWorld().getName() + "(" + wuuid + ")");
        AscendedPrison.DBI().submit((conf) -> {
            DSLContext ctx = DSL.using(conf);

            Result<AscendedprisonInventoryStackRecord> items = ctx.selectFrom(ASCENDEDPRISON_INVENTORY_STACK)
                    .where(ASCENDEDPRISON_INVENTORY_STACK.PUUID.eq(puuid))
                    .and(ASCENDEDPRISON_INVENTORY_STACK.WORLD_UUID.eq(wuuid))
                    .fetch();

            Map<Integer, SItemStack> stacks = new HashMap<>();

            for (AscendedprisonInventoryStackRecord rec : items) {
                int slotid = rec.getSlotId();
                stacks.compute(slotid, (slot, sItemStack) -> new SItemStack(slot));
                SItemStack sstack = stacks.get(slotid);
                sstack.setType(rec.getType());
                sstack.setData(rec.getData());
                sstack.setCustomName(rec.getCustomName());
                sstack.setDamage(rec.getDamage());
                sstack.setAmount(rec.getAmount());

                Result<AscendedprisonInventoryStackEnchantmentsRecord> enchantList = ctx
                        .selectFrom(ASCENDEDPRISON_INVENTORY_STACK_ENCHANTMENTS)
                        .where(ASCENDEDPRISON_INVENTORY_STACK_ENCHANTMENTS.PUUID.eq(puuid))
                        .and(ASCENDEDPRISON_INVENTORY_STACK_ENCHANTMENTS.WORLD_UUID.eq(wuuid))
                        .and(ASCENDEDPRISON_INVENTORY_STACK_ENCHANTMENTS.SLOT_ID.eq(slotid))
                        .fetch();

                List<SEnchant> enchants = new LinkedList<>();
                for (AscendedprisonInventoryStackEnchantmentsRecord enrec : enchantList) {
                    enchants.add(new SEnchant(enrec.getEnchantment(), enrec.getElevel()));
                }

                if (!enchants.isEmpty()) {
                    sstack.setEnchants(enchants);
                }

                Result<AscendedprisonInventoryStackLoreRecord> loreList = ctx
                        .selectFrom(ASCENDEDPRISON_INVENTORY_STACK_LORE)
                        .where(ASCENDEDPRISON_INVENTORY_STACK_LORE.PUUID.eq(puuid))
                        .and(ASCENDEDPRISON_INVENTORY_STACK_LORE.WORLD_UUID.eq(wuuid))
                        .and(ASCENDEDPRISON_INVENTORY_STACK_LORE.SLOT_ID.eq(slotid))
                        .fetch();

                List<SLore> lores = new LinkedList<>();
                for (AscendedprisonInventoryStackLoreRecord enrec : loreList) {
                    lores.add(new SLore(enrec.getLine(), enrec.getLore()));
                }

                if (!lores.isEmpty()) {
                    sstack.setLore(lores);
                }
            }

            SInventory inv = new SInventory(stacks);
            clearInventory(p);

            inv.apply(p);
            LOG.info("Restored player inventory: " + p.getName() + "(" + p.getUniqueId() + ") for world "
                    + p.getWorld().getName() + "(" + wuuid + ") "
                    + inv.items.size() + " items restored.");
            clearRecords(puuid, wuuid, ctx);
        });
    }

    private void storeRecords(UUID puuid, UUID wuuid, SInventory inv, DSLContext ctx) {
        for (SItemStack stack : inv) {
            ctx.insertInto(ASCENDEDPRISON_INVENTORY_STACK)
                    .set(ASCENDEDPRISON_INVENTORY_STACK.PUUID, puuid)
                    .set(ASCENDEDPRISON_INVENTORY_STACK.WORLD_UUID, wuuid)
                    .set(ASCENDEDPRISON_INVENTORY_STACK.TYPE, stack.getType())
                    .set(ASCENDEDPRISON_INVENTORY_STACK.DATA, stack.getData())
                    .set(ASCENDEDPRISON_INVENTORY_STACK.DAMAGE, stack.getDamage())
                    .set(ASCENDEDPRISON_INVENTORY_STACK.SLOT_ID, stack.getSlot())
                    .set(ASCENDEDPRISON_INVENTORY_STACK.AMOUNT, stack.getAmount())
                    .set(ASCENDEDPRISON_INVENTORY_STACK.CUSTOM_NAME, stack.getCustomName())
                    .execute();

            if (stack.hasEnchantments()) {
                for (SEnchant en : stack.getEnchants()) {
                    ctx.insertInto(ASCENDEDPRISON_INVENTORY_STACK_ENCHANTMENTS)
                            .set(ASCENDEDPRISON_INVENTORY_STACK_ENCHANTMENTS.PUUID, puuid)
                            .set(ASCENDEDPRISON_INVENTORY_STACK_ENCHANTMENTS.WORLD_UUID, wuuid)
                            .set(ASCENDEDPRISON_INVENTORY_STACK_ENCHANTMENTS.SLOT_ID, stack.getSlot())
                            .set(ASCENDEDPRISON_INVENTORY_STACK_ENCHANTMENTS.ENCHANTMENT, en.getEnchantment())
                            .set(ASCENDEDPRISON_INVENTORY_STACK_ENCHANTMENTS.ELEVEL, en.getLevel())
                            .execute();
                }
            }

            if (stack.hasLore()) {
                for (SLore lore : stack.getLore()) {
                    ctx.insertInto(ASCENDEDPRISON_INVENTORY_STACK_LORE)
                            .set(ASCENDEDPRISON_INVENTORY_STACK_LORE.PUUID, puuid)
                            .set(ASCENDEDPRISON_INVENTORY_STACK_LORE.WORLD_UUID, wuuid)
                            .set(ASCENDEDPRISON_INVENTORY_STACK_LORE.SLOT_ID, stack.getSlot())
                            .set(ASCENDEDPRISON_INVENTORY_STACK_LORE.LINE, lore.getLine())
                            .set(ASCENDEDPRISON_INVENTORY_STACK_LORE.LORE, lore.getText())
                            .execute();
                }
            }
        }
    }

    private void clearRecords(UUID puuid, UUID wuuid, DSLContext ctx) {
        ctx.deleteFrom(ASCENDEDPRISON_INVENTORY_STACK_LORE)
                .where(ASCENDEDPRISON_INVENTORY_STACK_LORE.PUUID.eq(puuid))
                .and(ASCENDEDPRISON_INVENTORY_STACK_LORE.WORLD_UUID.eq(wuuid))
                .execute();
        ctx.deleteFrom(ASCENDEDPRISON_INVENTORY_STACK_ENCHANTMENTS)
                .where(ASCENDEDPRISON_INVENTORY_STACK_ENCHANTMENTS.PUUID.eq(puuid))
                .and(ASCENDEDPRISON_INVENTORY_STACK_ENCHANTMENTS.WORLD_UUID.eq(wuuid))
                .execute();
        ctx.deleteFrom(ASCENDEDPRISON_INVENTORY_STACK)
                .where(ASCENDEDPRISON_INVENTORY_STACK.PUUID.eq(puuid))
                .and(ASCENDEDPRISON_INVENTORY_STACK.WORLD_UUID.eq(wuuid))
                .execute();
    }

    public void clearInventory(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[]{
                new ItemStack(Material.AIR),
                new ItemStack(Material.AIR),
                new ItemStack(Material.AIR),
                new ItemStack(Material.AIR)
        });
    }

    public void dropInventory(Player player) {
        World world = player.getWorld();
        PlayerInventory inv = player.getInventory();
        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) != null && inv.getItem(i).getType() != Material.AIR) {
                world.dropItemNaturally(player.getLocation(), inv.getItem(i));
                inv.setItem(i, new ItemStack(Material.AIR));
            }
        }

        ItemStack[] armor = copyOf(inv.getArmorContents());
        for (int i = 0; i < armor.length; i++) {
            if (armor[i] != null && armor[i].getType() != Material.AIR) {
                world.dropItemNaturally(player.getLocation(), armor[i]);
            }
            armor[i] = new ItemStack(Material.AIR);
        }

        inv.setArmorContents(armor);
    }

    private ItemStack[] copyOf(ItemStack[] armorContents) {
        ItemStack[] out = new ItemStack[armorContents.length];
        System.arraycopy(armorContents, 0, out, 0, armorContents.length);
        return out;
    }

    private class SInventory implements Iterable<SItemStack> {
        private List<SItemStack> items = new LinkedList<>();

        public SInventory(Player p) {
            ItemStack[] armor = p.getInventory().getArmorContents();
            for (int i = 0; i < armor.length; i++) {
                if (armor[i] != null && armor[i].getType() != Material.AIR) {
                    items.add(new SItemStack(i, armor[i]));
                }
            }

            for (int i = 0; i < p.getInventory().getSize(); i++) {
                ItemStack stack = p.getInventory().getItem(i);
                if (stack != null && stack.getType() != Material.AIR) {
                    items.add(new SItemStack(i + 50, stack));
                }
            }
        }

        public SInventory(Map<Integer, SItemStack> stacks) {
            items.addAll(stacks.values());
        }

        @Override
        public Iterator<SItemStack> iterator() {
            return items.iterator();
        }

        public void apply(Player p) {
            ItemStack[] armor = new ItemStack[4];

            for (SItemStack stack : items) {
                if (stack.getSlot() >= 50) {
                    p.getInventory().setItem(stack.getSlot() - 50, stack.toItemStack());
                } else {
                    armor[stack.getSlot()] = stack.toItemStack();
                }
            }

            p.getInventory().setArmorContents(armor);
        }
    }

    private class SItemStack {
        private int slot;
        private String type;
        private byte data;
        private short damage;
        private int amount;
        private String customName;
        private List<SLore> lore;
        private List<SEnchant> enchants;

        public SItemStack(int slot, ItemStack itemStack) {
            this.slot = slot;
            this.type = itemStack.getType().name();
            this.data = itemStack.getData().getData();
            this.damage = itemStack.getDurability();
            this.amount = itemStack.getAmount();

            if (itemStack.hasItemMeta()) {
                ItemMeta meta = itemStack.getItemMeta();
                if (meta.hasDisplayName()) {
                    customName = meta.getDisplayName();
                }

                if (meta.hasLore()) {
                    lore = new LinkedList<>();

                    int line = 0;
                    for (String lorestr : meta.getLore()) {
                        lore.add(new SLore(line, lorestr));
                        line++;
                    }
                }

                if (meta.hasEnchants()) {
                    enchants = new LinkedList<>();
                    for (Map.Entry<Enchantment, Integer> en : meta.getEnchants().entrySet()) {
                        enchants.add(new SEnchant(en.getKey(), en.getValue()));
                    }
                }
            }
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public SItemStack(Integer slot) {
            this.slot = slot;
        }

        public int getSlot() {
            return slot;
        }

        public void setSlot(int slot) {
            this.slot = slot;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public byte getData() {
            return data;
        }

        public void setData(byte data) {
            this.data = data;
        }

        public short getDamage() {
            return damage;
        }

        public void setDamage(short damage) {
            this.damage = damage;
        }

        public String getCustomName() {
            return customName;
        }

        public void setCustomName(String customName) {
            this.customName = customName;
        }

        public List<SLore> getLore() {
            return lore;
        }

        public void setLore(List<SLore> lore) {
            this.lore = lore;
        }

        public List<SEnchant> getEnchants() {
            return enchants;
        }

        public void setEnchants(List<SEnchant> enchants) {
            this.enchants = enchants;
        }

        public boolean hasEnchantments() {
            return enchants != null && !enchants.isEmpty();
        }

        public boolean hasLore() {
            return lore != null && !lore.isEmpty();
        }

        public ItemStack toItemStack() {
            /*
                    private int slot;
        private String type;
        private byte data;
        private short damage;
        private String customName;
        private List<SLore> lore;
        private List<SEnchant> enchants;
             */

            ItemStack stack = new ItemStack(Material.valueOf(type), amount, damage, data);
            if (hasCustomName()) {
                ItemMeta meta = stack.getItemMeta();
                meta.setDisplayName(getCustomName());
                stack.setItemMeta(meta);
            }

            if (hasEnchantments()) {
                ItemMeta meta = stack.getItemMeta();
                for (SEnchant sen : enchants) {
                    meta.addEnchant(sen.getEnchantmentBukkit(), sen.getLevel(), true);
                }
                stack.setItemMeta(meta);
            }

            if (hasLore()) {
                ItemMeta meta = stack.getItemMeta();
                ArrayList<String> lore = new ArrayList<>(this.lore.size());
                for (int i = 0; i < this.lore.size(); i++) {
                    lore.add("");
                }

                for (SLore slore : this.lore) {
                    lore.set(slore.getLine(), slore.getText());
                }

                meta.setLore(lore);
                stack.setItemMeta(meta);
            }

            return stack;
        }

        private boolean hasCustomName() {
            return customName != null && !"".equals(customName);
        }
    }

    private class SEnchant {
        private String enchantment;
        private int level;

        public SEnchant(Enchantment key, Integer value) {
            enchantment = key.getName();
            level = value;
        }

        public SEnchant(String key, Integer value) {
            enchantment = key;
            level = value;
        }

        public String getEnchantment() {
            return enchantment;
        }

        public void setEnchantment(String enchantment) {
            this.enchantment = enchantment;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public Enchantment getEnchantmentBukkit() {
            return Enchantment.getByName(enchantment);
        }
    }

    private class SLore {
        private int line;
        private String text;

        public SLore(int line, String text) {
            this.line = line;
            this.text = text;
        }

        public int getLine() {
            return line;
        }

        public void setLine(int line) {
            this.line = line;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
