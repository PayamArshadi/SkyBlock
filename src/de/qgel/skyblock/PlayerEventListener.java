/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.entity.Player
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.event.player.PlayerListener
 *  org.bukkit.event.player.PlayerRespawnEvent
 */
package de.qgel.skyblock;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerRespawnEvent;

import de.qgel.skyblock.Island;
import de.qgel.skyblock.skyblock;

public class PlayerEventListener
extends PlayerListener {
    private final skyblock plugin;

    public PlayerEventListener(skyblock instance) {
        this.plugin = instance;
    }

    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!this.plugin.hasIsland(event.getPlayer())) {
//            event.getPlayer().sendMessage("Welcome! This Server uses the Sky Island SMP mod.");
//            event.getPlayer().sendMessage("Use /newIsland to get your very own Island and be teleported there.");
//            event.getPlayer().sendMessage("Use /skyHelp for more commands.");
            Location spawn = event.getPlayer().getWorld().getSpawnLocation();
            event.getPlayer().getWorld().getSpawnLocation().getBlock().getChunk().load();
            event.getPlayer().teleport(new Location(spawn.getWorld(), spawn.getX(), (double)(spawn.getBlockY() + 3), spawn.getZ()));
        }
    }

    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (this.plugin.hasIsland(player) && player.getWorld().getEnvironment().getId() == 0) {
            Island home = this.plugin.getPlayerIsland(player.getName());
            event.setRespawnLocation(new Location(player.getWorld(), (double)home.x, (double)this.plugin.getISLANDS_Y(), (double)home.z));
        }
    }
}

