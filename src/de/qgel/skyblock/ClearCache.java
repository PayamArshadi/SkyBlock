package de.qgel.skyblock;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.qgel.skyblock.skyblock;

public class ClearCache
implements CommandExecutor {
    private final skyblock plugin;

    public ClearCache(skyblock plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
        if (!(sender instanceof Player) || !sender.isOp()) {
            return false;
        }
        
        final File mainMap = new File(plugin.getDataFolder()+ "/map");
        
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
        	public void run() {
        		System.gc();
                deleteFolder(mainMap);
        	}
        },30L);
        	
        return true;
    }

    public void deleteFolder(File file) {
    	if(file.isDirectory()) {
    		File[] files = file.listFiles();
    		if (files != null){
    			for (File child : files) {
    				deleteFolder(child);
    			}
    			
    		}
    	}
    	boolean result = file.delete();
    	
    	if (!result) {
    		System.out.println("Failed to delete : " + file.getAbsolutePath());
    	}
    }
}

