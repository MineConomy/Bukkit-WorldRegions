package com.wolflink289.bukkit.worldregions.flags;

import org.bukkit.command.CommandSender;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.wolflink289.bukkit.worldregions.misc.PotionEffectList;

/**
 * A flag containing a list of potion effects.
 * 
 * @author Wolflink289
 */
public class PotionEffectListFlag extends Flag<PotionEffectList> {
	
	// Constructor
	public PotionEffectListFlag(String name, RegionGroup defaultGroup) {
		super(name, defaultGroup);
	}
	
	public PotionEffectListFlag(String name) {
		super(name);
	}
	
	// Parsing
	@Override
	public PotionEffectList parseInput(WorldGuardPlugin plugin, CommandSender sender, String input) throws InvalidFlagFormat {
		try {
			return PotionEffectList.parse(input);
		} catch (RuntimeException ex) {
			ex.printStackTrace();
			throw new InvalidFlagFormat(ex.getMessage());
		}
	}
	
	// Marshalling
	@Override
	public PotionEffectList unmarshal(Object o) {
		try {
			return PotionEffectList.dem(o.toString());
		} catch (RuntimeException ex) {
			return null;
		}
	}
	
	@Override
	public Object marshal(PotionEffectList o) {
		if (o == null) return "";
		return o.toString();
	}
	
}