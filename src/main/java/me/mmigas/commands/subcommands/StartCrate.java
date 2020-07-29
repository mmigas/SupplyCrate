package me.mmigas.commands.subcommands;

import me.mmigas.crates.CrateController;
import me.mmigas.commands.CMD;
import me.mmigas.files.LanguageManager;
import org.bukkit.command.CommandSender;

public class StartCrate extends CMD {
    public StartCrate(CrateController crateController) {
        super(crateController);
    }

    @Override
    public void command(CommandSender sender, String... args) {
        try {
            if (crateController.startCrateSpawningTask() == -1) {
                LanguageManager.sendKey(sender, LanguageManager.CRATE_CYCLE_RUNNING);
            } else {
                LanguageManager.sendKey(sender, LanguageManager.CRATE_START);
            }
        } catch (NumberFormatException e) {
            LanguageManager.sendMessage(sender, "&dCooldown time invalid. Please specify a positive number.");
        }
    }

    @Override
    public String label() {
        return "start";
    }

    @Override
    public String usage() {
        return "/crate start";
    }

    @Override
    public String description() {
        return "Starts the automatic spawning of the crates.";
    }


}
