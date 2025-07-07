package com.rabbitcomapny.listeners;

import com.rabbitcomapny.Passky;
import com.rabbitcomapny.api.Identifier;
import com.rabbitcomapny.api.PasskyAPI;
import com.rabbitcomapny.events.SuccessfulLoginEvent;
import com.rabbitcomapny.utils.Session;
import com.rabbitcomapny.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


public class PlayerJoinListener implements Listener {

	private final Passky passky;

	public PlayerJoinListener(Passky plugin) {
		this.passky = plugin;

		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onPlayerJoin(final PlayerJoinEvent e) {

		if (Passky.getInstance().getConf().getBoolean("kick_illegal_usernames", true)) {
			if (!e.getPlayer().getName().matches("[a-zA-Z0-9_]+") || e.getPlayer().getName().length() < 3 || e.getPlayer().getName().length() > 16) {
				Utils.kickPlayer(e.getPlayer(), Utils.getMessages("illegal_username_kick"));
				return;
			}
		}

		Identifier identifier = new Identifier(e.getPlayer());

		Passky.failures.put(identifier.toString(), 0);
		Passky.isLoggedIn.put(identifier.toString(), false);

		if (Passky.getInstance().getConf().getBoolean("session_enabled", false)) {
			Session session = Utils.getSession(identifier);
			if (session != null) {
				if (e.getPlayer().getAddress() != null && e.getPlayer().getAddress().getAddress() != null) {
					if (session.ip.equals(e.getPlayer().getAddress().getAddress().toString().replace("/", "")) && (session.date + Passky.getInstance().getConf().getInt("session_time", 30) * 60000L) > System.currentTimeMillis()) {
						Passky.isLoggedIn.put(identifier.toString(), true);
						e.getPlayer().removePotionEffect(PotionEffectType.BLINDNESS);
						e.getPlayer().sendMessage(Utils.getMessages("prefix") + Utils.getMessages("login_successfully"));
						Bukkit.getPluginManager().callEvent(new SuccessfulLoginEvent(e.getPlayer()));
					}
				}
			}
		}

		if (!Passky.isLoggedIn.getOrDefault(identifier.toString(), false)) {

			if(Passky.getInstance().getConf().getBoolean("teleportation_enabled", true)){
				if(Passky.getInstance().getConf().getBoolean("teleport_player_last_location", true)){
					Location loc = Utils.getLastPlayerLocation(identifier);
					if (loc == null) Utils.saveLastPlayerLocation(identifier, e.getPlayer().getLocation());
				}

				String world = passky.getConf().getString("spawn_world", null);
				if(world == null){
					e.getPlayer().teleport(e.getPlayer().getWorld().getSpawnLocation());
				}else{
					World custom_world = Bukkit.getServer().getWorld(world);
					if(custom_world == null){
						e.getPlayer().teleport(e.getPlayer().getWorld().getSpawnLocation());
					}else{
						e.getPlayer().teleport(custom_world.getSpawnLocation());
					}
				}
			}

			e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 2147483647, 1));

			if(e.getPlayer().isInsideVehicle()){
				Entity vehicle = e.getPlayer().getVehicle();
				if (vehicle != null) {
					vehicle.eject();
				}
			}

			if (!PasskyAPI.isRegistered(identifier)) {
				e.getPlayer().sendMessage(Utils.getMessages("prefix") + Utils.getMessages("register_syntax"));
				if (passky.getConf().getBoolean("titles_enabled", true))
					e.getPlayer().sendTitle(Utils.chat(passky.getConf().getString("register_title", "&aRegister")), Utils.chat(passky.getConf().getString("register_subtitle", "&a/register <password> <password>")), 50, passky.getConf().getInt("time_before_kick", 30) * 20, 50);
			} else {
				e.getPlayer().sendMessage(Utils.getMessages("prefix") + Utils.getMessages("login_syntax"));
				if (passky.getConf().getBoolean("titles_enabled", true))
					e.getPlayer().sendTitle(Utils.chat(passky.getConf().getString("login_title", "&aLogin")), Utils.chat(passky.getConf().getString("login_subtitle", "&a/login <password>")), 50, passky.getConf().getInt("time_before_kick", 30) * 20, 50);
			}

			Bukkit.getScheduler().runTaskLater(Passky.getInstance(), () -> {
				if (!Passky.isLoggedIn.getOrDefault(identifier.toString(), false) && e.getPlayer().isOnline()) {
					Utils.kickPlayer(e.getPlayer(), Utils.getMessages("prefix") + Utils.getMessages("login_time").replace("{time}", Utils.getConfig("time_before_kick")));
				}
			}, Passky.getInstance().getConf().getInt("time_before_kick", 30) * 20L);
		}

	}
}
