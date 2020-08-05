package me.mmigas.commands;

import me.mmigas.crates.CrateController;
import org.bukkit.command.CommandSender;

public abstract class CMD {
    public final CrateController crateController;

    public CMD(CrateController crateController) {
        this.crateController = crateController;
    }

    public abstract void command(CommandSender sender, String... args);

    public abstract String label();

    public abstract String usage();

    public abstract String description();

    public String permission() {
        return CrateCommand.PERMISSION_PREFIX + label();
    }

    public boolean isEnabled() {
        return true;
    }
}
