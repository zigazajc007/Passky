package com.rabbitcomapny.commands;

import com.rabbitcomapny.Passky;
import com.rabbitcomapny.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;



public class Login implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player)sender;

        String password=Passky.getInstance().getPass().getString(player.getName());
        if (password!=null) {
            if (!Passky.isLoggedIn.getOrDefault(player, false)) {
                if (args.length == 1) {
                    if (password.equals(Utils.getHash(args[0], Utils.getConfig("encoder")))) {
                        Passky.isLoggedIn.put(player, true);
                        player.removePotionEffect(PotionEffectType.BLINDNESS);
                        player.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("login_successfully"));
                        double damage=Passky.damage.getOrDefault(player, 0D);
                        if(damage > 0D){
                            player.damage(damage);
                        }
                    } else {
                        int attempts=Passky.failures.merge(player,1, Integer::sum);
                        if (attempts >= Passky.getInstance().getConf().getInt("attempts")) {
                            Passky.failures.put(player, 0);
                            player.kickPlayer(Utils.getMessages("prefix") + Utils.getMessages("login_too_many_attempts"));
                        }
                        player.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("login_incorrect"));
                    }
                } else {
                    player.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("login_syntax"));
                }
            } else {
                player.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("login_already"));
            }
        } else {
            player.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("login_register"));
        }
        return true;
    }
}
