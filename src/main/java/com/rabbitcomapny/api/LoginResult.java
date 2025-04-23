package com.rabbitcomapny.api;

import org.bukkit.entity.Player;

public class LoginResult {
	public final boolean success;
	public final LoginStatus status;
	public final String message;
	public final Player player;

	public LoginResult(LoginStatus status, String message, Player player) {
		this.status = status;
		this.success = status == LoginStatus.SUCCESS;
		this.message = message;
		this.player = player;
	}
}