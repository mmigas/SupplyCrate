package me.mmigas.crates.menu;

import me.mmigas.gui.Item;

import java.util.ArrayList;
import java.util.List;

public class AddChest extends Page {
    private static final String TITLE = "Add Chest";
    private static final int SIZE = 3 * 9;

    public AddChest(Menu menu) {
        super(menu, TITLE, SIZE);
    }

    @Override
    void setup() {

    }

    @Override
    List<Item> createItems() {
        return new ArrayList<>();
    }
}
