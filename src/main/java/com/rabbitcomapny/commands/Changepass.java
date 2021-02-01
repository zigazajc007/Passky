package com.rabbitcomapny.commands;

import com.rabbitcomapny.Passky;
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

        boolean usernames = Passky.getInstance().getConf().getInt("player_identifier", 0) == 0;
        String password = (usernames) ? Utils.getPassword(player.getName()) : Utils.getPassword(player.getUniqueId().toString());

        if (password != null) {
            if (Passky.isLoggedIn.getOrDefault(player.getUniqueId(), false)) {
                if (args.length == 2) {
                    if (!args[0].equals(args[1])) {
                        if (password.equals(Utils.getHash(args[0], Utils.getConfig("encoder")))) {
                            if (args[1].length() <= Integer.parseInt(Utils.getConfig("max_password_length"))) {
                                if (args[1].length() >= Integer.parseInt(Utils.getConfig("min_password_length"))) {
                                    if (usernames) {
                                        Utils.savePassword(player.getName(), args[0]);
                                    } else {
                                        Utils.savePassword(player.getUniqueId().toString(), args[0]);
                                    }
                                    player.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("changepass_successfully"));
                                } else {
                                    player.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("changepass_too_short"));
                                }
                            } else {
                                player.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("changepass_too_long"));
                            }
                        } else {
                            player.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("changepass_incorrect"));
                        }
                    } else {
                        player.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("changepass_same"));
                    }
                } else {
                    player.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("changepass_syntax"));
                }
            } else {
                player.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("changepass_login_first"));
            }
        } else {
            player.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("changepass_register"));
        }
        return true;
    }
}
