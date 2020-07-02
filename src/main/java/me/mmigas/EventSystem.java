package me.mmigas;

import me.mmigas.commands.CrateCommand;
import me.mmigas.files.ConfigManager;
import me.mmigas.files.LanguageManager;
import me.mmigas.listeners.CrateInteractListener;
import me.mmigas.persistence.CratesRepository;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EventSystem extends JavaPlugin {

    private ConfigManager configManager;
    private EventController eventController;

    private Map<String, Integer> tasks = new HashMap<>();

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        eventController = new EventController(this, configManager);
        new LanguageManager(this);
        new CratesRepository(this);
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
        Objects.requireNonNull(getCommand("crate")).setExecutor(new CrateCommand(eventController));
    }

    private void registerListener() {
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

}
