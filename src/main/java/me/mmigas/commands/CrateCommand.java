package me.mmigas.commands;

import me.mmigas.SupplyCrate;
import me.mmigas.commands.subcommands.*;
import me.mmigas.crates.CrateController;
import me.mmigas.files.LanguageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CrateCommand implements CommandExecutor, TabCompleter {

    private final List<CMD> commands = new ArrayList<>();

    public static final String PERMISSION_PREFIX = "supplycrate.";

    private final CrateController crateController;

    public CrateCommand(CrateController controller) {
        this.crateController = controller;
        commands.add(new Help(controller, commands));
        commands.add(new SpawnCrate(controller));
        commands.add(new StartCrate(controller));
        commands.add(new StopCrate(controller));
        commands.add(new CleanupCrates(controller));
        commands.add(new Timer(controller));
        commands.add(new Info(controller));
        commands.add(new Buy(controller));
        commands.add(new MenuCommand(controller));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            commands.get(0).command(sender, args);
            return true;
        }

        for (CMD cmd : commands) {
            if (cmd.label().equalsIgnoreCase(args[0])) {
                if (!sender.hasPermission(cmd.permission()) || !cmd.isEnabled()) {
                    break;
                }
                cmd.command(sender, args);
                return true;
            }
        }

        LanguageManager.sendMessage(sender, "Command not found! Use /crate help for all the commands usages.");

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return getCommandsWithPermission(sender);
        } else if (args.length == 2 && (args[0].equals("info") || args[0].equals("spawn") || (args[0].equals("buy") && SupplyCrate.getEconomy() != null))) {
            return crateController.getIdentifiers();
        }
        return new ArrayList<>();
    }

    private List<String> getCommandsWithPermission(CommandSender sender) {
        List<String> cmds = new ArrayList<>();
        for (CMD cmd : commands) {
            if (sender.hasPermission(cmd.permission())) {
                cmds.add(cmd.label());
            }
        }
        return cmds;
    }
}
