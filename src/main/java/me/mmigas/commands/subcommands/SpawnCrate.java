package me.mmigas.commands.subcommands;

import me.mmigas.commands.CMD;
import me.mmigas.crates.CrateController;
import me.mmigas.files.LanguageManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCrate extends CMD {

    public SpawnCrate(CrateController crateController) {
        super(crateController);
    }

    @Override
    public void command(CommandSender sender, String... args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(LanguageManager.MUST_BE_PLAYER);
            return;
        }

        if (args.length != 2) {
            return;
        }

        Player player = (Player) sender;
        crateController.spawnCrate(player, args[1]);
    }

    @Override
    public String label() {
        return "spawn";
    }

    @Override
    public String usage() {
        return "/crate spawn {tier}";
    }

    @Override
    public String description() {
        return "Spawns a crate 30 blocks above the player.";
    }
}
