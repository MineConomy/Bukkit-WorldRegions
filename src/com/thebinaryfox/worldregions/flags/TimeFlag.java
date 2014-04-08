package com.thebinaryfox.worldregions.flags;

import java.util.logging.Level;

import org.bukkit.command.CommandSender;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.thebinaryfox.worldregions.WorldRegionsPlugin;
import com.thebinaryfox.worldregions.util.TimeUtil;

/**
 * A flag containing a time as day cycle ticks.
 * 
 * @author Wolflink289
 */
public class TimeFlag extends Flag<Integer> {
	
	// Constructor
	public TimeFlag(String name, RegionGroup defaultGroup) {
		super(name, defaultGroup);
	}
	
	public TimeFlag(String name) {
		super(name);
	}
	
	// Parsing
	@Override
	public Integer parseInput(WorldGuardPlugin plugin, CommandSender sender, String input) throws InvalidFlagFormat {
		try {
			return TimeUtil.timeAsCticks(input);
		} catch (NullPointerException ex) {
			ex.printStackTrace();
			throw new InvalidFlagFormat("An error occurred! Please send the developer the stack trace in the console.");
		} catch (NumberFormatException ex) {
			throw new InvalidFlagFormat(ex.getMessage());
		}
	}
	
	// Marshalling
	@Override
	public Integer unmarshal(Object o) {
		try {
			return Integer.parseInt(o.toString());
		} catch (NullPointerException ex) {
			WorldRegionsPlugin.getInstanceLogger().log(Level.SEVERE, "An error occurred!", ex);
			return null;
		} catch (RuntimeException ex) {
			return null;
		}
	}
	
	@Override
	public Object marshal(Integer o) {
		if (o == null) return "";
		return o.toString();
	}
	
}