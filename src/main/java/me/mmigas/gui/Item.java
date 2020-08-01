package me.mmigas.gui;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Item {

    private final ItemStack itemStack;

    private final List<IClickAction> iClickActions = new ArrayList<>();


    public Item(Material material) {
        this.itemStack = new ItemStack(material);
    }

    public Item(Material material, String name) {
        this.itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(name);
        itemStack.setItemMeta(meta);
    }

    public Item(Material material, String name, List<String> lore) {
        this.itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
    }

    public Item(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public Item addClickActions(IClickAction iClickAction) {
        iClickActions.add(iClickAction);
        return this;
    }

    public List<IClickAction> getIclickActions() {
        return iClickActions;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }
}

