package com.dalingrin.nookcolortweaks.romfs;


public class AltbootRomfs extends RomfsObj {
	private static String DEVICE_DIR = "/rom/";
	public AltbootRomfs(String name, String value) {
		super(name, value);
		DIR = DEVICE_DIR;
	}
}