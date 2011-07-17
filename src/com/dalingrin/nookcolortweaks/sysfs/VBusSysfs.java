package com.dalingrin.nookcolortweaks.sysfs;

import java.io.File;

public class VBusSysfs extends SysfsObj {
	public enum VBusMode {
		internal,
		external,
	}
	
	private static final String VBus_DIR = "/sys/devices/platform/i2c_omap.1/i2c-1/1-0048/twl4030_usb/";
	private static final String fileName = "vbussrc";
	
	public VBusSysfs(VBusMode mode) {
		super(fileName, mode.toString());
		DIR = VBus_DIR;
	}
	
	public VBusSysfs() {
		super(fileName, null);
		DIR = VBus_DIR;
	}

	public static boolean isKernelCompat() {
		File vbusFile = new File(VBus_DIR + fileName);
		return vbusFile.exists();
	}
}
