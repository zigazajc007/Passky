package com.rabbitcomapny.commands;

import com.rabbitcomapny.Passky;
import com.rabbitcomapny.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ForceChangePassword implements ICommand {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (args.length != 2) {
			sender.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("force_changepass_syntax"));
			return true;
		}

		String uuid = args[0];
		String password = args[1];

		boolean usernames = Passky.getInstance().getConf().getInt("player_identifier", 0) == 0;

		if(!usernames){
			uuid = Bukkit.getOfflinePlayer(uuid).getUniqueId().toString();
		}

		if (!Utils.isPlayerRegistered(uuid)) {
			sender.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("force_changepass_register"));
			return true;
		}

		if (password.length() > Integer.parseInt(Utils.getConfig("max_password_length"))) {
			sender.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("changepass_too_long"));
			return true;
		}

		if (password.length() < Integer.parseInt(Utils.getConfig("min_password_length"))) {
			sender.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("changepass_too_short"));
			return true;
		}

		Utils.changePassword(uuid, password);
		Utils.removeSession(uuid);

		Player player = (usernames) ? Bukkit.getPlayer(uuid) : Bukkit.getPlayer(UUID.fromString(uuid));
		if(player != null && player.isOnline()){
			if (Passky.isLoggedIn.getOrDefault(player.getUniqueId(), false)) {
				Utils.kickPlayer(player, Utils.getMessages("prefix") + Utils.getMessages("force_changepass_notice"));
			}
		}

		sender.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("force_changepass_successfully"));
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		return args.length == 0 ? null : Collections.emptyList();
	}
}
