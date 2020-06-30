package me.mmigas.events;

import me.mmigas.EventSystem;
import me.mmigas.persistence.LPRepository;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.security.SecureRandom;
import java.util.*;

public class LuckyPaperEventHandler {

    private final EventSystem plugin;
    private final LPRepository repository = LPRepository.getInstance();

    private static final long DELAY = 300; // Seconds
    private static final int MAX_WINNERS = 3;

    private static final String HEADER = ChatColor.YELLOW + "» Evento " + ChatColor.GOLD + "Papel da Sorte" +
            ChatColor.YELLOW;

    private static final long TPS = 20;
    private static final Random random = new SecureRandom();

    public LuckyPaperEventHandler(EventSystem plugin) {
        this.plugin = plugin;
    }

    public void startEvent() {
        announceBig("5 minutos", 300);
        announceBig("2 minutos", 120);
        announce("1 minuto", 60);
        announce("30 segundos", 30);

        new BukkitRunnable() {
            @Override
            public void run() {
                start();
            }
        }.runTaskLaterAsynchronously(plugin, DELAY * TPS);
    }

    private void announceBig(String timeLeft, int secondsLeft) {
        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getServer().broadcastMessage(
                        ChatColor.YELLOW + " \n \n»»» Evento " + ChatColor.GOLD + "Papel da Sorte " + ChatColor.YELLOW
                                + "«««\n\nVai ser escolhido o vencedor dentro de " + ChatColor.AQUA + timeLeft
                                + ChatColor.YELLOW + "!\nUsa " + ChatColor.AQUA + "/poço chance " + ChatColor.YELLOW
                                + "para veres a probabilidade de ganhares.\n \n");
            }
        }.runTaskLater(plugin, (DELAY - secondsLeft) * TPS);
    }

    private void announce(String timeLeft, int secondsLeft) {
        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getServer().broadcastMessage(HEADER + "\nVai ser escolhido o vencedor dentro de " +
                        ChatColor.AQUA + timeLeft + ChatColor.YELLOW + ".");
            }
        }.runTaskLater(plugin, (DELAY - secondsLeft) * TPS);
    }

    private void start() {
        int totalTickets = repository.totalTickets();
        if (totalTickets == 0) {
            plugin.getServer().broadcastMessage(HEADER + "\nNinguém ganhou.");
            return;
        }

        Map<String, Integer> ticketsPerPlayer = repository.ticketsPerPlayer();
        int totalTicketsCopy = totalTickets;
        for (int i = 0; i < MAX_WINNERS; i++) {
            int value = random.nextInt(totalTicketsCopy);
            int sum = 0;

            List<Map.Entry<String, Integer>> sortedTicketsPerPlayer = new ArrayList<>(ticketsPerPlayer.entrySet());
            sortedTicketsPerPlayer.sort(Map.Entry.comparingByValue());
            Collections.reverse(sortedTicketsPerPlayer);

            for (Map.Entry<String, Integer> player : sortedTicketsPerPlayer) {
                int playerTickets = player.getValue();
                sum += playerTickets;

                if (value < sum) {
                    String playerName = player.getKey();
                    announceWinner(playerName, i, totalTickets, playerTickets);
                    totalTicketsCopy -= playerTickets;
                    ticketsPerPlayer.remove(playerName);
                    break;
                }
            }

            if (totalTicketsCopy == 0) {
                break;
            }
        }

        repository.clear();
    }

    private void announceWinner(String name, int place, int totalTickets, int playerTickets) {
        double chance = (double) playerTickets / totalTickets * 100;

        new BukkitRunnable() {
            @Override
            public void run() {
                String message = HEADER + " » " + ChatColor.AQUA + name + ChatColor.YELLOW + " ficou em " +
                        ChatColor.GOLD + (place + 1) + "º " + ChatColor.YELLOW + "lugar!";
                if (place == 0) {
                    message += "! Tinha " + ChatColor.GOLD + String.format("%.2f%%", chance) + ChatColor.YELLOW +
                            " de probabilidade de ganhar.";
                } else {
                    message += ChatColor.GOLD + String.format(" %.2f%%", chance);
                }

                plugin.getServer().broadcastMessage(message);
            }
        }.runTaskLater(plugin, place * TPS * 5);
    }
}
