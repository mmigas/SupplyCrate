package me.mmigas.commands;

import me.mmigas.EventController;
import me.mmigas.commands.subcommands.*;
import me.mmigas.files.LanguageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CrateCommand implements CommandExecutor {

    private final List<CMD> commands = new ArrayList<>();

    public CrateCommand(EventController controller) {
        commands.add(new Help(controller, commands));
        commands.add(new SpawnCrate(controller));
        commands.add(new StartCrate(controller));
        commands.add(new StopCrate(controller));
        commands.add(new CleanupCrates(controller));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            commands.get(0).command(sender, args);
            return true;
        }

        for (CMD cmd : commands) {
            if (cmd.label().equalsIgnoreCase(args[0])) {
                cmd.command(sender, args);
                return true;
            }
        }

        LanguageManager.sendMessage(sender, "Command not found! Use /crate help for all the commands usages.");

        return true;
    }
}
