package com.rabbitcomapny.listeners;

import com.rabbitcomapny.Passky;
import com.rabbitcomapny.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeaveListener implements Listener {

	private final Passky passky;

	public PlayerLeaveListener(Passky plugin) {
		this.passky = plugin;

		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onPlayerLeave(final PlayerQuitEvent e) {
		if (Passky.isLoggedIn.getOrDefault(e.getPlayer().getUniqueId(), false)) {
			String uuid = (Passky.getInstance().getConf().getInt("player_identifier", 0) == 0) ? e.getPlayer().getName() : e.getPlayer().getUniqueId().toString();
			if(Passky.getInstance().getConf().getBoolean("teleport_player_last_location", true)){
				Utils.saveLastPlayerLocation(uuid, e.getPlayer().getLocation());
			}
		}
	}
}
