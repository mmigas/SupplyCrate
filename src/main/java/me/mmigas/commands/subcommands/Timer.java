package me.mmigas.commands.subcommands;

import me.mmigas.crates.CrateController;
import me.mmigas.commands.CMD;
import me.mmigas.files.LanguageManager;
import org.bukkit.command.CommandSender;

public class Timer extends CMD {
    public Timer(CrateController crateController) {
        super(crateController);
    }

    @Override
    public void command(CommandSender sender, String... args) {
        try {
            int cooldown = Integer.parseInt(args[1]);
            crateController.stopCrateSpawningTask();
            crateController.startCrateSpawningTask(cooldown);
        } catch (NumberFormatException e) {
            LanguageManager.sendMessage(sender, "&dCooldown time invalid. Please specify a positive number.");
        }
    }

    @Override
    public String label() {
        return "timer";
    }

    @Override
    public String usage() {
        return "/crate timer {delay}";
    }

    @Override
    public String description() {
        return "Changes the delay of the automatic crate spawning in minutes.";
    }
}
