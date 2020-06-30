package me.mmigas;

import me.mmigas.commands.CrateCommand;
import me.mmigas.commands.PocoCommand;
import me.mmigas.commands.WellCommand;
import me.mmigas.files.ConfigManager;
import me.mmigas.files.LanguageManager;
import me.mmigas.listeners.CrateInteractListener;
import me.mmigas.listeners.DropsListener;
import me.mmigas.listeners.FixCanceler;
import me.mmigas.persistence.CratesRepository;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class EventSystem extends JavaPlugin {

    private ConfigManager configManager;
    private EventController eventController;
    private WishingWells wishingWell;

    private Map<String, Integer> tasks = new HashMap<>();

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        eventController = new EventController(this, configManager);
        new LanguageManager(this);
        new CratesRepository(this);
        wishingWell = new WishingWells(this, configManager);
        registerCommands();
        registerListener();
    }

    @Override
    public void onDisable() {
        for (Map.Entry<String, Integer> entry : tasks.entrySet()) {
            Bukkit.getScheduler().cancelTask(entry.getValue());
        }
    }

    private void registerCommands() {
        getCommand("poço").setExecutor(new PocoCommand(this));
        getCommand("well").setExecutor(new WellCommand(this));
        getCommand("crate").setExecutor(new CrateCommand(eventController));
    }

    private void registerListener() {
        getServer().getPluginManager().registerEvents(new FixCanceler(), this);
        getServer().getPluginManager().registerEvents(new DropsListener(this, configManager), this);
        getServer().getPluginManager().registerEvents(new CrateInteractListener(), this);
    }

    boolean isWorldGuardEnabled() {
        return getServer().getPluginManager().isPluginEnabled("WorldGuard");
    }

    boolean isGriefPreventionEnabled() {
        return getServer().getPluginManager().isPluginEnabled("GriefPrevention");
    }

    public Map<String, Integer> getTasks() {
        return tasks;
    }

    public WishingWells getWishingWells() {
        return wishingWell;
    }
}
