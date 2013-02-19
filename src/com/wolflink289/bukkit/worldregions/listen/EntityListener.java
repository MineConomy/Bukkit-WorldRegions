package com.wolflink289.bukkit.worldregions.listen;

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
import com.wolflink289.bukkit.worldregions.WorldRegionsPlugin;
import com.wolflink289.bukkit.worldregions.flags.Flags;
import com.wolflink289.bukkit.worldregions.misc.WGCommon;
import com.wolflink289.bukkit.worldregions.util.RegionUtil;

public class EntityListener implements Listener {
	
	@EventHandler(priority = EventPriority.LOW)
	public void onTarget(EntityTargetEvent event) {
		if (!WorldRegionsPlugin.getInstance().getConf().MOB_TARGETING_ENABLED) return;
		
		// Check if player, then check if targeting allowed
		if (!(event.getTarget() instanceof Player)) return;
		if (RegionUtil.getFlag(Flags.MOB_TARGETING, event.getTarget().getLocation())) return;

		// Disabled?
		if (WGCommon.areRegionsDisabled(event.getEntity().getWorld())) return;

		// Not allowed
		if (!WGCommon.willFlagApply((Player) event.getTarget(), Flags.MOB_TARGETING)) return;
		
		// Cancel event
		event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onDamage(EntityDamageEvent event) {
		if (!WorldRegionsPlugin.getInstance().getConf().PVE_ENABLED) return;
		
		// Check cause, check if damaged by entity
		if (event.getCause() != DamageCause.ENTITY_ATTACK) return;
		if (!(event instanceof EntityDamageByEntityEvent)) return;
		
		// Check if player attacked
		if (event.getEntity() instanceof Player) return;

		// Disabled?
		if (WGCommon.areRegionsDisabled(event.getEntity().getWorld())) return;
		
		// Check if damaged by player
		EntityDamageByEntityEvent event2 = (EntityDamageByEntityEvent) event;
		
		if (!(event2.getDamager() instanceof Player)) return;
		if (RegionUtil.getFlag(Flags.PVE, event2.getDamager().getLocation())) return;

		// Not allowed
		if (!WGCommon.willFlagApply((Player) event2.getDamager(), Flags.PVE)) return;
		
		// Cancel event
		((Player) event2.getDamager()).sendMessage(ChatColor.DARK_RED + "You are in a no-PvE area.");
		event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onDoorBreak(EntityBreakDoorEvent event) {
		if (!WorldRegionsPlugin.getInstance().getConf().ZOMBIE_DOOR_BREAK_ENABLED) return;
		
		// Check if door breaking allowed
		if (RegionUtil.getFlag(Flags.ZOMBIE_DOOR_BREAK, event.getEntity().getLocation())) return;

		// Disabled?
		if (WGCommon.areRegionsDisabled(event.getEntity().getWorld())) return;
		
		// Cancel event
		event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onItemSpawn(ItemSpawnEvent event) {
		if (!WorldRegionsPlugin.getInstance().getConf().ITEM_SPAWN_ENABLED) return;
		
		// Check if door breaking allowed
		if (RegionUtil.getFlag(Flags.ITEM_SPAWN, event.getEntity().getLocation())) return;

		// Disabled?
		if (WGCommon.areRegionsDisabled(event.getEntity().getWorld())) return;
		
		// Cancel event
		event.setCancelled(true);
	}
}
