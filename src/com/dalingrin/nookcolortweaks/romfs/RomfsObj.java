package com.dalingrin.nookcolortweaks.romfs;

public class RomfsObj {
	String DIR;
	private String mName;
	private String mValue;
	
	public RomfsObj(String name, String value) {
		mName = name;
		mValue = value;
	}
	
	public String getName() {
		return mName;
	}
	
	public String getValue() {
		return mValue;
	}
	
	public String getFile() {
		return DIR + mName;
	}
}
