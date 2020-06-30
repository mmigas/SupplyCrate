package me.mmigas.listeners;

import me.mmigas.EventSystem;
import me.mmigas.files.ConfigManager;
import me.mmigas.files.LanguageManager;
import me.mmigas.items.Items;
import me.mmigas.utils.Pair;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;


public class DropsListener implements Listener {
    private final EventSystem plugin;

    private int delay;

    private Map<UUID, Long> storedTime;
    private List<Material> acceptedItems;

    private Random random = new Random();

    private List<Pair<ItemStack, Double>> returns;

    public DropsListener(EventSystem plugin, ConfigManager manager) {
        this.plugin = plugin;

        delay = manager.getConfig().getInt("Delay");
        acceptedItems = manager.readMaterialList(ConfigManager.WISHING_WELLS_ACCEPTED);
        storedTime = new HashMap<>();
        this.returns = plugin.getWishingWells().returns();
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        if(!e.getPlayer().hasPermission("well.use")) {
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if(!e.getItemDrop().isValid()) {
                    cancel();
                    return;
                }

                if(e.getItemDrop().isOnGround()) {
                    boolean nearWell = false;
                    for(Location well : plugin.getWishingWells().wells()) {
                        if(well.getWorld().equals(e.getItemDrop().getWorld())) {
                            if(well.distance(e.getItemDrop().getLocation()) < 2) {
                                nearWell = true;
                                break;
                            }
                        }
                    }

                    if(nearWell) {
                        if(!acceptedItems.contains(e.getItemDrop().getItemStack().getType())) {
                            LanguageManager.send(e.getPlayer(), LanguageManager.WISHING_WELLS_ITEM_NOT_ACCEPTED);
                            e.getPlayer().getInventory().addItem(e.getItemDrop().getItemStack());
                            e.getItemDrop().remove();
                            cancel();
                            return;
                        }

                        if(storedTime.containsKey(e.getPlayer().getUniqueId()) && (System.currentTimeMillis() - storedTime.get(e.getPlayer().getUniqueId())) < (delay * 1000)) {
                            LanguageManager.send(e.getPlayer(), LanguageManager.WISHING_WELLS_DELAY_MESSAGE, storedTime.get(e.getPlayer().getUniqueId()));
                            e.getPlayer().getInventory().addItem(e.getItemDrop().getItemStack());
                            e.getItemDrop().remove();
                            cancel();
                            return;
                        }

                        storedTime.put(e.getPlayer().getUniqueId(), System.currentTimeMillis());

                        if(e.getItemDrop().getItemStack().getAmount() > 1) {
                            ItemStack is = e.getItemDrop().getItemStack();
                            is.setAmount(is.getAmount() - 1);
                            e.getPlayer().getInventory().addItem(is);
                        }


                        double chance = random.nextDouble() * (returns.get(returns.size() - 1).second);
                        for(Pair<ItemStack, Double> pair : returns) {
                            if(chance < pair.second) {
                                LanguageManager.send(e.getPlayer(), LanguageManager.WISHING_WELLS_CONGRATS_MESSAGE);
                                ItemStack itemStack = pair.first;
                                itemStack = new ItemStack(itemStack);

                                Items.addLore(itemStack, ChatColor.BLUE + e.getPlayer().getName());

                                e.getPlayer().getInventory().addItem(itemStack);
                                break;
                            } else {
                                chance -= pair.second;
                            }
                        }

                        e.getItemDrop().remove();
                    }
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 20, 10);
    }
}
