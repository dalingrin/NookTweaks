package com.dalingrin.nookcolortweaks.voltage;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import android.util.Log;

public class OmapOPP {
	public enum OPP {
		OPP0(0, 0), OPP1(0x20, 15), OPP2(0x2d, 17), OPP3(0x38, 18), OPP4(0x3c, 19), OPP5(0x3e, 20);
		
		private int vsel;
		private int offset;
		private OPP(int vsel, int offset) {
			this.vsel = vsel;
			this.offset = offset;
		}
		public int getOffset() {
			return offset;
		}
		public int getVSel() {
			return vsel;
		}
	};
	
	private int stepping;
	private int seekBarOffset;
	private int vsel;
	private int stockVSel;
	
	private static final int MAX_VSEL = 0x43;
	private static final int MIN_VSEL = 0x10;
	private static final String VSEL_FILE = "/sys/power/vsel_control";
	private static final String TAG = "OmapOPP";
	
	public OmapOPP(int stepping) {
		this.stepping = stepping;
		OPP[] opps = OPP.values();
		this.seekBarOffset = opps[stepping].getOffset();
		stockVSel = opps[stepping].getVSel(); 
		vsel = getKernelVSel();
	}
	
	public void resetToStock() {
		vsel = stockVSel;
	}
	
	public int getStepping() {
		return stepping;
	}	
	
	public void setProgress(int progress) {
		vsel = stockVSel + (progress - seekBarOffset);
	}
	
	public int getProgress() {
		return vsel - stockVSel + seekBarOffset;
	}
	
	public int getSeekBarOffset() {
		return seekBarOffset;
	}
	
	static public int vsel_to_uV(int vsel) {
		return (((vsel * 125) + 6000)) * 100;
	}
	
	public float getVoltage() {
		return (float)vsel_to_uV(vsel) / 1000000;
	}
	
	public float getStockVoltage() {
		return (float)vsel_to_uV(stockVSel) / 1000000;
	}
	
	public int getKernelVSel() {
		try {
			String line = null;
			String currentVSel = "-1";
			
			BufferedReader br = new BufferedReader(new FileReader(VSEL_FILE));
			for(int i=1; i<=stepping; i++)
				line = br.readLine();
			
			if (line != null)
				currentVSel = line.split(" ")[1];
			
			return Integer.parseInt(currentVSel);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return -1;
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	public boolean setKernelVSel() {
		try {
			Process p = Runtime.getRuntime().exec("su");
			
			DataOutputStream dos = new DataOutputStream(p.getOutputStream());
			dos.writeBytes("echo \"" + stepping + " " + vsel + "\" > " + VSEL_FILE + "\n");
			dos.writeBytes("exit");
			dos.flush();
			dos.close();
			
			if(p.waitFor() != 0)
				Log.i(TAG, "Could not write to " + VSEL_FILE);
			
			return true;
		} catch (IOException io) {
			Log.e(TAG, "Unable to write vsel");
			return false;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean isKernelCompat() {
		File vselFile = new File(VSEL_FILE);
		if(!vselFile.exists())
			return false;
		return true;
	}
}
