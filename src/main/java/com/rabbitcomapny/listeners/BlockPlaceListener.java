package com.rabbitcomapny.listeners;

import com.rabbitcomapny.Passky;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceListener implements Listener {

	private final Passky passky;

	public BlockPlaceListener(Passky plugin) {
		passky = plugin;

		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		if (!Passky.isLoggedIn.getOrDefault(e.getPlayer().getUniqueId(), false)) {
			e.setCancelled(true);
		}
	}

}
