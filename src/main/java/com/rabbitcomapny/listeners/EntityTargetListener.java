package com.rabbitcomapny.listeners;

import com.rabbitcomapny.Passky;
import com.rabbitcomapny.api.Identifier;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

public class EntityTargetListener implements Listener {

	public EntityTargetListener(Passky plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onEntityTarget(EntityTargetLivingEntityEvent e) {
		if (e.getTarget() instanceof Player) {
			Player player = (Player) e.getTarget();
			if (!Passky.isLoggedIn.getOrDefault(new Identifier(player).toString(), false)) {
				e.setCancelled(true);
			}
		}
	}

}