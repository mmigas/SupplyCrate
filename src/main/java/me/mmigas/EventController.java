package me.mmigas;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import me.mmigas.events.CrateEvent;
import me.mmigas.files.ConfigManager;
import me.mmigas.files.LanguageManager;
import me.mmigas.utils.Pair;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class EventController {

    private final EventSystem plugin;
    private final ConfigManager configManager;

    /*Create Event*/
    public static final String TIMERID = "CrateTimerTask";
    private List<Pair<ItemStack, Double>> rewards;

    private Random random = new Random();

    private int cratesCounter = 0;

    EventController(EventSystem plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;

        rewards = configManager.getRewardsList(ConfigManager.CRATE_REWARDS, false);

        if (configManager.getConfig().getBoolean(ConfigManager.CRATE_AUTOSTART)) {
            plugin.getTasks().put(
                    EventController.TIMERID,
                    crateTimer(plugin.getConfig().getInt(ConfigManager.CRATE_COOLDOWN))
            );
        }
    }

    public void spawnCrate(Player player) {
        if (worldGuardTest(player.getLocation().getWorld(), player.getLocation())) {
            player.sendMessage("World Guard");
            return;
        }

        if (griefPreventionTest(player.getLocation())) {
            player.sendMessage("GriefPrevention");
            return;
        }

        new CrateEvent(this, configManager, player);
        LanguageManager.broadcast(LanguageManager.CRATE_BROADCAST, player.getLocation());
    }

    public int crateTimer(long cooldown) {
        return Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            Location location = generateLocation();
            LanguageManager.broadcast(LanguageManager.CRATE_BROADCAST, location);
            new CrateEvent(this, configManager, location);
        }, cooldown * 1200, cooldown * 1200);
    }

    private Location generateLocation() {
        List<World> worlds = configManager.enabledCrateWorlds();
        World world;
        if (worlds.isEmpty()) {
            world = Bukkit.getWorlds().get(cratesCounter);
        } else {
            world = worlds.get(cratesCounter % worlds.size());
        }

        cratesCounter++;

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

    private boolean worldGuardTest(World world, Location location) {
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

    private boolean griefPreventionTest(Location location) {
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

    private boolean isInside(Location maxPosition, Location minPosistion, Location location) {
        return maxPosition.getBlockX() >= location.getBlockX() && minPosistion.getBlockX() <= location.getBlockX() &&
                maxPosition.getBlockZ() >= location.getBlockZ() && minPosistion.getBlockZ() <= location.getBlockZ();
    }

    public EventSystem getPlugin() {
        return plugin;
    }

    public List<Pair<ItemStack, Double>> getRewards() {
        return rewards;
    }

}
