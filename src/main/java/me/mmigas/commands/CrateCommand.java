package me.mmigas.commands;

import me.mmigas.EventController;
import me.mmigas.commands.subcommands.CleanupCrates;
import me.mmigas.commands.subcommands.SpawnCrate;
import me.mmigas.commands.subcommands.StartCrate;
import me.mmigas.commands.subcommands.StopCrate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class CrateCommand implements CommandExecutor {

    private final EventController controller;

    public CrateCommand(EventController controller) {
        this.controller = controller;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {

        } else if (args[0].equalsIgnoreCase("spawncrate") || args[0].equalsIgnoreCase("spawn")) {
            new SpawnCrate(controller).command(sender);
        } else if (args[0].equalsIgnoreCase("startcrate") || args[0].equalsIgnoreCase("start")) {
            new StartCrate(controller).command(sender);
        } else if (args[0].equalsIgnoreCase("stopcrate") || args[0].equalsIgnoreCase("stop")) {
            new StopCrate(controller).command(sender);
        } else if (args[0].equalsIgnoreCase("cleanup")) {
            new CleanupCrates().cleanupCrates(sender, args);
        }

        return true;
    }
}
