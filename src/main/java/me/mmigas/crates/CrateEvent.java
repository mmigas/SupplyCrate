package me.mmigas.crates;

import me.mmigas.utils.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CrateEvent implements Observer {

    public static final String CRATE_METADATA = "Crate";
    public static final String CRATE_NAME = "Crate: ";

    private Status status;
    private ArmorStand stand;
    private Location location;
    private Location groundLocation;
    private LocalDateTime landingTime;
    private final int id;
    private boolean hasStand = false;

    private int taskID;

    private final CrateTier crateTier;
    private List<Pair<ItemStack, Double>> tierRewards;

    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss_dd-MM-yyyy");
    private static final Random RANDOM = new Random();

    public CrateEvent(CrateTier crateTier, int id) {
        this.crateTier = crateTier;
        this.id = id;
    }

    public void startEvent(Location location, float speed, List<Pair<ItemStack, Double>> tierRewards) {
        this.location = location;
        groundLocation = getGroundLocation(location);
        if (location.getChunk().isLoaded()) {
            spawnStand(location);
        }
        taskID = startCrateFall(speed, tierRewards);
        status = Status.FALLING;
    }

    public void spawnStand(Location location) {
        stand = location.getWorld().spawn(location, ArmorStand.class);
        stand.setGravity(false);
        stand.setVisible(false);
        stand.getEquipment().setHelmet(new ItemStack(Material.CHEST));
        stand.setCollidable(false);
        stand.setInvulnerable(true);
        stand.setMarker(true);
        stand.setMetadata(CRATE_METADATA, new FixedMetadataValue(crateTier.getCrateController().getPlugin(), CRATE_METADATA));
        hasStand = true;
    }

    public void removeStand() {
        stand.remove();
        hasStand = false;
    }

    private int startCrateFall(float speed, List<Pair<ItemStack, Double>> tierRewards) {
        return Bukkit.getScheduler().scheduleSyncRepeatingTask(crateTier.getCrateController().getPlugin(), () -> {
            location = location.subtract(0, speed, 0);
            if (hasStand) {
                stand.teleport(location);
            }

            if (isOnGround(location)) {
                this.tierRewards = tierRewards;
                fallIsOver();
            }
        }, 0L, 1L);
    }

    public void fallIsOver() {
        stopCrateFall();
        status = Status.LANDED;
        spawnCrateChest();
        crateTier.finishEvent(this);
    }

    @Override
    public void stopCrateFall() {
        Bukkit.getScheduler().cancelTask(taskID);
        if (hasStand) {
            stand.remove();
        }
    }

    private void spawnCrateChest() {
        Block block = location.getBlock();
        block.setType(Material.CHEST);

        Chest chest = (Chest) block.getState();
        chest.setMetadata(CRATE_METADATA, new FixedMetadataValue(crateTier.getCrateController().getPlugin(), CRATE_METADATA));
        chest.setCustomName(CRATE_NAME + id);
        chest.update();

        List<ItemStack> rewards = generateMaterials();
        Inventory inventory = chest.getInventory();
        for (ItemStack itemStack : rewards) {
            inventory.addItem(itemStack);
        }

        landingTime = LocalDateTime.now();
        location = chest.getLocation();
    }

    private List<ItemStack> generateMaterials() {
        List<ItemStack> rewards = new ArrayList<>();
        int rewardsNumber = RANDOM.nextInt(3) + 1;

        nextReward:
        for (int i = 0; i < rewardsNumber; i++) {
            double probabilityGenerated = RANDOM.nextDouble() * (tierRewards.get(
                    tierRewards.size() - 1).second);
            for (Pair<ItemStack, Double> pair : tierRewards) {
                if (probabilityGenerated < pair.second) {
                    rewards.add(pair.first);
                    continue nextReward;
                }
            }
        }
        return rewards;
    }

    private Location getGroundLocation(Location location) {
        return location.getWorld().getHighestBlockAt(location).getLocation().add(0, 1, 0);
    }

    private boolean isOnGround(Location location) {
        return groundLocation.distance(location) <= 0.9f && groundLocation.distance(location) >= 0;
    }

    public CrateTier getCrateTier() {
        return crateTier;
    }

    public Status getStatus() {
        return status;
    }

    public Location getCurrentLocation() {
        return location;
    }

    public String getLandingTime() {
        return landingTime.format(TIME_FORMATTER);
    }

    public int getId() {
        return id;
    }
}

