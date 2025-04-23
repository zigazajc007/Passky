package com.rabbitcomapny.listeners;

import com.rabbitcomapny.Passky;
import com.rabbitcomapny.api.Identifier;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener {

	private final Passky passky;

	public PlayerInteractListener(Passky plugin) {
		passky = plugin;

		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onPlayerInteraction(PlayerInteractEvent e) {
		if (!Passky.isLoggedIn.getOrDefault(new Identifier(e.getPlayer()).toString(), false)) {
			e.setCancelled(true);
		}
	}
}
