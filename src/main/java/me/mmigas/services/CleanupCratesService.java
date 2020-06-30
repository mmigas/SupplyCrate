package me.mmigas.services;

import me.mmigas.events.CrateEvent;
import me.mmigas.persistence.CratesRepository;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;

import java.time.LocalDateTime;
import java.util.Set;

import static java.time.temporal.ChronoUnit.DAYS;

public class CleanupCratesService {

    public void cleanupCrates(CommandSender sender, int maxDays) {
        notify(sender, "Cleaning up crates older than " + maxDays + " days.");

        CratesRepository cratesRepository = CratesRepository.getInstance();
        ConfigurationSection configuration = cratesRepository.getFileConfiguration();
        configuration = configuration.getConfigurationSection("crate");

        LocalDateTime now = LocalDateTime.now();
        Set<String> crateKeys = configuration.getKeys(false);
        for (String key : crateKeys) {
            ConfigurationSection section = configuration.getConfigurationSection(key);

            String stringDate = section.getString("landing-time");
            LocalDateTime time = LocalDateTime.parse(stringDate, CrateEvent.TIME_FORMATTER);

            if (DAYS.between(time, now) < maxDays) {
                continue;
            }

            notify(sender, "Removing old crate from " + stringDate);
            configuration.set(key, null);

            destroyBlock(section, sender, stringDate);
        }

        cratesRepository.save();
    }

    private static void destroyBlock(ConfigurationSection configuration, CommandSender sender, String stringDate) {
        World world = Bukkit.getWorld(configuration.getString("world"));
        if (world == null) {
            return;
        }

        double x = configuration.getDouble("location.x");
        double y = configuration.getDouble("location.y");
        double z = configuration.getDouble("location.z");
        Location crateLocation = new Location(world, x, y, z);

        Block block = world.getBlockAt(crateLocation);
        if (!block.getMetadata(CrateEvent.CRATE_METADATA).isEmpty()) {
            notify(sender, "Destroying old crate from " + stringDate);
            block.setType(Material.AIR);
        }
    }

    private static void notify(CommandSender sender, String message) {
        Bukkit.getLogger().info(message);

        if (!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage(message);
        }
    }
}
