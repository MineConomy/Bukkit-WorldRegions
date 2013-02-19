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
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.potion.PotionEffect;
import com.sk89q.worldguard.protection.flags.StateFlag;
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
		if (!WorldRegionsPlugin.getInstance().getConf().HUNGER_ENABLED) return;
		
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
		if (!WorldRegionsPlugin.getInstance().getConf().HEALING_ENABLED && !WorldRegionsPlugin.getInstance().getConf().REGEN_ENABLED) return;
		
		if (!(event.getEntity() instanceof Player)) return;
		
		// Disabled?
		if (WGCommon.areRegionsDisabled(event.getEntity().getWorld())) return;
		
		if (WorldRegionsPlugin.getInstance().getConf().HEALING_ENABLED && !RegionUtil.getFlag(Flags.HEALING, event.getEntity().getLocation())) {
			// Not allowed
			if (!WGCommon.willFlagApply((Player) event.getEntity(), Flags.HEALING)) return;
			
			// Cancel event
			event.setCancelled(true);
		} else if (WorldRegionsPlugin.getInstance().getConf().REGEN_ENABLED && event.getRegainReason() == RegainReason.SATIATED) {
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
	 * Listener for: APPLY-POTION, FLY
	 */
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerMove(PlayerMoveEvent event) {
		if (!WorldRegionsPlugin.getInstance().getConf().FLY_ENABLED && !WorldRegionsPlugin.getInstance().getConf().APPLY_POTION_ENABLED) return;
		
		// Disabled?
		if (WGCommon.areRegionsDisabled(event.getTo().getWorld())) return;
		Player player = event.getPlayer();
		
		
		// APPLY-POTION
		if (WorldRegionsPlugin.getInstance().getConf().APPLY_POTION_ENABLED && WGCommon.willFlagApply(player, Flags.APPLY_POTION)) {
			
			// Get / Check
			Object res = RegionUtil.getFlag(Flags.APPLY_POTION, event.getTo());
			if (res != null) {
				
				// Apply
				PlayerStore store = PlayerStore.get(player);
				store.effects = (PotionEffectList) res;
				
				player.addPotionEffects(store.effects);
				
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
		}
		
		// FLY
		if (WorldRegionsPlugin.getInstance().getConf().FLY_ENABLED && WGCommon.willFlagApply(player, Flags.FLY)) {
			// Get / Check
			Object res = RegionUtil.getFlagAsObject(Flags.FLY, event.getTo());
			PlayerStore store = PlayerStore.get(player);
			
			// Apply
			if (res == null && store.last_state_fly != -1) {
				if (store.orig_state_fly != (byte) (player.getAllowFlight() ? 1 : 0)) {
					player.setAllowFlight(store.orig_state_fly == 1);
					
					// Message
					String msg = "";
					if (store.orig_state_fly == 0) {
						msg = WorldRegionsPlugin.getInstance().getConf().FLY_MSG_RESET_ALLOW;
					} else {
						msg = WorldRegionsPlugin.getInstance().getConf().FLY_MSG_RESET_BLOCK;
					}
					
					if (!msg.isEmpty()) {
						player.sendMessage(msg);
					}
				}
				
				// Clear store
				store.orig_state_fly = (byte) -1;
				store.last_state_fly = store.orig_state_fly;
			} else if (res != null) {
				if (store.last_state_fly == -1 || (byte) (res == StateFlag.State.ALLOW ? 1 : 0) != store.last_state_fly) {
					if (player.getAllowFlight() != (res == StateFlag.State.ALLOW)) {
						if (store.last_state_fly == -1) {
							store.orig_state_fly = (byte) (player.getAllowFlight() ? 1 : 0);
							store.last_state_fly = store.orig_state_fly;
						}
						
						store.last_state_fly = (byte) (res == StateFlag.State.ALLOW ? 1 : 0);
						player.setAllowFlight(res == StateFlag.State.ALLOW);
						
						// Message
						String msg = "";
						if (res == StateFlag.State.ALLOW) {
							msg = WorldRegionsPlugin.getInstance().getConf().FLY_MSG_SET_ALLOW;
						} else {
							msg = WorldRegionsPlugin.getInstance().getConf().FLY_MSG_SET_BLOCK;
						}
						
						if (!msg.isEmpty()) {
							player.sendMessage(msg);
						}
					}
				}
			}
		}
	}
	
	/**
	 * Listener for: APPLY-POTION, FLY
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onModeChanged(PlayerGameModeChangeEvent event) {
		Player player = event.getPlayer();
		
		// FLY
		if (WorldRegionsPlugin.getInstance().getConf().FLY_ENABLED && WGCommon.willFlagApply(player, Flags.FLY)) {
			// Get / Check
			PlayerStore store = PlayerStore.get(player);
			
			if (store.last_state_fly != -1) {
				if (player.getAllowFlight()) {
					store.orig_state_fly = 1;
				} else {
					store.orig_state_fly = 0;
				}
				
				player.setAllowFlight(store.last_state_fly == 1);
			}
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
