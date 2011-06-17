package com.dalingrin.nookcolortweaks.sysfs;

import java.io.File;

public class CPUSysfs extends SysfsObj {
	private static String CPU_DIR = "/sys/power/";
	public CPUSysfs(int opp, int mhz) {
		super("mpu_freq_opp" + Integer.toString(opp), Integer.toString(mhz));
		DIR = CPU_DIR;
	}
	
	public static boolean isKernelCompat() {
		File mpuFile = new File(CPU_DIR + "mpu_freq_opp1");
		if(!mpuFile.exists())
			return false;
		
		mpuFile = new File(CPU_DIR + "mpu_freq_opp2");
		if(!mpuFile.exists())
			return false;
		
		mpuFile = new File(CPU_DIR + "mpu_freq_opp3");
		if(!mpuFile.exists())
			return false;
		
		mpuFile = new File(CPU_DIR + "mpu_freq_opp4");
		if(!mpuFile.exists())
			return false;
		
		mpuFile = new File(CPU_DIR + "mpu_freq_opp5");
		if(!mpuFile.exists())
			return false;
		
		return true;
	}
}
