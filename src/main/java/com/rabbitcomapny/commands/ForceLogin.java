package com.rabbitcomapny.commands;

import com.rabbitcomapny.api.Identifier;
import com.rabbitcomapny.api.LoginResult;
import com.rabbitcomapny.api.PasskyAPI;
import com.rabbitcomapny.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class ForceLogin implements ICommand {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (args.length != 1) {
			sender.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("force_login_syntax"));
			return true;
		}

		Player player = Bukkit.getPlayer(args[0]);

		if (player == null || !player.isOnline()) {
			sender.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("force_login_player_offline"));
			return true;
		}

		Identifier identifier = new Identifier(player);

		if (!PasskyAPI.isRegistered(identifier)) {
			sender.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("force_login_not_registered"));
			return true;
		}

		LoginResult result = PasskyAPI.forceLogin(identifier, true);

		sender.sendMessage(Utils.getMessages("prefix") + result.message);
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		return args.length == 0 ? null : Collections.emptyList();
	}
}
