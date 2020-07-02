package me.mmigas.commands.subcommands;

import me.mmigas.EventController;
import me.mmigas.commands.CMD;
import org.bukkit.command.CommandSender;

public class Help extends CMD {
    public Help(EventController eventController) {
        super(eventController);
    }

    @Override
    public void command(CommandSender sender, String... args) {
        sender.sendMessage("this is a help command.");
    }

    @Override
    public String label() {
        return "help";
    }
}
