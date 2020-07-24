package me.mmigas.commands.subcommands;

import me.mmigas.EventController;
import me.mmigas.commands.CMD;
import me.mmigas.files.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.List;

public class Help extends CMD {
    private final List<CMD> commands;

    public Help(EventController eventController, List<CMD> commands) {
        super(eventController);
        this.commands = commands;
    }

    @Override
    public void command(CommandSender sender, String... args) {
        LanguageManager.sendMessage(sender, "*---------------------------------------------------*");
        StringBuilder builder = new StringBuilder();
        LanguageManager.sendMessage(sender, "");
        for (CMD command : commands) {
            builder.setLength(0);
            builder.append("&b").append(command.usage()).append("&r&b: &c").append(command.description());
            LanguageManager.sendMessage(sender, builder.toString());
            LanguageManager.sendMessage(sender, "");
        }
        LanguageManager.sendMessage(sender, "*---------------------------------------------------*");
    }

    @Override
    public String label() {
        return "help";
    }

    @Override
    public String usage() {
        return "/crate or /crate help";
    }

    @Override
    public String description() {
        return "Helper command with the usages and descriptions of the other commands.";
    }
}
