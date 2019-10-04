package com.rabbitcomapny.listeners;

import com.rabbitcomapny.Passky;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerDamageListener implements Listener {

    private Passky passky;

    public PlayerDamageListener(Passky plugin){
        passky = plugin;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e){
        if (e.getEntity() instanceof Player){
            if(!Passky.isLoggedIn.getOrDefault(e.getEntity(), false)){
                e.setCancelled(true);
            }
        }
    }
}
