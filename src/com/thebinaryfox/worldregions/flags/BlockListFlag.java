package com.thebinaryfox.worldregions.flags;

import java.util.logging.Level;

import org.bukkit.command.CommandSender;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.thebinaryfox.worldregions.WorldRegionsPlugin;
import com.thebinaryfox.worldregions.misc.BlockList;

/**
 * A flag containing a list of blocks.
 * 
 * @author Wolflink289
 */
public class BlockListFlag extends Flag<BlockList> {

	// Constructor
	public BlockListFlag(String name, RegionGroup defaultGroup) {
		super(name, defaultGroup);
	}

	public BlockListFlag(String name) {
		super(name);
	}

	// Parsing
	@Override
	public BlockList parseInput(WorldGuardPlugin plugin, CommandSender sender, String input) throws InvalidFlagFormat {
		try {
			return BlockList.parse(input);
		} catch (NullPointerException ex) {
			ex.printStackTrace();
			throw new InvalidFlagFormat("An error occurred! Please send the developer the stack trace in the console.");
		} catch (RuntimeException ex) {
			throw new InvalidFlagFormat(ex.getMessage());
		}
	}

	// Marshalling
	@Override
	public BlockList unmarshal(Object o) {
		try {
			return BlockList.unmarshal(o.toString());
		} catch (NullPointerException ex) {
			WorldRegionsPlugin.getInstanceLogger().log(Level.SEVERE, "An error occurred!", ex);
			return null;
		} catch (RuntimeException ex) {
			return null;
		}
	}

	@Override
	public Object marshal(BlockList o) {
		if (o == null)
			return "";
		return o.toString();
	}

}