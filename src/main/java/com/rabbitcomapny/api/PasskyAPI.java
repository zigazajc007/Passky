package com.rabbitcomapny.api;

import com.rabbitcomapny.Passky;
import com.rabbitcomapny.events.SuccessfulLoginEvent;
import com.rabbitcomapny.events.SuccessfulRegisterEvent;
import com.rabbitcomapny.utils.Session;
import com.rabbitcomapny.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class PasskyAPI {

	/**
	 * Checks if the player is already registered.
	 *
	 * @param identifier The player identifier.
	 * @return true if the player is registered, false otherwise.
	 */
	public static boolean isRegistered(Identifier identifier){
		return Utils.isPlayerRegistered(identifier);
	}

	/**
	 * Checks if the player is already logged in.
	 *
	 * @param identifier Player identifier.
	 * @return true if the player is logged in, false otherwise.
	 */
	public static boolean isLoggedIn(Identifier identifier){
		if (Passky.getInstance().getConf().getBoolean("session_enabled", false)) {
			Session session = Utils.getSession(identifier);
			if (session != null) return true;
		}

		return Passky.isLoggedIn.getOrDefault(identifier.toString(), false);
	}

	/**
	 * Forcefully logs in a player without any password validation.
	 *
	 * This should be used only in trusted contexts (e.g., other plugins or admin commands).
	 * It performs the standard login actions: marking as logged in, teleporting,
	 * removing blindness, sending success message (optional), and firing events.
	 *
	 * @param identifier Player identifier to log in.
	 * @param notifyPlayer Whether to send success messages to the player.
	 * @return {@link LoginResult} representing the outcome.
	 */
	public static LoginResult forceLogin(Identifier identifier, boolean notifyPlayer) {
		Player player = identifier.getPlayer();

		if (player == null || !player.isOnline()) {
			return new LoginResult(LoginStatus.UNKNOWN_ERROR, Utils.getMessages("force_login_player_offline"), null);
		}

		if (Passky.isLoggedIn.getOrDefault(identifier.toString(), false)) {
			return new LoginResult(LoginStatus.ALREADY_LOGGED_IN, Utils.getMessages("force_login_already"), player);
		}

		Passky.isLoggedIn.put(identifier.toString(), true);
		Passky.failures.remove(identifier.toString());

		if (Passky.getInstance().getConf().getBoolean("teleport_player_last_location", true)) {
			Location loc = Utils.getLastPlayerLocation(identifier);
			if (loc != null) player.teleport(loc);
		}

		player.removePotionEffect(PotionEffectType.BLINDNESS);
		player.resetTitle();
		if (notifyPlayer) player.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("force_login_successfully"));
		Bukkit.getPluginManager().callEvent(new SuccessfulLoginEvent(player));

		if (Passky.getInstance().getConf().getBoolean("session_enabled", false)) {
			if (player.getAddress() != null && player.getAddress().getAddress() != null) {
				Utils.setSession(identifier, player.getAddress().getAddress().toString().replace("/", ""));
			}
		}

		return new LoginResult(LoginStatus.SUCCESS, Utils.getMessages("force_login_successfully"), player);
	}

	/**
	 * Forcefully registers a player with a given identifier.
	 *
	 * This method performs all necessary validation, saves the password,
	 * logs in the player (if online), teleports them to their last known location
	 * if configured, removes blindness, and fires a {@code SuccessfulRegisterEvent}.
	 *
	 * If {@code notifyPlayer} is true and the player is online, they will receive a
	 * success message in chat. This is useful when called in-game or through other user-facing commands.
	 *
	 * @param identifier     Player identifier
	 * @param password       The password to register the player with.
	 * @param notifyPlayer   Whether to send registration success message to the player (if online).
	 * @return {@link RegisterResult} containing success status, error reason (if any), and the {@link Player} instance if online.
	 */
	public static RegisterResult forceRegister(Identifier identifier, String password, boolean notifyPlayer) {

		if (Utils.isPlayerRegistered(identifier)) {
			return new RegisterResult(RegisterStatus.ALREADY_REGISTERED, Utils.getMessages("force_register_already"), null);
		}

		int maxLen = Integer.parseInt(Utils.getConfig("max_password_length"));
		int minLen = Integer.parseInt(Utils.getConfig("min_password_length"));

		if (password.length() > maxLen) {
			return new RegisterResult(RegisterStatus.PASSWORD_TOO_LONG, Utils.getMessages("changepass_too_long"), null);
		}

		if (password.length() < minLen) {
			return new RegisterResult(RegisterStatus.PASSWORD_TOO_SHORT, Utils.getMessages("changepass_too_short"), null);
		}

		try {
			Utils.savePassword(identifier, password, "0.0.0.0", String.valueOf(System.currentTimeMillis()));
			Player player = identifier.getPlayer();

			if (player != null && player.isOnline()) {
				Passky.isLoggedIn.put(identifier.toString(), true);

				if (Passky.getInstance().getConf().getBoolean("teleport_player_last_location", true)) {
					Location loc = Utils.getLastPlayerLocation(identifier);
					if (loc != null) player.teleport(loc);
				}

				player.removePotionEffect(PotionEffectType.BLINDNESS);
				player.resetTitle();
				if (notifyPlayer) player.sendMessage(Utils.getMessages("prefix") + Utils.getMessages("register_successfully"));
				Bukkit.getPluginManager().callEvent(new SuccessfulRegisterEvent(player));
			}

			return new RegisterResult(RegisterStatus.SUCCESS, Utils.getMessages("force_register_successfully"), player);
		} catch (Exception e) {
			return new RegisterResult(RegisterStatus.UNKNOWN_ERROR, "An unknown error occurred.", null);
		}
	}

}
