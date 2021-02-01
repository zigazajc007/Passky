package com.rabbitcomapny.commands;

import com.rabbitcomapny.Passky;
import com.rabbitcomapny.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class ForceChangePassword implements ICommand {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //Arg length
        if (args.length != 2) {
            sender.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("force_changepass_syntax"));
            return true;
        }
        boolean usernames = Passky.getInstance().getConf().getInt("player_identifier", 0) == 0;
        boolean isPlayerRegistered = usernames && Utils.isPlayerRegistered(args[0]);
        //Register checking
        if (!isPlayerRegistered) {
            sender.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("force_changepass_register"));
            return true;
        }
        //Password length
        if (args[1].length() > Integer.parseInt(Utils.getConfig("max_password_length"))) {
            sender.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("changepass_too_long"));
            return true;
        }
        if (args[1].length() < Integer.parseInt(Utils.getConfig("min_password_length"))) {
            sender.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("changepass_too_short"));
            return true;
        }
        //All passed, do register
        Utils.savePassword(args[0], args[1]);
        //Do online player checking and kick player
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getName().equals(args[0])) {
                if (Passky.isLoggedIn.getOrDefault(p.getUniqueId(), false)) {
                    Utils.kickPlayer(p, Utils.getMessages("prefix") + Utils.getMessages("force_changepass_notice"));
                }
                break;
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
