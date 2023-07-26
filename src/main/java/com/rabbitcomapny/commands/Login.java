package com.rabbitcomapny.commands;

import com.rabbitcomapny.Passky;
import com.rabbitcomapny.utils.Hash;
import com.rabbitcomapny.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;


public class Login implements ICommand {

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}

		Player player = (Player) sender;
		String uuid = (Passky.getInstance().getConf().getInt("player_identifier", 0) == 0) ? player.getName() : player.getUniqueId().toString();
		Hash hash = Utils.getHash(uuid);

		if(hash == null){
			player.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("login_register"));
			return true;
		}

		if(Passky.isLoggedIn.getOrDefault(player.getUniqueId(), false)){
			player.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("login_already"));
			return true;
		}

		if(args.length != 1){
			player.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("login_syntax"));
			return true;
		}

		Hash hash2 = new Hash(hash.algo, args[0], hash.salt, false);
		if(!hash2.hash.equals(hash.hash)){
			int attempts = Passky.failures.merge(player.getUniqueId(), 1, Integer::sum);
			if (attempts >= Passky.getInstance().getConf().getInt("attempts")) {
				Passky.failures.put(player.getUniqueId(), 0);
				player.kickPlayer(Utils.getMessages("prefix") + Utils.getMessages("login_too_many_attempts"));
				return true;
			}
			player.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("login_incorrect"));
			return true;
		}

		Passky.isLoggedIn.put(player.getUniqueId(), true);
		if(Passky.getInstance().getConf().getBoolean("location_protection", true)){
			Location loc = Utils.getLastPlayerLocation(uuid);
			if(loc != null) player.teleport(loc);
		}
		player.removePotionEffect(PotionEffectType.BLINDNESS);
		player.resetTitle();
		player.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("login_successfully"));

		double damage = Passky.damage.getOrDefault(player.getUniqueId(), 0D);
		if (damage > 0D) player.damage(damage);
		if (Passky.getInstance().getConf().getBoolean("session_enabled", false)) {
			if (player.getAddress() != null && player.getAddress().getAddress() != null)
				Utils.setSession(uuid, player.getAddress().getAddress().toString().replace("/", ""));
		}
		return true;
	}

}
