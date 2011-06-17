package com.dalingrin.nookcolortweaks.sysfs;

public class SysfsObj {
	String DIR;
	private String mName;
	private String mValue;
	
	public SysfsObj(String name, String value) {
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
