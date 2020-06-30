package me.mmigas.commands;

import me.mmigas.EventSystem;
import me.mmigas.files.ConfigManager;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class WellCommand implements CommandExecutor, TabCompleter {

    private EventSystem plugin;

    public WellCommand(EventSystem plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("squid:S3516")
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 0) {
            sender.sendMessage("--==Accepted commands==--");
            sender.sendMessage("/well create <name>");
            sender.sendMessage("/well destroy <name>");
            sender.sendMessage("/well info <name>");
            sender.sendMessage("/well list");
            return true;
        }

        if(!sender.hasPermission("well.command")) {
            sender.sendMessage(org.bukkit.ChatColor.RED + "You do not have permission to use this command");
            return true;
        }

        if(args[0].equalsIgnoreCase("create")) {
            if(args.length < 2) {
                sender.sendMessage("--==Accepted commands==--");
                sender.sendMessage("/well create <name>");
                return true;
            }

            plugin.getConfig().set(ConfigManager.WISHING_WELLS_WELL_LOCATION + "." + args[1], ((Player) sender).getLocation());
            plugin.saveConfig();
            plugin.getWishingWells().wells().add(((Player) sender).getLocation());
            sender.sendMessage("Well created!");
        } else if(args[0].equalsIgnoreCase("destroy")) {
            if(args.length < 2) {
                sender.sendMessage("--==Accepted commands==--");
                sender.sendMessage("/well destroy <name>");
                return true;
            }

            Location temp = (Location) plugin.getConfig().get(ConfigManager.WISHING_WELLS_WELL_LOCATION + "." + args[1]);
            plugin.getConfig().set(ConfigManager.WISHING_WELLS_WELL_LOCATION + "." + args[1], null);
            plugin.saveConfig();
            plugin.getWishingWells().wells().remove(temp);
            sender.sendMessage("Well destroyed!");
        } else if(args[0].equalsIgnoreCase("list")) {
            sender.sendMessage("--== Wells ==--");
            if(!plugin.getConfig().contains(ConfigManager.WISHING_WELLS_WELL_LOCATION)) {
                sender.sendMessage("No wells have been created");
                return true;
            }

            for(String keys : plugin.getConfig().getConfigurationSection(ConfigManager.WISHING_WELLS_WELL_LOCATION).getKeys(false)) {
                Location location = (Location) plugin.getConfig().get(ConfigManager.WISHING_WELLS_WELL_LOCATION + "." + keys);
                sender.sendMessage(keys + ": X:" + location.getBlockX() + " Y:" + location.getBlockY() + " Z:" + location.getBlockZ());
            }
        } else {
            sender.sendMessage("--==Accepted commands==--");
            sender.sendMessage("/well create <name>");
            sender.sendMessage("/well destroy <name>");
            sender.sendMessage("/well list");
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> tabs = new ArrayList<>();
        if(!sender.hasPermission("well.command")) {
            return tabs;
        }

        if(args.length == 1) {
            addApplicable("create", args[0], tabs);
            addApplicable("destroy", args[0], tabs);
            addApplicable("list", args[0], tabs);
        }

        if(args.length == 2 && plugin.getConfig().contains(ConfigManager.WISHING_WELLS_WELL_LOCATION)) {
            for(String key : plugin.getConfig().getConfigurationSection(ConfigManager.WISHING_WELLS_WELL_LOCATION).getKeys(false)) {
                addApplicable(key, args[1], tabs);
            }
        }

        return tabs;
    }

    private void addApplicable(String testing, String arg, List<String> array) {
        if(testing.toLowerCase().startsWith(arg.toLowerCase())) {
            array.add(testing);
        }
    }
}
