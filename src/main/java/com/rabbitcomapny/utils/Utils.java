package com.rabbitcomapny.utils;

import com.rabbitcomapny.Passky;
import com.rabbitcomapny.api.Identifier;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Utils {

	public static String chat(String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}

	public static String getConfig(String config) {
		return chat(Passky.getInstance().getConf().getString(config));
	}

	public static String getMessages(String config) {
		return chat(Passky.getInstance().getMess().getString(config, "Missing translation: " + config));
	}

	public static void kickPlayer(Player player, String reason) {
		player.kickPlayer(chat(reason));
	}

	public static Hash getHash(Identifier identifier) {
		String identi = identifier.toString();
		if (Passky.hikari != null) {
			String query = "SELECT algo, hash, salt FROM passky_players WHERE uuid = ?;";
			try {
				Connection conn = Passky.hikari.getConnection();
				PreparedStatement ps = conn.prepareStatement(query);
				ps.setString(1, identi);
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					String algo = rs.getString("algo");
					String hash = rs.getString("hash");
					String salt = rs.getString("salt");
					rs.close();
					ps.close();
					conn.close();
					return new Hash(algo, hash, salt, true);
				}
				rs.close();
				ps.close();
				conn.close();
			} catch (SQLException throwables) {
				throwables.printStackTrace();
			}
		} else {
			String algo = Passky.getInstance().getPass().getString(identi + ".algo");
			String hash = Passky.getInstance().getPass().getString(identi + ".hash");
			String salt = Passky.getInstance().getPass().getString(identi + ".salt");
			return (algo != null && hash != null && salt != null) ? new Hash(algo, hash, salt, true) : null;
		}
		return null;
	}

	public static void savePassword(Identifier identifier, String password, String ip, String date) {
		String identi = identifier.toString();

		Hash hash = new Hash(getConfig("encoder"), password);
		if (Passky.hikari != null) {
			String query = "INSERT INTO passky_players VALUES(?,?,?,?,?,?);";
			try {
				Connection conn = Passky.hikari.getConnection();
				PreparedStatement ps = conn.prepareStatement(query);
				ps.setString(1, identi);
				ps.setString(2, hash.algo);
				ps.setString(3, hash.hash);
				ps.setString(4, hash.salt);
				ps.setString(5, ip);
				ps.setString(6, date);
				ps.executeUpdate();
				ps.close();
				conn.close();
			} catch (SQLException throwables) {
				throwables.printStackTrace();
			}
		} else {
			Passky.getInstance().getPass().set(identi + ".algo", hash.algo);
			Passky.getInstance().getPass().set(identi + ".hash", hash.hash);
			Passky.getInstance().getPass().set(identi + ".salt", hash.salt);
			Passky.getInstance().savePass();
		}
	}

	public static void changePassword(Identifier identifier, String password) {
		String identi = identifier.toString();

		Hash hash = new Hash(getConfig("encoder"), password);
		if (Passky.hikari != null) {
			String query = "UPDATE passky_players SET algo = ?, hash = ?, salt = ? WHERE uuid = ?;";
			try {
				Connection conn = Passky.hikari.getConnection();
				PreparedStatement ps = conn.prepareStatement(query);
				ps.setString(1, hash.algo);
				ps.setString(2, hash.hash);
				ps.setString(3, hash.salt);
				ps.setString(4, identi);
				ps.executeUpdate();
				ps.close();
				conn.close();
			} catch (SQLException throwables) {
				throwables.printStackTrace();
			}
		} else {
			Passky.getInstance().getPass().set(identi + ".algo", hash.algo);
			Passky.getInstance().getPass().set(identi + ".hash", hash.hash);
			Passky.getInstance().getPass().set(identi + ".salt", hash.salt);
			Passky.getInstance().savePass();
		}
	}

	public static boolean isPlayerRegistered(Identifier identifier) {
		String identi = identifier.toString();

		if (Passky.hikari != null) {
			String query = "SELECT COUNT(*) AS amount FROM passky_players WHERE uuid = ?;";
			try {
				Connection conn = Passky.hikari.getConnection();
				PreparedStatement ps = conn.prepareStatement(query);
				ps.setString(1, identi);
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					if (rs.getInt("amount") == 0) {
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
		} else {
			return Passky.getInstance().getPass().contains(identi + ".hash");
		}
		return true;
	}

	public static Session setSession(Identifier identifier, String ip) {
		if (Passky.hikari != null) {
			String query = "UPDATE passky_players SET ip = ?, date = ? WHERE uuid = ?;";
			try {
				Connection conn = Passky.hikari.getConnection();
				PreparedStatement ps = conn.prepareStatement(query);
				ps.setString(1, ip);
				ps.setString(2, String.valueOf(System.currentTimeMillis()));
				ps.setString(3, identifier.toString());
				ps.executeUpdate();
				ps.close();
				conn.close();
			} catch (SQLException throwables) {
				throwables.printStackTrace();
			}
		} else {
			return Passky.session.put(identifier.toString(), new Session(ip, System.currentTimeMillis()));
		}
		return null;
	}

	public static void removeSession(Identifier identifier) {
		if (Passky.hikari != null) {
			String query = "UPDATE passky_players SET date = ? WHERE uuid = ?;";
			try {
				Connection conn = Passky.hikari.getConnection();
				PreparedStatement ps = conn.prepareStatement(query);
				ps.setString(1, "0");
				ps.setString(2, identifier.toString());
				ps.executeUpdate();
				ps.close();
				conn.close();
			} catch (SQLException throwables) {
				throwables.printStackTrace();
			}
		} else {
			Passky.session.remove(identifier.toString());
		}
	}

	public static Session getSession(Identifier identifier) {
		if (Passky.hikari != null) {
			String query = "SELECT ip, date FROM passky_players WHERE uuid = ?;";
			try {
				Connection conn = Passky.hikari.getConnection();
				PreparedStatement ps = conn.prepareStatement(query);
				ps.setString(1, identifier.toString());
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
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
		} else {
			return Passky.session.getOrDefault(identifier.toString(), null);
		}
		return null;
	}

	public static Location getLastPlayerLocation(Identifier identifier) {
		String identi = identifier.toString();

		String sWorld = Passky.getInstance().getPass().getString(identi + ".loc.world");
		double x = Passky.getInstance().getPass().getDouble(identi + ".loc.x");
		double y = Passky.getInstance().getPass().getDouble(identi + ".loc.y");
		double z = Passky.getInstance().getPass().getDouble(identi + ".loc.z");
		float yaw = (float) Passky.getInstance().getPass().getDouble(identi + ".loc.yaw");
		float pitch = (float) Passky.getInstance().getPass().getDouble(identi + ".loc.pitch");
		if (sWorld == null) return null;
		World world = Bukkit.getServer().getWorld(sWorld);
		return new Location(world, x, y, z, yaw, pitch);
	}

	public static void saveLastPlayerLocation(Identifier identifier, Location loc) {
		String identi = identifier.toString();

		Passky.getInstance().getPass().set(identi + ".loc.world", loc.getWorld().getName());
		Passky.getInstance().getPass().set(identi + ".loc.x", loc.getX());
		Passky.getInstance().getPass().set(identi + ".loc.y", loc.getY());
		Passky.getInstance().getPass().set(identi + ".loc.z", loc.getZ());
		Passky.getInstance().getPass().set(identi + ".loc.yaw", loc.getYaw());
		Passky.getInstance().getPass().set(identi + ".loc.pitch", loc.getPitch());
		Passky.getInstance().savePass();
	}

}
