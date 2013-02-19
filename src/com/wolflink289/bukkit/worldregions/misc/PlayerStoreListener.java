package com.wolflink289.bukkit.worldregions.misc;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * The listener to destroy PlayerStore objects when a player leaves.
 * 
 * @author Wolflink289
 */
public class PlayerStoreListener implements Listener {
	
	@EventHandler(priority = EventPriority.LOW)
	public void onQuit(PlayerQuitEvent event) {
		PlayerStore.destroy(event.getPlayer());
	}
	
	public void onKick(PlayerKickEvent event) {
		PlayerStore.destroy(event.getPlayer());
	}
	
}
