package me.mmigas.commands.subcommands;

import me.mmigas.EventController;
import me.mmigas.commands.CMD;
import me.mmigas.files.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.Map;

public class StopCrate extends CMD {
    public StopCrate(EventController eventController) {
        super(eventController);
    }

    @Override
    public void command(CommandSender sender, String... args) {

        if (!sender.hasPermission("eventsystem.stopcrate")) {
            sender.sendMessage(LanguageManager.NO_PERMISSION);
            return;
        }

        for (Map.Entry<String, Integer> entry : eventController.getPlugin().getTasks().entrySet()) {
            if (entry.getKey().equals(EventController.TIMERID)) {
                Bukkit.getScheduler().cancelTask(entry.getValue());
                eventController.getPlugin().getTasks().remove(entry.getKey());
                LanguageManager.sendKey(sender, LanguageManager.CRATE_STOP);
                return;
            }
        }
        LanguageManager.sendKey(sender, LanguageManager.CRATE_CICLE_NOT_RUNNING);
    }

    @Override
    public String label() {
        return "stop";
    }
}
