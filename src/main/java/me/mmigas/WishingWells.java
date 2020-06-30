
package me.mmigas;

import me.mmigas.files.ConfigManager;
import me.mmigas.persistence.LPRepository;
import me.mmigas.utils.Pair;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ConstantConditions unused")
public class WishingWells {
    public static final String WELL_ID_LORE = ChatColor.GREEN + "Poço da Sorte";
    public static final String ITEM_ID_LORE = ChatColor.GREEN + "Clandestino";

    private List<Pair<ItemStack, Double>> returns;
    private List<Location> wells = new ArrayList<>();

    WishingWells(EventSystem plugin, ConfigManager manager) {
        LPRepository.init(plugin);

        if (manager.getConfig().contains(ConfigManager.WISHING_WELLS_WELL_LOCATION)) {
            for (String keys : manager.getConfig().getConfigurationSection(ConfigManager.WISHING_WELLS_WELL_LOCATION)
                    .getKeys(false)) {
                wells.add((Location) manager.getConfig().get(ConfigManager.WISHING_WELLS_WELL_LOCATION + "." + keys));
            }
        }

        returns = manager.getRewardsList(ConfigManager.WISHING_WELLS_REWARDS, true);
    }

    public List<Pair<ItemStack, Double>> returns() {
        return returns;
    }

    public List<Location> wells() {
        return wells;
    }
}

