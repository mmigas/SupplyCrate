package me.mmigas.commands.subcommands;

import me.mmigas.EventController;
import me.mmigas.commands.CMD;
import me.mmigas.files.ConfigManager;
import me.mmigas.files.LanguageManager;
import org.bukkit.command.CommandSender;

import java.util.Map;

public class StartCrate extends CMD {
    public StartCrate(EventController eventController) {
        super(eventController);
    }

    @Override
    public void command(CommandSender sender, String... args) {
        if (!sender.hasPermission("eventsystem.startcrate")) {
            sender.sendMessage(LanguageManager.NO_PERMISSION);
            return;
        }

        for (Map.Entry<String, Integer> entry : eventController.getPlugin().getTasks().entrySet()) {
            if (entry.getKey().equals(EventController.TIMERID)) {
                LanguageManager.sendKey(sender, LanguageManager.CRATE_CICLE_RUNNING);
                return;
            }
        }

        LanguageManager.sendKey(sender, LanguageManager.CRATE_START);
        eventController.getPlugin().getTasks().put(EventController.TIMERID,
                eventController.crateTimer(eventController.getPlugin().getConfig().getInt(ConfigManager.CRATE_COOLDOWN)));
    }

    @Override
    public String label() {
        return "start";
    }
}
