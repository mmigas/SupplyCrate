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
    public static final String CRATE_CICLE_RUNNING = CRATE_CATEGORY + "crate-cicle-running";
    public static final String CRATE_CICLE_NOT_RUNNING = CRATE_CATEGORY + "no-crate-cicle";
    public static final String CRATE_BROADCAST = CRATE_CATEGORY + "broadcast";
    public static final String CRATE_COLLECTED = CRATE_CATEGORY + "collected";
    public static final String CRATE_CANNOT_BREAK = CRATE_CATEGORY + "cannot-break";
    public static final String NO_PERMISSION = "&cNot enought permission!";
    public static final String MUST_BE_PLAYER = "&cYou must be a player!";

    private static final String WISHING_WELLS_CATEGORY = "wishing-wells.";
    public static final String WISHING_WELLS_DELAY_MESSAGE = WISHING_WELLS_CATEGORY + "delay-message";
    public static final String WISHING_WELLS_ITEM_NOT_ACCEPTED = WISHING_WELLS_CATEGORY + "item-not-accepetd";
    public static final String WISHING_WELLS_TOO_BAD = WISHING_WELLS_CATEGORY + "too-bad";
    public static final String WISHING_WELLS_CONGRATS_MESSAGE = WISHING_WELLS_CATEGORY + "congrats-message";

    private static final String FILE = "language.yml";

    private EventSystem plugin;

    private Map<String, String> strings;

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

        if(languageFile.exists()) {
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
            for(String key : keys) {
                ConfigurationSection section = configuration.getConfigurationSection(key);
                for(String string : Objects.requireNonNull(section).getKeys(false)) {
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
        fileConfiguration.addDefault(CRATE_START, "&bCrate events cicle started.");
        fileConfiguration.addDefault(CRATE_STOP, "&bCrate events cicle stoped.");
        fileConfiguration.addDefault(CRATE_CICLE_NOT_RUNNING, "&bThe crate event is not running.");
        fileConfiguration.addDefault(CRATE_CICLE_RUNNING, "&bThe crate event is already running.");
        fileConfiguration.addDefault(CRATE_BROADCAST, "&bCrate spawned ate %careLocation%");
        fileConfiguration.addDefault(CRATE_COLLECTED, "&bCrate collected by &a%player%");
        fileConfiguration.addDefault(CRATE_CANNOT_BREAK, "&bYou cannot break this crate.");
        fileConfiguration.addDefault(WISHING_WELLS_DELAY_MESSAGE, "&cYou cant make another wish for %delay% seconds!");
        fileConfiguration.addDefault(WISHING_WELLS_ITEM_NOT_ACCEPTED, "&4That item isnt accepted in the well!");
        fileConfiguration.addDefault(WISHING_WELLS_TOO_BAD, "&cOh no! You lost your item in the well. Too bad!");
        fileConfiguration.addDefault(WISHING_WELLS_CONGRATS_MESSAGE, "&aYou got a new item back from the well!");
        fileConfiguration.options().copyDefaults(false);
        return fileConfiguration;
    }

    public static void broadcast(String message, Object... objects) {
        for(Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
            LanguageManager.send(player, message, objects);
        }
    }

    public static void send(CommandSender sender, String key, Object... objects) {
        String message = instance.strings.get(key);
        message = instance.updatePlaceholders(message, objects);
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    private String updatePlaceholders(String message, Object... objects) {
        if(message.contains("%careLocation%")) {
            Location location = findLocationInObjects(objects);
            message = message.replace("%careLocation%", "&bx: &c" + location.getBlockX() + " &bz: &c" + location.getBlockZ());
        }

        if(message.contains("%player%")) {
            Player player = findPlayerInObjects(objects);
            message = message.replace("%player%", player.getDisplayName());
        }

        if(message.contains("%delay%")){
            long storedTime = findStoredTimeInObjects(objects);
            message = message.replace("%delay%", String.valueOf(((plugin.getConfig().getInt("Delay") * 1000) - (System.currentTimeMillis() - storedTime)) / 1000));
        }

        return message;
    }

    private Location findLocationInObjects(Object... objects) {
        for(Object object : objects) {
            if(object instanceof Location) {
                return (Location) object;
            }
        }
        throw new IllegalStateException();
    }

    private Player findPlayerInObjects(Object... objects) {
        for(Object object : objects) {
            if(object instanceof Player) {
                return (Player) object;
            }
        }
        throw new IllegalStateException();

    }

    private Long findStoredTimeInObjects(Object... objects){
        for(Object object : objects) {
            if(object instanceof Long) {
                return (long) object;
            }
        }
        throw new IllegalStateException();

    }
}
