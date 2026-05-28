/*
 *
 * Vault-Expansion
 * Copyright (C) 2018-2020 Ryan McCarthy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */
package com.extendedclip.papi.expansion.vault;

import me.clip.placeholderapi.expansion.Cacheable;
import me.clip.placeholderapi.expansion.Configurable;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class VaultExpansion extends PlaceholderExpansion implements Cacheable, Configurable {

    private final String VERSION = "1.12";
    private VaultPermsHook perms;
    private VaultEcoHook eco;

    public VaultExpansion() {
        perms = new VaultPermsHook();
        eco = new VaultEcoHook(this, perms);
    }

    @Override
    public void clear() {
        if (eco != null) {
            eco.clear();
        }
        eco = null;
        perms = null;
    }

    @Override
    public boolean canRegister() {
        return isFolia() && Bukkit.getPluginManager().getPlugin(getRequiredPlugin()) != null;
    }

    private boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    public Map<String, Object> getDefaults() {
        final Map<String, Object> defaults = new HashMap<>();
        defaults.put("baltop.enabled", false);
        defaults.put("baltop.cache_size", 100);
        defaults.put("baltop.check_delay", 30);
        defaults.put("formatting.thousands", "k");
        defaults.put("formatting.millions", "M");
        defaults.put("formatting.billions", "B");
        defaults.put("formatting.trillions", "T");
        defaults.put("formatting.quadrillions", "Q");
        return defaults;
    }

    @Override
    public boolean register() {
        if (!eco.setup()) {
            eco = null;
        }
        if (!perms.setup()) {
            perms = null;
        }
        if (perms != null || eco != null) {
            return super.register();
        }
        return false;
    }

    @Override
    public @NotNull String getAuthor() {
        return "clip";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "vault";
    }

    @Override
    public String getRequiredPlugin() {
        return "Vault";
    }

    @Override
    public @NotNull String getVersion() {
        return VERSION;
    }

    @Override
    public String onRequest(OfflinePlayer p, String i) {
        if (i.startsWith("eco_") && eco != null) {
            return eco.onPlaceholderRequest(p, i.replace("eco_", ""));
        }
        if (perms != null) {
            return perms.onPlaceholderRequest(p, i);
        }
        return null;
    }
}
