package com.rabbitcomapny.listeners;

import com.rabbitcomapny.Passky;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PlayerAttackListener implements Listener {

	private final Passky passky;

	public PlayerAttackListener(Passky plugin) {
		passky = plugin;

		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onPlayerAttack(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player) {
			if (!Passky.isLoggedIn.getOrDefault(e.getDamager().getUniqueId(), false)) {
				e.setCancelled(true);
			}
		}
	}
}
