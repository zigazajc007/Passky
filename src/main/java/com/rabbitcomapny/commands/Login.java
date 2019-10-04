package com.rabbitcomapny.commands;

import com.rabbitcomapny.Passky;
import com.rabbitcomapny.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class Login implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        if(Passky.getInstance().getPass().contains(player.getName())) {
            if(!Passky.isLoggedIn.getOrDefault(player, false)) {
                if (args.length == 1) {
                    if (Passky.getInstance().getPass().get(player.getName()).equals(Utils.getHash(args[0], Utils.getConfig("encoder")))) {
                        Passky.isLoggedIn.put(player, true);
                        player.removePotionEffect(PotionEffectType.BLINDNESS);
                        player.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("login_successfully"));
                    } else {
                        player.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("login_incorrect"));
                    }
                } else {
                    player.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("login_syntax"));
                }
            }else{
                player.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("login_already"));
            }
        }else{
            player.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("login_register"));
        }
        return true;
    }
}
