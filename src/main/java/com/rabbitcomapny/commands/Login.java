package com.rabbitcomapny.commands;

import com.rabbitcomapny.Passky;
import com.rabbitcomapny.api.Identifier;
import com.rabbitcomapny.events.SuccessfulLoginEvent;
import com.rabbitcomapny.utils.Hash;
import com.rabbitcomapny.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class Login implements ICommand {

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}

		Player player = (Player) sender;
		Identifier identifier = new Identifier(player);
		Hash hash = Utils.getHash(identifier);

		if (hash == null) {
			player.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("login_register"));
			return true;
		}

		if (Passky.isLoggedIn.getOrDefault(identifier.toString(), false)) {
			player.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("login_already"));
			return true;
		}

		if (args.length != 1) {
			player.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("login_syntax"));
			return true;
		}

		Hash hash2 = new Hash(hash.algo, args[0], hash.salt, false);
		if (!hash2.hash.equals(hash.hash)) {
			int attempts = Passky.failures.merge(identifier.toString(), 1, Integer::sum);
			if (attempts >= Passky.getInstance().getConf().getInt("attempts")) {
				Passky.failures.put(identifier.toString(), 0);
				player.kickPlayer(Utils.getMessages("prefix") + Utils.getMessages("login_too_many_attempts"));
				return true;
			}
			player.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("login_incorrect"));
			return true;
		}

		Passky.isLoggedIn.put(identifier.toString(), true);

		if(Passky.getInstance().getConf().getBoolean("teleportation_enabled", true) && Passky.getInstance().getConf().getBoolean("teleport_player_last_location", true)){
			Location loc = Utils.getLastPlayerLocation(identifier);
			if (loc != null) player.teleport(loc);
		}

		player.removePotionEffect(PotionEffectType.BLINDNESS);
		player.resetTitle();
		player.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("login_successfully"));
		Bukkit.getPluginManager().callEvent(new SuccessfulLoginEvent(player));

		if (Passky.getInstance().getConf().getBoolean("session_enabled", false)) {
			if (player.getAddress() != null && player.getAddress().getAddress() != null)
				Utils.setSession(identifier, player.getAddress().getAddress().toString().replace("/", ""));
		}
		return true;
	}

}
