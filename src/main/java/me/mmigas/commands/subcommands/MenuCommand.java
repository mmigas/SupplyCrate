package me.mmigas.commands.subcommands;

import me.mmigas.commands.CMD;
import me.mmigas.crates.CrateController;
import me.mmigas.crates.menu.Menu;
import me.mmigas.files.LanguageManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MenuCommand extends CMD {
    public MenuCommand(CrateController crateController) {
        super(crateController);
    }

    @Override
    public void command(CommandSender sender, String... args) {
        if (!(sender instanceof Player)) {
            LanguageManager.sendKey(sender, LanguageManager.MUST_BE_PLAYER);
            return;
        }

        Player player = (Player) sender;

        new Menu(player);
    }

    @Override
    public String label() {
        return "menu";
    }

    @Override
    public String usage() {
        return "/crate menu";
    }

    @Override
    public String description() {
        return "Gui menu for change all the plugins settings.";
    }
}
