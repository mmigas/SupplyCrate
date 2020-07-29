package me.mmigas.crates;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import me.mmigas.EventSystem;
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

    private final EventSystem plugin;
    private final ConfigManager configManager;
    private final CratesRepository cratesRepository;

    private final Random random;

    private final List<CrateTier> tiers;

    private int spawnCrateTaskId = -1;

    public CrateController(EventSystem plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.random = new Random();
        this.cratesRepository = CratesRepository.getInstance();

        tiers = configManager.readTiersFromConfigs(this);

        if (configManager.getConfig().getBoolean(ConfigManager.AUTOSTART)) {
            spawnCrateTaskId = startCrateSpawningTask(plugin.getConfig().getInt(ConfigManager.COOLDOWN));
        }

        continueCrateEvents();
    }


    public void spawnCrate(Player player, String name) {
        if (worldGuardTest(player.getLocation().getWorld(), player.getLocation())) {
            LanguageManager.sendMessage(player, "&dYou cannot spawn crates in world guard's regions.");
            return;
        }

        if (griefPreventionTest(player.getLocation())) {
            player.sendMessage("&dYou cannot spawn crates in Grief Prevention's regions.");
            return;
        }
        CrateTier crateTier = getCrateTierByIdentifier(name);
        startEvent(crateTier, player.getLocation().add(0, 30, 0));
    }


    public int startCrateSpawningTask(long cooldown) {
        if (spawnCrateTaskId != -1) {
            return -1;
        } else {
            spawnCrateTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                //TODO: Select a random crate tier and spawn is chest.
                CrateTier crateTier = tiers.get(0);
                startEvent(crateTier);

            }, cooldown * 1200, cooldown * 1200);
            return spawnCrateTaskId;
        }
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
            location = new Location(world, x + 0.5f, 200, z + 0.5f);
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

    public boolean isInside(Location maxPosition, Location minPosistion, Location location) {
        return maxPosition.getBlockX() >= location.getBlockX() && minPosistion.getBlockX() <= location.getBlockX() &&
                maxPosition.getBlockZ() >= location.getBlockZ() && minPosistion.getBlockZ() <= location.getBlockZ();
    }

    private CrateTier getCrateTierByIdentifier(String identifier) {
        for (CrateTier crateTier : tiers) {
            if (crateTier.getIdentifier().equalsIgnoreCase(identifier)) {
                return crateTier;
            }
        }
        return null;
    }


    EventSystem getPlugin() {
        return plugin;
    }

    CratesRepository cratesRepository() {
        return cratesRepository;
    }


    public int getSpawCrateTaskId() {
        return spawnCrateTaskId;
    }

}
