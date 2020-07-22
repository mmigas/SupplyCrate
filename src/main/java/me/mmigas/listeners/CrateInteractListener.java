package me.mmigas.listeners;

import me.mmigas.events.CrateEvent;
import me.mmigas.files.LanguageManager;
import me.mmigas.persistence.CratesRepository;
import me.mmigas.utils.InventoryUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Chest;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.logging.Level;

public class CrateInteractListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null || event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getClickedBlock().getType() != Material.CHEST) {
            return;
        }
        Chest chest = (Chest) event.getClickedBlock().getState();
        if (crateIDFromChest(chest) == -1 || !chest.hasMetadata(CrateEvent.CRATE_METADATA)) {
            return;
        }
        event.setCancelled(true);
        event.getPlayer().openInventory(chest.getInventory());
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof Chest) || !(event.getPlayer() instanceof Player)) {
            return;
        }

        Chest chest = (Chest) event.getInventory().getHolder();
        int crateID = crateIDFromChest(chest);
        if (crateID == -1 || !InventoryUtil.isInventoryEmpty(chest.getInventory()) || !chest.hasMetadata(CrateEvent.CRATE_METADATA)) {
            return;
        }

        Player player = (Player) event.getPlayer();
        chest.getBlock().setType(Material.AIR);
        player.playSound(event.getPlayer().getLocation(), Sound.BLOCK_METAL_PLACE, 2, 1);
        LanguageManager.broadcast(LanguageManager.CRATE_COLLECTED, player);

        CratesRepository cratesRepository = CratesRepository.getInstance();
        cratesRepository.removeCrate(crateID);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() != Material.CHEST) {
            return;
        }

        Chest chest = (Chest) event.getBlock().getState();
        int crateID = crateIDFromChest(chest);
        if (crateID == -1 || !chest.hasMetadata(CrateEvent.CRATE_METADATA)) {
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

    private static int crateIDFromChest(Chest chest) {
        String chestCustomName = chest.getCustomName();
        if (chestCustomName == null) {
            return -1;
        }

        try {
            int crateID = Integer.parseInt(chestCustomName.replace(CrateEvent.CRATE_NAME, ""));

            CratesRepository cratesRepository = CratesRepository.getInstance();
            if (cratesRepository.checkCrateByID(crateID)) {
                return crateID;
            } else {
                Bukkit.getLogger().log(Level.WARNING, "The crate {0} was not found.", crateID);
                return -1;
            }
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
