package com.rabbitcomapny.listeners;

import com.rabbitcomapny.Passky;
import com.rabbitcomapny.api.Identifier;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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
		if (e.getPlayer() instanceof Player) {
			Player player = (Player) e.getPlayer();
			if (!Passky.isLoggedIn.getOrDefault(new Identifier(player).toString(), false)) {
				e.setCancelled(true);
			}
		}
	}

}
