package me.mmigas.listeners;

import me.mmigas.WishingWells;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class FixCanceler implements Listener {

    @EventHandler
    public void onAnvil(InventoryClickEvent event) {
        if (event.getCurrentItem() == null || event.getInventory().getType() != InventoryType.ANVIL) {
            return;
        }

        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }

        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) {
            return;
        }

        List<String> lore = itemMeta.getLore();
        if (lore != null && lore.contains(WishingWells.WELL_ID_LORE)) {
            event.setCancelled(true);

            Player player = (Player) event.getWhoClicked();
            player.sendMessage(ChatColor.RED + "Não podes modificar esse item.");
        }
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        if (!event.getMessage().startsWith("/fix")) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }

        List<String> lore = meta.getLore();
        if (lore == null || !lore.contains(WishingWells.WELL_ID_LORE)) {
            return;
        }

        String playerName = player.getName();
        boolean containsPlayer = false;
        for (String part : lore) {
            part = ChatColor.stripColor(part);
            if (part.equalsIgnoreCase(playerName)) {
                containsPlayer = true;
                break;
            }
        }

        if (!containsPlayer) {
            player.sendMessage(ChatColor.RED + "Apenas o/a jogador(a) que recebeu este item pode repará-lo.");
            event.setCancelled(true);
        }
    }
}
