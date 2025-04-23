package com.rabbitcomapny.api;

import com.rabbitcomapny.Passky;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Represents a unified player identifier that can be a name or UUID.
 * Automatically adapts based on Passky's `player_identifier` config.
 * Includes caching for resolved UUID, name, and player instances.
 */
public class Identifier {

	private final String raw;

	private UUID cachedUUID = null;
	private String cachedName = null;
	private OfflinePlayer cachedOffline = null;
	private Player cachedPlayer = null;

	public Identifier(String input) {
		this.raw = input;
	}

	public Identifier(UUID uuid) {
		this.raw = uuid.toString();
		this.cachedUUID = uuid;
	}

	public Identifier(Player player) {
		this.raw = useIdentifierFrom(player.getName(), player.getUniqueId());
		this.cachedName = player.getName();
		this.cachedUUID = player.getUniqueId();
		this.cachedOffline = player;
		this.cachedPlayer = player;
	}

	public Identifier(OfflinePlayer offline) {
		this.raw = useIdentifierFrom(offline.getName(), offline.getUniqueId());
		this.cachedName = offline.getName();
		this.cachedUUID = offline.getUniqueId();
		this.cachedOffline = offline;
	}

	private String useIdentifierFrom(String name, UUID uuid) {
		int mode = Passky.getInstance().getConf().getInt("player_identifier", 0);
		return (mode == 0) ? name : uuid.toString();
	}

	/**
	 * Resolves the identifier string based on the plugin's configuration.
	 *
	 * @return Resolved identifier: name or UUID string depending on config.
	 */
	public String resolve() {
		int mode = Passky.getInstance().getConf().getInt("player_identifier", 0);
		return mode == 0 ? getName() : getUUID().toString();
	}

	/**
	 * Resolves and caches the UUID of the player.
	 *
	 * @return The player's UUID.
	 */
	public UUID getUUID() {
		if (cachedUUID != null) return cachedUUID;

		try {
			cachedUUID = UUID.fromString(raw);
		} catch (IllegalArgumentException e) {
			cachedUUID = Bukkit.getOfflinePlayer(raw).getUniqueId();
		}
		return cachedUUID;
	}

	/**
	 * Resolves and caches the name of the player.
	 *
	 * @return The player's name, or null if not available.
	 */
	public String getName() {
		if (cachedName != null) return cachedName;

		try {
			UUID uuid = UUID.fromString(raw);
			cachedName = Bukkit.getOfflinePlayer(uuid).getName();
		} catch (IllegalArgumentException e) {
			cachedName = raw;
		}
		return cachedName;
	}

	/**
	 * Resolves and caches the OfflinePlayer instance.
	 *
	 * @return The OfflinePlayer corresponding to this identifier.
	 */
	public OfflinePlayer getOfflinePlayer() {
		if (cachedOffline != null) return cachedOffline;

		try {
			cachedOffline = Bukkit.getOfflinePlayer(UUID.fromString(raw));
		} catch (IllegalArgumentException e) {
			cachedOffline = Bukkit.getOfflinePlayer(raw);
		}
		return cachedOffline;
	}

	/**
	 * Resolves and caches the Player instance, if the player is online.
	 *
	 * @return The Player object if online, or null.
	 */
	public Player getPlayer() {
		if (cachedPlayer != null && cachedPlayer.isOnline()) return cachedPlayer;

		cachedPlayer = getOfflinePlayer().getPlayer();
		return cachedPlayer;
	}

	@Override
	public String toString() {
		return resolve();
	}
}