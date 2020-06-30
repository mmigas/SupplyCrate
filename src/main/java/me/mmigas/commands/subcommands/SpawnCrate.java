package me.mmigas.commands.subcommands;

import me.mmigas.EventController;
import me.mmigas.files.LanguageManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCrate {

    private final EventController controller;

    public SpawnCrate(EventController controller) {
        this.controller = controller;
    }

    public void command(CommandSender sender) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(LanguageManager.MUST_BE_PLAYER);
            return;
        }

        if(!sender.hasPermission("eventsystem.spawncrate")){
            sender.sendMessage(LanguageManager.NO_PERMISSION);
            return;
        }

        Player player = (Player) sender;
        controller.spawnCrate(player);
    }
}
