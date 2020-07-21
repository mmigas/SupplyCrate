package me.mmigas.utils;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryUtil {

    private InventoryUtil() {

    }

    public static boolean isInventoryEmpty(Inventory inventory) {
        for (ItemStack itemStack : inventory) {
            if (itemStack != null) {
                return false;
            }
        }
        return true;
    }
}
