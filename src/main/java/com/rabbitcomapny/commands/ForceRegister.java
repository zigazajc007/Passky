package com.rabbitcomapny.commands;

import com.rabbitcomapny.Passky;
import com.rabbitcomapny.events.SuccessfulRegisterEvent;
import com.rabbitcomapny.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ForceRegister implements ICommand {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (args.length != 2) {
			sender.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("force_register_syntax"));
			return true;
		}

		String uuid = args[0];
		String password = args[1];

		boolean usernames = Passky.getInstance().getConf().getInt("player_identifier", 0) == 0;

		if(!usernames){
			uuid = Bukkit.getOfflinePlayer(uuid).getUniqueId().toString();
		}

		if (Utils.isPlayerRegistered(uuid)) {
			sender.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("force_register_already"));
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

		Utils.savePassword(uuid, password, "0.0.0.0", String.valueOf(System.currentTimeMillis()));

		Player player = (usernames) ? Bukkit.getPlayer(uuid) : Bukkit.getPlayer(UUID.fromString(uuid));
		if(player != null && player.isOnline()){
			Passky.isLoggedIn.put(player.getUniqueId(), true);

			if(Passky.getInstance().getConf().getBoolean("teleport_player_last_location", true)){
				Location loc = Utils.getLastPlayerLocation(uuid);
				if (loc != null) player.teleport(loc);
			}

			player.removePotionEffect(PotionEffectType.BLINDNESS);
			player.resetTitle();
			player.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("register_successfully"));
			Bukkit.getPluginManager().callEvent(new SuccessfulRegisterEvent(player));
		}

		sender.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("force_register_successfully"));
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		return args.length == 0 ? null : Collections.emptyList();
	}
}
