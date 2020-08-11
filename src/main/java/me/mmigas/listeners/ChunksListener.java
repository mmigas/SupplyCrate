package me.mmigas.listeners;

import me.mmigas.SupplyCrate;
import me.mmigas.persistence.CratesRepository;
import me.mmigas.utils.Pair;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.List;

public class ChunksListener implements Listener {
    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChunkLoading(ChunkLoadEvent event) {
        CratesRepository cratesRepository = CratesRepository.getInstance();
        List<Pair<String, Integer>> crates = cratesRepository.getFallingCratesTiersAndIDs();
        for (Pair<String, Integer> pair : crates) {
            Location location = cratesRepository.getCrateLocation(pair.second);
            if (ListenerUtils.isCrateInChunk(event.getChunk(), location.getX(), location.getZ())) {
                SupplyCrate.getInstance().getCrateController().spawnCrateStand(pair);
            }
        }
    }

    @EventHandler (priority = EventPriority.MONITOR)
    public void onChunkUnloading(ChunkUnloadEvent event) {
        CratesRepository cratesRepository = CratesRepository.getInstance();
        List<Pair<String, Integer>> crates = cratesRepository.getFallingCratesTiersAndIDs();
        for (Pair<String, Integer> pair : crates) {
            Location location = cratesRepository.getCrateLocation(pair.second);
            if (ListenerUtils.isCrateInChunk(event.getChunk(), location.getX(), location.getZ())) {
                SupplyCrate.getInstance().getCrateController().despawnCrate(pair);
            }
        }
    }
}
