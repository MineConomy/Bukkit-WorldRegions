package com.wolflink289.bukkit.worldregions.lstn;

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
import com.wolflink289.bukkit.worldregions.flags.Flags;
import com.wolflink289.bukkit.worldregions.util.RegionUtil;

public class EntityListener implements Listener {
	
	@EventHandler(priority = EventPriority.LOW)
	public void onTarget(EntityTargetEvent event) {
		// Check if player, then check if targeting allowed
		if (!(event.getTarget() instanceof Player)) return;
		if (RegionUtil.getFlag(Flags.MOB_TARGETING, event.getTarget().getLocation())) return;

		// Bypass
		if (((Player) event.getTarget()).hasPermission("worldguard.region.flag.flags.mob-targeting")) return;
		
		// Cancel event
		event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onDamage(EntityDamageEvent event) {
		// Check cause, check if damaged by entity
		if (event.getCause() != DamageCause.ENTITY_ATTACK) return;
		if (!(event instanceof EntityDamageByEntityEvent)) return;
		
		// Check if player attacked
		if (event.getEntity() instanceof Player) return;
		
		// Check if damaged by player
		EntityDamageByEntityEvent event2 = (EntityDamageByEntityEvent) event;
		
		if (!(event2.getDamager() instanceof Player)) return;
		if (RegionUtil.getFlag(Flags.PVE, event2.getDamager().getLocation())) return;

		// Bypass
		if (((Player) event2.getDamager()).hasPermission("worldguard.region.flag.flags.mob-targeting")) return;
		
		// Cancel event
		((Player) event2.getDamager()).sendMessage(ChatColor.DARK_RED + "You are in a no-PvE area.");
		event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onDoorBreak(EntityBreakDoorEvent event) {
		// Check if door breaking allowed
		if (RegionUtil.getFlag(Flags.ZOMBIE_DOOR_BREAK, event.getEntity().getLocation())) return;
		
		// Cancel event
		event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onItemSpawn(ItemSpawnEvent event) {
		// Check if door breaking allowed
		if (RegionUtil.getFlag(Flags.ITEM_SPAWN, event.getEntity().getLocation())) return;
		
		// Cancel event
		event.setCancelled(true);
	}
}
