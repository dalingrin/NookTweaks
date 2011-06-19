package com.dalingrin.nookcolortweaks.romfs;

import java.io.*;

import com.dalingrin.nookcolortweaks.romfs.RomfsObj;

import android.util.Log;

public class Romfs {
	private static String TAG = "NookColorTweaks";

	public static boolean write(RomfsObj obj) {
		try {
			File mpuFile = new File(obj.getFile());
			if(mpuFile.canWrite()) {
				BufferedWriter bw = new BufferedWriter(new FileWriter(obj.getFile()));
				bw.write(obj.getValue());
				bw.close();
			} else {
				String su = "/system/xbin/su";
				File suFile = new File(su);
				if (!suFile.exists())
					su = "/system/bin/su";
					
				Process p = Runtime.getRuntime().exec(su);
				
				DataOutputStream dos = new DataOutputStream(p.getOutputStream());
				dos.writeBytes("echo " + obj.getValue() + " > " + obj.getFile());
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
		}
		return true;
	}
	
	public static String read(RomfsObj obj) {
		String value;
		try {
			File test = new File(obj.getFile());
			if (test.canRead()){
				Log.i(TAG, "Reading " + obj.getFile() + " without root.");
				BufferedReader br = new BufferedReader(new FileReader(obj.getFile()));
				value = br.readLine();
				br.close();
		    }
			 else {
				    Log.i(TAG, "Reading " + obj.getFile() + " with root.");
					String su = "/system/xbin/su";
					File suFile = new File(su);
					if (!suFile.exists())
						su = "/system/bin/su";
						
					Process p = Runtime.getRuntime().exec(su);

					DataOutputStream dos = new DataOutputStream(p.getOutputStream());
					DataInputStream dosin = new DataInputStream(p.getInputStream());
				//	make sure file exists before reading
					dos.writeBytes("if [ -f " + obj.getFile() + " ]; then cat " + obj.getFile() + "; else echo \"0\"; fi\n");
					value =  dosin.readLine();
					dos.flush();
					dos.close();
					dosin.close();

					if(p.waitFor() != 0)
					  Log.i(TAG, "Could not read from " + obj.getFile());
				 } 
		} catch (IOException ex) {
			Log.i(TAG, "IO exception, such as file not found.  This is okay.");
			value = "0";
		} catch (InterruptedException e) {
			Log.i(TAG, "Error reading with root permission");
			e.printStackTrace();
			value = "0";
		}
		return value;

	}
	
	public static boolean delete(RomfsObj obj) {
		boolean value;
		try {
			File test = new File(obj.getFile());
			if (test.canWrite()){
				test.delete();
				value=true;
		    }
			 else {
				    Log.i(TAG, "Deleting " + obj.getFile() + " with root.");
					String su = "/system/xbin/su";
					File suFile = new File(su);
					if (!suFile.exists())
						su = "/system/bin/su";
						
					Process p = Runtime.getRuntime().exec(su);

					DataOutputStream dos = new DataOutputStream(p.getOutputStream());
					dos.writeBytes("rm " + obj.getFile() + "\n");
					dos.flush();
					dos.close();;
					value=true;
					if(p.waitFor() != 0)
					  Log.i(TAG, "Could not read from " + obj.getFile());
				 } 
		} catch (IOException ex) {
			Log.i(TAG, "IO exception when deleting.");
			value = false;
		} catch (InterruptedException e) {
			Log.i(TAG, "Error deleting with root permission");
			e.printStackTrace();
			value = false;
		}
		return value;

	}
	
	
	
}