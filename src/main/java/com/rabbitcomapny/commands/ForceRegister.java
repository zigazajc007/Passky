package com.rabbitcomapny.commands;

import com.rabbitcomapny.Passky;
import com.rabbitcomapny.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;
import java.util.List;

public class ForceRegister implements ICommand {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (args.length != 2) {
			sender.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("force_register_syntax"));
			return true;
		}

		boolean usernames = Passky.getInstance().getConf().getInt("player_identifier", 0) == 0;
		boolean isPlayerRegistered = usernames && Utils.isPlayerRegistered(args[0]);

		if (isPlayerRegistered) {
			sender.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("force_register_already"));
			return true;
		}

		if (args[1].length() > Integer.parseInt(Utils.getConfig("max_password_length"))) {
			sender.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("changepass_too_long"));
			return true;
		}

		if (args[1].length() < Integer.parseInt(Utils.getConfig("min_password_length"))) {
			sender.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("changepass_too_short"));
			return true;
		}

		if (usernames) Utils.savePassword(args[0], args[1], "0.0.0.0", String.valueOf(System.currentTimeMillis()));

		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.getName().equals(args[0])) {
				Passky.isLoggedIn.put(p.getUniqueId(), true);

				if(Passky.getInstance().getConf().getBoolean("teleport_player_last_location", true)){
					Location loc = Utils.getLastPlayerLocation(args[0]);
					if (loc != null) p.teleport(loc);
				}

				p.removePotionEffect(PotionEffectType.BLINDNESS);
				p.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("register_successfully"));
				break;
			}
		}
		sender.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("force_register_successfully"));
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		return args.length == 0 ? null : Collections.emptyList();
	}
}
