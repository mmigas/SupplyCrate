package me.mmigas.files;

import me.mmigas.EventSystem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class LanguageManager {

    private static final String CRATE_CATEGORY = "crate.";
    public static final String CRATE_START = CRATE_CATEGORY + "start";
    public static final String CRATE_STOP = CRATE_CATEGORY + "stop";
    public static final String CRATE_CYCLE_RUNNING = CRATE_CATEGORY + "crate-cycle-running";
    public static final String CRATE_CYCLE_NOT_RUNNING = CRATE_CATEGORY + "no-crate-cycle";
    public static final String CRATE_BROADCAST = CRATE_CATEGORY + "broadcast";
    public static final String CRATE_COLLECTED = CRATE_CATEGORY + "collected";
    public static final String CRATE_CANNOT_BREAK = CRATE_CATEGORY + "cannot-break";
    public static final String NO_PERMISSION = "&cNot enough permission!";
    public static final String MUST_BE_PLAYER = "&cYou must be a player!";

    public static final String WORLD_GUARD_REGION = CRATE_CATEGORY + "world_guard_region";
    public static final String GRIEF_PREVENTION_REGION = CRATE_CATEGORY + "grief_prevention_region";
    public static final String INVALID_CRATE_TIER = CRATE_CATEGORY + "invalid_crate_tier";

    private static final String LOCATION_PLACEHOLDER = "%CrateLocation%";
    private static final String PLAYER_PLACEHOLDER = "%Player%";
    private static final String DELAY_PLACEHOLDER = "%Delay%";
    private static final String CRATE_TIER_PLACEHOLDER = "%CrateTier%";

    private static final String FILE = "language.yml";

    private final EventSystem plugin;

    private final Map<String, String> strings;

    private static LanguageManager instance;

    public LanguageManager(EventSystem plugin) {
        this.plugin = plugin;
        FileConfiguration fileConfiguration = createFileConfigurator();
        strings = new HashMap<>();
        instance = this;
        save(fileConfiguration);
        load(fileConfiguration);
    }

    private void save(FileConfiguration fileConfiguration) {
        File languageFile = new File(plugin.getDataFolder(), FILE);

        if (languageFile.exists()) {
            return;
        }

        languageFile.getParentFile().mkdirs();
        fileConfiguration.options().copyDefaults(true);
        try {
            fileConfiguration.save(languageFile);
        } catch (IOException e) {
            throw new IllegalStateException("Could not save the language file!", e);
        }
    }

    private void load(FileConfiguration configuration) {
        File languageFile = new File(plugin.getDataFolder(), FILE);

        try {
            configuration.load(languageFile);

            Set<String> keys = configuration.getKeys(false);
            for (String key : keys) {
                ConfigurationSection section = configuration.getConfigurationSection(key);
                for (String string : Objects.requireNonNull(section).getKeys(false)) {
                    String value = Objects.requireNonNull(section.getString(string));
                    strings.put(key + "." + string, value);
                }
            }
        } catch (IOException | InvalidConfigurationException e) {
            throw new IllegalStateException("Could not load the language file!", e);
        }
    }

    private FileConfiguration createFileConfigurator() {
        FileConfiguration fileConfiguration = new YamlConfiguration();
        fileConfiguration.addDefault(CRATE_START, "&bCrate events cycle started.");
        fileConfiguration.addDefault(CRATE_STOP, "&bCrate events cycle stopped.");
        fileConfiguration.addDefault(CRATE_CYCLE_NOT_RUNNING, "&bThe crate event is not running.");
        fileConfiguration.addDefault(CRATE_CYCLE_RUNNING, "&bThe crate event is already running.");
        fileConfiguration.addDefault(CRATE_BROADCAST, "&b" + CRATE_TIER_PLACEHOLDER + " Crate spawned at " + LOCATION_PLACEHOLDER);
        fileConfiguration.addDefault(CRATE_COLLECTED, "&bCrate collected by &a" + PLAYER_PLACEHOLDER);
        fileConfiguration.addDefault(CRATE_CANNOT_BREAK, "&bYou cannot break this crate.");

        fileConfiguration.addDefault(WORLD_GUARD_REGION, "&dYou cannot spawn crates in world guard's regions.");
        fileConfiguration.addDefault(GRIEF_PREVENTION_REGION, "&dYou cannot spawn crates in Grief Prevention's regions.");
        fileConfiguration.addDefault(INVALID_CRATE_TIER, "&dInvalid crate tier." + CRATE_TIER_PLACEHOLDER);

        fileConfiguration.options().copyDefaults(false);
        return fileConfiguration;
    }

    public static void broadcast(String key, Object... objects) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
            LanguageManager.sendKey(player, key, objects);
        }
    }

    public static void sendKey(CommandSender sender, String key, Object... objects) {
        String message = instance.strings.get(key);
        message = instance.updatePlaceholders(message, objects);
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public static void sendMessage(CommandSender sender, String message, Object... objects) {
        message = instance.updatePlaceholders(message, objects);
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    private String updatePlaceholders(String message, Object... objects) {
        if (message.contains(LOCATION_PLACEHOLDER)) {
            Location location = findLocationInObjects(objects);
            message = message.replace(LOCATION_PLACEHOLDER, "&bx: &c" + location.getBlockX() + " &bz: &c" + location.getBlockZ());
        }

        if (message.contains(PLAYER_PLACEHOLDER)) {
            Player player = findPlayerInObjects(objects);
            message = message.replace(PLAYER_PLACEHOLDER, player.getDisplayName());
        }

        if (message.contains(DELAY_PLACEHOLDER)) {
            long storedTime = findStoredTimeInObjects(objects);
            message = message.replace(DELAY_PLACEHOLDER, String.valueOf(((plugin.getConfig().getInt("Delay") * 1000) - (System.currentTimeMillis() - storedTime)) / 1000));
        }

        if (message.contains(CRATE_TIER_PLACEHOLDER)) {
            String tier = findCrateTierInObjects(objects);
            message = message.replace(INVALID_CRATE_TIER, tier);
        }

        return message;
    }

    private Location findLocationInObjects(Object... objects) {
        for (Object object : objects) {
            if (object instanceof Location) {
                return (Location) object;
            }
        }
        throw new IllegalStateException();
    }

    private Player findPlayerInObjects(Object... objects) {
        for (Object object : objects) {
            if (object instanceof Player) {
                return (Player) object;
            }
        }
        throw new IllegalStateException();

    }

    private Long findStoredTimeInObjects(Object... objects) {
        for (Object object : objects) {
            if (object instanceof Long) {
                return (long) object;
            }
        }
        throw new IllegalStateException();
    }

    private String findCrateTierInObjects(Object... objects) {
        for (Object object : objects) {
            if (object instanceof String) {
                return (String) object;
            }
        }
        throw new IllegalStateException();
    }
}
