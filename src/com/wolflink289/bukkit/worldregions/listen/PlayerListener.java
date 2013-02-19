package com.wolflink289.bukkit.worldregions.listen;

import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import com.wolflink289.bukkit.worldregions.WorldRegionsPlugin;
import com.wolflink289.bukkit.worldregions.flags.Flags;
import com.wolflink289.bukkit.worldregions.misc.PlayerStore;
import com.wolflink289.bukkit.worldregions.misc.PotionEffectList;
import com.wolflink289.bukkit.worldregions.misc.WGCommon;
import com.wolflink289.bukkit.worldregions.util.RegionUtil;

public class PlayerListener implements Listener {
	
	private boolean timedtask = false;
	
	/**
	 * Listener for: HUNGER
	 */
	@EventHandler(priority = EventPriority.LOW)
	public void onFoodLevelChance(FoodLevelChangeEvent event) {
		if (!(event.getEntity() instanceof Player)) return;
		if (RegionUtil.getFlag(Flags.HUNGER, event.getEntity().getLocation())) return;
		if (((Player) event.getEntity()).getFoodLevel() < event.getFoodLevel()) return;
		
		// Disabled?
		if (WGCommon.areRegionsDisabled(event.getEntity().getWorld())) return;
		
		// Not allowed
		if (!WGCommon.willFlagApply((Player) event.getEntity(), Flags.HUNGER)) return;
		
		// Cancel event
		event.setCancelled(true);
	}
	
	/**
	 * Listener for: HEALING, REGEN
	 */
	@EventHandler(priority = EventPriority.LOW)
	public void onFoodLevelChance(EntityRegainHealthEvent event) {
		if (!(event.getEntity() instanceof Player)) return;
		
		// Disabled?
		if (WGCommon.areRegionsDisabled(event.getEntity().getWorld())) return;
		
		if (!RegionUtil.getFlag(Flags.HEALING, event.getEntity().getLocation())) {
			// Not allowed
			if (!WGCommon.willFlagApply((Player) event.getEntity(), Flags.HEALING)) return;
			
			// Cancel event
			event.setCancelled(true);
		} else if (event.getRegainReason() == RegainReason.SATIATED) {
			if (RegionUtil.getFlag(Flags.REGEN, event.getEntity().getLocation())) return;
			
			// Not allowed
			if (!WGCommon.willFlagApply((Player) event.getEntity(), Flags.REGEN)) return;
			
			// Cancel event
			event.setCancelled(true);
		}
	}
	
	/**
	 * Listener for: HUNGER
	 */
	@EventHandler(priority = EventPriority.LOW)
	public void onHungerDamage(EntityDamageEvent event) {
		if (event.getCause() != DamageCause.STARVATION) return;
		if (RegionUtil.getFlag(Flags.HUNGER, event.getEntity().getLocation())) return;
		
		// Disabled?
		if (WGCommon.areRegionsDisabled(event.getEntity().getWorld())) return;
		
		// Not allowed
		if (!WGCommon.willFlagApply((Player) event.getEntity(), Flags.HUNGER)) return;
		
		// Cancel event
		event.setCancelled(true);
	}
	
	/**
	 * Listener for: APPLY-POTION
	 */
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerMove(PlayerMoveEvent event) {
		// Disabled?
		if (WGCommon.areRegionsDisabled(event.getTo().getWorld())) return;
		
		// Not allowed
		if (!WGCommon.willFlagApply((Player) event.getPlayer(), Flags.APPLY_POTION)) return;
		
		// Get / Check
		Object res = RegionUtil.getFlag(Flags.APPLY_POTION, event.getTo());
		if (res == null) return;
		
		// Apply
		PlayerStore store = PlayerStore.get(event.getPlayer());
		store.effects = (PotionEffectList) res;
		
		event.getPlayer().addPotionEffects(store.effects);
		
		// Start?
		if (!timedtask) {
			timedtask = true;
			WorldRegionsPlugin.getInstance().getServer().getScheduler().scheduleSyncRepeatingTask(WorldRegionsPlugin.getInstance(), new Runnable() {
				@Override
				public void run() {
					onTick();
				}
			}, 1, 1);
		}
	}
	
	// Tick stuff
	private int counter;
	
	/**
	 * Listener for actions that need to be executed every few ticks.
	 */
	private void onTick() {
		counter++;
		if (counter > 19) counter = 0;
		
		// Every 19 ticks
		if (counter % 19 == 0) {
			List<PlayerStore> stores = PlayerStore.all();
			
			for (int i = 0; i < stores.size(); i++) {
				PlayerStore store = stores.get(i);
				Player player = store.getPlayer();
				
				// Update
				onTickRegionUpdate(store, player);
				
				// Do actions
				// APPLY-POTION
				if (store.effects != null) {
					for (int j = 0; j < store.effects.size(); j++) {
						PotionEffect effect = store.effects.get(j);
						player.removePotionEffect(effect.getType());
						player.addPotionEffect(effect);
					}
				}
			}
		}
	}
	
	/**
	 * Update cached information for a player
	 */
	private void onTickRegionUpdate(PlayerStore store, Player player) {
		if (player.getLocation().equals(store.last_location)) return;
		store.last_location = player.getLocation().clone();
		
		// APPLY-POTION
		Object res = RegionUtil.getFlag(Flags.APPLY_POTION, player.getLocation());
		if (res == null) {
			store.effects = null;
		} else {
			store.effects = (PotionEffectList) res;
		}
	}
}
