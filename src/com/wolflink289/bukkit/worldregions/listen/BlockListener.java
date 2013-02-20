package com.wolflink289.bukkit.worldregions.listen;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import com.wolflink289.bukkit.worldregions.WorldRegionsPlugin;
import com.wolflink289.bukkit.worldregions.flags.Flags;
import com.wolflink289.bukkit.worldregions.misc.BlockList;
import com.wolflink289.bukkit.worldregions.misc.WGCommon;
import com.wolflink289.bukkit.worldregions.util.RegionUtil;

public class BlockListener implements Listener {
	
	/**
	 * Listener for: BLOCKED-BREAK, ALLOWED-BREAK
	 */
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled=true)
	public void onBlockDamage(BlockDamageEvent event) {
		if (!handleBreak(event.getPlayer(), event.getBlock())) {
			event.setCancelled(true);
			event.getPlayer().sendMessage(WorldRegionsPlugin.getInstanceConfig().MSG_NO_BREAK);
		}
	}
	
	/**
	 * Listener for: BLOCKED-BREAK, ALLOWED-BREAK
	 */
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled=true)
	public void onBlockBreak(BlockBreakEvent event) {
		if (!handleBreak(event.getPlayer(), event.getBlock())) {
			event.setCancelled(true);
			event.getPlayer().sendMessage(WorldRegionsPlugin.getInstanceConfig().MSG_NO_BREAK);
		}
	}
	
	/**
	 * Listener for: BLOCKED-PLACE, ALLOWED-PLACE
	 */
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled=true)
	public void onBlockPlace(BlockPlaceEvent event) {
		if (!handlePlace(event.getPlayer(), event.getBlock())) {
			event.setCancelled(true);
			event.getPlayer().sendMessage(WorldRegionsPlugin.getInstanceConfig().MSG_NO_PLACE);
		}
	}
	
	private boolean handleBreakAllowed(Player player, Block block) {
		if (WorldRegionsPlugin.getInstanceConfig().ENABLE_ALLOWED_BREAK && WGCommon.willFlagApply(player, Flags.ALLOWED_BREAK)) {
			// Disabled?
			if (WGCommon.areRegionsDisabled(player.getWorld())) return true;
			
			// Not allowed
			if (!WGCommon.willFlagApply(player, Flags.ALLOWED_BREAK)) return true;
			
			// Get blocked
			Object blocked = RegionUtil.getFlag(Flags.ALLOWED_BREAK, block.getLocation());
			if (blocked == null) return true;
			
			// Check
			BlockList list = (BlockList) blocked;
			if (list.contains(block.getType())) { return true; }
			
			return false;
		}
		
		return true;
	}
	
	private boolean handleBreakBlocked(Player player, Block block) {
		if (WorldRegionsPlugin.getInstanceConfig().ENABLE_BLOCKED_BREAK && WGCommon.willFlagApply(player, Flags.BLOCKED_BREAK)) {
			// Disabled?
			if (WGCommon.areRegionsDisabled(player.getWorld())) return true;
			
			// Not allowed
			if (!WGCommon.willFlagApply(player, Flags.BLOCKED_BREAK)) return true;
			
			// Get blocked
			Object blocked = RegionUtil.getFlag(Flags.BLOCKED_BREAK, block.getLocation());
			System.out.println("Blocked Break: " + blocked);
			if (blocked == null) return true;
			
			// Check
			BlockList list = (BlockList) blocked;
			if (list.contains(block.getType())) { return false; }
			
			return true;
		}
		
		return true;
	}
	
	private boolean handlePlaceAllowed(Player player, Block block) {
		if (WorldRegionsPlugin.getInstanceConfig().ENABLE_ALLOWED_PLACE && WGCommon.willFlagApply(player, Flags.ALLOWED_PLACE)) {
			// Disabled?
			if (WGCommon.areRegionsDisabled(player.getWorld())) return true;
			
			// Not allowed
			if (!WGCommon.willFlagApply(player, Flags.ALLOWED_PLACE)) return true;
			
			// Get blocked
			Object blocked = RegionUtil.getFlag(Flags.ALLOWED_PLACE, block.getLocation());
			if (blocked == null) return true;
			
			// Check
			BlockList list = (BlockList) blocked;
			if (list.contains(block.getType())) { return true; }
			
			return false;
		}
		
		return true;
	}
	
	private boolean handlePlaceBlocked(Player player, Block block) {
		if (WorldRegionsPlugin.getInstanceConfig().ENABLE_BLOCKED_PLACE && WGCommon.willFlagApply(player, Flags.BLOCKED_PLACE)) {
			// Disabled?
			if (WGCommon.areRegionsDisabled(player.getWorld())) return true;
			
			// Not allowed
			if (!WGCommon.willFlagApply(player, Flags.BLOCKED_PLACE)) return true;
			
			// Get blocked
			Object blocked = RegionUtil.getFlag(Flags.BLOCKED_PLACE, block.getLocation());
			if (blocked == null) return true;
			
			// Check
			BlockList list = (BlockList) blocked;
			if (list.contains(block.getType())) { return false; }
			
			return true;
		}
		
		return true;
	}
	
	private boolean handleBreak(Player player, Block block) {
		// Enabled?
		if (!WorldRegionsPlugin.getInstanceConfig().ENABLE_ALLOWED_BREAK && !WorldRegionsPlugin.getInstanceConfig().ENABLE_BLOCKED_BREAK) return true;
		
		// ALLOWED-BREAK
		if (!handleBreakAllowed(player, block)) return false;
		
		// BLOCKED-BREAK
		if (!handleBreakBlocked(player, block)) return false;
		
		// ...
		return true;
	}
	
	private boolean handlePlace(Player player, Block block) {
		// Enabled?
		if (!WorldRegionsPlugin.getInstanceConfig().ENABLE_ALLOWED_PLACE && !WorldRegionsPlugin.getInstanceConfig().ENABLE_BLOCKED_PLACE) return true;
		
		// ALLOWED-PLACE
		if (!handlePlaceAllowed(player, block)) return false;
		
		// BLOCKED-PLACE
		if (!handlePlaceBlocked(player, block)) return false;
		
		// ...
		return true;
	}
}
