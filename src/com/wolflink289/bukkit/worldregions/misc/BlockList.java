package com.wolflink289.bukkit.worldregions.misc;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.Material;

/**
 * The array list used in the BlockListFlag flag type.
 * 
 * @author Wolflink289
 */
public class BlockList extends ArrayList<Material> {
	private static final long serialVersionUID = 2230002600165589763L;
	
	// Replace toString method
	@Override
	public String toString() {
		String msd = "";
		for (int i = 0; i < size(); i++) {
			Material block = get(i);
			if (i == 0) {
				msd = block.name();
			} else {
				msd = msd + ", " + block.name();
			}
		}
		
		return "[" + msd + "]";
	}
	
	// Parse
	static public BlockList unmarshal(String str) {
		str = str.trim();
		
		if (str.startsWith("[")) str = str.substring(1);
		if (str.endsWith("]")) str = str.substring(0, str.length() - 1);
		
		BlockList blk = new BlockList();
		String[] list = str.split(",");
		for (int i = 0; i < list.length; i++) {
			try {
				blk.add(getType(list[i].trim()));
			} catch (Exception ex) {}
		}
		
		if (blk.isEmpty()) return null;
		return blk;
	}
	
	static public BlockList parse(String str) {
		if (str == null) return null;
		if (str.equals("*")) {
			BlockList all = new BlockList();
			Collections.addAll(all, getTypes());
			return all;
		}
		
		// Split
		str = str.trim();
		String[] blocks = str.split(",");
		
		BlockList blk = new BlockList();
		for (int i = 0; i < blocks.length; i++) {
			// Get data
			blk.add(getType(blocks[i].trim()));
		}
		
		return blk;
	}
	
	// Get potion effect type
	static private Material getType(String str) {
		// By ID
		try {
			Material mat = Material.getMaterial(Integer.parseInt(str));
			if (mat != null && mat.isBlock()) return mat;
		} catch (Exception ex) {}
		
		// By Name
		Material[] types = getTypes();
		for (int i = 0; i < types.length; i++) {
			if (types[i].name().equalsIgnoreCase(str)) return types[i];
		}
		
		for (int i = 0; i < types.length; i++) {
			if (types[i].name().replace("_", "").equalsIgnoreCase(str)) return types[i];
		}
		
		for (int i = 0; i < types.length; i++) {
			if (types[i].name().replace("_", " ").equalsIgnoreCase(str)) return types[i];
		}
		
		throw new RuntimeException("Unknown block: " + str);
	}
	
	// Get all block types via reflection
	static private Material[] typecache = null;
	
	static private Material[] getTypes() {
		if (typecache != null) return typecache;
		
		List<Material> materials = new ArrayList<Material>();
		Field[] materialFields = Material.class.getFields();
		
		for (Field f : materialFields) {
			try {
				Object got = f.get(null);
				if (got instanceof Material) {
					if (!((Material) got).isBlock()) continue;
					
					materials.add((Material) f.get(null));
				}
			} catch (Exception ex) {}
		}
		
		typecache = materials.toArray(new Material[0]);
		return typecache;
	}
}
