package com.dalingrin.nookcolortweaks.dac3100;

import java.io.*;

public class DAC3100sysfs {
	public enum GainType {
		dac_level,
		spkr_analog_gain,
		hp_analog_gain,	
	}
	
	private static final String DAC3100_DIR = "/sys/class/misc/tlv320dac3100_tweaks/";
	
	public static boolean setGain(GainType type, int level) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(DAC3100_DIR + type));
			bw.write(level + "\n");
			bw.close();
		} catch (IOException ex) {
			return false;
		}
		return true;
	}
	
	public static Integer read_level(GainType type) {
		String level;
		try {
			BufferedReader br = new BufferedReader(new FileReader(DAC3100_DIR + type));
			level = br.readLine();
			br.close();
		} catch (IOException ex) {
			return -1;
		}
		return Integer.parseInt(level);
	}
	
	public static boolean isKernelCompat() {
		File dacFile = new File(DAC3100_DIR + GainType.dac_level);
		if(!dacFile.canWrite())
			return false;
		
		File spkrFile = new File(DAC3100_DIR + GainType.spkr_analog_gain);
		if(!spkrFile.canWrite())
			return false;
		
		File hpFile = new File(DAC3100_DIR + GainType.hp_analog_gain);
		if(!hpFile.canWrite())
			return false;
		
		return true;
		
	}

}
