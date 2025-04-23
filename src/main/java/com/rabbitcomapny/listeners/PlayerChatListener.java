package com.rabbitcomapny.listeners;

import com.rabbitcomapny.Passky;
import com.rabbitcomapny.api.Identifier;
import com.rabbitcomapny.api.PasskyAPI;
import com.rabbitcomapny.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChatListener implements Listener {

	private final Passky passky;

	public PlayerChatListener(Passky plugin) {
		passky = plugin;

		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		Identifier identifier = new Identifier(e.getPlayer());

		if (!Passky.isLoggedIn.getOrDefault(identifier.toString(), false)) {
			e.setCancelled(true);

			if (!PasskyAPI.isRegistered(identifier)) {
				e.getPlayer().sendMessage(Utils.getMessages("prefix") + Utils.getMessages("register_syntax"));
			} else {
				e.getPlayer().sendMessage(Utils.getMessages("prefix") + Utils.getMessages("login_syntax"));
			}
		}
	}

}
