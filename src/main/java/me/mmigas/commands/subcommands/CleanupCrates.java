package me.mmigas.commands.subcommands;

import me.mmigas.commands.CMD;
import me.mmigas.crates.CrateController;
import me.mmigas.files.LanguageManager;
import me.mmigas.services.CleanupCratesService;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.logging.Level;

public class CleanupCrates extends CMD {

    public CleanupCrates(CrateController crateController) {
        super(crateController);
    }

    @Override
    public void command(CommandSender sender, String... args) {
        CleanupCratesService service = new CleanupCratesService();
        if (args.length == 1) {
            service.cleanupCrates(sender, 30);
        } else {
            try {
                int maxDays = Integer.parseInt(args[1]);
                service.cleanupCrates(sender, maxDays);
            } catch (NumberFormatException e) {
                LanguageManager.sendKey(sender, LanguageManager.WRONG_COMMAND_USAGE);
            } catch (Exception e) {
                sender.sendMessage("Error: " + e.getMessage());
                Bukkit.getLogger().log(Level.WARNING, "Error while cleaning up old crates", e);
            }
        }
    }

    @Override
    public String label() {
        return "cleanup";
    }

    @Override
    public String usage() {
        return "/crate cleanup {days}";
    }

    @Override
    public String description() {
        return "Cleans and destries all the crates that are older then inserted days.";
    }
}
