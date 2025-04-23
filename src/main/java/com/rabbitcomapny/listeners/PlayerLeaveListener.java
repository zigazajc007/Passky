package com.rabbitcomapny.listeners;

import com.rabbitcomapny.Passky;
import com.rabbitcomapny.api.Identifier;
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
		if (Passky.isLoggedIn.getOrDefault(new Identifier(e.getPlayer()).toString(), false)) {
			if(Passky.getInstance().getConf().getBoolean("teleport_player_last_location", true)){
				Utils.saveLastPlayerLocation(new Identifier(e.getPlayer()), e.getPlayer().getLocation());
			}
		}
	}
}
