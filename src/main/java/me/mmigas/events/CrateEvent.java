package me.mmigas.events;

import me.mmigas.EventController;
import me.mmigas.files.ConfigManager;
import me.mmigas.persistence.CratesRepository;
import me.mmigas.utils.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CrateEvent {

    public enum Status {
        FALLING,
        LANDED
    }

    public static final String CRATE_METADATA = "Crate";
    public static final String CRATE_NAME = "Crate: ";

    private Status status;
    private Location currentLocation;
    private LocalDateTime landingTime;
    private int id;
    private final float speed;

    private final EventController controller;
    private final CratesRepository repository = CratesRepository.getInstance();

    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss_dd-MM-yyyy");
    private static final Random RANDOM = new Random();

    public CrateEvent(EventController controller, ConfigManager manager, Player player) {
        this.controller = controller;

        speed = (float) manager.getConfig().getDouble(ConfigManager.CRATE_SPEED);
        carePackageEventPlayer(player);
    }

    public CrateEvent(EventController controller, ConfigManager manager, Location location) {
        this.controller = controller;

        speed = (float) manager.getConfig().getDouble(ConfigManager.CRATE_SPEED);
        startEvent(location);
    }

    private void carePackageEventPlayer(Player player) {
        startEvent(player.getLocation().add(new Vector(0, 15f, 0)));
    }

    private void startEvent(Location location) {
        location.getWorld().loadChunk(location.getChunk());
        currentLocation = location;
        id = RANDOM.nextInt();

        ArmorStand stand = location.getWorld().spawn(location, ArmorStand.class);
        stand.setGravity(false);
        stand.setVisible(false);
        stand.getEquipment().setHelmet(new ItemStack(Material.CHEST));
        stand.setCollidable(false);
        stand.setInvulnerable(true);
        stand.setMarker(true);
        stand.setMetadata(CRATE_METADATA, new FixedMetadataValue(controller.getPlugin(), CRATE_METADATA));

        List<ItemStack> rewards = generateMaterials();
        fallTask(stand, rewards);
        status = Status.FALLING;
        repository.addCrate(this);
    }

    private void fallTask(ArmorStand stand, List<ItemStack> rewards) {
        new BukkitRunnable() {
            public void run() {
                if (!stand.getLocation().getWorld().isChunkLoaded(stand.getLocation().getChunk())) {
                    Bukkit.getLogger().info("Not loaded");
                    stand.getLocation().getWorld().loadChunk(stand.getLocation().getChunk());
                    return;
                }
                stand.teleport(stand.getLocation().subtract(0, speed, 0));

                if (isOnGround(stand.getLocation(), stand.getWorld())) {
                    stand.remove();
                    status = Status.LANDED;
                    spawnCrateChest(stand, rewards);
                    stand.getLocation().getWorld().unloadChunk(stand.getLocation().getChunk());
                    this.cancel();
                }
            }
        }.runTaskTimer(controller.getPlugin(), 0L, 1L);
    }

    private void spawnCrateChest(ArmorStand stand, List<ItemStack> rewards) {
        Block block = stand.getLocation().getBlock();
        block.setType(Material.CHEST);

        Chest chest = (Chest) block.getState();
        chest.setMetadata(CRATE_METADATA, new FixedMetadataValue(controller.getPlugin(), CRATE_METADATA));
        chest.setCustomName(CRATE_NAME + id);
        chest.update();

        Inventory inventory = chest.getInventory();
        for (ItemStack itemStack : rewards) {
            inventory.addItem(itemStack);
        }

        landingTime = LocalDateTime.now();
        currentLocation = chest.getLocation();
        repository.addCrate(this);
    }

    private boolean isOnGround(Location location, World world) {
        return world.getBlockAt(location.subtract(0, 1, 0)).getType() != Material.AIR;
    }

    private List<ItemStack> generateMaterials() {
        List<Pair<ItemStack, Double>> possibleRewards = controller.getRewards();
        List<ItemStack> rewards = new ArrayList<>();

        new BukkitRunnable() {
            public void run() {
                int rewardsNumber = RANDOM.nextInt(3) + 1;
                nextreward:
                for (int i = 0; i < rewardsNumber; i++) {
                    double probabilityGenerated = RANDOM.nextDouble() * (possibleRewards.get(
                            possibleRewards.size() - 1).second);
                    for (Pair<ItemStack, Double> pair : possibleRewards) {
                        if (probabilityGenerated < pair.second) {
                            rewards.add(pair.first);
                            continue nextreward;
                        }
                    }
                }
                this.cancel();
            }
        }.runTaskAsynchronously(controller.getPlugin());

        return rewards;
    }

    public Status getStatus() {
        return status;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public String getLandingTime() {
        return landingTime.format(TIME_FORMATTER);
    }

    public int getId() {
        return id;
    }
}

