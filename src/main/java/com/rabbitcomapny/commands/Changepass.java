package com.rabbitcomapny.commands;

import com.rabbitcomapny.Passky;
import com.rabbitcomapny.utils.Hash;
import com.rabbitcomapny.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Changepass implements ICommand {

	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}

		Player player = (Player) sender;
		String uuid = (Passky.getInstance().getConf().getInt("player_identifier", 0) == 0) ? player.getName() : player.getUniqueId().toString();
		Hash hash = Utils.getHash(uuid);

		if(hash == null){
			player.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("changepass_register"));
			return true;
		}

		if(!Passky.isLoggedIn.getOrDefault(player.getUniqueId(), false)){
			player.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("changepass_login_first"));
			return true;
		}

		if(args.length != 2){
			player.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("changepass_syntax"));
			return true;
		}

		String oldPassword = args[0];
		String newPassword = args[1];
		if(oldPassword.equals(newPassword)){
			player.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("changepass_same"));
			return true;
		}

		Hash hash2 = new Hash(hash.algo, oldPassword, hash.salt, false);
		if(!hash2.hash.equals(hash.hash)){
			player.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("changepass_incorrect"));
			return true;
		}

		if(newPassword.length() > Integer.parseInt(Utils.getConfig("max_password_length"))){
			player.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("changepass_too_long"));
			return true;
		}

		if(newPassword.length() < Integer.parseInt(Utils.getConfig("min_password_length"))){
			player.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("changepass_too_short"));
			return true;
		}

		Utils.changePassword(uuid, newPassword);
		player.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("changepass_successfully"));
		return true;
	}
}
