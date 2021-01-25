package com.rabbitcomapny.commands;

import com.rabbitcomapny.Passky;
import com.rabbitcomapny.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;
import java.util.List;

public class ForceRegister implements ICommand {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //Arg length
        if (args.length != 2) {
            sender.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("force_register_syntax"));
            return true;
        }
        //Register checking
        if (Passky.getInstance().getPass().contains(args[0])) {
            sender.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("force_register_already"));
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
        //All passed, changing password
        Passky.getInstance().getPass().set(args[0], Utils.getHash(args[1], Utils.getConfig("encoder")));
        Passky.getInstance().savePass();

        //Do online player checking and log in automatically
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getName().equals(args[0])) {
                Passky.isLoggedIn.put(p.getUniqueId(), true);
                p.removePotionEffect(PotionEffectType.BLINDNESS);
                p.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("register_successfully"));
                double damage = Passky.damage.getOrDefault(p.getUniqueId(), 0D);
                if (damage > 0D) p.damage(damage);
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
