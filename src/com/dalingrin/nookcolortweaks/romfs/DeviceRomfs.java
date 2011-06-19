package com.dalingrin.nookcolortweaks.romfs;

public class DeviceRomfs extends RomfsObj {
	private static String DEVICE_DIR = "/rom/";
	public DeviceRomfs(String name, String value) {
		super(name, value);
		DIR = DEVICE_DIR;
	}
}
