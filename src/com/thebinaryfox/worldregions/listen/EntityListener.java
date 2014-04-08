package com.thebinaryfox.worldregions.listen;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.entity.ItemSpawnEvent;

import com.thebinaryfox.worldregions.WorldRegionsFlags;
import com.thebinaryfox.worldregions.WorldRegionsPlugin;
import com.thebinaryfox.worldregions.util.RegionUtil;
import com.thebinaryfox.worldregions.util.WGUtil;

public class EntityListener implements Listener {
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onTarget(EntityTargetEvent event) {
		if (!WorldRegionsPlugin.getInstanceConfig().ENABLE_MOB_TARGETING) return;
		
		// Check if player, then check if targeting allowed
		if (!(event.getTarget() instanceof Player)) return;
		if (RegionUtil.getFlag(WorldRegionsFlags.MOB_TARGETING, event.getTarget().getLocation())) return;
		
		// Disabled?
		if (WGUtil.areRegionsDisabled(event.getEntity().getWorld())) return;
		
		// Bypass?
		if (!WGUtil.willFlagApply((Player) event.getTarget(), WorldRegionsFlags.MOB_TARGETING)) return;
		
		// Cancel event
		event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onDamage(EntityDamageEvent event) {
		if (!WorldRegionsPlugin.getInstanceConfig().ENABLE_PVE) return;
		
		// Check cause, check if damaged by entity
		if (event.getCause() != DamageCause.ENTITY_ATTACK) return;
		if (!(event instanceof EntityDamageByEntityEvent)) return;
		
		// Check if player attacked
		if (event.getEntity() instanceof Player) return;
		
		// Disabled?
		if (WGUtil.areRegionsDisabled(event.getEntity().getWorld())) return;
		
		// Check if damaged by player
		EntityDamageByEntityEvent event2 = (EntityDamageByEntityEvent) event;
		
		if (!(event2.getDamager() instanceof Player)) return;
		if (RegionUtil.getFlag(WorldRegionsFlags.PVE, event2.getDamager().getLocation())) return;
		
		// Bypass?
		if (!WGUtil.willFlagApply((Player) event2.getDamager(), WorldRegionsFlags.PVE)) return;
		
		// Cancel event
		((Player) event2.getDamager()).sendMessage(ChatColor.DARK_RED + "You are in a no-PvE area.");
		event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onDoorBreak(EntityBreakDoorEvent event) {
		if (!WorldRegionsPlugin.getInstanceConfig().ENABLE_ZOMBIE_DOOR_BREAK) return;
		
		// Check if door breaking allowed
		if (RegionUtil.getFlag(WorldRegionsFlags.ZOMBIE_DOOR_BREAK, event.getEntity().getLocation())) return;
		
		// Disabled?
		if (WGUtil.areRegionsDisabled(event.getEntity().getWorld())) return;
		
		// Cancel event
		event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onItemSpawn(ItemSpawnEvent event) {
		if (!WorldRegionsPlugin.getInstanceConfig().ENABLE_ITEM_SPAWN) return;
		
		// Check if item spawning allowed
		if (RegionUtil.getFlag(WorldRegionsFlags.ITEM_SPAWN, event.getEntity().getLocation())) return;
		
		// Disabled?
		if (WGUtil.areRegionsDisabled(event.getEntity().getWorld())) return;
		
		// Cancel event
		event.setCancelled(true);
	}
}
