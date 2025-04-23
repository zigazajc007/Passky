package com.rabbitcomapny.commands;

import com.rabbitcomapny.Passky;
import com.rabbitcomapny.api.Identifier;
import com.rabbitcomapny.api.PasskyAPI;
import com.rabbitcomapny.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class ForceChangePassword implements ICommand {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (args.length != 2) {
			sender.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("force_changepass_syntax"));
			return true;
		}

		Identifier identifier = new Identifier(args[0]);
		String password = args[1];

		if (!PasskyAPI.isRegistered(identifier)) {
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

		Utils.changePassword(identifier, password);
		Utils.removeSession(identifier);

		Player player = identifier.getPlayer();
		if(player != null && player.isOnline()){
			if (Passky.isLoggedIn.getOrDefault(identifier.toString(), false)) {
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
