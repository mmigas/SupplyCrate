package me.mmigas.files;

import me.mmigas.crates.CrateController;
import me.mmigas.SupplyCrate;
import me.mmigas.crates.CrateTier;
import me.mmigas.items.Enchantments;
import me.mmigas.items.Potion;
import me.mmigas.utils.Pair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;


public class ConfigManager {

    private final SupplyCrate plugin;

    public static final String CRATE_RADIUS = "Radius";
    public static final String COOLDOWN = "Cooldown";
    public static final String AUTOSTART = "Autostart";
    public static final String WORLDS = "Worlds";

    public static final String CRATE = "Crates";
    private static final String NAME = "Name";
    private static final String PERCENTAGE = "Percentage";
    public static final String REWARDS = "Rewards";
    public static final String SPEED = "Speed";

    private static final String LORE = "Lore";
    private static final String EFFECTS = "Effects";
    private static final String POTION_DURATION = "Duration";
    private static final String POTION_MULTIPLIER = "Multiplier";
    private static final String ENCHANTMENTS = "Enchantments";


    public ConfigManager(SupplyCrate plugin) {
        this.plugin = plugin;
        plugin.getConfig().options().copyDefaults(true);
        plugin.saveDefaultConfig();
    }

    public List<CrateTier> readTiersFromConfigs(CrateController crateController) {
        List<CrateTier> crateTiers = new ArrayList<>();
        int prevPercentage = 0;
        for (String tier : plugin.getConfig().getConfigurationSection(CRATE).getKeys(false)) {
            ConfigurationSection section = plugin.getConfig().getConfigurationSection(CRATE + "." + tier);
            if (section != null) {

                String name = section.getString(NAME);
                Bukkit.getLogger().info(tier);
                int percentage = section.getInt(PERCENTAGE) + prevPercentage;
                prevPercentage = percentage;
                float speed = (float) section.getDouble(SPEED);
                section = section.getConfigurationSection(REWARDS);

                List<Pair<ItemStack, Double>> rewards = null;
                if (section != null) {
                    rewards = readRewardsFromConfigs(section);
                }
                crateTiers.add(new CrateTier(crateController, tier, name, percentage, speed, rewards));
            }
        }
        return crateTiers;
    }

    private List<Pair<ItemStack, Double>> readRewardsFromConfigs(ConfigurationSection section) {
        List<Pair<ItemStack, Double>> rewards = new ArrayList<>();
        for (String itemName : section.getKeys(false)) {
            Material material = Material.matchMaterial(itemName);
            if (material != null) {

                ItemStack itemStack = new ItemStack(material);
                Pair<ItemStack, Double> reward = applyAttributes(section, itemStack, itemName, rewards.isEmpty() ? 0 : rewards.get(rewards.size() - 1).second);
                if (reward != null) {
                    rewards.add(reward);
                }
            } else {
                Bukkit.getLogger().log(Level.WARNING, "The name " + itemName + " isn't a valid material.");
            }
        }

        return rewards;
    }

    private Pair<ItemStack, Double> applyAttributes(ConfigurationSection section, ItemStack item, String itemName, double previousPercentage) {
        section = section.getConfigurationSection(itemName);
        double percentage;

        percentage = readPercentage(section, itemName, previousPercentage);
        if (percentage == -1) {
            return null;
        }

        applyName(section, item);
        applyLore(section, item);
        if (item.getType() == Material.POTION || item.getType() == Material.SPLASH_POTION) {
            applyEffects(section, item);
        } else {
            applyEnchantments(section, item);
        }

        return new Pair<>(item, percentage);
    }

    private void applyName(ConfigurationSection section, ItemStack item) {
        if (section.contains(NAME)) {
            String name = section.getString(NAME);
            ItemMeta meta = item.getItemMeta();
            if (meta != null && name != null)
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            item.setItemMeta(meta);
        }
    }

    private void applyLore(ConfigurationSection section, ItemStack item) {
        if (section.contains(LORE)) {
            ItemMeta meta = item.getItemMeta();
            List<String> lore = Arrays.asList(section.getString(LORE).split("\n"));
            if (meta != null)
                meta.setLore(lore);
            item.setItemMeta(meta);
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

    private void applyEffects(ConfigurationSection section, ItemStack item) {
        if (section.isConfigurationSection(EFFECTS)) {
            section = section.getConfigurationSection(EFFECTS);
            PotionMeta potionMeta = (PotionMeta) item.getItemMeta();

            for (String effect : section.getKeys(false)) {
                ConfigurationSection effectSection = section.getConfigurationSection(effect);
                PotionEffectType potionEffectType = Potion.byName(effect);

                if (potionEffectType == null) {
                    Bukkit.getLogger().log(Level.WARNING, String.format("Effect %s doesn't exists.", effect));
                    continue;
                }

                int duration = 60;
                int amplifier = 1;
                if (effectSection.contains(POTION_DURATION)) {
                    duration = effectSection.getInt(POTION_DURATION);
                } else {
                    Bukkit.getLogger().log(Level.WARNING, String.format("No duration found in %s effect in %s potion. Applying default.", effect, item.getItemMeta().getDisplayName()));
                }

                if (effectSection.contains(POTION_MULTIPLIER)) {
                    amplifier = effectSection.getInt(POTION_MULTIPLIER);
                } else {
                    Bukkit.getLogger().log(Level.WARNING, String.format("No multiplier found on %s effect in %s potion. Applying default.", effect, item.getItemMeta().getDisplayName()));
                }
                potionMeta.addCustomEffect(new PotionEffect(potionEffectType, duration * 20, amplifier), true);
            }
            item.setItemMeta(potionMeta);
        } else {
            Bukkit.getLogger().log(Level.WARNING, String.format("Effects not found for %s", item.getItemMeta().getDisplayName()));
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
        List<String> worldNames = getConfig().getStringList(WORLDS);
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


