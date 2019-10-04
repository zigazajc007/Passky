package com.rabbitcomapny.listeners;

import com.rabbitcomapny.Passky;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {

    private Passky passky;

    public PlayerMoveListener(Passky plugin){
        passky = plugin;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e){
        if(!Passky.isLoggedIn.getOrDefault(e.getPlayer(), false)){
            e.setCancelled(true);
        }
    }

}
