package com.thebinaryfox.worldregions;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;
import java.util.logging.Level;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.thebinaryfox.worldregions.flags.BlockListFlag;
import com.thebinaryfox.worldregions.flags.DamageListFlag;
import com.thebinaryfox.worldregions.flags.PotionEffectListFlag;
import com.thebinaryfox.worldregions.flags.TimeFlag;

public final class WorldRegionsFlags {

	// Flags
	static public final StateFlag PVE = new StateFlag("pve", true, RegionGroup.ALL);
	static public final StateFlag MOB_TARGETING = new StateFlag("mob-targeting", true, RegionGroup.ALL);
	static public final StateFlag ZOMBIE_DOOR_BREAK = new StateFlag("zombie-door-break", true);
	static public final StateFlag ITEM_SPAWN = new StateFlag("item-spawn", true);
	static public final StateFlag HUNGER = new StateFlag("hunger", true, RegionGroup.ALL);
	static public final StateFlag REGEN = new StateFlag("regen", true, RegionGroup.ALL);
	static public final StateFlag HEALING = new StateFlag("healing", true, RegionGroup.ALL);
	static public final StateFlag FLY = new StateFlag("fly", true, RegionGroup.ALL);
	static public final StateFlag ITEM_PICKUP = new StateFlag("item-pickup", true, RegionGroup.ALL);
	static public final StateFlag INSTABREAK = new StateFlag("instabreak", false, RegionGroup.ALL);
	static public final PotionEffectListFlag APPLY_POTION = new PotionEffectListFlag("apply-potion", RegionGroup.ALL);
	static public final BlockListFlag BLOCKED_PLACE = new BlockListFlag("blocked-place", RegionGroup.ALL);
	static public final BlockListFlag BLOCKED_BREAK = new BlockListFlag("blocked-break", RegionGroup.ALL);
	static public final BlockListFlag ALLOWED_PLACE = new BlockListFlag("allowed-place", RegionGroup.ALL);
	static public final BlockListFlag ALLOWED_BREAK = new BlockListFlag("allowed-break", RegionGroup.ALL);
	static public final DamageListFlag BLOCKED_DAMAGE = new DamageListFlag("blocked-damage", RegionGroup.ALL);
	static public final DamageListFlag ALLOWED_DAMAGE = new DamageListFlag("allowed-damage", RegionGroup.ALL);
	static public final TimeFlag TIME = new TimeFlag("time", RegionGroup.ALL);

	static public final Flag<?>[] defaults = new Flag<?>[] { PVE, MOB_TARGETING, ZOMBIE_DOOR_BREAK, ITEM_SPAWN, HUNGER, REGEN, HEALING, APPLY_POTION, FLY, BLOCKED_PLACE, BLOCKED_BREAK, ALLOWED_PLACE, ALLOWED_BREAK, ITEM_PICKUP, INSTABREAK, ALLOWED_DAMAGE, BLOCKED_DAMAGE, TIME };
	static public final Flag<?>[] originals = DefaultFlag.getFlags();

	// Get Flags
	/**
	 * Get an array of all custom flags injected into WorldGuard.
	 * 
	 * @return an array of flags.
	 */
	static public Flag<?>[] getCustomFlags() {
		return defaults;
	}

	/**
	 * Get an array of all original flags included in WorldGuard.
	 * 
	 * @return an array of flags.
	 */
	static public Flag<?>[] getOriginalFlags() {
		return originals;
	}

	// Loading/Unloading
	static private boolean injected = false;

	/**
	 * Inject region flags in WorldGuard.
	 */
	static public void inject() {
		if (injected)
			return;

		try {
			// Define
			boolean fa;
			boolean ff;
			Field flagsList;
			Field flagsListMod;

			// Get: fields required for reflection
			flagsList = DefaultFlag.class.getDeclaredField("flagsList");
			flagsListMod = Field.class.getDeclaredField("modifiers");

			// Check: field access
			fa = flagsList.isAccessible();
			ff = Modifier.isFinal(flagsList.getModifiers());

			// Access: set accessible
			if (fa) {
				flagsList.setAccessible(true);
			}

			// Access: set non-final
			if (ff) {
				flagsListMod.setAccessible(true);
				flagsListMod.setInt(flagsList, flagsList.getModifiers() & ~Modifier.FINAL);
			}

			// Create: array
			Flag<?>[] flags = getCustomFlags();
			Flag<?>[] oldFlags = (Flag<?>[]) flagsList.get(null);
			Flag<?>[] newFlags = new Flag<?>[oldFlags.length + flags.length];

			// Create: populate array (originals)
			for (int i = 0; i < oldFlags.length; i++) {
				newFlags[i] = oldFlags[i];
			}

			// Create: populate array (new)
			for (int i = 0; i < flags.length; i++) {
				newFlags[oldFlags.length + i] = flags[i];
			}

			// Sort
			Arrays.sort(newFlags, new Comparator<Flag<?>>() {

				@Override
				public int compare(Flag<?> o1, Flag<?> o2) {
					return o1.getName().compareTo(o2.getName());
				}

			});

			// Set: field
			flagsList.set(null, newFlags);

			// Reset access changes
			if (ff) {
				flagsListMod.setInt(flagsList, flagsList.getModifiers() | Modifier.FINAL);
				flagsListMod.setAccessible(false);
			}

			if (fa) {
				flagsList.setAccessible(false);
			}

			// Done!
			injected = true;

			WorldRegionsPlugin.getInstanceLogger().info("Injected flags!");
		} catch (Throwable ex) {
			WorldRegionsPlugin.getInstanceLogger().log(Level.SEVERE, "Unable to inject flags!", ex);
		}
	}

	/**
	 * Release injected flags in WorldGuard.
	 */
	static public void release() {
		if (!injected)
			return;

		try {
			// Define
			boolean fa;
			boolean ff;
			Field flagsList;
			Field flagsListMod;

			// Get: fields required for reflection
			flagsList = DefaultFlag.class.getDeclaredField("flagsList");
			flagsListMod = Field.class.getDeclaredField("modifiers");

			// Check: field access
			fa = flagsList.isAccessible();
			ff = Modifier.isFinal(flagsList.getModifiers());

			// Access: set accessible
			if (fa) {
				flagsList.setAccessible(true);
			}

			// Access: set non-final
			if (ff) {
				flagsListMod.setAccessible(true);
				flagsListMod.setInt(flagsList, flagsList.getModifiers() & ~Modifier.FINAL);
			}

			// Set: field
			flagsList.set(null, getOriginalFlags());

			// Reset access changes
			if (ff) {
				flagsListMod.setInt(flagsList, flagsList.getModifiers() | Modifier.FINAL);
				flagsListMod.setAccessible(false);
			}

			if (fa) {
				flagsList.setAccessible(false);
			}

			// Done!
			injected = false;

			WorldRegionsPlugin.getInstanceLogger().info("Released flags!");
		} catch (Throwable ex) {
			WorldRegionsPlugin.getInstanceLogger().log(Level.SEVERE, "Unable to release flags!", ex);
		}
	}
}
