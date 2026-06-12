package de.qgel.skyblock;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class islandCommand
implements CommandExecutor {
    private final skyblock plugin;

    public islandCommand(skyblock plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player)sender;
        if (this.plugin.hasIsland(player.getName())) {
            if (player.getWorld().getEnvironment().getId() == 0) {
                player.sendMessage("Sending you to your island!");
                this.plugin.teleportHome((Player)sender);
                return true;
            }
            player.sendMessage("Can't tphome in the nether, sorry");
            return true;
        }
        CreateIslandCommand createIslandCommand = new CreateIslandCommand(plugin);
		createIslandCommand.createIsland(player);
        sender.sendMessage("You don't have an Island, Creating new one!");
        return true;
    }
}

