package me.mmigas.crates;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import me.mmigas.SupplyCrate;
import me.mmigas.utils.Pair;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrateTier {
    private final CrateController crateController;
    private final String identifier;
    private String name;
    private String hdText;
    private final float speed;
    private final int chance;
    private double price;

    private final List<Pair<ItemStack, Integer>> rewards;
    private final List<CrateEvent> events;

    private final Map<Integer, Hologram> holograms;

    public CrateTier(CrateController crateController, String identifier, int chance, float speed, List<Pair<ItemStack, Integer>> rewards) {
        this.crateController = crateController;
        this.identifier = identifier;
        this.speed = speed;
        this.chance = chance;
        this.rewards = rewards;
        this.events = new ArrayList<>();
        this.holograms = new HashMap<>();
    }

    CrateEvent startEvent(Location location, int id) {
        CrateEvent crateEvent = new CrateEvent(this, id);
        crateEvent.startEvent(location, speed, rewards);
        events.add(crateEvent);
        return crateEvent;
    }

    void stopFallingCrates() {
        List<CrateEvent> toRemove = new ArrayList<>();
        for (CrateEvent crateEvent : events) {
            if (crateEvent.getStatus() == Status.FALLING) {
                crateController.addCrateToRepository(crateEvent);
                crateEvent.stopCrateFall();
                toRemove.add(crateEvent);
            }
        }
        events.removeAll(toRemove);
    }

    void finishEvent(CrateEvent crateEvent) {
        crateController.updateCrateToRepository(crateEvent);
        events.remove(crateEvent);
    }

    void spawnCrate(int id) {
        CrateEvent event = getCrateEventById(id);
        if (event != null) {
            event.spawn();
        }
    }

    void despawnCrate(int id) {
        CrateEvent event = getCrateEventById(id);
        if (event != null) {
            event.despawn();
        }
    }

    /**
     * Used when the server starts and when the crate spawns the chest. It need to recreate all the holograms of the since the hd aren't persistence and despawn
     * after server close.
     *
     * @param id       crate id
     * @param location hologram location
     */
    public void spawnHD(int id, Location location) {
        if (SupplyCrate.getInstance().isHdEnabled()) {
            String[] lines = getHdText().split("\n");
            Hologram hd = HologramsAPI.createHologram(SupplyCrate.getInstance(),
                    new Location(location.getWorld(), location.getBlockX() + 0.5, location.getBlockY() + ((double) lines.length) / 5 + 1.5, location.getBlockZ() + 0.5));

            for (String line : lines) {
                hd.appendTextLine(ChatColor.translateAlternateColorCodes('&', line));
            }
            holograms.put(id, hd);
        }
    }

    public void removeHologram(int id) {
        Hologram hd = holograms.get(id);
        hd.delete();
        holograms.remove(id);
    }

    private CrateEvent getCrateEventById(int id) {
        for (CrateEvent event : events) {
            if (event.getId() == id) {
                return event;
            }
        }
        return null;
    }

    CrateController getCrateController() {
        return crateController;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<Pair<ItemStack, Integer>> getRewards() {
        return rewards;
    }

    public int getChance() {
        return chance;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public void setHdText(String text) {
        this.hdText = text;
    }

    String getHdText() {
        return hdText;
    }

    public Map<Integer, Hologram> getHolograms() {
        return holograms;
    }
}
