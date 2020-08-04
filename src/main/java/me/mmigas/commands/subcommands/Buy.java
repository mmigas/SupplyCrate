package me.mmigas.commands.subcommands;

import me.mmigas.SupplyCrate;
import me.mmigas.commands.CMD;
import me.mmigas.crates.CrateController;
import me.mmigas.crates.CrateTier;
import me.mmigas.files.LanguageManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Buy extends CMD {

    public Buy(CrateController crateController) {
        super(crateController);
    }

    @Override
    public void command(CommandSender sender, String... args) {
        if (!(sender instanceof Player)) {
            LanguageManager.sendKey(sender, LanguageManager.MUST_BE_PLAYER);
            return;
        }

        if (args.length != 2) {
            LanguageManager.sendKey(sender, LanguageManager.WRONG_COMMAND_USAGE);
            return;
        }

        CrateTier crateTier = crateController.getCrateTierByIdentifier(args[1]);
        if (crateTier == null) {

            return;
        }
        Economy economy = SupplyCrate.getEconomy();
        Player player = ((Player) sender);
        if (economy.getBalance(player) > crateTier.getPrice()) {
            SupplyCrate.getEconomy().withdrawPlayer(player, crateTier.getPrice());
            crateController.startEventFromPlayer(player, crateTier.getIdentifier());
        } else {
            LanguageManager.sendKey(sender, LanguageManager.NOT_ENOUGH_MONEY);
        }
    }

    @Override
    public String label() {
        return "buy";
    }

    @Override
    public String usage() {
        return "/crate buy {tier}";
    }

    @Override
    public String description() {
        return "Buy's a crate in the players location.";
    }
}
