package me.mmigas.persistence;

import me.mmigas.EventSystem;
import me.mmigas.utils.StringUtil;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Stream;

public class LPRepository {

    private EventSystem plugin;

    private static LPRepository instance;

    private static final String TICKET_AMOUNT_FIELD = "ticket-amount";
    private static final String STORAGE_FOLDER_NAME = "storage" + File.separator + "LuckyPapers";

    private LPRepository(EventSystem plugin) {
        this.plugin = plugin;
    }

    public int addTickets(Player player, int amount) {
        FileConfiguration fileConfiguration = loadPlayerFile(player);
        if (fileConfiguration == null) {
            return -1; // Only in case of error
        }

        int newAmount = fileConfiguration.getInt(TICKET_AMOUNT_FIELD) + amount;
        fileConfiguration.set(TICKET_AMOUNT_FIELD, newAmount);
        savePlayerFile(player, fileConfiguration);
        return newAmount;
    }

    public int totalTickets() {
        File[] files = storageFolder().listFiles();
        if (files == null) {
            return 0;
        }

        int count = 0;
        for (File file : files) {
            FileConfiguration fileConfiguration = loadPlayerFile(file);
            if (fileConfiguration != null) {
                count += fileConfiguration.getInt(TICKET_AMOUNT_FIELD);
            }
        }

        return count;
    }

    public int playerTickets(String name) {
        File playerFile = playerFile(name);
        FileConfiguration fileConfiguration = loadPlayerFile(playerFile);
        if (fileConfiguration == null) {
            return 0;
        }

        return fileConfiguration.getInt(TICKET_AMOUNT_FIELD);
    }

    public Map<String, Integer> ticketsPerPlayer() {
        Map<String, Integer> chances = new HashMap<>();
        File[] files = storageFolder().listFiles();
        if (files == null) {
            return chances;
        }

        for (File file : files) {
            String playerName = StringUtil.withoutExtension(file.getName());
            int playerTickets = playerTickets(playerName);
            chances.put(playerName, playerTickets);
        }

        return chances;
    }

    public void clear() {
        try (Stream<Path> pathStream = Files.walk(Paths.get(storageFolder().toURI()))) {
            pathStream.sorted(Comparator.reverseOrder())
                    .forEach(p -> {
                        try {
                            Files.delete(p);
                        } catch (IOException e) {
                            plugin.getLogger().log(Level.WARNING,
                                    "Could not clear the storage files. Disabling the plugin.", e);
                            plugin.getServer().getPluginManager().disablePlugin(plugin);
                        }
                    });
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Could not clear the storage files. Disabling the plugin.", e);
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }
    }

    public static LPRepository getInstance() {
        return instance;
    }

    public static void init(EventSystem plugin) {
        instance = new LPRepository(plugin);
    }

    private FileConfiguration loadPlayerFile(Player player) {
        createDefaultPlayerStorage(player);
        File playerFile = playerFile(player);
        return loadPlayerFile(playerFile);
    }

    private FileConfiguration loadPlayerFile(File playerFile) {
        if (!playerFile.exists()) {
            return null;
        }

        FileConfiguration fileConfiguration = new YamlConfiguration();
        try {
            fileConfiguration.load(playerFile);
            return fileConfiguration;
        } catch (IOException | InvalidConfigurationException e) {
            plugin.getLogger().log(Level.WARNING, "Could not load the player storage file " + playerFile.getName(), e);
            return null;
        }
    }

    private void savePlayerFile(Player player, FileConfiguration fileConfiguration) {
        File playerFile = playerFile(player);
        try {
            fileConfiguration.save(playerFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Could not save the player storage file " + playerFile.getName(), e);
        }
    }

    private File playerFile(Player player) {
        return new File(storageFolder(), player.getName().toLowerCase() + ".yml");
    }

    private File playerFile(String name) {
        return new File(storageFolder(), name.toLowerCase() + ".yml");
    }

    private File storageFolder() {
        return new File(plugin.getDataFolder(), STORAGE_FOLDER_NAME);
    }

    private void createDefaultPlayerStorage(Player player) {
        File playerFile = playerFile(player);
        if (playerFile.exists()) {
            return;
        }

        FileConfiguration fileConfiguration = new YamlConfiguration();
        fileConfiguration.set(TICKET_AMOUNT_FIELD, 0);
        savePlayerFile(player, fileConfiguration);
    }
}
