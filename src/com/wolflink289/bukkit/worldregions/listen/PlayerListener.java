package com.wolflink289.bukkit.worldregions.listen;

import java.util.List;
import org.bukkit.entity.EntityType;
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
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.potion.PotionEffect;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.wolflink289.bukkit.worldregions.WorldRegionsFlags;
import com.wolflink289.bukkit.worldregions.WorldRegionsPlugin;
import com.wolflink289.bukkit.worldregions.misc.DamageList;
import com.wolflink289.bukkit.worldregions.misc.PlayerStore;
import com.wolflink289.bukkit.worldregions.misc.PotionEffectList;
import com.wolflink289.bukkit.worldregions.misc.WGCommon;
import com.wolflink289.bukkit.worldregions.util.RegionUtil;

public class PlayerListener implements Listener {
	
	private boolean timedtask = false;
	
	/**
	 * Listener for: HUNGER
	 */
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onFoodLevelChance(FoodLevelChangeEvent event) {
		if (!WorldRegionsPlugin.getInstanceConfig().ENABLE_HUNGER) return;
		
		if (!(event.getEntity() instanceof Player)) return;
		if (RegionUtil.getFlag(WorldRegionsFlags.HUNGER, event.getEntity().getLocation())) return;
		if (((Player) event.getEntity()).getFoodLevel() < event.getFoodLevel()) return;
		
		// Disabled?
		if (WGCommon.areRegionsDisabled(event.getEntity().getWorld())) return;
		
		// Bypass?
		if (!WGCommon.willFlagApply((Player) event.getEntity(), WorldRegionsFlags.HUNGER)) return;
		
		// Cancel event
		event.setCancelled(true);
	}
	
	/**
	 * Listener for: HEALING, REGEN
	 */
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onFoodLevelChance(EntityRegainHealthEvent event) {
		if (!WorldRegionsPlugin.getInstanceConfig().ENABLE_HEALING && !WorldRegionsPlugin.getInstanceConfig().ENABLE_REGEN) return;
		
		if (!(event.getEntity() instanceof Player)) return;
		
		// Disabled?
		if (WGCommon.areRegionsDisabled(event.getEntity().getWorld())) return;
		
		if (WorldRegionsPlugin.getInstanceConfig().ENABLE_HEALING && !RegionUtil.getFlag(WorldRegionsFlags.HEALING, event.getEntity().getLocation())) {
			// Bypass?
			if (!WGCommon.willFlagApply((Player) event.getEntity(), WorldRegionsFlags.HEALING)) return;
			
			// Cancel event
			event.setCancelled(true);
		} else if (WorldRegionsPlugin.getInstanceConfig().ENABLE_REGEN && event.getRegainReason() == RegainReason.SATIATED) {
			if (RegionUtil.getFlag(WorldRegionsFlags.REGEN, event.getEntity().getLocation())) return;
			
			// Bypass?
			if (!WGCommon.willFlagApply((Player) event.getEntity(), WorldRegionsFlags.REGEN)) return;
			
			// Cancel event
			event.setCancelled(true);
		}
	}
	
	/**
	 * Listener for: HUNGER
	 */
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onHungerDamage(EntityDamageEvent event) {
		if (event.getCause() != DamageCause.STARVATION) return;
		if (RegionUtil.getFlag(WorldRegionsFlags.HUNGER, event.getEntity().getLocation())) return;
		
		// Disabled?
		if (WGCommon.areRegionsDisabled(event.getEntity().getWorld())) return;
		
		// Bypass?
		if (!WGCommon.willFlagApply((Player) event.getEntity(), WorldRegionsFlags.HUNGER)) return;
		
		// Cancel event
		event.setCancelled(true);
	}
	
	/**
	 * Listener for: APPLY-POTION, FLY
	 */
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerMove(PlayerMoveEvent event) {
		if (!WorldRegionsPlugin.getInstanceConfig().ENABLE_FLY && !WorldRegionsPlugin.getInstanceConfig().ENABLE_APPLY_POTION) return;
		
		// Disabled?
		if (WGCommon.areRegionsDisabled(event.getTo().getWorld())) return;
		Player player = event.getPlayer();
		
		
		// APPLY-POTION
		if (WorldRegionsPlugin.getInstanceConfig().ENABLE_APPLY_POTION && WGCommon.willFlagApply(player, WorldRegionsFlags.APPLY_POTION)) {
			
			// Get / Check
			Object res = RegionUtil.getFlag(WorldRegionsFlags.APPLY_POTION, event.getTo());
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
		
		// TIME
		if (WorldRegionsPlugin.getInstanceConfig().ENABLE_TIME && WGCommon.willFlagApply(player, WorldRegionsFlags.TIME)) {
			// Get / Check
			Object res = RegionUtil.getFlag(WorldRegionsFlags.TIME, event.getTo());
			if (res != null) {
				
				// Apply
				PlayerStore store = PlayerStore.get(player);
				store.time = (Integer) res;
				player.setPlayerTime(store.time + 18000, false);

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
		if (WorldRegionsPlugin.getInstanceConfig().ENABLE_FLY && WGCommon.willFlagApply(player, null)) {
			// Get / Check
			Object res = RegionUtil.getFlagAsObject(WorldRegionsFlags.FLY, event.getTo());
			PlayerStore store = PlayerStore.get(player);
			
			// Bypass
			if (player.hasPermission("worldregions.bypass.flag.fly")) {
				if (res == StateFlag.State.DENY) return;
			}
			
			// Apply
			if (res == null && store.last_state_fly != -1) {
				if (store.orig_state_fly != (byte) (player.getAllowFlight() ? 1 : 0)) {
					player.setAllowFlight(store.orig_state_fly == 1);
					
					// Message
					String msg = "";
					if (store.orig_state_fly == 0) {
						msg = WorldRegionsPlugin.getInstanceConfig().MSG_FLY_RESET_ALLOW;
					} else {
						msg = WorldRegionsPlugin.getInstanceConfig().MSG_FLY_RESET_BLOCK;
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
							msg = WorldRegionsPlugin.getInstanceConfig().MSG_FLY_SET_ALLOW;
						} else {
							msg = WorldRegionsPlugin.getInstanceConfig().MSG_FLY_SET_BLOCK;
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
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onModeChanged(PlayerGameModeChangeEvent event) {
		Player player = event.getPlayer();
		
		// FLY
		if (WorldRegionsPlugin.getInstanceConfig().ENABLE_FLY && WGCommon.willFlagApply(player, WorldRegionsFlags.FLY)) {
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
	
	/**
	 * Listener for: ITEM-PICKUP
	 */
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onItemSpawn(PlayerPickupItemEvent event) {
		if (!WorldRegionsPlugin.getInstanceConfig().ENABLE_ITEM_PICKUP) return;
		
		// Bypass
		if (!WGCommon.willFlagApply((Player) event.getPlayer(), WorldRegionsFlags.ITEM_PICKUP)) return;
		
		// Check if item pickup allowed
		if (RegionUtil.getFlag(WorldRegionsFlags.ITEM_PICKUP, event.getPlayer().getLocation())) return;
		
		// Disabled?
		if (WGCommon.areRegionsDisabled(event.getPlayer().getWorld())) return;
		
		// Cancel event
		event.setCancelled(true);
	}
	
	/**
	 * Listener for: ALLOWED-DAMAGE, BLOCKED-DAMAGE
	 */
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onDamage(EntityDamageEvent event) {
		if (event.getEntityType() != EntityType.PLAYER) return;
		
		// BLOCKED-BREAK, ALLOWED-BREAK
		if (!handleBreak((Player) event.getEntity(), event.getCause())) {
			event.setCancelled(true);
			return;
		}
	}
	
	// Entity damage stuff
	
	private boolean handleDamageAllowed(Player player, DamageCause type) {
		if (WorldRegionsPlugin.getInstanceConfig().ENABLE_ALLOWED_DAMAGE && WGCommon.willFlagApply(player, WorldRegionsFlags.ALLOWED_DAMAGE)) {
			// Disabled?
			if (WGCommon.areRegionsDisabled(player.getWorld())) return true;
			
			// Bypass?
			if (!WGCommon.willFlagApply(player, WorldRegionsFlags.ALLOWED_DAMAGE)) return true;
			
			// Get blocked
			Object blocked = RegionUtil.getFlag(WorldRegionsFlags.ALLOWED_DAMAGE, player.getLocation());
			if (blocked == null) return true;
			
			// Check
			DamageList list = (DamageList) blocked;
			if (list.contains(type)) return true;
			
			return false;
		}
		
		return true;
	}
	
	private boolean handleDamageBlocked(Player player, DamageCause type) {
		if (WorldRegionsPlugin.getInstanceConfig().ENABLE_BLOCKED_DAMAGE && WGCommon.willFlagApply(player, WorldRegionsFlags.BLOCKED_DAMAGE)) {
			// Disabled?
			if (WGCommon.areRegionsDisabled(player.getWorld())) return true;
			
			// Bypass?
			if (!WGCommon.willFlagApply(player, WorldRegionsFlags.BLOCKED_DAMAGE)) return true;
			
			// Get blocked
			Object blocked = RegionUtil.getFlag(WorldRegionsFlags.BLOCKED_DAMAGE, player.getLocation());
			if (blocked == null) return true;
			
			// Check
			DamageList list = (DamageList) blocked;
			if (list.contains(type)) return false;
			
			return true;
		}
		
		return true;
	}
	
	private boolean handleBreak(Player player, DamageCause type) {
		// Enabled?
		if (!WorldRegionsPlugin.getInstanceConfig().ENABLE_ALLOWED_DAMAGE && !WorldRegionsPlugin.getInstanceConfig().ENABLE_BLOCKED_DAMAGE) return true;
		
		// ALLOWED-BREAK
		if (!handleDamageAllowed(player, type)) return false;
		
		// BLOCKED-BREAK
		if (!handleDamageBlocked(player, type)) return false;
		
		// ...
		return true;
	}
	
	// Tick stuff
	private int counter;
	
	/**
	 * Listener for actions that need to be executed every few ticks.
	 */
	private void onTick() {
		counter++;
		if (counter > 250 * 19) counter = 0;
		
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
		
		// Every 250 ticks
		if (counter % 250 == 0) {
			List<PlayerStore> stores = PlayerStore.all();
			
			for (int i = 0; i < stores.size(); i++) {
				PlayerStore store = stores.get(i);
				Player player = store.getPlayer();
				
				// TIME
				if (store.time != null) {
					player.setPlayerTime(store.time + 18000, false);
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
		Object res = RegionUtil.getFlag(WorldRegionsFlags.APPLY_POTION, player.getLocation());
		if (res == null) {
			store.effects = null;
		} else {
			store.effects = (PotionEffectList) res;
		}
		
		// TIME
		res = RegionUtil.getFlag(WorldRegionsFlags.TIME, player.getLocation());
		if (res == null) {
			store.time = null;
			player.resetPlayerTime();
		} else {
			store.time = (Integer) res;
		}
	}
}
