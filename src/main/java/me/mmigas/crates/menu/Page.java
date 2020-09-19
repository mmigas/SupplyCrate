package me.mmigas.crates.menu;

import me.mmigas.gui.Gui;
import me.mmigas.gui.Item;

import java.util.List;

abstract class Page {
    final Gui gui;
    final Menu menu;

    public Page(Menu menu, String title, int size) {
        this.menu = menu;
        this.gui = new Gui(title, size);
        setup();
    }

    abstract void setup();

    abstract List<Item> createItems();
}
