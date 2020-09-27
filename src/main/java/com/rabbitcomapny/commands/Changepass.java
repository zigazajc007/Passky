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
        String password = Passky.getInstance().getPass().getString(player.getName());
        if (password != null) {
            if (Passky.isLoggedIn.getOrDefault(player.getUniqueId(), false)) {
                if (args.length == 2) {
                    if (!args[0].equals(args[1])) {
                        if (password.equals(Utils.getHash(args[0], Utils.getConfig("encoder")))) {
                            if (args[1].length() <= Integer.parseInt(Utils.getConfig("max_password_length"))) {
                                if (args[1].length() >= Integer.parseInt(Utils.getConfig("min_password_length"))) {
                                    Passky.getInstance().getPass().set(player.getName(), Utils.getHash(args[1], Utils.getConfig("encoder")));
                                    Passky.getInstance().savePass();
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
