package com.wolflink289.bukkit.worldregions.lstn;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import com.wolflink289.bukkit.worldregions.flags.Flags;
import com.wolflink289.bukkit.worldregions.util.RegionUtil;

public class PlayerListener implements Listener {
	
	@EventHandler(priority = EventPriority.LOW)
	public void onFoodLevelChance(FoodLevelChangeEvent event) {
		if (!(event.getEntity() instanceof Player)) return;
		if (RegionUtil.getFlag(Flags.HUNGER, event.getEntity().getLocation())) return;
		if (((Player) event.getEntity()).getFoodLevel() < event.getFoodLevel()) return;
		
		// Bypass
		if (((Player) event.getEntity()).hasPermission("worldguard.region.flag.flags.hunger")) return;
		
		// Cancel event
		event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onFoodLevelChance(EntityRegainHealthEvent event) {
		if (!(event.getEntity() instanceof Player)) return;
		
		if (!RegionUtil.getFlag(Flags.HEALING, event.getEntity().getLocation())) {
			
			// Bypass
			if (((Player) event.getEntity()).hasPermission("worldguard.region.flag.flags.healing")) return;
			
			// Cancel event
			event.setCancelled(true);
		} else if (event.getRegainReason() == RegainReason.SATIATED) {
			if (RegionUtil.getFlag(Flags.REGEN, event.getEntity().getLocation())) return;
			
			// Bypass
			if (((Player) event.getEntity()).hasPermission("worldguard.region.flag.flags.regen")) return;
			
			// Cancel event
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onHungerDamage(EntityDamageEvent event) {
		if (event.getCause() != DamageCause.STARVATION) return;
		if (RegionUtil.getFlag(Flags.HUNGER, event.getEntity().getLocation())) return;
		
		// Bypass
		if (((Player) event.getEntity()).hasPermission("worldguard.region.flag.flags.hunger")) return;
		
		// Cancel event
		event.setCancelled(true);
	}
}
