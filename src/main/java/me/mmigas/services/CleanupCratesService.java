package me.mmigas.services;

import me.mmigas.events.CrateEvent;
import me.mmigas.events.Status;
import me.mmigas.files.LanguageManager;
import me.mmigas.persistence.CratesRepository;
import me.mmigas.utils.InventoryUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import java.time.LocalDateTime;
import java.util.Set;

import static java.time.temporal.ChronoUnit.DAYS;

public class CleanupCratesService {

    public void cleanupCrates(CommandSender sender, int maxDays) {
        LanguageManager.sendMessage(sender, "Cleaning up crates older than " + maxDays + " days.");

        CratesRepository cratesRepository = CratesRepository.getInstance();
        ConfigurationSection configuration = cratesRepository.getFileConfiguration();
        configuration = configuration.getConfigurationSection(CratesRepository.CRATE);

        LocalDateTime now = LocalDateTime.now();
        Set<String> crateKeys = configuration.getKeys(false);
        int counter = 0;
        for (String key : crateKeys) {
            ConfigurationSection section = configuration.getConfigurationSection(key);

            String stringDate = section.getString(CratesRepository.LANDING_TIME);
            LocalDateTime time = LocalDateTime.parse(stringDate, CrateEvent.TIME_FORMATTER);

            if (DAYS.between(time, now) < maxDays || CratesRepository.getStatus(section) == Status.FALLING) {
                continue;
            }

            if (destroyBlock(section, sender, stringDate)) {
                cratesRepository.removeCrate(key);

                counter++;
            }
        }
        LanguageManager.sendMessage(sender, counter + " removed succefully ");
    }

    private static boolean destroyBlock(ConfigurationSection configuration, CommandSender sender, String stringDate) {
        World world = Bukkit.getWorld(configuration.getString(CratesRepository.WORLD));
        if (world == null) {
            return false;
        }

        double x = configuration.getDouble(CratesRepository.LOCATION_X);
        double y = configuration.getDouble(CratesRepository.LOCATION_Y);
        double z = configuration.getDouble(CratesRepository.LOCATION_Z);
        Location crateLocation = new Location(world, x, y, z);

        Block block = world.getBlockAt(crateLocation);
        if (block.getType() != Material.CHEST) {
            LanguageManager.sendMessage(sender, "&dCrate's chest not found at " + crateLocation.getBlockX() + " " + crateLocation.getBlockY() + " " + crateLocation.getBlockZ());
            return false;
        }
        ((Chest) block.getState()).getBlockInventory().clear();
        LanguageManager.sendMessage(sender, "Destroying old crate from " + stringDate);
        block.setType(Material.AIR);
        return true;
    }
}
