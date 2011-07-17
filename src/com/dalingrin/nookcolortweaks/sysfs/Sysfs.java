package com.dalingrin.nookcolortweaks.sysfs;

import java.io.*;

import android.util.Log;

public class Sysfs {
	private static String TAG = "NookColorTweaks";

	public static boolean write(SysfsObj obj) {
		try {
			File mpuFile = new File(obj.getFile());
			if(mpuFile.canWrite()) {
				BufferedWriter bw = new BufferedWriter(new FileWriter(obj.getFile()));
				bw.write(obj.getValue());
				bw.close();
			} else {
				Process p = Runtime.getRuntime().exec("su");
				
				DataOutputStream dos = new DataOutputStream(p.getOutputStream());
				dos.writeBytes("echo " + obj.getValue() + " > " + obj.getFile() + "\n");
				dos.writeBytes("exit");
				dos.flush();
				dos.close();
				
				if(p.waitFor() != 0)
					Log.i(TAG, "Could not write to " + obj.getFile());
			}
		} catch (IOException ex) {
			return false;
		} catch (InterruptedException e) {
			Log.i(TAG, "Error writing with root permission");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static String read(SysfsObj obj) {
		String value;
		try {
			BufferedReader br = new BufferedReader(new FileReader(obj.getFile()));
			value = br.readLine();
			br.close();
		} catch (IOException ex) {
			return "-1";
		}
		return value;
	}
}
