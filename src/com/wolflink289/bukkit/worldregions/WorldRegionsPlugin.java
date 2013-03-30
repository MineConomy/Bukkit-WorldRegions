package com.wolflink289.bukkit.worldregions;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.wolflink289.bukkit.worldregions.listen.BlockListener;
import com.wolflink289.bukkit.worldregions.listen.EntityListener;
import com.wolflink289.bukkit.worldregions.listen.PlayerListener;
import com.wolflink289.bukkit.worldregions.misc.PlayerStore;

public class WorldRegionsPlugin extends JavaPlugin {
	
	// Variables
	static private WorldRegionsConfig config;
	static private Logger log;
	static private WorldRegionsPlugin instance;
	static private WorldGuardPlugin instance_wg;
	static private boolean loaded = false;
	
	// Methods
	/**
	 * Get the instance of the WorldRegionsPlugin.
	 * 
	 * @return the instance of WorldRegionsPlugin.
	 */
	static public WorldRegionsPlugin getInstance() {
		return instance;
	}
	
	/**
	 * Get the instance of WorldGuardPlugin.
	 * 
	 * @return the instance of WorldGuardPlugin.
	 */
	static public WorldGuardPlugin getWorldGuard() {
		return instance_wg;
	}
	
	/**
	 * Get the WorldRegionsPlugin logger.
	 * 
	 * @return the WorldRegionsPlugin logger.
	 */
	static public Logger getInstanceLogger() {
		return log;
	}
	
	static public WorldRegionsConfig getInstanceConfig() {
		return config;
	}
	
	// Listener: Plugin Loaded
	@Override
	public void onLoad() {
		if (loaded) return;
		
		// Set static variables
		instance = this;
		instance_wg = (WorldGuardPlugin) getServer().getPluginManager().getPlugin("WorldGuard");
		log = getLogger();
		
		// Inject flags
		WorldRegionsFlags.inject();
		
		// Set loaded
		loaded = true;
	}
	
	// Listener: Plugin Enabled
	@Override
	public void onEnable() {
		if (!loaded) onLoad();
		
		instance_wg = (WorldGuardPlugin) getServer().getPluginManager().getPlugin("WorldGuard");
		
		config = new WorldRegionsConfig(new File(instance_wg.getDataFolder(), "config_worldregions.yml"));
		
		getServer().getPluginManager().registerEvents(new EntityListener(), instance);
		getServer().getPluginManager().registerEvents(new PlayerListener(), instance);
		getServer().getPluginManager().registerEvents(new BlockListener(), instance);
	}
	
	// Listener: Plugin Disabled
	@Override
	public void onDisable() {
		WorldRegionsFlags.release();
		
		// Reset
		List<PlayerStore> stores = PlayerStore.all();
		
		for (int i = 0; i < stores.size(); i++) {
			PlayerStore store = stores.get(i);
			
			// FLY
			if (store.orig_state_fly != -1) {
				store.getPlayer().setAllowFlight(store.orig_state_fly == 1);
			}
		}
		
		// Finish
		PlayerStore.clear();
		loaded = false;
	}
}
