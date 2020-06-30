package me.mmigas.commands.subcommands;

import me.mmigas.EventController;
import me.mmigas.files.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.Map;

public class StopCrate {
    private final EventController controller;

    public StopCrate(EventController controller) {
        this.controller = controller;
    }

    public void command(CommandSender sender) {
        if (!sender.hasPermission("eventsystem.stopcrate")) {
            sender.sendMessage(LanguageManager.NO_PERMISSION);
            return;
        }

        for (Map.Entry<String, Integer> entry : controller.getPlugin().getTasks().entrySet()) {
            if (entry.getKey().equals(EventController.TIMERID)) {
                Bukkit.getScheduler().cancelTask(entry.getValue());
                controller.getPlugin().getTasks().remove(entry.getKey());
                LanguageManager.send(sender, LanguageManager.CRATE_STOP);
                return;
            }
        }
        LanguageManager.send(sender, LanguageManager.CRATE_CICLE_NOT_RUNNING);
    }
}
