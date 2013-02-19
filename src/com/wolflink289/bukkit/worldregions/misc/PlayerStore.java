package com.wolflink289.bukkit.worldregions.misc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import com.wolflink289.bukkit.worldregions.WorldRegionsPlugin;

/**
 * WorldRegions player information storage utility.
 * 
 * @author Wolflink289
 */
public class PlayerStore {
	
	static private boolean setup = false;
	static private ArrayList<PlayerStore> stored_vals = new ArrayList<PlayerStore>();
	static private final Object stored_sync = new Object();
	static private final HashMap<Integer, PlayerStore> stored = new HashMap<Integer, PlayerStore>();
	
	/**
	 * Get and generate (if need be) a PlayerStore object for a player.
	 * 
	 * @param player the player.
	 * @return the PlayerStore object.
	 */
	static public PlayerStore get(Player player) {
		PlayerStore store;
		synchronized (stored_sync) {
			store = stored.get(player.getEntityId());
			if (store == null) {
				store = new PlayerStore(player);
				stored.put(player.getEntityId(), store);
				regenlist();
			}
		}
		return store;
	}
	
	/**
	 * Get all PlayerStore objects.
	 * 
	 * @return all PlayerStore objects.
	 */
	static public List<PlayerStore> all() {
		return stored_vals;
	}
	
	/**
	 * Destroy a PlayerStore object if it exists.
	 * 
	 * @param player the player.
	 */
	static public void destroy(Player player) {
		synchronized (stored_sync) {
			if (stored.containsKey(player.getEntityId())) {
				stored.remove(player.getEntityId());
				regenlist();
			}
		}
	}
	
	/**
	 * Clear all the PlayerStore objects from the PlayerStore cache.
	 */
	static public void clear() {
		synchronized (stored_sync) {
			stored.clear();
		}
	}
	
	static private void regenlist() {
		stored_vals = new ArrayList<PlayerStore>(stored.size());
		PlayerStore[] stored_rval = stored.values().toArray(new PlayerStore[0]);
		
		for (int i = 0; i < stored_rval.length; i++) {
			stored_vals.add(stored_rval[i]);
		}
	}
	
	// Instance
	private PlayerStore(Player player) {
		if (!setup) {
			setup = true;
			WorldRegionsPlugin.getInstance().getServer().getPluginManager().registerEvents(new PlayerStoreListener(), WorldRegionsPlugin.getInstance());
		}
		
		name = player.getName();
	}
	
	/**
	 * Get the player of the player store.
	 * 
	 * @return the player, or null.
	 */
	public Player getPlayer() {
		return Bukkit.getPlayer(name);
	}
	
	private final String name;
	
	public byte orig_state_fly = (byte) -1;
	public byte last_state_fly = (byte) -1;
	public Location last_location = null;
	public PotionEffectList effects = null;
}
