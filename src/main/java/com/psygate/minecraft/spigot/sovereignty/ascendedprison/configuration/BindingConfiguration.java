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

package com.psygate.minecraft.spigot.sovereignty.ascendedprison.configuration;

import com.psygate.minecraft.spigot.sovereignty.ascendedprison.binding.BindStrategy;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;

import java.util.*;

/**
 * Created by psygate on 27.06.2016.
 */
public class BindingConfiguration {
    private Material bindType;
    private boolean requireHotBar;
    private BindStrategy bindStrategy;
    private List<Enchantment> enchants = new LinkedList<>();
    private String nameTemplate;
    private String loreTemplate;

    public BindingConfiguration(ConfigurationSection sec) {
        bindType = Material.valueOf(sec.getString("bind_item").trim().toUpperCase());
        requireHotBar = sec.getBoolean("require_hotbar");
        bindStrategy = BindStrategy.valueOf(sec.getString("bind_strategy").trim().toUpperCase());
        sec.getStringList("enchants").stream().map(this::mapAsEnchant).forEach(enchants::add);
        nameTemplate = sec.getString("name");
        loreTemplate = sec.getString("lore");
        /*
          enchants: [UNBREAKING]
  name: "Soul of <bound_name>"
  lore: "Bound: <bound_name>\nUUID: <bound_uuid>\nBinder: <binder_name>\nUUID: <binder_uuid>\nDate: <date>"
         */
    }

    private Enchantment mapAsEnchant(String s) {
        return Objects.requireNonNull(Enchantment.getByName(s));
    }

    public Material getBindType() {
        return bindType;
    }

    public boolean isRequireHotBar() {
        return requireHotBar;
    }

    public BindStrategy getBindStrategy() {
        return bindStrategy;
    }

    public List<Enchantment> getEnchants() {
        return enchants;
    }

    public String getNameTemplate() {
        return nameTemplate;
    }

    public String getLoreTemplate() {
        return loreTemplate;
    }
}
