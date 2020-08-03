package me.mmigas.crates;

import me.mmigas.files.LanguageManager;
import me.mmigas.utils.Pair;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CrateTier {
    private final CrateController crateController;
    private final String identifier;
    private final String name;
    private final float speed;
    private final int percentage;
    private final double price;
    private final List<Pair<ItemStack, Double>> rewards;

    private final List<CrateEvent> events;

    public CrateTier(CrateController crateController, String identifier, String name, int percentage, float speed, List<Pair<ItemStack, Double>> rewards, double price) {
        this.crateController = crateController;
        this.identifier = identifier;
        this.name = name;
        this.speed = speed;
        this.percentage = percentage;
        this.rewards = rewards;
        this.events = new ArrayList<>();
        this.price = price;
    }

    CrateEvent startEvent(Location location, int id) {
        CrateEvent crateEvent = new CrateEvent(this, id);
        crateEvent.startEvent(location, speed, rewards);
        events.add(crateEvent);
        LanguageManager.broadcast(LanguageManager.CRATE_BROADCAST, crateEvent);
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

    CrateController getCrateController() {
        return crateController;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getName() {
        return name;
    }

    public List<Pair<ItemStack, Double>> getRewards() {
        return rewards;
    }

    public int getPercentage() {
        return percentage;
    }

    public double getPrice() {
        return price;
    }
}
