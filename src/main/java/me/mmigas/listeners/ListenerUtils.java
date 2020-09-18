package me.mmigas.listeners;

import me.mmigas.crates.CrateEvent;
import me.mmigas.persistence.CratesRepository;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.util.NumberConversions;

import java.util.logging.Level;

public class ListenerUtils {

    private ListenerUtils() {

    }

    static boolean isCrateInChunk(Chunk chunk, double x, double z) {
        int chunkX = NumberConversions.floor(x) >> 4;
        int chunkZ = NumberConversions.floor(z) >> 4;
        return chunk.getX() == chunkX && chunk.getZ() == chunkZ;
    }

    static int crateIDFromChest(Chest chest) {
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

    static boolean isBlockACrate(Block block) {
        if (block.getType() != Material.CHEST) {
            return false;
        }
        Chest chest = (Chest) block.getState();
        int crateID = ListenerUtils.crateIDFromChest(chest);
        return crateID != -1;
    }

}
