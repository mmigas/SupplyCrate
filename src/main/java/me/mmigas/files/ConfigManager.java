package me.mmigas.files;

import me.mmigas.EventSystem;
import me.mmigas.items.Enchantments;
import me.mmigas.utils.Pair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;


public class ConfigManager {

    private final EventSystem plugin;

    public static final String CRATE_REWARDS = "rewards";
    public static final String CRATE_SPEED = "speed";
    public static final String CRATE_RADIUS = "radius";
    public static final String CRATE_COOLDOWN = "cooldown";
    public static final String CRATE_AUTOSTART = "autostart";
    public static final String CRATE_WORLDS = "worlds";

    private static final String NAME = "Name";
    private static final String PERCENTAGE = "Percentage";
    private static final String LORE = "Lore";
    private static final String ENCHANTMENTS = "Enchantments";


    public ConfigManager(EventSystem plugin) {
        this.plugin = plugin;
        createDefaultConfig();
        getConfig().options().copyDefaults(true);
        plugin.saveConfig();
    }

    private void createDefaultConfig() {
        FileConfiguration config = getConfig();
        config.addDefault(CRATE_SPEED, 0.05);
        config.addDefault(CRATE_RADIUS, 6000);
        config.addDefault(CRATE_COOLDOWN, 30);
        config.addDefault(CRATE_AUTOSTART, true);
        config.addDefault(CRATE_WORLDS, Collections.singletonList("world"));

        config.addDefault(CRATE_REWARDS + ".Diamond_Sword." + NAME, "TNT");
        config.addDefault(CRATE_REWARDS + ".Diamond_Sword." + PERCENTAGE, 10.5);
        config.addDefault(CRATE_REWARDS + ".Diamond_Sword." + LORE, "THIS IS A FUCKING TNT");
        config.addDefault(CRATE_REWARDS + ".Diamond_Sword." + ENCHANTMENTS + ".Sharpness", 1);
        config.addDefault(CRATE_REWARDS + ".Diamond_Sword." + ENCHANTMENTS + ".Unbreaking", 2);

        config.addDefault(CRATE_REWARDS + ".Stone." + NAME, "Stone");
        config.addDefault(CRATE_REWARDS + ".Stone." + PERCENTAGE, 11.5);
        config.addDefault(CRATE_REWARDS + ".Stone." + LORE, "THIS IS A FUCKING TNT");

    }

    public List<Pair<ItemStack, Double>> readRewardsFromConfigs() {
        List<Pair<ItemStack, Double>> rewards = new ArrayList<>();
        for (String itemName : plugin.getConfig().getConfigurationSection(CRATE_REWARDS).getKeys(false)) {
            Material material = Material.matchMaterial(itemName);
            if (material != null) {

                ItemStack itemStack = new ItemStack(material);
                Pair<ItemStack, Double> reward = applyAtributes(itemStack, itemName, rewards.isEmpty() ? 0 : rewards.get(rewards.size() - 1).second);
                if (reward != null) {
                    rewards.add(reward);
                }
            } else {
                Bukkit.getLogger().log(Level.WARNING, "The name " + itemName + " isn't a valid material.");
            }
        }

        return rewards;
    }

    private Pair<ItemStack, Double> applyAtributes(ItemStack item, String itemName, double previousPercentage) {
        String sectionName = CRATE_REWARDS + "." + itemName;
        ConfigurationSection section = plugin.getConfig().getConfigurationSection(sectionName);
        double percentage;

        percentage = readPercentage(section, itemName, previousPercentage);
        if (percentage == -1) {
            return null;
        }

        applyName(section, item);
        applyLore(section, item);
        applyEnchantments(section, item);

        return new Pair<>(item, percentage);
    }

    private void applyName(ConfigurationSection section, ItemStack item) {
        if (section.contains(NAME)) {
            String name = section.getString(NAME);
            item.getItemMeta().setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        }
    }

    private void applyLore(ConfigurationSection section, ItemStack item) {
        if (section.contains(LORE)) {
            List<String> lore = Arrays.asList(section.getString(LORE).split("\n"));
            item.getItemMeta().setLore(lore);
        }
    }

    private double readPercentage(ConfigurationSection section, String itemName, double prevPercentage) {
        double percentage;
        if (section.contains(PERCENTAGE)) {
            percentage = section.getDouble(PERCENTAGE);

            if (percentage >= 0) {
                percentage = percentage + prevPercentage;
                return percentage;
            } else {
                Bukkit.getLogger().log(Level.WARNING, String.format("The %s has an invalid percentage. Current percentage %d", itemName, percentage));
                return -1;
            }

        } else {
            Bukkit.getLogger().log(Level.WARNING, String.format("The %s has an no percentage.", itemName));
            return -1;
        }
    }

    private void applyEnchantments(ConfigurationSection section, ItemStack item) {
        if (section.contains(ENCHANTMENTS)) {
            for (String enchant : section.getConfigurationSection(ENCHANTMENTS).getKeys(false)) {
                item.addEnchantment(Enchantments.byName(enchant), section.getConfigurationSection(ENCHANTMENTS).getInt(enchant));
            }
        }

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


