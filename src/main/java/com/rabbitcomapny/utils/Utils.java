package com.rabbitcomapny.utils;

import com.rabbitcomapny.Passky;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Set;

public class Utils {

    public static String chat(String s){
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static String getConfig(String config){
        return chat(Passky.getInstance().getConf().getString(config));
    }

    public static String getMessages(String config){
        return chat(Passky.getInstance().getMess().getString(config));
    }

    public static String getPassword(String config){
        return chat(Passky.getInstance().getPass().getString(config));
    }

    public static void kickPlayer(Player player, String reason) { player.kickPlayer(chat(reason)); }

    public static String getHash(String password, String algorithm) {
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            return bytesToHex(digest.digest(password.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            return password;
        }
    }

    public static void damagePlayerWithHeight(Player player){
        int height = 0;

        Location loc = player.getLocation();

        while (loc.getBlock().getType() == Material.AIR || loc.getBlock().getType() == Material.LADDER) {
            loc.setY(loc.getBlockY() - 1);

            if (loc.getBlock().getType() == Material.WATER || loc.getBlock().getType() == Material.LADDER || loc.getBlock().getType() == Material.SLIME_BLOCK || loc.getBlock().getType() == Material.LAVA) {
                height = 0;
            }else{
                height++;
            }
        }

        player.teleport(loc.add(0,1,0));

        Passky.damage.put(player.getUniqueId(), height * 0.5D - 1.5D);
    }

    public static void mergeYaml(String origin, File old) {
        InputStream stream = Passky.getInstance().getResource(origin);
        if (stream != null) {
            YamlConfiguration originYaml = YamlConfiguration.loadConfiguration(new InputStreamReader(stream));
            YamlConfiguration oldYaml = YamlConfiguration.loadConfiguration(old);
            Set<String> oldSet = oldYaml.getKeys(true);
            for (String originYamlKey : originYaml.getKeys(true)) {
                if (!oldSet.contains(originYamlKey)) {
                    oldYaml.set(originYamlKey, originYaml.get(originYamlKey));
                }
            }
            try {
                oldYaml.save(old);
            } catch (IOException e) {
                throw new RuntimeException("Cannot save old yaml file", e);
            }
        }
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
