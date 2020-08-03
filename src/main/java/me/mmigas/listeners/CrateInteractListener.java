package me.mmigas.listeners;

import me.mmigas.SupplyCrate;
import me.mmigas.crates.CrateEvent;
import me.mmigas.files.LanguageManager;
import me.mmigas.gui.Gui;
import me.mmigas.persistence.CratesRepository;
import me.mmigas.utils.InventoryUtil;
import me.mmigas.utils.Pair;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.List;
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
    public void onInventoryClickEvent(InventoryClickEvent event) {
        if (event.getClickedInventory() != null && event.getClickedInventory().getHolder() instanceof Gui) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClickEvent(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof Gui) {
            event.setCancelled(true);
        }
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

    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChunkLoading(ChunkLoadEvent event) {
        if (event.getChunk().getX() == 187 && event.getChunk().getZ() == 187)
            Bukkit.getLogger().info("Chunk loading " + event.getChunk().getX() + " " + event.getChunk().getZ());
        CratesRepository cratesRepository = CratesRepository.getInstance();
        List<Pair<String, Integer>> crates = cratesRepository.getFallingCratesTiersAndIds();
        for (Pair<String, Integer> pair : crates) {
            Location location =
                    cratesRepository.getCrateLocation(pair.second);
            if (isCrateInChunk(event.getChunk(), location.getX(), location.getZ())) {
                Bukkit.getLogger().info("Chunk with crate being loaded and spawning crate stand");
                SupplyCrate.getInstance().getCrateController().spawnCrateStand(pair, location);
            }
        }
    }

    @EventHandler (priority = EventPriority.MONITOR)
    public void onChunkUnloading(ChunkUnloadEvent event) {
        if (event.getChunk().getX() == 187 && event.getChunk().getZ() == 187)
            Bukkit.getLogger().info("Chunk unloading " + event.getChunk().getX() + " " + event.getChunk().getZ());
        CratesRepository cratesRepository = CratesRepository.getInstance();
        List<Pair<String, Integer>> crates = cratesRepository.getFallingCratesTiersAndIds();
        for (Pair<String, Integer> pair : crates) {
            Location location = cratesRepository.getCrateLocation(pair.second);
            if (isCrateInChunk(event.getChunk(), location.getX(), location.getZ())) {

                Bukkit.getLogger().info("Chunk with crate being unloaded and despawning crate stand");
                SupplyCrate.getInstance().getCrateController().despawnCrateStand(pair);
            }
        }
    }

    private boolean isCrateInChunk(Chunk chunk, double x, double z) {
        int chunkX = floor(x) >> 4;
        int chunkZ = floor(z) >> 4;
        return chunk.getX() == chunkX && chunk.getZ() == chunkZ;
    }

    public static int floor(double num) {
        int floor = (int) num;
        return floor == num ? floor : floor - (int) (Double.doubleToRawLongBits(num) >>> 63);
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
