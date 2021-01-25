package com.rabbitcomapny.listeners;

import com.rabbitcomapny.Passky;
import com.rabbitcomapny.utils.Session;
import com.rabbitcomapny.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;




public class PlayerJoinListener implements Listener {

    private final Passky passky;

    public PlayerJoinListener(Passky plugin) {
        this.passky = plugin;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent e) {

        if(Passky.getInstance().getConf().getBoolean("kick_illegal_usernames", true)){
            if(!e.getPlayer().getName().matches("[a-zA-Z0-9_]+") || e.getPlayer().getName().length() < 3 || e.getPlayer().getName().length() > 16){
                Utils.kickPlayer(e.getPlayer(), Utils.getMessages("illegal_username_kick"));
                return;
            }
        }

        Passky.failures.put(e.getPlayer().getUniqueId(), 0);
        Passky.isLoggedIn.put(e.getPlayer().getUniqueId(), false);
        Passky.damage.put(e.getPlayer().getUniqueId(), 0D);

        if(Passky.getInstance().getConf().getBoolean("session_enabled", false)){
            if(Passky.session.getOrDefault(e.getPlayer().getUniqueId(), null) != null){
                Session session = Passky.session.get(e.getPlayer().getUniqueId());
                if(e.getPlayer().getAddress() != null && e.getPlayer().getAddress().getAddress() != null){
                    if(session.ip.equals(e.getPlayer().getAddress().getAddress().toString().replace("/", "")) && (session.date + Passky.getInstance().getConf().getInt("session_time", 30) * 60000L) > System.currentTimeMillis()) {
                        Passky.isLoggedIn.put(e.getPlayer().getUniqueId(), true);
                        Utils.damagePlayerWithHeight(e.getPlayer());
                        double damage = Passky.damage.getOrDefault(e.getPlayer().getUniqueId(), 0D);
                        if (damage > 0D) e.getPlayer().damage(damage);
                        e.getPlayer().sendMessage(Utils.getMessages("prefix") + Utils.getMessages("login_successfully"));
                    }
                }
            }
        }

        if (!Passky.isLoggedIn.getOrDefault(e.getPlayer().getUniqueId(), false)) {

            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 2147483647, 1));

            Utils.damagePlayerWithHeight(e.getPlayer());

            if (!Passky.getInstance().getPass().contains(e.getPlayer().getName())) {
                e.getPlayer().sendMessage(Utils.getMessages("prefix") + Utils.getMessages("register_syntax"));
            } else {
                e.getPlayer().sendMessage(Utils.getMessages("prefix") + Utils.getMessages("login_syntax"));
            }

            Bukkit.getScheduler().runTaskLater(Passky.getInstance(), () -> {
                if (!Passky.isLoggedIn.getOrDefault(e.getPlayer().getUniqueId(), false) && e.getPlayer().isOnline()) {
                    Utils.kickPlayer(e.getPlayer(), Utils.getMessages("prefix") + Utils.getMessages("login_time").replace("{time}", Utils.getConfig("time_before_kick")));
                }
            }, Passky.getInstance().getConf().getInt("time_before_kick", 30) * 20L);
        }
    }
}
