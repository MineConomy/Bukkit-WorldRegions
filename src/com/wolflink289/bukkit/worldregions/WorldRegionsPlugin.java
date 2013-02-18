package com.wolflink289.bukkit.worldregions;

import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.wolflink289.bukkit.worldregions.flags.Flags;
import com.wolflink289.bukkit.worldregions.lstn.EntityListener;
import com.wolflink289.bukkit.worldregions.lstn.PlayerListener;

public class WorldRegionsPlugin extends JavaPlugin {
	
	// Variables
	static private Logger log;
	static private WorldRegionsPlugin instance;
	static private WorldGuardPlugin instance_wg;
	
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
	
	// Listener: Plugin Loaded
	@Override
	public void onLoad() {
		// Set static variables
		instance = this;
		instance_wg = (WorldGuardPlugin) getServer().getPluginManager().getPlugin("WorldGuard");
		log = getLogger();
		
		// Inject flags
		Flags.inject();
	}
	
	// Listener: Plugin Enabled
	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(new EntityListener(), instance);
		getServer().getPluginManager().registerEvents(new PlayerListener(), instance);
	}
	
	// Listener: Plugin Disabled
	@Override
	public void onDisable() {
		Flags.release();
	}
}