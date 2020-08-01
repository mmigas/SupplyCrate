package me.mmigas.commands.subcommands;

import me.mmigas.commands.CMD;
import me.mmigas.crates.CrateController;
import me.mmigas.crates.CrateTier;
import me.mmigas.files.LanguageManager;
import me.mmigas.gui.Gui;
import me.mmigas.gui.Item;
import me.mmigas.utils.Pair;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Info extends CMD {

    public Info(CrateController crateController) {
        super(crateController);
    }

    @Override
    public void command(CommandSender sender, String... args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(LanguageManager.MUST_BE_PLAYER);
            return;
        }

        if (args.length != 2) {
            //TODO: Manage all the wrong usages.
            return;
        }

        String tierId = args[1];
        CrateTier tier = crateController.getCrateTierByIdentifier(tierId);
        if (tier == null) {
            LanguageManager.sendKey(sender, LanguageManager.INVALID_CRATE_TIER);
            return;
        }
        Gui gui = setupGui(tier);
        Player player = (Player) sender;
        player.openInventory(gui.getInventory());
    }

    private Gui setupGui(CrateTier tier) {
        int size = (tier.getRewards().size() / 9 + 1) * 9 + 18;
        String title = tier.getIdentifier();

        Gui gui = new Gui(title, size);
        fillInventory(tier, gui);
        return gui;
    }

    private void fillInventory(CrateTier tier, Gui gui) {
        Item item;
        for (int i = 0; i < tier.getRewards().size(); i++) {
            Pair<ItemStack, Double> pair = tier.getRewards().get(i);
            ItemStack itemStack = pair.first;
            item = new Item(itemStack);
            gui.setItem(i, item);
        }

        int rewardsSize = (tier.getRewards().size() / 9 + 1) * 9;
        int i = rewardsSize;
        for (; i < rewardsSize + 9; i++) {
            item = new Item(Material.BLACK_STAINED_GLASS_PANE);
            gui.setItem(i, item);
        }

        item = new Item(Material.GREEN_WOOL, ChatColor.translateAlternateColorCodes('&', "&cPercentage: " + tier.getPercentage()));
        gui.setItem(rewardsSize + 13, item);
    }

    @Override
    public String label() {
        return "info";
    }

    @Override
    public String usage() {
        return "/crate info {tier}";
    }

    @Override
    public String description() {
        return "Opens a gui with the rewards and all the information of the Crate's tier.";
    }
}
