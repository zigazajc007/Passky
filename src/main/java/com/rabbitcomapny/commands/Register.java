package com.rabbitcomapny.commands;

import com.rabbitcomapny.Passky;
import com.rabbitcomapny.utils.Session;
import com.rabbitcomapny.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;


public class Register implements ICommand {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        boolean usernames = Passky.getInstance().getConf().getInt("player_identifier", 0) == 0;
        boolean isPlayerRegistered = (usernames) ? Utils.isPlayerRegistered(player.getName()) : Utils.isPlayerRegistered(player.getUniqueId().toString());

        if (!isPlayerRegistered) {
            if (!Passky.isLoggedIn.getOrDefault(player.getUniqueId(), false)) {
                if (args.length == 2) {
                    if (args[0].equals(args[1])) {
                        if (args[0].length() <= Integer.parseInt(Utils.getConfig("max_password_length"))) {
                            if (args[0].length() >= Integer.parseInt(Utils.getConfig("min_password_length"))) {

                                if (usernames) {
                                    Utils.savePassword(player.getName(), args[0]);
                                }else{
                                    Utils.savePassword(player.getUniqueId().toString(), args[0]);
                                }

                                Passky.isLoggedIn.put(player.getUniqueId(), true);
                                player.removePotionEffect(PotionEffectType.BLINDNESS);
                                player.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("register_successfully"));
                                double damage = Passky.damage.getOrDefault(player.getUniqueId(), 0D);
                                if(damage > 0D) player.damage(damage);
                                if(Passky.getInstance().getConf().getBoolean("session_enabled", false)){
                                    if(player.getAddress() != null && player.getAddress().getAddress() != null)
                                        Passky.session.put(player.getUniqueId(), new Session(player.getAddress().getAddress().toString().replace("/", ""), System.currentTimeMillis()));
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
