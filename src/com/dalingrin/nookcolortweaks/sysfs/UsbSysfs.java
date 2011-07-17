package com.dalingrin.nookcolortweaks.sysfs;

import java.io.File;

public class UsbSysfs extends SysfsObj {
	public enum USBMode {
		host,
		peripheral,
	}
	
	private static final String USB_DIR = "/sys/devices/platform/musb_hdrc/";
	private static final String fileName = "mode";
	
	public UsbSysfs(USBMode mode) {
		super(fileName, mode.toString());
		DIR = USB_DIR;
	}
	
	public UsbSysfs() {
		super(fileName, null);
		DIR = USB_DIR;
	}
	
	public static boolean isKernelCompat() {
		File usbFile = new File(USB_DIR + fileName);
		return usbFile.exists();
	}
}
