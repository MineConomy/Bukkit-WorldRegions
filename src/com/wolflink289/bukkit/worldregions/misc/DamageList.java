package com.wolflink289.bukkit.worldregions.misc;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

/**
 * The array list used in the DamageListFlag flag type.
 * 
 * @author Wolflink289
 */
public class DamageList extends ArrayList<DamageCause> {
	private static final long serialVersionUID = 2230002600165589763L;
	
	// Replace toString method
	@Override
	public String toString() {
		String msd = "";
		for (int i = 0; i < size(); i++) {
			DamageCause block = get(i);
			if (i == 0) {
				msd = block.name();
			} else {
				msd = msd + ", " + block.name();
			}
		}
		
		return "[" + msd + "]";
	}
	
	// Parse
	static public DamageList unmarshal(String str) {
		str = str.trim();
		
		if (str.startsWith("[")) str = str.substring(1);
		if (str.endsWith("]")) str = str.substring(0, str.length() - 1);
		
		DamageList dmg = new DamageList();
		String[] list = str.split(",");
		for (int i = 0; i < list.length; i++) {
			try {
				dmg.add(getType(list[i].trim()));
			} catch (Exception ex) {}
		}
		
		if (dmg.isEmpty()) return null;
		return dmg;
	}
	
	static public DamageList parse(String str) {
		if (str == null) return null;
		
		// Split
		str = str.trim();
		String[] blocks = str.split(",");
		
		DamageList dmg = new DamageList();
		for (int i = 0; i < blocks.length; i++) {
			// Get data
			dmg.add(getType(blocks[0].trim()));
		}
		
		return dmg;
	}
	
	// Get potion effect type
	static private DamageCause getType(String str) {
		// By Name
		DamageCause[] types = getTypes();
		for (int i = 0; i < types.length; i++) {
			if (types[i].name().equalsIgnoreCase(str)) return types[i];
		}
		
		for (int i = 0; i < types.length; i++) {
			if (types[i].name().replace("_", "").equalsIgnoreCase(str)) return types[i];
		}
		
		for (int i = 0; i < types.length; i++) {
			if (types[i].name().replace("_", " ").equalsIgnoreCase(str)) return types[i];
		}
		
		throw new RuntimeException("Unknown damage cause: " + str);
	}
	
	// Get all block types via reflection
	static private DamageCause[] typecache = null;
	
	static private DamageCause[] getTypes() {
		if (typecache != null) return typecache;
		
		List<DamageCause> causes = new ArrayList<DamageCause>();
		Field[] materialFields = DamageCause.class.getFields();
		
		for (Field f : materialFields) {
			try {
				Object got = f.get(null);
				if (got instanceof DamageCause) {
					if (got == DamageCause.CUSTOM || got == DamageCause.FIRE_TICK || got == DamageCause.VOID || got == DamageCause.SUICIDE) continue;
					
					causes.add((DamageCause) f.get(null));
				}
			} catch (Exception ex) {}
		}
		
		typecache = causes.toArray(new DamageCause[0]);
		return typecache;
	}
}
