package com.dalingrin.nookcolortweaks.sysfs;

import java.io.File;

public class AudioSysfs extends SysfsObj {
	private static String DAC3100_DIR =  "/sys/class/misc/tlv320dac3100_tweaks/";
	public enum GainType {
		dac_level,
		spkr_analog_gain,
		hp_analog_gain,	
	}
	
	public AudioSysfs(GainType type, int gain) {
		super(type.toString(), Integer.toString(gain));
		DIR = DAC3100_DIR;
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
