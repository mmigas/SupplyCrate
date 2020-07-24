package me.mmigas.commands;

import me.mmigas.EventController;
import org.bukkit.command.CommandSender;

public abstract class CMD {
    public final EventController eventController;

    public CMD(EventController eventController) {
        this.eventController = eventController;
    }

    public abstract void command(CommandSender sender, String... args);

    public abstract String label();

    public abstract String usage();

    public abstract String description();
}
