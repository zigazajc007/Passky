package com.rabbitcomapny.listeners;

import com.rabbitcomapny.Passky;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class InventoryOpenListener implements Listener {

	private final Passky passky;

	public InventoryOpenListener(Passky plugin) {
		passky = plugin;

		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent e) {
		if (!Passky.isLoggedIn.getOrDefault(e.getPlayer().getUniqueId(), false)) {
			e.setCancelled(true);
		}
	}

}
