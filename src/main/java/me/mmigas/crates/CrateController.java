package me.mmigas.crates;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import me.mmigas.SupplyCrate;
import me.mmigas.files.ConfigManager;
import me.mmigas.files.LanguageManager;
import me.mmigas.persistence.CratesRepository;
import me.mmigas.utils.Pair;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;

public class CrateController {

    private final SupplyCrate plugin;
    private final ConfigManager configManager;
    private final CratesRepository cratesRepository;

    private final Random random;

    private final List<CrateTier> tiers;

    private int spawnCrateTaskId = -1;
    private long cooldown;
    private final int totalTiersPercentage;


    public CrateController(SupplyCrate plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.random = new Random();
        this.cratesRepository = CratesRepository.getInstance();

        tiers = configManager.readTiersFromConfigs(this);

        if (configManager.getConfig().getBoolean(ConfigManager.AUTOSTART)) {
            cooldown = plugin.getConfig().getInt(ConfigManager.COOLDOWN);
            spawnCrateTaskId = startCrateSpawningTask();
        }

        totalTiersPercentage = tiers.get(tiers.size() - 1).getPercentage();

        continueCrateEvents();
    }


    public void spawnCrate(Player player, String identifier) {
        if (worldGuardTest(player.getLocation().getWorld(), player.getLocation())) {
            LanguageManager.sendKey(player, LanguageManager.WORLD_GUARD_REGION);
            return;
        }

        if (griefPreventionTest(player.getLocation())) {
            LanguageManager.sendKey(player, LanguageManager.GRIEF_PREVENTION_REGION);
            return;
        }
        CrateTier crateTier = getCrateTierByIdentifier(identifier);
        if (crateTier == null) {
            LanguageManager.sendKey(player, LanguageManager.INVALID_CRATE_TIER);
        } else {
            Location location = player.getLocation();
            startEvent(crateTier, new Location(player.getWorld(), location.getBlockX(), 250, location.getBlockZ()));
        }
    }


    public int startCrateSpawningTask() {
        if (spawnCrateTaskId != -1) {
            return -1;
        } else {
            spawnCrateTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () ->
                    startEvent(selectRandomCrateTier()), cooldown * 1200, cooldown * 1200);
            return spawnCrateTaskId;
        }
    }

    private CrateTier selectRandomCrateTier() {
        int percentage = random.nextInt(totalTiersPercentage);
        CrateTier crateTier = null;
        for (CrateTier tier : tiers) {
            if (percentage < tier.getPercentage()) {
                crateTier = tier;
                break;
            }
        }
        return crateTier;
    }

    private void startEvent(CrateTier crateTier) {
        Location location = generateLocation();
        startEvent(crateTier, location);
    }

    private void startEvent(CrateTier crateTier, Location location) {
        int id = random.nextInt();
        CrateEvent crateEvent = crateTier.startEvent(location, id);
        cratesRepository.addCrate(crateEvent);
    }

    public boolean stopCrateSpawningTask() {
        if (spawnCrateTaskId == -1) {
            return false;
        } else {
            Bukkit.getScheduler().cancelTask(spawnCrateTaskId);
            spawnCrateTaskId = -1;
            return true;
        }
    }

    private void continueCrateEvents() {
        List<Pair<String, Integer>> cratesID = CratesRepository.getInstance().getFallingCratesIDsAndTiers();
        for (Pair<String, Integer> pair : cratesID) {
            for (CrateTier tier : tiers) {
                if (tier.getIdentifier().equals(pair.first)) {
                    tier.startEvent(CratesRepository.getInstance().getCrateLocation(pair.second), pair.second);
                }
            }
        }
    }

    public void stopFallingCrates() {
        for (CrateTier crateTier : tiers) {
            crateTier.stopFallingCrates();
        }
    }

    void addCrateToRepository(CrateEvent crateEvent) {
        cratesRepository.addCrate(crateEvent);
    }

    void updateCrateToRepository(CrateEvent crateEvent) {
        cratesRepository.updateCrate(crateEvent);
    }

    private Location generateLocation() {
        List<World> worlds = configManager.enabledCrateWorlds();
        World world;
        if (worlds.isEmpty()) {
            world = Bukkit.getWorlds().get(0);
        } else {
            world = worlds.get(random.nextInt(worlds.size()));
        }

        int spawnX = world.getSpawnLocation().getBlockX();
        int spawnZ = world.getSpawnLocation().getBlockZ();
        int radius = plugin.getConfig().getInt(ConfigManager.CRATE_RADIUS);
        int x;
        int z;

        Location location;
        do {
            x = random.nextInt(((spawnX + radius) - (spawnX - radius)) + 1) + (spawnX - radius);
            z = random.nextInt(((spawnZ + radius) - (spawnZ - radius)) + 1) + (spawnZ - radius);
            location = new Location(world, x + 0.5f, 250, z + 0.5f);
        } while (worldGuardTest(world, location) || griefPreventionTest(location));

        return location;
    }

    public boolean worldGuardTest(World world, Location location) {
        if (!plugin.isWorldGuardEnabled()) {
            return false;
        }

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager manager = container.get(BukkitAdapter.adapt(world));
        if (manager != null) {
            for (Map.Entry<String, ProtectedRegion> entry : manager.getRegions().entrySet()) {
                Location maxPosition = new Location(world, entry.getValue().getMaximumPoint().getBlockX(), 0,
                        entry.getValue().getMaximumPoint().getBlockZ());
                Location minPosition = new Location(world, entry.getValue().getMinimumPoint().getBlockX(), 0,
                        entry.getValue().getMinimumPoint().getBlockZ());

                if (isInside(maxPosition, minPosition, location)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean griefPreventionTest(Location location) {
        if (!plugin.isGriefPreventionEnabled()) {
            return false;
        }

        for (Claim claim : GriefPrevention.instance.dataStore.getClaims()) {
            if (isInside(claim.getGreaterBoundaryCorner(), claim.getLesserBoundaryCorner(), location)) {
                return true;
            }
        }

        return false;
    }

    public boolean isInside(Location maxPosition, Location minPosition, Location location) {
        return maxPosition.getBlockX() >= location.getBlockX() && minPosition.getBlockX() <= location.getBlockX() &&
                maxPosition.getBlockZ() >= location.getBlockZ() && minPosition.getBlockZ() <= location.getBlockZ();
    }

    public CrateTier getCrateTierByIdentifier(String identifier) {
        for (CrateTier crateTier : tiers) {
            if (crateTier.getIdentifier().equalsIgnoreCase(identifier)) {
                return crateTier;
            }
        }
        return null;
    }

    public void changeCooldown(long cooldown) {
        stopCrateSpawningTask();
        this.cooldown = cooldown;
        startCrateSpawningTask();
    }

    SupplyCrate getPlugin() {
        return plugin;
    }

    public List<String> getTiersNames() {
        List<String> names = new ArrayList<>();
        for (CrateTier tier : tiers) {
            names.add(tier.getName());
        }
        return names;
    }
}
