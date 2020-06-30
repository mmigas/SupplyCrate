package me.mmigas.items;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Items {

    /**
     * Private constructor to hide the implicit public one.
     */
    private Items() {
        // Should be empty.
    }

    /**
     * Adds lore to an item.
     *
     * @param item the item to add lore to.
     * @param lore the content to add to the lore.
     */
    public static void addLore(ItemStack item, String lore) {
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) {
            throw new IllegalStateException("Item doesn't have item meta");
        }

        List<String> itemLore = itemMeta.getLore();
        if (itemLore == null) {
            itemLore = new ArrayList<>();
        }

        itemLore.add(lore);
        itemMeta.setLore(itemLore);
        item.setItemMeta(itemMeta);
    }

    /**
     * Adds lore to an item.
     *
     * @param item the item to add lore to.
     * @param lore the content to add to the lore.
     */
    public static void addLore(ItemStack item, String... lore) {
        for (String lorePart : lore) {
            addLore(item, lorePart);
        }
    }

}
