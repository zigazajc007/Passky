package com.rabbitcomapny.utils;

import com.rabbitcomapny.Passky;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

    public static void kickPlayer(Player player, String reason) { player.kickPlayer(chat(reason)); }

    public static String getPassword(String uuid){
        if(Passky.hikari != null){
            String query = "SELECT password FROM passky_players WHERE uuid = '" + uuid + "';";
            try {
                Connection conn = Passky.hikari.getConnection();
                PreparedStatement ps = conn.prepareStatement(query);
                ResultSet rs = ps.executeQuery();
                if(rs.next()){
                    String password = rs.getString("password");
                    rs.close();
                    ps.close();
                    conn.close();
                    return password;
                }
                rs.close();
                ps.close();
                conn.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }else{
            return Passky.getInstance().getPass().getString(uuid);
        }
        return null;
    }

    public static void savePassword(String uuid, String password, String ip, String date){
        if(Passky.hikari != null){
            String query = "INSERT INTO passky_players VALUES('" + uuid + "','" + getHash(password, getConfig("encoder")) + "', '" + ip + "', '" + date + "');";
            try {
                Connection conn = Passky.hikari.getConnection();
                conn.createStatement().executeUpdate(query);
                conn.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }else{
            Passky.getInstance().getPass().set(uuid, getHash(password, getConfig("encoder")));
            Passky.getInstance().savePass();
        }
    }

    public static void changePassword(String uuid, String password){
        if(Passky.hikari != null){
            String query = "UPDATE passky_players SET password = '" + getHash(password, getConfig("encoder")) + "' WHERE uuid = '" + uuid + "';";
            try {
                Connection conn = Passky.hikari.getConnection();
                conn.createStatement().executeUpdate(query);
                conn.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }else{
            Passky.getInstance().getPass().set(uuid, getHash(password, getConfig("encoder")));
            Passky.getInstance().savePass();
        }
    }

    public static boolean isPlayerRegistered(String uuid){
        if(Passky.hikari != null){
            String query = "SELECT COUNT(*) AS amount FROM passky_players WHERE uuid = '" + uuid + "';";
            try {
                Connection conn = Passky.hikari.getConnection();
                PreparedStatement ps = conn.prepareStatement(query);
                ResultSet rs = ps.executeQuery();
                if(rs.next()){
                    if(rs.getInt("amount") == 0){
                        rs.close();
                        ps.close();
                        conn.close();
                        return false;
                    }
                }
                rs.close();
                ps.close();
                conn.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }else{
            return Passky.getInstance().getPass().contains(uuid);
        }
        return true;
    }

    public static Session setSession(String uuid, String ip){
        if(Passky.hikari != null){
            String query = "UPDATE passky_players SET ip = '" + ip + "', date = '" + System.currentTimeMillis() + "' WHERE uuid = '" + uuid + "';";
            try {
                Connection conn = Passky.hikari.getConnection();
                conn.createStatement().executeUpdate(query);
                conn.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }else{
            return Passky.session.put(uuid, new Session(ip, System.currentTimeMillis()));
        }
        return null;
    }

    public static Session getSession(String uuid){
        if(Passky.hikari != null){
            String query = "SELECT ip, date FROM passky_players WHERE uuid = '" + uuid + "';";
            try {
                Connection conn = Passky.hikari.getConnection();
                PreparedStatement ps = conn.prepareStatement(query);
                ResultSet rs = ps.executeQuery();
                if(rs.next()){
                    String ip = rs.getString("ip");
                    long date = rs.getLong("date");
                    rs.close();
                    ps.close();
                    conn.close();
                    return new Session(ip, date);
                }
                rs.close();
                ps.close();
                conn.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }else{
            return Passky.session.getOrDefault(uuid, null);
        }
        return null;
    }

    public static String getHash(String password, String algorithm) {
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            return bytesToHex(digest.digest(password.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            return password;
        }
    }

    public static void savePlayerDamage(Player player){
        int height = 0;

        Location loc = player.getLocation();

        while (!isSafeLocation(loc)){
            if(loc.getY() > 0){
                loc.subtract(0, 1, 0);
            }else{
                loc.add(1, 100, 1);
            }
            if(loc.getBlock().getType() == Material.WATER){
                height = 0;
            }else{
                height++;
            }
        }

        player.teleport(loc);

        Passky.damage.put(player.getUniqueId(), height * 0.5D - 1.5D);
    }

    public static boolean isSafeLocation(Location location) {
        Block feet = location.getBlock();
        if ((!feet.getType().isAir() && feet.getType() != Material.WATER) && (!feet.getLocation().add(0, 1, 0).getBlock().getType().isAir() && feet.getLocation().add(0, 1, 0).getBlock().getType() != Material.WATER)) return false;
        if (!feet.getRelative(BlockFace.UP).getType().isAir() && feet.getRelative(BlockFace.UP).getType() != Material.WATER) return false;
        if (!feet.getRelative(BlockFace.DOWN).getType().isSolid()) return false;
        return true;
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
