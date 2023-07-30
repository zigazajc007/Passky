package com.rabbitcomapny.listeners;

import com.rabbitcomapny.Passky;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class PlayerHungerListener implements Listener {

	private final Passky passky;

	public PlayerHungerListener(Passky plugin) {
		passky = plugin;

		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onPlayerHunger(FoodLevelChangeEvent e){
		if(e.getEntity() instanceof Player){
			if (!Passky.isLoggedIn.getOrDefault(e.getEntity().getUniqueId(), false)) {
				e.setCancelled(true);
			}
		}
	}

}
