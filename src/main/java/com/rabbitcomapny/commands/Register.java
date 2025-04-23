package com.rabbitcomapny.commands;

import com.rabbitcomapny.Passky;
import com.rabbitcomapny.api.Identifier;
import com.rabbitcomapny.events.SuccessfulRegisterEvent;
import com.rabbitcomapny.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class Register implements ICommand {

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}

		Player player = (Player) sender;
		Identifier identifier = new Identifier(player);

		if (Utils.isPlayerRegistered(identifier)) {
			player.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("register_account_already"));
			return true;
		}

		if (Passky.isLoggedIn.getOrDefault(identifier.toString(), false)) {
			player.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("register_already"));
			return true;
		}

		if (args.length != 2) {
			player.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("register_syntax"));
			return true;
		}

		String password = args[0];
		String password2 = args[1];
		if (!password.equals(password2)) {
			player.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("register_doesnt_match"));
			return true;
		}

		if (password.length() > Integer.parseInt(Utils.getConfig("max_password_length"))) {
			player.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("register_too_long"));
			return true;
		}

		if (password.length() < Integer.parseInt(Utils.getConfig("min_password_length"))) {
			player.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("register_too_short"));
			return true;
		}

		if (player.getAddress() != null && player.getAddress().getAddress() != null) {
			Utils.savePassword(identifier, password, player.getAddress().getAddress().toString().replace("/", ""), String.valueOf(System.currentTimeMillis()));
		} else {
			Utils.savePassword(identifier, password, null, String.valueOf(System.currentTimeMillis()));
		}

		Passky.isLoggedIn.put(identifier.toString(), true);

		if(Passky.getInstance().getConf().getBoolean("teleport_player_last_location", true)){
			Location loc = Utils.getLastPlayerLocation(identifier);
			if (loc != null) player.teleport(loc);
		}

		player.removePotionEffect(PotionEffectType.BLINDNESS);
		player.resetTitle();
		player.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("register_successfully"));
		Bukkit.getPluginManager().callEvent(new SuccessfulRegisterEvent(player));

		if (Passky.getInstance().getConf().getBoolean("session_enabled", false)) {
			if (player.getAddress() != null && player.getAddress().getAddress() != null)
				Utils.setSession(identifier, player.getAddress().getAddress().toString().replace("/", ""));
		}

		return true;
	}
}
