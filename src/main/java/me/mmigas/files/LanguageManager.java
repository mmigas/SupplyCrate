package me.mmigas.files;

import me.mmigas.SupplyCrate;
import me.mmigas.crates.CrateEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

    public static final String TIMER_COMMAND_SUCCESSFULLY = CRATE_CATEGORY + "timer_command_successfully";

    public static final String NOT_ENOUGH_MONEY = "not_enough_money";

    public static final String WORLD_GUARD_REGION = CRATE_CATEGORY + "world_guard_region";
    public static final String GRIEF_PREVENTION_REGION = CRATE_CATEGORY + "grief_prevention_region";
    public static final String INVALID_CRATE_TIER = CRATE_CATEGORY + "invalid_crate_tier";

    public static final String WRONG_COMMAND_USAGE = CRATE_CATEGORY + "wrong_command_usage";

    private static final String LOCATION_PLACEHOLDER = "%CrateLocation%";
    private static final String CRATE_TIER_PLACEHOLDER = "%CrateTier%";
    private static final String CRATE_PRICE_PLACEHOLDER = "%CratePrice%";
    private static final String PLAYER_PLACEHOLDER = "%Player%";
    private static final String DELAY_PLACEHOLDER = "%Delay%";

    private static final String FILE = "language.yml";

    private final SupplyCrate plugin;

    private final Map<String, String> strings;

    private static LanguageManager instance;

    public LanguageManager(SupplyCrate plugin) {
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

        fileConfiguration.addDefault(NOT_ENOUGH_MONEY, "&cYou dont have enough money to buy " + CRATE_TIER_PLACEHOLDER + " crate. You need " + CRATE_PRICE_PLACEHOLDER + "$");

        fileConfiguration.addDefault(TIMER_COMMAND_SUCCESSFULLY, "%bCooldown changed to " + DELAY_PLACEHOLDER + " minutes.");

        fileConfiguration.addDefault(WORLD_GUARD_REGION, "&dYou cannot spawn crates in world guard's regions.");
        fileConfiguration.addDefault(GRIEF_PREVENTION_REGION, "&dYou cannot spawn crates in Grief Prevention's regions.");
        fileConfiguration.addDefault(INVALID_CRATE_TIER, "&dInvalid crate tier.");

        fileConfiguration.addDefault(WRONG_COMMAND_USAGE, "&cWrong command usage. Use /crate help to see all the commands usages.");

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
        CrateEvent crate = findCrateInObjects(objects);
        if (crate != null) {
            if (message.contains(LOCATION_PLACEHOLDER)) {
                message = message.replace(LOCATION_PLACEHOLDER, "&bx: &c" + crate.getCurrentLocation().getBlockX() + " &bz: &c"
                        + crate.getCurrentLocation().getBlockZ());
            }

            if (message.contains(CRATE_TIER_PLACEHOLDER)) {
                message = message.replace(LOCATION_PLACEHOLDER, crate.getCrateTier().getName());
            }
            if (message.contains(CRATE_PRICE_PLACEHOLDER)) {
                message = message.replace(CRATE_PRICE_PLACEHOLDER, String.valueOf(crate.getCrateTier().getPrice()));
            }
        }

        if (message.contains(PLAYER_PLACEHOLDER)) {
            Player player = findPlayerInObjects(objects);
            message = message.replace(PLAYER_PLACEHOLDER, player.getDisplayName());
        }

        if (message.contains(DELAY_PLACEHOLDER)) {
            long storedTime = findStoredTimeInObjects(objects);
            message = message.replace(DELAY_PLACEHOLDER, String.valueOf(((plugin.getConfig().getInt("Delay") * 1000) - (System.currentTimeMillis() - storedTime)) / 1000));
        }

        return message;
    }

    private CrateEvent findCrateInObjects(Object... objects) {
        for (Object object : objects) {
            if (object instanceof CrateEvent) {
                return (CrateEvent) object;
            }
        }
        return null;
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

}
