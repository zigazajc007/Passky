package com.rabbitcomapny.commands;

import com.rabbitcomapny.Passky;
import com.rabbitcomapny.api.Identifier;
import com.rabbitcomapny.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Logout implements ICommand {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}

		Player player = (Player) sender;
		Identifier identifier = new Identifier(player);

		if (!Passky.isLoggedIn.getOrDefault(identifier.toString(), false)) {
			player.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("logout_login_first"));
			return true;
		}

		Passky.isLoggedIn.put(identifier.toString(), false);
		Utils.removeSession(identifier);

		if(Passky.getInstance().getConf().getBoolean("teleport_player_last_location", true)){
			Utils.saveLastPlayerLocation(identifier, player.getLocation());
			player.teleport(player.getWorld().getSpawnLocation());
		}

		player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 2147483647, 1));

		if (!Utils.isPlayerRegistered(identifier)) {
			player.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("register_syntax"));
			if (Passky.getInstance().getConf().getBoolean("titles_enabled", true))
				player.sendTitle(Utils.chat(Passky.getInstance().getConf().getString("register_title", "&aRegister")), Utils.chat(Passky.getInstance().getConf().getString("register_subtitle", "&a/register <password> <password>")), 50, Passky.getInstance().getConf().getInt("time_before_kick", 30) * 20, 50);
		} else {
			player.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("login_syntax"));
			if (Passky.getInstance().getConf().getBoolean("titles_enabled", true))
				player.sendTitle(Utils.chat(Passky.getInstance().getConf().getString("login_title", "&aLogin")), Utils.chat(Passky.getInstance().getConf().getString("login_subtitle", "&a/login <password>")), 50, Passky.getInstance().getConf().getInt("time_before_kick", 30) * 20, 50);
		}

		Bukkit.getScheduler().runTaskLater(Passky.getInstance(), () -> {
			if (!Passky.isLoggedIn.getOrDefault(identifier.toString(), false) && player.isOnline()) {
				Utils.kickPlayer(player, Utils.getMessages("prefix") + Utils.getMessages("login_time").replace("{time}", Utils.getConfig("time_before_kick")));
			}
		}, Passky.getInstance().getConf().getInt("time_before_kick", 30) * 20L);

		return true;
	}
}
