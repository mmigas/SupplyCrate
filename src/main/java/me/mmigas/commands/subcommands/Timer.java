package me.mmigas.commands.subcommands;

import me.mmigas.crates.CrateController;
import me.mmigas.commands.CMD;
import me.mmigas.files.ConfigManager;
import me.mmigas.files.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class Timer extends CMD {
    public Timer(CrateController crateController) {
        super(crateController);
    }

    @Override
    public void command(CommandSender sender, String... args) {
        if (args.length != 2) {
            LanguageManager.sendKey(sender, LanguageManager.WRONG_COMMAND_USAGE);
            return;
        }
        try {
            long cooldown = Long.parseLong(args[1]);
            crateController.changeCooldown(cooldown);
            LanguageManager.sendKey(sender, LanguageManager.TIMER_COMMAND_SUCCESSFULLY, cooldown);
            ConfigManager.getInstance().saveTimer(cooldown);
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
