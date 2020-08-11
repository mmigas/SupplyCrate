package me.mmigas.listeners;

import me.mmigas.crates.CrateEvent;
import me.mmigas.files.LanguageManager;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class InteractionListener implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null || event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getClickedBlock().getType() != Material.CHEST) {
            return;
        }
        Chest chest = (Chest) event.getClickedBlock().getState();
        if (ListenerUtils.crateIDFromChest(chest) == -1 || !chest.hasMetadata(CrateEvent.CRATE_METADATA)) {
            return;
        }
        event.setCancelled(true);
        event.getPlayer().openInventory(chest.getInventory());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() != Material.CHEST) {
            return;
        }

        Chest chest = (Chest) event.getBlock().getState();
        int crateID = ListenerUtils.crateIDFromChest(chest);
        if (crateID == -1) {
            return;
        }

        event.setCancelled(true);
        LanguageManager.sendKey(event.getPlayer(), LanguageManager.CRATE_CANNOT_BREAK);
    }

    @EventHandler
    public void onPlayerArmorStandInteract(PlayerArmorStandManipulateEvent event) {
        ArmorStand stand = event.getRightClicked();
        if (stand.hasMetadata(CrateEvent.CRATE_METADATA)) {
            event.setCancelled(true);
        }
    }
}

