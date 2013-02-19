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
		APPLY_POTION_ENABLED = cfg.getBoolean("apply-potion.enabled");
		HEALING_ENABLED = cfg.getBoolean("healing.enabled");
		HUNGER_ENABLED = cfg.getBoolean("hunger.enabled");
		ITEM_SPAWN_ENABLED = cfg.getBoolean("item-spawn.enabled");
		MOB_TARGETING_ENABLED = cfg.getBoolean("mob-targeting.enabled");
		PVE_ENABLED = cfg.getBoolean("pve.enabled");
		REGEN_ENABLED = cfg.getBoolean("regen.enabled");
		ZOMBIE_DOOR_BREAK_ENABLED = cfg.getBoolean("zombie-door-break.enabled");
	}
	
	private void defaults() {
		setDefault("apply-potion.enabled", true);
		setDefault("fly.enabled", true);
		setDefault("fly.message.set.allow", "&9You are allowed to fly here.");
		setDefault("fly.message.set.block", "&9You are not allowed to fly here.");
		setDefault("fly.message.reset.allow", "&9You are no longer allowed to fly.");
		setDefault("fly.message.reset.block", "&9You are now allowed to fly again.");
		setDefault("healing.enabled", true);
		setDefault("hunger.enabled", true);
		setDefault("item-spawn.enabled", true);
		setDefault("mob-targeting.enabled", true);
		setDefault("pve.enabled", true);
		setDefault("regen.enabled", true);
		setDefault("zombie-door-break.enabled", true);
	}
	
	private void setDefault(String name, Object value) {
		if (!cfg.contains(name)) {
			cfg.set(name, value);
		}
	}
	
	public final boolean FLY_ENABLED;
	public final boolean APPLY_POTION_ENABLED;
	public final boolean MOB_TARGETING_ENABLED;
	public final boolean PVE_ENABLED;
	public final boolean ITEM_SPAWN_ENABLED;
	public final boolean HUNGER_ENABLED;
	public final boolean REGEN_ENABLED;
	public final boolean HEALING_ENABLED;
	public final boolean ZOMBIE_DOOR_BREAK_ENABLED;
	public final String FLY_MSG_SET_ALLOW;
	public final String FLY_MSG_SET_BLOCK;
	public final String FLY_MSG_RESET_ALLOW;
	public final String FLY_MSG_RESET_BLOCK;
}
