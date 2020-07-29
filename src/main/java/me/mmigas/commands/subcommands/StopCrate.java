package me.mmigas.commands.subcommands;

import me.mmigas.crates.CrateController;
import me.mmigas.commands.CMD;
import me.mmigas.files.LanguageManager;
import org.bukkit.command.CommandSender;

public class StopCrate extends CMD {
    public StopCrate(CrateController crateController) {
        super(crateController);
    }

    @Override
    public void command(CommandSender sender, String... args) {
        if (crateController.stopCrateSpawningTask()) {
            LanguageManager.sendKey(sender, LanguageManager.CRATE_STOP);
        } else {
            LanguageManager.sendKey(sender, LanguageManager.CRATE_CYCLE_NOT_RUNNING);
        }
    }


    @Override
    public String label() {
        return "stop";
    }

    @Override
    public String usage() {
        return "/crate stop";
    }

    @Override
    public String description() {
        return "Stops the automatic spawning of the crates.";
    }
}
