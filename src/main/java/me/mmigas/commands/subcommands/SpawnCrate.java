package me.mmigas.commands.subcommands;

import me.mmigas.EventController;
import me.mmigas.commands.CMD;
import me.mmigas.files.LanguageManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCrate extends CMD {

    public SpawnCrate(EventController eventController) {
        super(eventController);
    }

    @Override
    public void command(CommandSender sender, String... args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(LanguageManager.MUST_BE_PLAYER);
            return;
        }

        if (!sender.hasPermission("eventsystem.spawncrate")) {
            sender.sendMessage(LanguageManager.NO_PERMISSION);
            return;
        }

        Player player = (Player) sender;
        eventController.spawnCrate(player);
    }

    @Override
    public String label() {
        return "spawn";
    }

    @Override
    public String usage() {
        return "/crate spawn";
    }

    @Override
    public String description() {
        return "Spawns a crate 30 blocks above the player.";
    }
}
