package me.mmigas.files;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigManagerDefaultTiers {

    static FileConfiguration normalCrateTier() {
        FileConfiguration configuration = new YamlConfiguration();
        configuration.addDefault(ConfigManager.NAME, "Normal");
        configuration.addDefault(ConfigManager.HD, "-------------\nNormal Crate\n-------------");
        configuration.addDefault(ConfigManager.CHANCE, 90);
        configuration.addDefault(ConfigManager.SPEED, 1);
        configuration.addDefault(ConfigManager.PRICE, 1000);
        configuration.addDefault(ConfigManager.REWARDS + ".Diamond_Sword.Name", "SWORD");
        configuration.addDefault(ConfigManager.REWARDS + ".Diamond_Sword.Lore", "THIS IS MY LORE");
        configuration.addDefault(ConfigManager.REWARDS + ".Diamond_Sword.Chance", 10);
        configuration.addDefault(ConfigManager.REWARDS + ".Diamond_Sword.Enchancts.Sharpness", 1);
        configuration.addDefault(ConfigManager.REWARDS + ".Diamond_Sword.Enchancts.Unbreaking", 2);
        configuration.addDefault(ConfigManager.REWARDS + ".Potion.Name", "Regen");
        configuration.addDefault(ConfigManager.REWARDS + ".Potion.Lore", "Regen Me");
        configuration.addDefault(ConfigManager.REWARDS + ".Potion.Chance", 30);
        configuration.addDefault(ConfigManager.REWARDS + ".Potion.Effects.Regen.Duration", 60);
        configuration.addDefault(ConfigManager.REWARDS + ".Potion.Effects.Regen.Multiplier", 4);
        configuration.addDefault(ConfigManager.REWARDS + ".Potion.Effects.Speed.Duration", 60);
        configuration.addDefault(ConfigManager.REWARDS + ".Potion.Effects.Speed.Multiplier", 5);
        configuration.addDefault(ConfigManager.REWARDS + ".Splash_Potion.Name", "DIE DIE");
        configuration.addDefault(ConfigManager.REWARDS + ".Splash_Potion.LORE", "You're dead to me");
        configuration.addDefault(ConfigManager.REWARDS + ".Splash_Potion.Chance", 50);
        configuration.addDefault(ConfigManager.REWARDS + ".Splash_Potion.Effects.Harm.Duration", 10);
        configuration.addDefault(ConfigManager.REWARDS + ".Splash_Potion.Effects.Harm.Multiplier", 10);
        configuration.addDefault(ConfigManager.REWARDS + ".Splash_Potion.Effects.Poison.Duration", 10);
        configuration.addDefault(ConfigManager.REWARDS + ".Splash_Potion.Effects.Poison.Multiplier", 10);
        configuration.options().copyDefaults(true);
        return configuration;
    }

    static FileConfiguration epicCrateTier() {
        FileConfiguration configuration = new YamlConfiguration();
        configuration.addDefault(ConfigManager.NAME, "EPIC");
        configuration.addDefault(ConfigManager.HD, "-------------\nEPIC Crate\n-------------");
        configuration.addDefault(ConfigManager.CHANCE, 10);
        configuration.addDefault(ConfigManager.SPEED, 0.1);
        configuration.addDefault(ConfigManager.PRICE, 1000000);
        configuration.addDefault(ConfigManager.REWARDS + ".Diamond_Chestplate.Name", "ARMOR");
        configuration.addDefault(ConfigManager.REWARDS + ".Diamond_Chestplate.Lore", "THIS IS MY LORE");
        configuration.addDefault(ConfigManager.REWARDS + ".Diamond_Chestplate.Chance", 50);
        configuration.addDefault(ConfigManager.REWARDS + ".Diamond_Chestplate.Enchancts.Protection", 1);
        configuration.addDefault(ConfigManager.REWARDS + ".Diamond_Chestplate.Enchancts.Unbreaking", 2);
        configuration.options().copyDefaults(true);
        return configuration;
    }
}
