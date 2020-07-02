package me.mmigas.persistence;

import me.mmigas.EventSystem;
import me.mmigas.events.CrateEvent;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class CratesRepository {

    private final EventSystem plugin;
    private final FileConfiguration fileConfiguration;

    private static CratesRepository instance;

    private static final String CRATE = "crate";
    private static final String STATUS = ".status";
    private static final String WORLD = ".world";
    private static final String LOCATION = ".location";
    private static final String LOCATION_X = LOCATION + ".x";
    private static final String LOCATION_Y = LOCATION + ".y";
    private static final String LOCATION_Z = LOCATION + ".z";
    private static final String LANDING_TIME = ".landing-time";

    private static final String STORAGE_FILE_DIRECTORY = "storage" + File.separator + "Crate";
    private static final String STORAGE_FILE_NAME = "Crates.yml";

    public CratesRepository(EventSystem plugin) {
        this.plugin = plugin;
        this.fileConfiguration = loadCrateFile(getFile());

        instance = this;
    }

    public void addCrate(CrateEvent crate) {
        String id = keyFromId(crate.getId());

        fileConfiguration.set(id + WORLD, Objects.requireNonNull(crate.getCurrentLocation().getWorld()).getName());
        fileConfiguration.set(id + LOCATION_X, crate.getCurrentLocation().getX());
        fileConfiguration.set(id + LOCATION_Y, crate.getCurrentLocation().getY());
        fileConfiguration.set(id + LOCATION_Z, crate.getCurrentLocation().getZ());
        fileConfiguration.set(id + STATUS, crate.getStatus().toString());

        if (crate.getStatus() == CrateEvent.Status.LANDED) {
            fileConfiguration.set(id + LANDING_TIME, crate.getLandingTime());
        }

        save();
    }

    public void removeCrate(CrateEvent crate) {
        removeCrate(crate.getId());
    }

    public void removeCrate(int crateId) {
        String id = keyFromId(crateId);
        fileConfiguration.set(id, null);

        save();
    }

    public boolean checkCrateByID(int id) {
        for (String key : Objects.requireNonNull(fileConfiguration.getConfigurationSection(CRATE)).getKeys(false)) {
            if (Integer.parseInt(key) == id) {
                return true;
            }
        }

        return false;
    }

    public FileConfiguration getFileConfiguration() {
        return fileConfiguration;
    }

    public void save() {
        try {
            fileConfiguration.save(getFile());
        } catch (IOException e) {
            plugin.getLogger().warning("Could not save crates storage file: " + e.getMessage());
        }
    }

    public static CratesRepository getInstance() {
        return instance;
    }

    private FileConfiguration loadCrateFile(File file) {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().warning("Could not create crates storage file: " + e.getMessage());
            }
        }

        try {
            FileConfiguration configuration = new YamlConfiguration();
            configuration.load(file);
            return configuration;
        } catch (IOException | InvalidConfigurationException e) {
            throw new IllegalStateException("Could not load the crates storage file: " + e.getMessage());
        }
    }

    private File getFile() {
        return new File(storageFolder(), STORAGE_FILE_NAME);
    }

    private File storageFolder() {
        return new File(plugin.getDataFolder(), STORAGE_FILE_DIRECTORY);
    }

    private static String keyFromId(int id) {
        return CRATE + "." + id;
    }
}
