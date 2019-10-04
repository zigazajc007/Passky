package com.rabbitcomapny.listeners;

import com.rabbitcomapny.Passky;
import com.rabbitcomapny.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChatListener implements Listener {

    private Passky passky;

    public PlayerChatListener(Passky plugin){
        passky = plugin;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e){
        if(!Passky.isLoggedIn.getOrDefault(e.getPlayer(), false)){
            e.setCancelled(true);

            if(!Passky.getInstance().getPass().contains(e.getPlayer().getName())) {
                e.getPlayer().sendMessage(Utils.getMessages("prefix") + Utils.getMessages("register_syntax"));
            }else{
                e.getPlayer().sendMessage(Utils.getMessages("prefix") + Utils.getMessages("login_syntax"));
            }
        }
    }

}
