package me.mmigas.files;

import me.mmigas.EventSystem;
import me.mmigas.items.Enchantments;
import me.mmigas.items.Items;
import me.mmigas.utils.Pair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static me.mmigas.WishingWells.WELL_ID_LORE;

public class ConfigManager {

    private final EventSystem plugin;

    private static final String CRATE_CATEGORY = "care-package.";
    public static final String CRATE_REWARDS = CRATE_CATEGORY + "rewards";
    public static final String CRATE_SPEED = CRATE_CATEGORY + "speed";
    public static final String CRATE_RADIUS = CRATE_CATEGORY + "radius";
    public static final String CRATE_COOLDOWN = CRATE_CATEGORY + "cooldown";
    public static final String CRATE_AUTOSTART = CRATE_CATEGORY + "autostart";
    public static final String CRATE_WORLDS = CRATE_CATEGORY + "worlds";

    private static final String WISHING_WELLS_CATEGORY = "whising-wells.";
    public static final String WISHING_WELLS_ACCEPTED = WISHING_WELLS_CATEGORY + "accepted";
    public static final String WISHING_WELLS_REWARDS = WISHING_WELLS_CATEGORY + "rewards";
    public static final String WISHING_WELLS_WELL_LOCATION = WISHING_WELLS_CATEGORY + "well-locations";

    private static final String FILE_NAME = "config.yml";

    public ConfigManager(EventSystem plugin) {
        this.plugin = plugin;
        this.createInitial();
    }

    private void createInitial() {
        File file = new File(plugin.getDataFolder(), FILE_NAME);
        if (file.exists()) {
            return;
        }

        try {
            createDefaultConfig();
            getConfig().save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("Could not save the config file.");
        }
    }

    private void createDefaultConfig() {
        FileConfiguration config = getConfig();
        config.addDefault(CRATE_SPEED, 0.05);
        config.addDefault(CRATE_RADIUS, 6000);
        config.addDefault(CRATE_COOLDOWN, 30);
        config.addDefault(CRATE_AUTOSTART, true);
        config.addDefault(CRATE_WORLDS, Collections.singletonList("world"));

        List<String> crateRewardList = new ArrayList<>();
        crateRewardList.add("TNT 1 percentage:10");
        crateRewardList.add("STONE 1 percentage:10");
        crateRewardList.add("DIRT 1 percentage:10");
        config.set(CRATE_REWARDS, crateRewardList);

        List<String> acceptedList = new ArrayList<>();
        acceptedList.add("DIAMOND");
        config.set(WISHING_WELLS_ACCEPTED, acceptedList);

        List<String> wWRewardsList = new ArrayList<>();
        wWRewardsList.add("COOKED_BEEF 20 name:&9Deves_querer_beef. percentage:80.0");
        wWRewardsList.add("ENCHANTED_GOLDEN_APPLE 1 percentage:10.0");
        wWRewardsList.add("EXPERIENCE_BOTTLE 192 name:&aXP_&9Kubano lore:|&9Um_presente_dos_deuses_Kubanos. percentage:10.0");
        config.set(WISHING_WELLS_REWARDS, wWRewardsList);
        config.options().copyDefaults(true);
    }

    public List<Pair<ItemStack, Double>> getRewardsList(String section, boolean isFromWell) {
        List<Pair<ItemStack, Double>> rewardList = new ArrayList<>();

        double previousPercentage = 0;
        for (String string : Objects.requireNonNull(plugin.getConfig().getStringList(section))) {
            Pair<ItemStack, Double> pair = readPair(string, previousPercentage, isFromWell);
            rewardList.add(pair);
            previousPercentage = pair.second;
        }

        return rewardList;
    }

    private Pair<ItemStack, Double> readPair(String message, double previousPercentage, boolean fromWishingWells) {
        String[] parts = message.split(" ");

        double probability = 0;
        Material material = Material.matchMaterial(parts[0]);
        if (material == null) {
            throw new IllegalStateException(String.format("Material %s not found!", parts[0]));
        }

        ItemStack item = new ItemStack(material);
        if (parts.length > 1) {
            item.setAmount(Integer.parseInt(parts[1]));
        }

        for (int i = 2; i < parts.length; i++) {
            String[] attrParts = parts[i].split(":");

            if (attrParts[0].equalsIgnoreCase("name")) {
                ItemMeta im = item.getItemMeta();
                String name = ChatColor.translateAlternateColorCodes('&', attrParts[1].replace("_", " "));
                im.setDisplayName(name);
                item.setItemMeta(im);
                continue;
            }

            if (attrParts[0].equalsIgnoreCase("lore")) {
                String newLore = ChatColor.translateAlternateColorCodes('&', attrParts[1]).replace("_", " ");
                Items.addLore(item, newLore.split("\\|"));
                continue;
            }

            if (attrParts[0].equalsIgnoreCase("percentage")) {
                probability = Double.parseDouble(attrParts[1]) + previousPercentage;
                continue;
            }

            Enchantment enchantment = Enchantments.byName(attrParts[0]);
            if (enchantment != null) {
                try {
                    item.addUnsafeEnchantment(enchantment, Integer.parseInt(attrParts[1]));
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning(String.format("%s cannot be applied to %s", attrParts[0], parts[0]));
                }
            } else {
                plugin.getLogger().warning(String.format("The enchantment %s does not exist.", attrParts[0]));
            }
        }

        if (fromWishingWells) {
            Items.addLore(item, WELL_ID_LORE);
        }

        return new Pair<>(item, probability);
    }

    public List<Material> readMaterialList(String section) {
        List<Material> list = new ArrayList<>();

        for (String string : getConfig().getStringList(section)) {
            list.add(Material.matchMaterial(string));
        }

        return list;
    }

    public List<World> enabledCrateWorlds() {
        List<String> worldNames = getConfig().getStringList(CRATE_WORLDS);
        List<World> worlds = new ArrayList<>();

        for (String name : worldNames) {
            World world = Bukkit.getWorld(name);
            if (world != null) {
                worlds.add(world);
            }
        }

        return worlds;
    }

    public FileConfiguration getConfig() {
        return plugin.getConfig();
    }
}


