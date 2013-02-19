package com.wolflink289.bukkit.worldregions.util;

public class TimeUtil {
	
	// Constructor
	
	
	// Methods
	/**
	 * Convert ticks to a human readable HH:MM:SS time format.
	 * 
	 * @param ticks the ticks to convert.
	 * @return a human readable time format.
	 */
	static public String ticksAsReadable(int ticks) {
		int tc = (int) Math.ceil(ticks / 20d);
		int s = tc % 60;
		int m = ((int) Math.floor(tc / 60d)) % 60;
		int h = (int) Math.floor(tc / 60d / 60d);
		
		if (h > 0) {
			return pad(h, 1) + ":" + pad(m, 2) + ":" + pad(s, 2);
		} else {
			return pad(m, 1) + ":" + pad(s, 2);
		}
	}
	
	/**
	 * Convert a human readable HH:MM:SS time format to ticks.
	 * 
	 * @param hrtf the human readable time format.
	 * @return the time in ticks.
	 */
	static public int readableAsTicks(String hrtf) {
		String[] split = hrtf.split(":");
		
		int h = 0;
		int m;
		int s;
		
		int i = 0;
		
		if (split.length == 3) h = Integer.parseInt(split[i++]);
		m = Integer.parseInt(split[i++]);
		s = Integer.parseInt(split[i]);
		
		return (h * 60 * 60 + m * 60 + s) * 20;
	}
	
	// Utility in a utility
	static private String pad(long number, int padding) {
		String padded = String.valueOf(number);
		while (padded.length() < padding) {
			padded = "0" + padded;
		}
		return padded;
	}
}
