package me.mmigas.commands.subcommands;

import me.mmigas.EventController;
import me.mmigas.files.ConfigManager;
import me.mmigas.files.LanguageManager;
import org.bukkit.command.CommandSender;

import java.util.Map;

public class StartCrate {

    private final EventController controller;

    public StartCrate(EventController controller) {
        this.controller = controller;
    }

    public void command(CommandSender sender) {
        if (!sender.hasPermission("eventsystem.spawncrate")) {
            sender.sendMessage(LanguageManager.NO_PERMISSION);
            return;
        }

        for (Map.Entry<String, Integer> entry : controller.getPlugin().getTasks().entrySet()) {
            if (entry.getKey().equals(EventController.TIMERID)) {
                LanguageManager.send(sender, LanguageManager.CRATE_CICLE_RUNNING);
                return;
            }
        }

        LanguageManager.send(sender, LanguageManager.CRATE_START);
        controller.getPlugin().getTasks().put(EventController.TIMERID,
                controller.crateTimer(controller.getPlugin().getConfig().getInt(ConfigManager.CRATE_COOLDOWN)));
    }
}
