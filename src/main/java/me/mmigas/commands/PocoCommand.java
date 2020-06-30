package me.mmigas.commands;


import me.mmigas.EventSystem;
import me.mmigas.WishingWells;
import me.mmigas.events.LuckyPaperEventHandler;
import me.mmigas.persistence.LPRepository;
import me.mmigas.utils.Pair;
import me.mmigas.utils.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class PocoCommand implements CommandExecutor, TabCompleter {


    private EventSystem plugin;
    private LPRepository repository = LPRepository.getInstance();

    private static final String EVENT_NAME = ChatColor.GOLD + "Papel da Sorte";

    public PocoCommand(EventSystem plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings({"squid:S2676"})
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 0) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', chancesList()));
            return true;
        }

        String subCommand = args[0].toLowerCase();
        switch(subCommand) {
            case "submeter":
                submitPapers(sender);
                break;
            case "chances":
            case "chance":
                checkChances(sender, args);
                break;
            case "event":
            case "evento":
                startEvent(sender);
                break;
            case "top":
                top(sender);
                break;
            default:
                sender.sendMessage(ChatColor.RED + "Comando inválido.\nUsa " + ChatColor.GREEN + "/poço" + ChatColor.RED
                        + " para ver as probabilidades ou " + ChatColor.GREEN + "/poço papeis" + ChatColor.RED
                        + " para submeter os teus papeis para o evento " + EVENT_NAME + ChatColor.RED
                        + ".");
                break;
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> commands = Arrays.asList("submeter", "chances", "top");

        if(args.length == 0) {
            return commands;
        }

        if(args.length == 1) {
            return commands.stream()
                    .filter(c -> c.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return sender.getServer().getOnlinePlayers().stream().map(HumanEntity::getName)
                .filter(p -> p.toLowerCase().contains(args[args.length - 1].toLowerCase()))
                .collect(Collectors.toList());
    }

    private void startEvent(CommandSender sender) {
        if(!sender.hasPermission("poço.event")) {
            return;
        }

        LuckyPaperEventHandler luckyPaperEventHandler = new LuckyPaperEventHandler(plugin);
        luckyPaperEventHandler.startEvent();
    }

    private void checkChances(CommandSender sender, @NotNull String[] args) {
        if(args.length > 1) {
            Pair<Integer, Double> chances = probability(args[1]);
            sender.sendMessage(ChatColor.AQUA + args[1] + ChatColor.GREEN + " tem, neste momento, " + ChatColor.BLUE
                    + formatChance(chances.second) + ChatColor.GREEN + " de probabilidade de ganhar o evento "
                    + EVENT_NAME + ChatColor.GREEN + ". Já submeteu " + ChatColor.BLUE + chances.first + ChatColor.GREEN
                    + " papeis da tristeza.");
            return;
        }

        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Apenas jogadores podem usar este comando.");
            return;
        }

        Player player = (Player) sender;
        Pair<Integer, Double> chances = probability(player.getName());
        sender.sendMessage(ChatColor.GREEN + "Neste momento, tens " + ChatColor.BLUE
                + formatChance(chances.second) + ChatColor.GREEN + " de probabilidade de ganhar o evento "
                + EVENT_NAME + ChatColor.GREEN + ". Já submeteste " + ChatColor.BLUE + chances.first + ChatColor.GREEN
                + " papeis da tristeza.");
    }

    private void submitPapers(CommandSender sender) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Apenas jogadores podem usar este comando.");
            return;
        }

        Player player = (Player) sender;
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if(isLuckyPaper(itemInHand)) {
            int newAmount = repository.addTickets(player, itemInHand.getAmount());
            player.getInventory().setItemInMainHand(null);
            double chance = probability(player.getName()).second;
            sender.sendMessage(ChatColor.YELLOW + "Submeteste " + ChatColor.GOLD + itemInHand.getAmount()
                    + ChatColor.YELLOW + " papeis da tristeza.\n"
                    + "Já submeteste " + ChatColor.GOLD + newAmount + ChatColor.YELLOW + " no total para o evento "
                    + EVENT_NAME + ChatColor.YELLOW + ". Neste momento, tens " + ChatColor.GOLD
                    + formatChance(chance) + ChatColor.YELLOW + " de chances de ganhar " + ChatColor.AQUA
                    + "/poço chances" + ChatColor.YELLOW + ".");
        } else {
            sender.sendMessage(ChatColor.RED + "Precisas de ter os papeis da tristeza na mão para submetê-los.");
        }
    }

    private void top(CommandSender sender) {
        int totalTickets = repository.totalTickets();
        if(totalTickets == 0) {
            sender.sendMessage(ChatColor.YELLOW + "Ainda ninguém submeteu papeis da sorte para o evento " + EVENT_NAME);
            return;
        }

        StringBuilder message = new StringBuilder().append(ChatColor.YELLOW)
                .append("Jogadores com maior probabilidade de ganhar o evento ").append(EVENT_NAME)
                .append(ChatColor.YELLOW).append(":\n");

        Map<String, Integer> ticketsPerPlayer = repository.ticketsPerPlayer();
        List<Map.Entry<String, Integer>> sortedTicketsPerPlayer = new ArrayList<>(ticketsPerPlayer.entrySet());
        sortedTicketsPerPlayer.sort(Comparator.comparing(Map.Entry::getValue));
        Collections.reverse(sortedTicketsPerPlayer);

        for(int i = 0; i < sortedTicketsPerPlayer.size(); i++) {
            Map.Entry<String, Integer> playerTickets = sortedTicketsPerPlayer.get(i);
            double chances = (double) playerTickets.getValue() / totalTickets * 100;

            message.append(ChatColor.YELLOW).append(i + 1).append(". ").append(ChatColor.GOLD)
                    .append(playerTickets.getKey()).append(' ').append(ChatColor.GRAY).append(formatChance(chances))
                    .append('\n');
        }

        sender.sendMessage(message.toString());
    }

    private Pair<Integer, Double> probability(String name) {
        int totalTickets = repository.totalTickets();
        int playerTickets = repository.playerTickets(name);

        double chance = 0;
        if(playerTickets != 0) {
            chance = (double) playerTickets / totalTickets * 100d;
        }

        return new Pair<>(playerTickets, chance);
    }

    private String chancesList() {
        List<Pair<ItemStack, Double>> returns = plugin.getWishingWells().returns();


        List<Pair<ItemStack, Double>> itemChancesList = new ArrayList<>();
        for(int i = returns.size() - 1; i > 0; i--) {
            itemChancesList.add(new Pair<>(returns.get(i).first, returns.get(i).second - returns.get(i - 1).second));
        }

        itemChancesList.add(returns.get(0));

        itemChancesList.sort((pair1, pair2) -> Double.compare(pair2.second, pair1.second));

        StringBuilder builder = new StringBuilder();
        builder.append(ChatColor.YELLOW).append("Probabilidades do poço:\n");

        for(Pair<ItemStack, Double> itemChance : itemChancesList) {
            String name = nameFromItem(itemChance.first);
            builder.append(ChatColor.WHITE).append("- ").append(name).append(' ').append(ChatColor.GRAY)
                    .append(formatChance(itemChance.second)).append("\n");
        }

        builder.append(ChatColor.GRAY).append("Só o jogador que recebe um item o pode reparar, usando /fix.");
        return ChatColor.translateAlternateColorCodes('&', builder.toString());
    }

    private static String nameFromItem(ItemStack item) {
        ItemMeta itemMeta = item.getItemMeta();
        if(itemMeta == null) {
            return nameFromMaterial(item);
        } else {
            String name = "";
            if(itemMeta.hasLocalizedName()) {
                name += ChatColor.GRAY + itemMeta.getLocalizedName();
            }

            if(itemMeta.hasDisplayName()) {
                if(!name.isEmpty()) {
                    name += " ";
                }

                name += itemMeta.getDisplayName();
            }

            if(name.isEmpty() || name.equals(item.getType().name())) {
                return nameFromMaterial(item);
            }

            return name;
        }
    }

    private static String nameFromMaterial(ItemStack item) {
        String name = item.getType().name();
        return ChatColor.GRAY + StringUtil.snakeCaseToReadable(name);
    }

    private static boolean isLuckyPaper(ItemStack item) {
        if(!item.getType().equals(Material.PAPER)) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        if(meta == null) {
            return false;
        }

        List<String> lore = meta.getLore();
        return lore != null && lore.contains(WishingWells.WELL_ID_LORE);
    }

    private static String formatChance(double chance) {
        return String.format("%.2f%%", chance);
    }
}

