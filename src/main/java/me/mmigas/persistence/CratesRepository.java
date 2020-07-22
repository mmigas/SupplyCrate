package me.mmigas.persistence;

import me.mmigas.EventSystem;
import me.mmigas.events.CrateEvent;
import me.mmigas.events.Status;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CratesRepository {

    private final EventSystem plugin;
    private final FileConfiguration fileConfiguration;

    private static CratesRepository instance;

    public static final String CRATE = "crate";
    public static final String STATUS = "status";
    public static final String WORLD = "world";
    public static final String LOCATION = "location";
    public static final String LOCATION_X = LOCATION + "x";
    public static final String LOCATION_Y = LOCATION + "y";
    public static final String LOCATION_Z = LOCATION + "z";
    public static final String LANDING_TIME = "landing-time";

    private static final String STORAGE_FILE_DIRECTORY = "storage";
    private static final String STORAGE_FILE_NAME = "Crates.yml";

    public CratesRepository(EventSystem plugin) {
        this.plugin = plugin;
        this.fileConfiguration = loadCrateFile(getFile());

        instance = this;
    }

    public void addCrate(CrateEvent crate) {
        String id = keyFromId(crate.getId());
        Location location = crate.getCurrentLocation();
        ConfigurationSection configuration = fileConfiguration;

        configuration.createSection(id);
        configuration = configuration.getConfigurationSection(id);
        configuration.set(WORLD, (Objects.requireNonNull(location.getWorld())).getName());
        configuration.set(LOCATION_X, location.getX());
        configuration.set(LOCATION_Y, location.getY());
        configuration.set(LOCATION_Z, location.getZ());
        configuration.set(STATUS, crate.getStatus().toString());

        if (crate.getStatus() == Status.LANDED) {
            configuration.set(LANDING_TIME, crate.getLandingTime());
        }

        save();
    }

    public void updateCrate(CrateEvent crate) {
        String id = keyFromId(crate.getId());
        if (!fileConfiguration.isConfigurationSection(id)) {
            Bukkit.getLogger().info("Crate not found!");
            return;
        }
        Location location = crate.getCurrentLocation();

        ConfigurationSection configuration = fileConfiguration.getConfigurationSection(id);
        configuration.set(LOCATION_X, location.getBlockX());
        configuration.set(LOCATION_Y, location.getBlockY());
        configuration.set(LOCATION_Z, location.getBlockZ());
        configuration.set(STATUS, crate.getStatus().toString());
        configuration.set(LANDING_TIME, crate.getLandingTime());
        save();
    }

    public void removeCrate(int crateId) {
        String id = keyFromId(crateId);
        removeCrate(id);
    }

    public void removeCrate(String crateId) {
        Bukkit.getLogger().info(crateId);
        fileConfiguration.set(CRATE + "." + crateId, null);
        save();
    }

    public boolean checkCrateByID(int id) {
        for (String key : fileConfiguration.getConfigurationSection(CRATE).getKeys(false)) {
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
            if (!configuration.isConfigurationSection(CRATE)) {
                configuration.createSection(CRATE);
                configuration.save(getFile());
            }

            return configuration;
        } catch (IOException | InvalidConfigurationException e) {
            throw new IllegalStateException("Could not load the crates storage file: " + e.getMessage());
        }
    }

    public List<Integer> getFallingCratesIDs() {
        List<Integer> crates = new ArrayList<>();
        if (fileConfiguration.getConfigurationSection(CRATE) != null) {
            for (String key : fileConfiguration.getConfigurationSection(CRATE).getKeys(false)) {
                if (Objects.equals(fileConfiguration.getString(CRATE + "." + key + "." + STATUS), Status.FALLING.toString())) {
                    crates.add(Integer.parseInt(key));
                }
            }
        }
        return crates;
    }

    public Location getCrateLocation(int crateId) {
        int x = fileConfiguration.getInt(CRATE + "." + crateId + "." + LOCATION_X);
        int y = fileConfiguration.getInt(CRATE + "." + crateId + "." + LOCATION_Y);
        int z = fileConfiguration.getInt(CRATE + "." + crateId + "." + LOCATION_Z);
        World world = Bukkit.getServer().getWorld(fileConfiguration.getString(CRATE + "." + crateId + "." + WORLD));

        return new Location(world, x, y, z);
    }

    private File getFile() {
        return new File(storageFolder(), STORAGE_FILE_NAME);
    }

    private File storageFolder() {
        return new File(plugin.getDataFolder(), STORAGE_FILE_DIRECTORY);
    }

    public static Status getStatus(ConfigurationSection section) {
        return section.getString(STATUS).equals(Status.FALLING.toString()) ? Status.FALLING : Status.LANDED;
    }

    private static String keyFromId(int id) {
        return CRATE + "." + id;
    }
}
