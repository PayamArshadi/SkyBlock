package de.qgel.skyblock;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.qgel.skyblock.skyblock;

public class skyDevCommand
implements CommandExecutor {
    private final skyblock plugin;

    public skyDevCommand(skyblock plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
        if (!(sender instanceof Player) || !sender.isOp()) {
            return false;
        }
        sender.sendMessage(String.valueOf(this.plugin.getLastIsland().x) + " / " + this.plugin.getLastIsland().z);
        return true;
    }
}

