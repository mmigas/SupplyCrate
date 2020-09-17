package me.mmigas;

import me.mmigas.commands.CrateCommand;
import me.mmigas.crates.CrateController;
import me.mmigas.files.ConfigManager;
import me.mmigas.files.LanguageManager;
import me.mmigas.listeners.ChunksListener;
import me.mmigas.listeners.InteractionListener;
import me.mmigas.listeners.InventoryListener;
import me.mmigas.persistence.CratesRepository;
import net.milkbowl.vault.economy.Economy;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Level;

public class SupplyCrate extends JavaPlugin {

    private CrateController crateController;
    private static Economy economy = null;

    private static SupplyCrate instance;

    private boolean isWorldGuardEnabled;
    private boolean isGriefPreventionEnabled;
    private boolean isVaultEnabled;
    private boolean isHdEnabled;

    @Override
    public void onEnable() {
        int pluginId = 8870;
        Metrics metrics = new Metrics(this, pluginId);
        instance = this;
        isWorldGuardEnabled = checkWorldGuard();
        isGriefPreventionEnabled = checkGriefPrevention();
        isVaultEnabled = checkVault();
        isHdEnabled = checkHd();

        ConfigManager configManager = new ConfigManager(this);
        CratesRepository cratesRepository = new CratesRepository(this);
        crateController = new CrateController(this, configManager);
        new LanguageManager(this, cratesRepository);
        registerCommands();
        registerListener();

        if (!isVaultEnabled) {
            Bukkit.getLogger().log(Level.WARNING, (String.format("[%s] - Vault not found. You won't have access to the buy command.", getDescription().getName())));
        }
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
        getServer().getPluginManager().registerEvents(new InventoryListener(), this);
        getServer().getPluginManager().registerEvents(new ChunksListener(), this);
        getServer().getPluginManager().registerEvents(new InteractionListener(), this);
    }

    private boolean checkWorldGuard() {
        return getServer().getPluginManager().isPluginEnabled("WorldGuard");
    }

    private boolean checkGriefPrevention() {
        return getServer().getPluginManager().isPluginEnabled("GriefPrevention");
    }

    private boolean checkVault() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return true;
    }

    private boolean checkHd() {
        return Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays");
    }

    public boolean isWorldGuardEnabled() {
        return isWorldGuardEnabled;
    }

    public boolean isGriefPreventionEnabled() {
        return isGriefPreventionEnabled;
    }

    public boolean isVaultEnabled() {
        return isVaultEnabled;
    }

    public boolean isHdEnabled() {
        return isHdEnabled;
    }

    public static Economy getEconomy() {
        return economy;
    }

    public CrateController getCrateController() {
        return crateController;
    }

    public static SupplyCrate getInstance() {
        return instance;
    }
}
