package com.rabbitcomapny.utils;

import com.rabbitcomapny.Passky;
import org.bukkit.ChatColor;
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

    public static boolean sendMessageIfNot(CommandSender sender, boolean bool, String message) {
        if (!bool) {
            sender.sendMessage(message);
        }
        return bool;
    }
}
