// Decompiled with: FernFlower
// Class Version: 6
package de.qgel.skyblock;

import java.io.File;
import java.util.HashMap;
import java.util.Stack;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import com.onarandombox.MultiverseCore.MVWorld;
import com.onarandombox.MultiverseCore.MultiverseCore;

public class skyblock extends JavaPlugin {
    private final PlayerEventListener playerListener = new PlayerEventListener(this);
    private HashMap playerIslands = new HashMap();
    private Stack orphaned = new Stack();
    private static int SPAWN_X = 0;
    private static int SPAWN_Z = 0;
    private Island lastIsland;
    private static int ISLANDS_Y = 65;
    private static int ISLAND_SPACING = 100;

    public void onDisable() {
        try {
            SLAPI.save(this.playerIslands, "playerIslands.bin");
            SLAPI.save(this.lastIsland, "lastIsland.bin");
            SLAPI.save(this.orphaned, "orpahnedIslands.bin");
        } catch (Exception var2) {
            System.out.println("Something went wrong saving the Island data. That's really bad but there is nothing we can really do about it. Sorry");
            var2.printStackTrace();
        }

        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is now Disabled!");
    }

    public void onEnable() {
        File folder = getDataFolder();
        folder.mkdirs(); // ensures /plugins/Skyblock exists

        File map = new File(folder, "map");
        map.mkdirs(); // creates /plugins/Skyblock/map
        
        File schematics = new File(folder, "schematics");
        schematics.mkdirs(); // creates /plugins/Skyblock/schematics
        
        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvent(Type.PLAYER_JOIN, this.playerListener, Priority.Normal, this);
        pm.registerEvent(Type.PLAYER_RESPAWN, this.playerListener, Priority.Normal, this);
        this.getCommand("is").setExecutor(new islandCommand(this));
        this.getCommand("removeIsland").setExecutor(new RemoveIslandCommand(this));
        this.getCommand("island").setExecutor(new islandCommand(this));
        this.getCommand("skyHelp").setExecutor(new skyHelpCommand());
//        this.getCommand("skydev").setExecutor(new skyDevCommand(this));
        PluginDescriptionFile pdfFile = this.getDescription();

        try {
            if ((new File("lastIsland.bin")).exists()) {
                this.lastIsland = (Island)SLAPI.load("lastIsland.bin");
            }

            if (this.lastIsland == null) {
                this.lastIsland = new Island();
                this.lastIsland.x = 0;
                this.lastIsland.z = 0;
            }

            if ((new File("playerIslands.bin")).exists()) {
                HashMap load = (HashMap)SLAPI.load("playerIslands.bin");
                if (load != null) {
                    this.playerIslands = load;
                }
            }

            if ((new File("orphanedIslands.bin")).exists()) {
                Stack load = (Stack)SLAPI.load("orphanedIslands.bin");
                if (load != null) {
                    this.orphaned = load;
                }
            }
        } catch (Exception var4) {
            System.out.println("Could not load Island data from disk.");
            var4.printStackTrace();
        }

//        this.makeSpawn("skyIsland");
        System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
    }

    public boolean hasIsland(Player player) {
        return this.playerIslands.containsKey(player.getName());
    }

    public boolean hasIsland(String playername) {
        return this.playerIslands.containsKey(playername);
    }

    public boolean hasOrphanedIsland() {
        return !this.orphaned.empty();
    }

    public Island getOrphanedIsland() {
        if (this.hasOrphanedIsland()) {
            return (Island)this.orphaned.pop();
        } else {
            Island spawn = new Island();
            spawn.x = SPAWN_X;
            spawn.z = SPAWN_Z;
            return spawn;
        }
    }

    public Island getPlayerIsland(String playerName) {
        Island spawn = new Island();
        spawn.x = SPAWN_X;
        spawn.z = SPAWN_Z;
        return spawn;
    }

    public int getISLANDS_Y() {
        return ISLANDS_Y;
    }

    public Island getLastIsland() {
        return this.lastIsland;
    }

    public void setLastIsland(Island island) {
        this.lastIsland = island;
    }

    public int getISLAND_SPACING() {
        return ISLAND_SPACING;
    }

    public void deleteIsland(String playerName, World world) {
        if (this.hasIsland(playerName)) {
            Island island = this.getPlayerIsland(playerName);
//
//            for(int x = island.x - 50; x < island.x + 50; ++x) {
//                for(int y = ISLANDS_Y - 35; y < world.getMaxHeight(); ++y) {
//                    for(int z = island.z - 50; z < island.z + 50; ++z) {
//                        Block block = world.getBlockAt(x, y, z);
//                        if (block.getTypeId() != 0) {
//                            block.setTypeId(0);
//                        }
//                    }
//                }
//            }

            this.orphaned.push(island);
            this.playerIslands.remove(playerName);
        }

    }

    public void registerPlayerIsland(Player player, Island newIsland) {
        this.playerIslands.put(player.getName(), newIsland);
    }

    public void teleportHome(Player player) {

    	MultiverseCore mv = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");	

    	MVWorld mvWorld = mv.getWorldManager().getMVWorld("skyblock/"+player.getName());
    	
    	Island home = this.getPlayerIsland(player.getName());

        int h = ISLANDS_Y;
//        for(h = ISLANDS_Y; player.getWorld().getBlockTypeIdAt(home.x, h, home.z) != 0; ++h) {
//        }
        
//        mv.teleportPlayer(player, new Location(Bukkit.getWorld(), (double)home.x, (double)h, (double)home.z));
        Bukkit.getServer().getWorld("skyblock/"+player.getName()).loadChunk(home.x, home.z);
        player.teleport(new Location(Bukkit.getServer().getWorld("skyblock/"+player.getName()), (double)home.x, (double)h+0.1, (double)home.z));
        player.setVelocity(new Vector(0,0,0));
    }

    private void makeSpawn(String worldname) {
        World world = this.getServer().getWorld(worldname);
        if (world == null) {
            System.out.println("No world named \"" + worldname + "\" found, no specific spawn created");
        } else {
            Location spawn = world.getSpawnLocation();
            System.out.println("[skyblock] making spawn on skyIsland");

            for(int x = (int)(spawn.getX() - 5.0D); (double)x < spawn.getX() + 5.0D; ++x) {
                for(int z = (int)(spawn.getZ() - 5.0D); (double)z < spawn.getZ() + 5.0D; ++z) {
                    Block block = world.getBlockAt(x, spawn.getBlockY(), z);
                    block.setTypeId(7);
                }
            }

        }
    }
}
 