package com.wolflink289.bukkit.worldregions.misc;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import com.wolflink289.bukkit.worldregions.util.TimeUtil;

/**
 * The array list used in the PotionEffectListFlag flag type.
 * 
 * @author Wolflink289
 */
public class PotionEffectList extends ArrayList<PotionEffect> {
	private static final long serialVersionUID = 2230002600165589763L;
	
	// Replace toString method
	@Override
	public String toString() {
		String msd = "";
		for (int i = 0; i < size(); i++) {
			PotionEffect effect = get(i);
			
			if (i == 0) {
				msd = effect.getType().getName() + " " + (effect.getAmplifier() + 1) + " (" + TimeUtil.ticksAsReadable(effect.getDuration()) + ")";
			} else {
				msd = msd + ", " + effect.getType().getName().trim() + " " + (effect.getAmplifier() + 1) + " (" + TimeUtil.ticksAsReadable(effect.getDuration()) + ")";
			}
		}
		
		return "[" + msd + "]";
	}
	
	// Parse
	static public PotionEffectList unmarshal(String str) {
		str = str.trim();
		
		if (str.startsWith("[")) str = str.substring(1);
		if (str.endsWith("]")) str = str.substring(0, str.length() - 1);
		
		PotionEffectList ef = new PotionEffectList();
		String[] list = str.split(",");
		for (int i = 0; i < list.length; i++) {
			String[] split = list[i].trim().split(" ");
			
			try {
				String durs = split[2].trim();
				durs = durs.substring(1);
				durs = durs.substring(0, durs.length() - 1);
				
				PotionEffectType type = getType(split[0].trim());
				int amp = Integer.parseInt(split[1].trim()) - 1;
				int dur = TimeUtil.readableAsTicks(durs) + 19;
				
				if (dur < 1) continue;
				if (amp < 0) continue;
				
				ef.add(type.createEffect((int) (dur / type.getDurationModifier()), amp));
			} catch (Exception ex) {}
		}
		
		return ef;
	}
	
	static public PotionEffectList parse(String str) {
		if (str == null) return null;
		if (str.equals("*")) {
			PotionEffectList all = new PotionEffectList();
			PotionEffectType[] types = getTypes();
			for (int i = 0; i < types.length; i++) {
				all.add(types[i].createEffect(20 * 60, 0));
			}
			return all;
		}
		if (str.equals(".")) {
			return new PotionEffectList();
		}
		
		// Split
		str = str.trim();
		String[] effects = str.split(",");
		
		PotionEffectList ef = new PotionEffectList();
		for (int i = 0; i < effects.length; i++) {
			String[] split = effects[i].trim().split(":");
			if (split.length != 3) throw new RuntimeException("Expected format: EFFECT:LEVEL:SECONDS, ...");
			
			// Get data
			PotionEffectType type = getType(split[0].trim());
			int amp = Integer.parseInt(split[1].trim()) - 1;
			int dur = Integer.parseInt(split[2].trim()) * 20 + 19;
			
			if (dur < 1) throw new RuntimeException("An invalid duration was supplied.");
			if (amp < 0) throw new RuntimeException("An invalid amplifier was supplied.");
			
			ef.add(type.createEffect((int) (dur / type.getDurationModifier()), amp));
		}
		
		return ef;
	}
	
	// Get potion effect type
	static private PotionEffectType getType(String str) {
		PotionEffectType[] types = getTypes();
		for (int i = 0; i < types.length; i++) {
			if (types[i].getName().equalsIgnoreCase(str)) return types[i];
		}
		
		throw new RuntimeException("Unknown potion effect: " + str);
	}
	
	// Get all potion effect types via reflection
	static private PotionEffectType[] typecache = null;
	
	static private PotionEffectType[] getTypes() {
		if (typecache != null) return typecache;
		
		List<PotionEffectType> effects = new ArrayList<PotionEffectType>();
		Field[] effectFields = PotionEffectType.class.getFields();
		
		for (Field f : effectFields) {
			try {
				if (f.get(null) instanceof PotionEffectType) {
					effects.add((PotionEffectType) f.get(null));
				}
			} catch (Exception ex) {}
		}
		
		typecache = effects.toArray(new PotionEffectType[0]);
		return typecache;
	}
}
