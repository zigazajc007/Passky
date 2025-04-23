package com.rabbitcomapny.api;

import org.bukkit.entity.Player;

/**
 * Encapsulates the result of a forced registration attempt.
 */
public class RegisterResult {
	public final boolean success;
	public final RegisterStatus status;
	public final String message;
	public final Player player;

	public RegisterResult(RegisterStatus status, String message, Player player) {
		this.status = status;
		this.success = status == RegisterStatus.SUCCESS;
		this.message = message;
		this.player = player;
	}
}
