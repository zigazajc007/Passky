package com.rabbitcomapny.events;

import org.bukkit.event.HandlerList;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;

public class SuccessfulRegisterEvent extends PlayerEvent {
	private static final HandlerList HANDLERS = new HandlerList();

	public SuccessfulRegisterEvent(Player player) {
		super(player);
	}
	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

}