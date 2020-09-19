package me.mmigas.crates.menu;

import me.mmigas.gui.Item;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class MainPage extends Page {
    private static final String TITLE = "Main Page";
    private static final int SIZE = 3*9;

    public MainPage(Menu menu) {
        super(menu, TITLE, SIZE);
    }

    @Override
    public void setup() {
        List<Item> items = createItems();
        gui.setItem(1, items.get(0));
    }

    @Override
    List<Item> createItems() {
        List<Item> items = new ArrayList<>();
        Item item = new Item(Material.CHEST);
        item.addClickActions(player -> menu.switchPage(PageID.ADD_CREATE));
        items.add(item);
        return items;
    }
}
