package de.qgel.skyblock;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class skyHelpCommand
implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
        if (!(sender instanceof Player)) {
            return false;
        }
        if (split.length == 0) {
        	sender.sendMessage("");
            sender.sendMessage("skyblock is a plugin that gives you a (very) small map in the sky. Survive the best you can there!");
            sender.sendMessage("Commands:");
            sender.sendMessage("/island or /is : To get start or get teleported you to your skyblock island.");
            sender.sendMessage("/skyhelp challenges: To see the challenges.");
            if (sender.isOp()) {
                sender.sendMessage("/removeIsland <playername> : remove the Island of a given player");
                sender.sendMessage("/clearMapCache : remove the Cached Island used for fast map generation");
            }
            sender.sendMessage("/skyhelp : Print this help message");
        } else if (String.join(" ", split) == "c" | String.join(" ", split) == "challenges") {
        	sender.sendMessage("");
            sender.sendMessage("These are the challenges:");
            sender.sendMessage("1.Build a Cobble Stone generator.");
            sender.sendMessage("2.Build a house.");
            sender.sendMessage("3.Expand the island.");
            sender.sendMessage("4.Make a reed farm.");
            sender.sendMessage("5.Make a wheat farm.");
            sender.sendMessage("6.Make a giant red mushroom. ");
            sender.sendMessage("7.Build a bed.");
            sender.sendMessage("8.Make 40 stone brick's. ");
            sender.sendMessage("9.Make atleast 20 torches.");
            sender.sendMessage("10.Make an infinite water source. ");
            sender.sendMessage("11.Build a furnace.");
            sender.sendMessage("12.Make a small lake.");
            sender.sendMessage("13.Make a platform 24 blocks away from the island, for mobs to spawn. ");
            sender.sendMessage("14.Make 10 cactus green dye. ");
            sender.sendMessage("15.Make 10 mushroom stew. ");
            sender.sendMessage("16.Build 10 bookcases.");
            sender.sendMessage("17.Make 10 bread.");
            //sender.sendMessage("SMP challanges:");
            //sender.sendMessage("18.Connect to another Island");
        }
        return true;
    }
}

