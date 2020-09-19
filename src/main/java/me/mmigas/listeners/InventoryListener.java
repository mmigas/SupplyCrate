package me.mmigas.listeners;

import me.mmigas.files.LanguageManager;
import me.mmigas.gui.Gui;
import me.mmigas.gui.IClickAction;
import me.mmigas.gui.Item;
import me.mmigas.persistence.CratesRepository;
import me.mmigas.utils.InventoryUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;


public class InventoryListener implements Listener {
    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {
        if (event.getClickedInventory() != null && event.getClickedInventory().getHolder() instanceof Gui) {

            Player player = (Player) event.getWhoClicked();
            ItemStack itemStack = event.getCurrentItem();

            if (itemStack == null || itemStack.getType() == Material.AIR) {
                return;
            }

            event.setCancelled(true);

            Gui gui = (Gui) event.getView().getTopInventory().getHolder();
            Item item = gui.getItem(event.getRawSlot());
            if (item == null) {
                return;
            }

            for (IClickAction IClickAction : item.getIClickActions()) {
                IClickAction.execute(player);
            }
        }
    }


    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof Chest) || !(event.getPlayer() instanceof Player)) {
            return;
        }

        Chest chest = (Chest) event.getInventory().getHolder();
        int crateID = ListenerUtils.crateIDFromChest(chest);
        if (crateID == -1 || !InventoryUtil.isInventoryEmpty(chest.getInventory())) {
            return;
        }

        Player player = (Player) event.getPlayer();
        chest.getBlock().setType(Material.AIR);
        player.playSound(event.getPlayer().getLocation(), Sound.BLOCK_METAL_PLACE, 2, 1);
        LanguageManager.broadcast(LanguageManager.CRATE_COLLECTED, player);

        CratesRepository cratesRepository = CratesRepository.getInstance();
        cratesRepository.removeCrate(crateID);
    }
}


