package me.mmigas;

import me.mmigas.commands.CrateCommand;
import me.mmigas.crates.CrateController;
import me.mmigas.files.ConfigManager;
import me.mmigas.files.LanguageManager;
import me.mmigas.listeners.CrateInteractListener;
import me.mmigas.persistence.CratesRepository;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class EventSystem extends JavaPlugin {

    private CrateController crateController;

    @Override
    public void onEnable() {
        ConfigManager configManager = new ConfigManager(this);
        new CratesRepository(this);
        crateController = new CrateController(this, configManager);
        new LanguageManager(this);
        registerCommands();
        registerListener();
    }

    @Override
    public void onDisable() {
        crateController.stopFallingCrates();
        crateController.stopCrateSpawningTask();
    }

    private void registerCommands() {
        Objects.requireNonNull(getCommand("crate")).setExecutor(new CrateCommand(crateController));
    }

    private void registerListener() {
        getServer().getPluginManager().registerEvents(new CrateInteractListener(), this);
    }

    public boolean isWorldGuardEnabled() {
        return getServer().getPluginManager().isPluginEnabled("WorldGuard");
    }

    public boolean isGriefPreventionEnabled() {
        return getServer().getPluginManager().isPluginEnabled("GriefPrevention");
    }

}
