package me.mmigas.crates.menu;

import org.bukkit.entity.Player;

import java.util.EnumMap;

enum PageID {
    MAIN_PAGE, ADD_CREATE,
}

public class Menu {

    private final EnumMap<PageID, Page> pages;
    private final Player player;


    public Menu(Player player) {
        this.pages = new EnumMap<>(PageID.class);
        this.player = player;
        createPages();
        switchPage(PageID.MAIN_PAGE);
    }

    private void createPages() {
        pages.put(PageID.MAIN_PAGE, new MainPage(this));
        pages.put(PageID.ADD_CREATE, new AddChest(this));
    }

    void switchPage(PageID newPage) {
        Page page = pages.get(newPage);
        player.openInventory(page.gui.getInventory());
    }
}

