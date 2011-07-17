package com.dalingrin.nookcolortweaks;

import java.io.*;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

public class DisplaySettings {
	Context mContext;
	private boolean isCompatDispSettings = false;
	private String kernel16Checksum = null;
	private String kernel32Checksum = null;
	private String currentKernChecksum = null;
	private static final String kernel16 = "/system/bin/kernel/uImage16";
	private static final String kernel32 = "/system/bin/kernel/uImage32";
	private static String boot_dev = null;
	private static final String boot_mount_point = "/system/bin/kernel/boot";
	private static final String currentKern = boot_mount_point + "/uImage";
	private static final String TAG = "Nook Color Tweaks";
    File kernel16File = new File(kernel16);
    File kernel32File = new File(kernel32);
	
	public DisplaySettings(Context context) {
		mContext = context;
		getBootDevice();
		isCompatDispSettings = kernel16File.exists() && kernel32File.exists() && boot_dev != null;

		if(isCompatDispSettings) {
			
			kernel16Checksum = Utils.calcMD5(kernel16);
			kernel32Checksum = Utils.calcMD5(kernel32);
			Log.i(TAG, "kernel16 md5sum: " + kernel16Checksum);
			Log.i(TAG, "kernel32 md5sum: " + kernel32Checksum);
			
			mountBoot();
			currentKernChecksum = Utils.calcMD5(currentKern);
			Log.i(TAG, "Current kernel md5sum: " + currentKernChecksum);
			unmountBoot();

		}

	}
	
	private void getBootDevice() {
		try {
			Process p = Runtime.getRuntime().exec("su");
			
			DataOutputStream dos = new DataOutputStream(p.getOutputStream());
			DataInputStream dis = new DataInputStream(p.getInputStream());
			
			dos.writeBytes("busybox mount | busybox grep /system\n");
			dos.flush();

			String line = dis.readLine();
			if (line != null) {
				if (line.contains("mmcblk0")) 
					boot_dev = "/dev/block/mmcblk0p1";
				else if (line.contains("mmcblk1"))
					boot_dev = "/dev/block/mmcblk1p1";
				Log.i(TAG, "Boot Device: " + boot_dev);
			} else
				Log.i(TAG, "Error getting Mount output");
			dos.writeBytes("exit\n");
			dos.close();
			dis.close();
			
			if(p.waitFor() != 0)
				Log.e(TAG, "Error getting boot device");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void mountBoot() {
		Process p;
		try {
			p = Runtime.getRuntime().exec("su");

		
			DataOutputStream dos = new DataOutputStream(p.getOutputStream());
			dos.writeBytes("busybox mount -o rw,remount /system\n");
			dos.writeBytes("mkdir " + boot_mount_point + "\n");
			dos.writeBytes("busybox mount -t vfat " + boot_dev + " " + boot_mount_point + "\n");
			dos.writeBytes("exit\n");
			dos.close();
			
			if(p.waitFor() != 0)
				Log.i(TAG, "Could not mount boot partition");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	private void unmountBoot() {
		Process p;
		try {
			p = Runtime.getRuntime().exec("su");
		
			DataOutputStream dos = new DataOutputStream(p.getOutputStream());
			dos.writeBytes("busybox umount " + boot_mount_point + "\n");
			dos.writeBytes("busybox mount -o ro,remount /system\n");
			dos.writeBytes("exit\n");
			dos.close();
			
			if(p.waitFor() != 0)
				Log.i(TAG, "Could not mount boot partition");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public void promptReboot() {
		AlertDialog.Builder displayReboot = new AlertDialog.Builder(mContext);
		displayReboot.setTitle("Nook Color Tweaks");
		displayReboot.setIcon(R.drawable.icon);
		displayReboot.setMessage("Must reboot in order for changes to take effect");
		displayReboot.setNegativeButton("Reboot Later", null);
		displayReboot.setPositiveButton("Reboot Now", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Process p;
				try {
					p = Runtime.getRuntime().exec("su");
					DataOutputStream dos = new DataOutputStream(p.getOutputStream());
					dos.writeBytes("reboot\n");
					dos.close();
					
					if(p.waitFor() != 0)
						Log.i(TAG, "Unable to reboot");			
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		displayReboot.show();
	}
	
	public boolean is32BitFramebuffer() {
		return kernel32Checksum.equals(currentKernChecksum);
	}
	
	public boolean setKernel(boolean set32Bit) {
		boolean ret = false;
		
		mountBoot();
		Process p;
		try {
			p = Runtime.getRuntime().exec("su");

			DataOutputStream dos = new DataOutputStream(p.getOutputStream());
			File currentKernFile = new File(currentKern);
			if(currentKernFile.exists()) {
				if(set32Bit)
					dos.writeBytes("cp " + kernel32 + " " + currentKern + "\n");
				else
					dos.writeBytes("cp " + kernel16 + " " + currentKern + "\n");
			}
			dos.writeBytes("exit");
			dos.close();
			
			if(p.waitFor() != 0)
				Log.i(TAG, "Could not write kernel");
			unmountBoot();

			ret = true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ret;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ret;
		} 
		return ret;
	}
	
	public boolean isCompat() {
		return (kernel16Checksum != null && kernel32Checksum != null
				&& currentKernChecksum != null);
	}
}
