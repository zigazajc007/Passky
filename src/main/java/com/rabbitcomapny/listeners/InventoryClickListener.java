package com.rabbitcomapny.listeners;

import com.rabbitcomapny.Passky;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClickListener implements Listener {

    private Passky passky;

    public InventoryClickListener(Passky plugin){
        passky = plugin;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        if(!Passky.isLoggedIn.getOrDefault(e.getWhoClicked(), false)){
            e.setCancelled(true);
        }
    }
}
