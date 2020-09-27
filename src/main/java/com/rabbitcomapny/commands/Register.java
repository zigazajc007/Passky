package com.rabbitcomapny.commands;

import com.rabbitcomapny.Passky;
import com.rabbitcomapny.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;


public class Register implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player)sender;

        if (!Passky.getInstance().getPass().contains(player.getName())) {
            if (!Passky.isLoggedIn.getOrDefault(player, false)) {
                if (args.length == 2) {
                    if (args[0].equals(args[1])) {
                        if (args[0].length() <= Integer.parseInt(Utils.getConfig("max_password_length"))) {
                            if (args[0].length() >= Integer.parseInt(Utils.getConfig("min_password_length"))) {
                                Passky.getInstance().getPass().set(player.getName(), Utils.getHash(args[0], Utils.getConfig("encoder")));
                                Passky.getInstance().savePass();
                                Passky.isLoggedIn.put(player, true);
                                player.removePotionEffect(PotionEffectType.BLINDNESS);
                                player.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("register_successfully"));
                                double damage=Passky.damage.getOrDefault(player, 0D);
                                if(damage > 0D){
                                    player.damage(damage);
                                }
                            } else {
                                player.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("register_too_short"));
                            }
                        } else {
                            player.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("register_too_long"));
                        }
                    } else {
                        player.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("register_doesnt_match"));
                    }
                } else {
                    player.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("register_syntax"));
                }
            } else {
                player.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("register_already"));
            }
        } else {
            player.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("register_account_already"));
        }
        return true;
    }
}
