package com.rabbitcomapny.listeners;

import com.rabbitcomapny.Passky;
import com.rabbitcomapny.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;




public class PlayerJoinListener implements Listener {

    private Passky passky;

    public PlayerJoinListener(Passky plugin) {
        this.passky = plugin;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent e) {
        Passky.failures.put(e.getPlayer(), 0);
        Passky.isLoggedIn.put(e.getPlayer(), false);
        Passky.damage.put(e.getPlayer(), 0D);

        if (!Passky.isLoggedIn.getOrDefault(e.getPlayer(), false)) {

            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 2147483647, 1));

            int height = 0;

            Location loc = e.getPlayer().getLocation();

            while (loc.getBlock().getType() == Material.AIR || loc.getBlock().getType() == Material.LADDER) {
                loc.setY(loc.getBlockY() - 1);

                if (loc.getBlock().getType() == Material.WATER || loc.getBlock().getType() == Material.LADDER || loc.getBlock().getType() == Material.SLIME_BLOCK || loc.getBlock().getType() == Material.LAVA) {
                    height = 0;
                }else{
                    height++;
                }
            }

            e.getPlayer().teleport(loc.add(0,1,0));

            Passky.damage.put(e.getPlayer(), height * 0.5D - 1.5D);

            if (!Passky.getInstance().getPass().contains(e.getPlayer().getName())) {
                e.getPlayer().sendMessage(Utils.getMessages("prefix") + Utils.getMessages("register_syntax"));
            } else {
                e.getPlayer().sendMessage(Utils.getMessages("prefix") + Utils.getMessages("login_syntax"));
            }

            Bukkit.getScheduler().runTaskLater(Passky.getInstance(), () -> {
                if (!Passky.isLoggedIn.getOrDefault(e.getPlayer(), false) && e.getPlayer().isOnline()) {
                    Utils.kickPlayer(e.getPlayer(), Utils.getMessages("prefix") + Utils.getMessages("login_time").replace("{time}", Utils.getConfig("time_before_kick")));
                }
            }, Passky.getInstance().getConf().getInt("time_before_kick") * 20);
        }
    }
}
