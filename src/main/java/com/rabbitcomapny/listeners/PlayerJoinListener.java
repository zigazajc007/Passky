package com.rabbitcomapny.listeners;

import com.rabbitcomapny.Passky;
import com.rabbitcomapny.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerJoinListener implements Listener {

    private Passky passky;

    public PlayerJoinListener(Passky plugin){
        passky = plugin;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Passky.isLoggedIn.put(e.getPlayer(), false);
        if(!Passky.isLoggedIn.getOrDefault(e.getPlayer(), false)){
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 1));
            if(!Passky.getInstance().getPass().contains(e.getPlayer().getName())) {
                e.getPlayer().sendMessage(Utils.getMessages("prefix") + Utils.getMessages("register_syntax"));
            }else{
                e.getPlayer().sendMessage(Utils.getMessages("prefix") + Utils.getMessages("login_syntax"));
            }
        }
    }

}
