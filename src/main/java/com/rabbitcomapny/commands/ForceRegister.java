package com.rabbitcomapny.commands;

import com.rabbitcomapny.api.Identifier;
import com.rabbitcomapny.api.PasskyAPI;
import com.rabbitcomapny.api.RegisterResult;
import com.rabbitcomapny.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class ForceRegister implements ICommand {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (args.length != 2) {
			sender.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("force_register_syntax"));
			return true;
		}

		Identifier identifier = new Identifier(args[0]);
		String password = args[1];

		RegisterResult result = PasskyAPI.forceRegister(identifier, password, true);

		sender.sendMessage(Utils.getMessages("prefix") + result.message);
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		return args.length == 0 ? null : Collections.emptyList();
	}
}
