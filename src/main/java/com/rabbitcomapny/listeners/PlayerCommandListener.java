package com.rabbitcomapny.listeners;

import com.rabbitcomapny.Passky;
import com.rabbitcomapny.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class PlayerCommandListener implements Listener {

    private final Passky passky;

    public PlayerCommandListener(Passky plugin){
        passky = plugin;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
        if (!Passky.isLoggedIn.getOrDefault(e.getPlayer().getUniqueId(), false)) {
            if (!e.getMessage().toLowerCase().startsWith("/login") && !e.getMessage().toLowerCase().startsWith("/register") && !e.getMessage().toLowerCase().startsWith("/log") && !e.getMessage().toLowerCase().startsWith("/reg") && !e.getMessage().toLowerCase().startsWith("/l") && !e.getMessage().toLowerCase().startsWith("/r")) {
                e.setCancelled(true);

                boolean usernames = Passky.getInstance().getConf().getInt("player_identifier", 0) == 0;
                boolean isPlayerRegistered = (usernames) ? Utils.isPlayerRegistered(e.getPlayer().getName()) : Utils.isPlayerRegistered(e.getPlayer().getUniqueId().toString());

                if (!isPlayerRegistered) {
                    e.getPlayer().sendMessage(Utils.getMessages("prefix") + Utils.getMessages("register_syntax"));
                } else {
                    e.getPlayer().sendMessage(Utils.getMessages("prefix") + Utils.getMessages("login_syntax"));
                }
            }
        }
    }

}
