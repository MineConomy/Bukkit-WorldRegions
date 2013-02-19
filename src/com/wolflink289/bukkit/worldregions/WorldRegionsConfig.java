package com.wolflink289.bukkit.worldregions;

import java.io.File;
import org.bukkit.configuration.file.YamlConfiguration;

public class WorldRegionsConfig {
	
	private YamlConfiguration cfg;
	
	public WorldRegionsConfig(File file) {
		// Load
		cfg = new YamlConfiguration();
		
		try {
			cfg.load(file);
		} catch (Exception ex) {}
		
		// Defaults
		defaults();
		
		// Save
		try {
			cfg.save(file);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		// Read
		FLY_ENABLED = cfg.getBoolean("fly.enabled");
		FLY_MSG_SET_ALLOW = cfg.getString("fly.message.set.allow").trim().replace('&', '\247');
		FLY_MSG_SET_BLOCK = cfg.getString("fly.message.set.block").trim().replace('&', '\247');
		FLY_MSG_RESET_ALLOW = cfg.getString("fly.message.reset.allow").trim().replace('&', '\247');
		FLY_MSG_RESET_BLOCK = cfg.getString("fly.message.reset.block").trim().replace('&', '\247');
	}
	
	private void defaults() {
		setDefault("fly.enabled", true);
		setDefault("fly.message.set.allow", "&9You are allowed to fly here.");
		setDefault("fly.message.set.block", "&9You are not allowed to fly here.");
		setDefault("fly.message.reset.allow", "&9You are no longer allowed to fly.");
		setDefault("fly.message.reset.block", "&9You are now allowed to fly again.");
	}
	
	private void setDefault(String name, Object value) {
		if (!cfg.contains(name)) {
			cfg.set(name, value);
		}
	}
	
	public final boolean FLY_ENABLED;
	public final String FLY_MSG_SET_ALLOW;
	public final String FLY_MSG_SET_BLOCK;
	public final String FLY_MSG_RESET_ALLOW;
	public final String FLY_MSG_RESET_BLOCK;
}
