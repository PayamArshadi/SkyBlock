/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.block.Block
 *  org.bukkit.block.Chest
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 */
package de.qgel.skyblock;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.MVWorld;
import com.onarandombox.utils.WorldManager;

import org.bukkit.plugin.java.JavaPlugin;

import de.qgel.skyblock.Island;
import de.qgel.skyblock.skyblock;
import javafx.beans.value.WeakChangeListener;

import com.onarandombox.MultiverseCore.MVPlugin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.data.DataException;

import net.minecraft.server.Chunk;
import net.minecraft.server.MinecraftServer;
import net.neo_vortex.bukkit.CleanroomGenerator.CleanroomChunkGenerator;
import net.neo_vortex.bukkit.CleanroomGenerator.CleanroomGenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.Set;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.generator.NormalChunkGenerator;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CreateIslandCommand
implements CommandExecutor {
	

	MultiverseCore mv = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");	
	
    private final skyblock plugin;

    public CreateIslandCommand(skyblock plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player)sender;

        if (player.getWorld().getEnvironment().getId() != 0) {
            player.sendMessage("Can only do that in the normal world, sorry");
            return true;
        }
        if (this.plugin.hasIsland(player)) {
            if (split.length == 0) {
                Island location = this.plugin.getPlayerIsland(player.getName());
                player.sendMessage("You already have an Island at " + location.x + " / " + location.z + " If you want a new one, use \"/newIsland replace\" instead.");
                return true;
            }
            if (split[0].equals("replace")) {
                this.plugin.deleteIsland(player.getName(), player.getWorld());
                player.getInventory().clear();
                List Entities = player.getNearbyEntities(15.0, 15.0, 15.0);
                Iterator ent = Entities.iterator();
                while (ent.hasNext()) {
                    ((Entity)ent.next()).remove();
                }
                return this.createIsland(player);
            }
            if (split[0].equals("override")) {
                if (!player.isOp()) {
                    return false;
                }
                return this.createIsland(player);
            }
        } else {
            return this.createIsland(player);
        }
        return false;
    }

    public boolean createIsland(Player player) {
    	
        Island last = this.plugin.getLastIsland();
        try {
            Island next;
            if (this.plugin.hasOrphanedIsland()) {
                next = this.plugin.getOrphanedIsland();
            } else {
                next = this.nextIslandLocation(last);
                this.plugin.setLastIsland(next);
            }
            this.copyIslandWorld(0, 0, player, true);
            this.plugin.registerPlayerIsland(player, next);
            
        }
        catch (Exception ex) {
            player.sendMessage("Could not create your Island. Pleace contact a server moderator.");
            this.plugin.setLastIsland(last);
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    public World createIslandWorld() {

    	ChunkGenerator generator = new CleanroomChunkGenerator(".");
    	World world = Bukkit.getServer().createWorld(plugin.getDataFolder()+ "/map", Environment.NORMAL, generator);
    	
    	mv.getWorldManager().addWorld(plugin.getDataFolder()+ "/map", Environment.NORMAL, null , "CleanroomGenerator:.");

    	World mvWorld = mv.getWorldManager().getMVWorld(plugin.getDataFolder()+ "/map").getCBWorld();
    	
    	return mvWorld;
    	
    }
    
    public void copyIslandWorld(final int x, final int z, final Player player,final boolean firstRun) {

    	unloadTempWorld();

		File sourceFolder = new File(plugin.getDataFolder(), "map");
        File targetFolder = new File(System.getProperty("user.dir")+"/skyblock/"+player.getName());
	
		if (hasAnyFiles(sourceFolder)) {

	    	Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
	    	    private final skyblock plugin1 = new skyblock();

				@Override
	    	    public void run() {

	            	MultiverseCore mv = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");	

	            	MVWorld mvWorld = mv.getWorldManager().getMVWorld(plugin.getDataFolder()+ "/map");
	            	
	            	
	    	    	File targetFolder1 = new File(System.getProperty("user.dir")+"/skyblock/"+player.getName());
//	    			File folder = plugin.getDataFolder();
	    			File sourceFolder1 = new File(plugin.getDataFolder(), "map");
	    	        
	    	    	try {
	    				copyWorld(sourceFolder1, targetFolder1);

	    	        	mv.getWorldManager().addWorld("skyblock/"+player.getName(), Environment.NORMAL, null , "CleanroomGenerator:.");
	    	        	
	    			} catch (IOException e1) {
	    				org.bukkit.Bukkit.getServer().getLogger().severe("failed to copy the world");
	    				if (firstRun) {
	    					createIslandUsingSchematic(x, z, player);
	    				}
	    				e1.printStackTrace();
	    			}
	    	    	
	                this.plugin1.teleportHome(player);
	    	    }
	    	}, 1L);
		} else {
			org.bukkit.Bukkit.getServer().getLogger().severe("no world were found");
			createIslandUsingSchematic(x, z, player);
		}
    }
    
    public void unloadTempWorld() {
	
    	MultiverseCore mv = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");	

    	String maploc = plugin.getDataFolder()+ "/map";
    	
    	
        Bukkit.getServer().getWorld("world").loadChunk(-13, 44);
        World world = Bukkit.getServer().getWorld(maploc);
        
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
        

        mv.removeWorldFromConfig(maploc);
        mv.removeWorldFromList(maploc);
        
//        for (org.bukkit.Chunk chunk : world.getLoadedChunks()) {
//        	chunk.unload(false, false);
//        }
        
        Bukkit.getServer().unloadWorld(Bukkit.getServer().getWorld(maploc), false);
        
        if(Bukkit.getServer().getWorld(maploc) != null) {
        	System.out.println("failed to unload the world");
        }
        
    }
    
    public final void createIslandUsingSchematic(final int x,final int z, final Player player) {
    	
    	final File file = new File(plugin.getDataFolder(),"/schematics/island.schematic");
    	final World mvWorld = createIslandWorld();

        final int y = this.plugin.getISLANDS_Y();
//        int wd = 0;


		if (org.bukkit.Bukkit.getServer().getPluginManager().getPlugin("WorldEdit")!=null) {
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
	
				@Override
	    	    public void run() {
					try {
							CuboidClipboard clipboard = CuboidClipboard.loadSchematic(file);
							EditSession editSession = new EditSession(new BukkitWorld(mvWorld), Integer.MAX_VALUE);
		
		
					        int minChunkX = x >> 4;
					        int minChunkZ = z >> 4;
		
					        int maxChunkX = (x+clipboard.getWidth() -1 ) >> 4;
					        
					        int maxChunkZ = (z+clipboard.getLength() -1 ) >> 4;
					        
					        for (int cx = minChunkX;cx <= maxChunkX; cx++) {
					        	for(int cz = minChunkZ; cz <= maxChunkZ; cz++) {
					        		if(!Bukkit.getServer().getWorld(plugin.getDataFolder()+ "/map").isChunkLoaded(cx, cz)) {
					        	        Bukkit.getServer().getWorld(plugin.getDataFolder()+ "/map").loadChunk(cx, cz);
					        	        org.bukkit.Bukkit.getServer().getLogger().severe("loading chunks" +cx + " " + cz);
					        		}else {
					        			org.bukkit.Bukkit.getServer().getLogger().severe("loaded chunks" +cx + " " + cz);
					        		}
					        	}
					        }
							
							clipboard.paste(editSession, new Vector(x,y,z), false);
							copyIslandWorld(x, z, player, false);
				}catch (DataException e) {
					// TODO Auto-generated catch block
					org.bukkit.Bukkit.getServer().getLogger().severe("file doesn't exsits");
					
					createClassicIsland(x, y, z, player,mvWorld);
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					org.bukkit.Bukkit.getServer().getLogger().severe("error loading the file.");
					createClassicIsland(x, y, z, player,mvWorld);
					e.printStackTrace();
				} 
		    	catch (MaxChangedBlocksException e) {
					// TODO Auto-generated catch block
					org.bukkit.Bukkit.getServer().getLogger().severe("server cancelled the schematic paste to stop the server from crashing");
					e.printStackTrace();
				}
	
	    	    	
	    	    }
	    	}, 1L);
		}else {
			createClassicIsland(x, y, z, player,mvWorld);
			
		}
			
    }
    

    public final void createClassicIsland(int x, int y, int z, final Player player, World mvWorld) {

    	Block blockToChange;
        int z_operate;
        int y_operate;
        int x_operate = x;
        
        while (x_operate < x + 3) {
            y_operate = y;
            while (y_operate < y + 3) {
            	if (y_operate < y + 2) {
	                z_operate = z + 0;
	                while (z_operate < z + 6) {
	                    blockToChange = mvWorld.getBlockAt(x_operate, y_operate, z_operate);
	                    blockToChange.setTypeId(3);
	                    ++z_operate;
	                }
            	}
            	if (y_operate == y + 2) {
	                z_operate = z + 0;
	                while (z_operate < z + 6) {
	                    blockToChange = mvWorld.getBlockAt(x_operate, y_operate, z_operate);
	                    blockToChange.setTypeId(2);
	                    ++z_operate;
	                }
	                
                }
                ++y_operate;
            }
            ++x_operate;
        }
        x_operate = x + 3;
        while (x_operate < x + 6) {
            y_operate = y;
            while (y_operate < y + 3) {
            	if (y_operate < y + 2) {
	                z_operate = z + 3;
	                while (z_operate < z + 6) {
	                    blockToChange = mvWorld.getBlockAt(x_operate, y_operate, z_operate);
	                    blockToChange.setTypeId(3);
	                    ++z_operate;
	                }
            	}
            	if (y_operate == y + 2) {
	                z_operate = z + 3;
	                while (z_operate < z + 6) {
	                    blockToChange = mvWorld.getBlockAt(x_operate, y_operate, z_operate);
	                    blockToChange.setTypeId(2);
	                    ++z_operate;
	                }
	                
                }
                ++y_operate;
            }
            ++x_operate;
        }
        x_operate = x + 2;
        while (x_operate < x + 7) {
            y_operate = y + 6;
            z_operate = z + 2;
            while (z_operate < z + 7) {
                blockToChange = mvWorld.getBlockAt(x_operate, y_operate, z_operate);
                blockToChange.setTypeId(18);
                ++z_operate;
            }
            ++x_operate;
        }
        x_operate = x + 2;
        while (x_operate < x + 7) {
	        y_operate = y + 7;
            z_operate = z + 3;
            while (z_operate < z + 6) {
                blockToChange = mvWorld.getBlockAt(x_operate, y_operate, z_operate);
                blockToChange.setTypeId(18);
                ++z_operate;
	            }
            ++x_operate;
        }
        x_operate = x + 3;
        while (x_operate < x + 6) {
	        y_operate = y + 7;
            z_operate = z + 2;
            while (z_operate < z + 7) {
                blockToChange = mvWorld.getBlockAt(x_operate, y_operate, z_operate);
                blockToChange.setTypeId(18);
                ++z_operate;
	            }
            ++x_operate;
        }
        x_operate = x + 4;
        while (x_operate < x + 5) {
	        y_operate = y + 8;
            z_operate = z + 2;
            while (z_operate < z + 7) {
                blockToChange = mvWorld.getBlockAt(x_operate, y_operate, z_operate);
                blockToChange.setTypeId(18);
                ++z_operate;
	            }
            ++x_operate;
        }
        x_operate = x + 2;
        while (x_operate < x + 7) {
	        y_operate = y + 8;
            z_operate = z + 4;
            while (z_operate < z + 5) {
                blockToChange = mvWorld.getBlockAt(x_operate, y_operate, z_operate);
                blockToChange.setTypeId(18);
                ++z_operate;
	            }
            ++x_operate;
        }
        x_operate = x + 3;
        while (x_operate < x + 6) {
	        y_operate = y + 8;
            z_operate = z + 3;
            while (z_operate < z + 6) {
                blockToChange = mvWorld.getBlockAt(x_operate, y_operate, z_operate);
                blockToChange.setTypeId(18);
                ++z_operate;
	            }
            ++x_operate;
        }
        x_operate = x + 3;
        while (x_operate < x + 6) {
	        y_operate = y + 9;
            z_operate = z + 4;
            while (z_operate < z + 5) {
                blockToChange = mvWorld.getBlockAt(x_operate, y_operate, z_operate);
                blockToChange.setTypeId(18);
                ++z_operate;
	            }
            ++x_operate;
        }
        x_operate = x + 4;
        while (x_operate < x + 5) {
	        y_operate = y + 9;
            z_operate = z + 3;
            while (z_operate < z + 6) {
                blockToChange = mvWorld.getBlockAt(x_operate, y_operate, z_operate);
                blockToChange.setTypeId(18);
                ++z_operate;
	            }
            ++x_operate;
        }
        x_operate = x + 4;
        while (x_operate < x + 5) {
	        y_operate = y + 10;
            z_operate = z + 4;
            while (z_operate < z + 5) {
                blockToChange = mvWorld.getBlockAt(x_operate, y_operate, z_operate);
                blockToChange.setTypeId(18);
                ++z_operate;
	            }
            ++x_operate;
        }
        int y_operate2 = y + 3;
        while (y_operate2 < y + 9) {
            Block blockToChange2 = mvWorld.getBlockAt(x + 4, y_operate2, z + 4);
            blockToChange2.setTypeId(17);
            ++y_operate2;
        }
        Block blockToChange3 = mvWorld.getBlockAt(x + 1, y + 3, z); //mvWorld.getBlockAt(x + 1, y + 3, z);
        blockToChange3.setTypeId(54);
        Chest chest = (Chest)blockToChange3.getState();
        Inventory inventory = chest.getInventory();
        ItemStack item = new ItemStack(287, 12);
        inventory.addItem(new ItemStack[]{item});
        item = new ItemStack(327, 1);
        inventory.addItem(new ItemStack[]{item});
        item = new ItemStack(352, 1);
        inventory.addItem(new ItemStack[]{item});
        item = new ItemStack(338, 1);
        inventory.addItem(new ItemStack[]{item});
        item = new ItemStack(40, 1);
        inventory.addItem(new ItemStack[]{item});
        item = new ItemStack(79, 2);
        inventory.addItem(new ItemStack[]{item});
        //item = new ItemStack(361, 1);
        //inventory.addItem(new ItemStack[]{item});
        item = new ItemStack(39, 1);
        //inventory.addItem(new ItemStack[]{item});
        //item = new ItemStack(360, 1);
        inventory.addItem(new ItemStack[]{item});
        item = new ItemStack(81, 1);
        inventory.addItem(new ItemStack[]{item});
        blockToChange3 = mvWorld.getBlockAt(x, y, z);
        blockToChange3.setTypeId(7);
        blockToChange3 = mvWorld.getBlockAt(x + 2, y + 1, z + 1);
        blockToChange3.setTypeId(12);
        blockToChange3 = mvWorld.getBlockAt(x + 2, y + 1, z + 2);
        blockToChange3.setTypeId(12);
        blockToChange3 = mvWorld.getBlockAt(x + 2, y + 1, z + 3);
        blockToChange3.setTypeId(12);
		copyIslandWorld(x, z, player, false);
	
    }
    
    private Island nextIslandLocation(Island lastIsland) {
        int x = lastIsland.x;
        int z = lastIsland.z;
        Island nextPos = new Island();
        nextPos.x = x;
        nextPos.z = z;
        if (x < z) {
            if (-1 * x < z) {
                nextPos.x += this.plugin.getISLAND_SPACING();
                return nextPos;
            }
            nextPos.z += this.plugin.getISLAND_SPACING();
            return nextPos;
        }
        if (x > z) {
            if (-1 * x >= z) {
                nextPos.x -= this.plugin.getISLAND_SPACING();
                return nextPos;
            }
            nextPos.z -= this.plugin.getISLAND_SPACING();
            return nextPos;
        }
        if (x <= 0) {
            nextPos.z += this.plugin.getISLAND_SPACING();
            return nextPos;
        }
        nextPos.z -= this.plugin.getISLAND_SPACING();
        return nextPos;
    }



    /**
     * Copies a directory from a source path to a target path, including all subdirectories and files.
     *
     * @param sourceDirectory The path to the source directory.
     * @param targetDirectory The path to the target directory. If the target directory does not exist, it will be created.
     *                        If the target directory already exists, the source directory's contents will be copied into it.
     * @throws IOException If any I/O error occurs during the copy operation.
     */
    public static void copyWorld(File source, File target) throws IOException {
        if (!source.isDirectory()) {
            throw new IllegalArgumentException("Source must be a directory: " + source);
        }

        if (!target.exists()) {
            target.mkdirs();
        }

        for (String name : source.list()) {
            if (name.equals("session.lock")) continue;
            if (name.equals("uid.dat")) continue;

            File src = new File(source, name);
            File dst = new File(target, name);

            if (src.isDirectory()) {
                copyWorld(src, dst);
            } else {
                InputStream in = new FileInputStream(src);
                OutputStream out = new FileOutputStream(dst);

                byte[] buffer = new byte[8192];
                int length;

                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }

                in.close();
                out.close();
            }
        }
    }

    public static boolean hasAnyFiles(File folder) {
        if (folder == null) return false;
        if (!folder.exists()) return false;
        if (!folder.isDirectory()) return false;

        String[] list = folder.list();
        return list != null && list.length > 0;
    }
    
}

