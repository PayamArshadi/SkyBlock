/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package de.qgel.skyblock;

import net.minecraft.server.Chunk;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.omg.CORBA.PUBLIC_MEMBER;

import com.onarandombox.MultiverseCore.MVWorld;
import com.onarandombox.MultiverseCore.MultiverseCore;

import de.qgel.skyblock.skyblock;

public class RemoveIslandCommand
implements CommandExecutor {
    private final skyblock plugin;

    public RemoveIslandCommand(skyblock plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
        if (!(sender instanceof Player) || !sender.isOp()) {
            return false;
        }
        if (split.length == 1) {
            String playerName = split[0];
            if (this.plugin.hasIsland(playerName)) {
                this.plugin.deleteIsland(playerName, ((Player)sender).getWorld());

            	MultiverseCore mv = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");	

            	MVWorld mvWorld = mv.getWorldManager().getMVWorld("skyblock/"+playerName);
            	
                Bukkit.getServer().getWorld("world").loadChunk(-13, 44);
                World world = Bukkit.getServer().getWorld("skyblock/"+playerName);
                
                if (world != null) {
                	for (Player p : world.getPlayers()) {
                		Location spawn = world.getSpawnLocation();
                		double x = spawn.getX();
                		double y = spawn.getY();
                		double z = spawn.getZ();
                		float yaw = spawn.getYaw();
                		float pitch = spawn.getPitch();
                        p.teleport(new Location(Bukkit.getServer().getWorld("world"), x, y, z, yaw, pitch));
                		
                	}
                }
            	

                mv.removeWorldFromConfig("skyblock/"+playerName);
                mv.removeWorldFromList("skyblock/"+playerName);
                
                for (org.bukkit.Chunk chunk : world.getLoadedChunks()) {
                	chunk.unload(false, false);
                }
                
                Bukkit.getServer().unloadWorld(Bukkit.getServer().getWorld("skyblock/"+playerName), false);
                
                if(Bukkit.getServer().getWorld("skyblock/"+playerName) != null) {
                	System.out.println("failed to unload the world");
                }

                

                final File invFolder = new File(System.getProperty("user.dir"),"plugins/MultiInv/Worlds/skyblock/"+playerName);
                final File worldFolder = new File(System.getProperty("user.dir"),"skyblock/"+playerName);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                	public void run() {
                		System.gc();
                        deleteFolder(worldFolder);
                        deleteFolder(invFolder);
                	}
                },30L);
                
                return true;
            }
            sender.sendMessage("Player \"" + playerName + "\" doesn't have an island registered.");
            return true;
        }
        return false;
    }
    public void deleteFolder(File file) {
    	if(file.isDirectory()) {
    		File[] files = file.listFiles()
;
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

	
