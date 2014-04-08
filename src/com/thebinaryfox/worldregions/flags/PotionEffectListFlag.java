package com.thebinaryfox.worldregions.flags;

import java.util.logging.Level;

import org.bukkit.command.CommandSender;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.thebinaryfox.worldregions.WorldRegionsPlugin;
import com.thebinaryfox.worldregions.misc.PotionEffectList;

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
		} catch (NullPointerException ex) {
			ex.printStackTrace();
			throw new InvalidFlagFormat("An error occurred! Please send the developer the stack trace in the console.");
		} catch (RuntimeException ex) {
			throw new InvalidFlagFormat(ex.getMessage());
		}
	}
	
	// Marshalling
	@Override
	public PotionEffectList unmarshal(Object o) {
		try {
			return PotionEffectList.unmarshal(o.toString());
		} catch (NullPointerException ex) {
			WorldRegionsPlugin.getInstanceLogger().log(Level.SEVERE, "An error occurred!", ex);
			return null;
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