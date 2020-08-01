package me.mmigas.gui;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class Gui implements InventoryHolder {
    private final Map<Integer, Item> items = new HashMap<>();

    private final int size;
    private final String title;

    public Gui(String title, int size) {
        this.size = size;
        this.title = title;
    }

    public void setItem(int position, Item item) {
        items.put(position, item);
    }

    public Item getItem(int position) {
        return items.get(position);
    }

    @Override
    public @NotNull Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, size, title);

        for (Map.Entry<Integer, Item> entry : items.entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue().getItemStack());
        }
        return inventory;
    }
}
