package me.mmigas.crates;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import me.mmigas.SupplyCrate;
import me.mmigas.utils.Pair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
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
    private Hologram hd;
    private final int id;
    private boolean isChunkLoaded = false;

    private int taskID;

    private final CrateTier crateTier;
    private List<Pair<ItemStack, Integer>> tierRewards;

    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss_dd-MM-yyyy");
    private static final Random RANDOM = new Random();

    public CrateEvent(CrateTier crateTier, int id) {
        this.crateTier = crateTier;
        this.id = id;
    }

    public void startEvent(Location location, float speed, List<Pair<ItemStack, Integer>> tierRewards) {
        this.location = location;
        groundLocation = getGroundLocation();
        if (location.getChunk().isLoaded()) {
            spawn();
        }
        taskID = runCrateFall(speed, tierRewards);
        status = Status.FALLING;
    }

    void spawn() {
        spawnStand();
        spawnHD();
        isChunkLoaded = true;
    }

    private void spawnStand() {
        stand = location.getWorld().spawn(location, ArmorStand.class);
        stand.setGravity(false);
        stand.setVisible(false);
        stand.getEquipment().setHelmet(new ItemStack(Material.CHEST));
        stand.setCollidable(false);
        stand.setInvulnerable(true);
        stand.setMarker(true);
        stand.setMetadata(CRATE_METADATA, new FixedMetadataValue(crateTier.getCrateController().getPlugin(), CRATE_METADATA));
    }

    private void spawnHD() {
        if (SupplyCrate.getInstance().isHdEnabled()) {
            hd = HologramsAPI.createHologram(SupplyCrate.getInstance(), new Location(location.getWorld(), location.getX(), location.getY() + 3.0, location.getZ()));
            String[] lines = crateTier.getHdText().split("\n");
            for (String line : lines) {
                hd.appendTextLine(ChatColor.translateAlternateColorCodes('&', line));
            }
        }
    }

    void despawn() {
        stand.remove();
        if (SupplyCrate.getInstance().isHdEnabled())
            hd.delete();
        isChunkLoaded = false;
    }

    private void teleportHD(float speed) {
        if (SupplyCrate.getInstance().isHdEnabled()) {
            hd.teleport(hd.getLocation().subtract(0, speed, 0));
        }
    }

    private int runCrateFall(float speed, List<Pair<ItemStack, Integer>> tierRewards) {
        return Bukkit.getScheduler().scheduleSyncRepeatingTask(crateTier.getCrateController().getPlugin(), () -> {
            location.subtract(0, speed, 0);
            if (isChunkLoaded) {
                stand.teleport(location);
                teleportHD(speed);
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
        if (isChunkLoaded) {
            despawn();
        }
    }

    private void spawnCrateChest() {
        Block block = location.getBlock();
        block.setType(Material.CHEST);

        Chest chest = (Chest) block.getState();
        chest.setCustomName(CRATE_NAME + id);
        chest.update();

        List<ItemStack> rewards = generateMaterials();
        Inventory inventory = chest.getInventory();
        for (ItemStack itemStack : rewards) {
            inventory.addItem(itemStack);
        }

        landingTime = LocalDateTime.now();
        location = chest.getLocation();
        crateTier.spawnHD(id, location);
    }

    private List<ItemStack> generateMaterials() {
        List<ItemStack> rewards = new ArrayList<>();
        int rewardsNumber = RANDOM.nextInt(5) + 1;

        nextReward:
        for (int i = 0; i < rewardsNumber; i++) {
            double probabilityGenerated = RANDOM.nextDouble() * (tierRewards.get(
                    tierRewards.size() - 1).second);
            for (Pair<ItemStack, Integer> pair : tierRewards) {
                if (probabilityGenerated < pair.second) {
                    rewards.add(pair.first);
                    continue nextReward;
                }
            }
        }
        return rewards;
    }

    private Location getGroundLocation() {
        return location.getWorld().getHighestBlockAt(location).getLocation().add(0, 1, 0);
    }

    private boolean isOnGround(Location location) {
        groundLocation = getGroundLocation();
        return groundLocation.distance(location) <= 0.2f && groundLocation.distance(location) >= 0;
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

