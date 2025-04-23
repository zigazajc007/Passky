package com.rabbitcomapny.listeners;

import com.rabbitcomapny.Passky;
import com.rabbitcomapny.api.Identifier;
import com.rabbitcomapny.api.PasskyAPI;
import com.rabbitcomapny.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.HashSet;
import java.util.Set;

public class PlayerCommandListener implements Listener {

	private final Passky passky;
	private final Set<String> allowedCommands = new HashSet<String>(){{
		add("/register");
		add("/login");
		add("/reg");
		add("/log");
		add("/r");
		add("/l");
	}};

	public PlayerCommandListener(Passky plugin) {
		passky = plugin;

		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
		Identifier identifier = new Identifier(e.getPlayer());
		if (!Passky.isLoggedIn.getOrDefault(identifier.toString(), false)) {
			String command = e.getMessage().toLowerCase().split(" ")[0];
			if (!allowedCommands.contains(command)) {
				e.setCancelled(true);

				if (!PasskyAPI.isRegistered(identifier)) {
					e.getPlayer().sendMessage(Utils.getMessages("prefix") + Utils.getMessages("register_syntax"));
				} else {
					e.getPlayer().sendMessage(Utils.getMessages("prefix") + Utils.getMessages("login_syntax"));
				}
			}
		}
	}

}
