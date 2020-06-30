package me.mmigas.commands.subcommands;

import me.mmigas.services.CleanupCratesService;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.logging.Level;

public class CleanupCrates {

    public void cleanupCrates(CommandSender sender, String... args) {
        CleanupCratesService service = new CleanupCratesService();
        if (args.length == 1) {
            service.cleanupCrates(sender, 30);
        } else {
            try {
                int maxDays = Integer.parseInt(args[1]);
                service.cleanupCrates(sender, maxDays);
            } catch (NumberFormatException e) {
                sender.sendMessage("Usage: /crate cleanup (max days)");
            } catch (Exception e) {
                sender.sendMessage("Error: " + e.getMessage());
                Bukkit.getLogger().log(Level.WARNING, "Error while cleaning up old crates", e);
            }
        }
    }
}
