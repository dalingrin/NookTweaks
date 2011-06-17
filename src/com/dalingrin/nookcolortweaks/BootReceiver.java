package com.dalingrin.nookcolortweaks;

import com.dalingrin.nookcolortweaks.sysfs.*;
import com.dalingrin.nookcolortweaks.sysfs.AudioSysfs.GainType;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;


public class BootReceiver extends BroadcastReceiver {
	private static final String TAG = "NookColorTweaks";
	SharedPreferences prefs;
	@Override 
	public void onReceive (Context context, Intent intent) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		
		if (prefs.getBoolean("audio_set_on_boot", false)) {
			
			int dac = prefs.getInt(GainType.dac_level.toString(), 
					Integer.parseInt(Sysfs.read(new AudioSysfs(GainType.dac_level, 0))));
			int hp_gain = prefs.getInt("hp_analog_gain",
					Integer.parseInt(Sysfs.read(new AudioSysfs(GainType.hp_analog_gain, 0))));
			int spkr_gain = prefs.getInt("spkr_analog_gain",
					Integer.parseInt(Sysfs.read(new AudioSysfs(GainType.spkr_analog_gain, 0))));
			
			Sysfs.write(new AudioSysfs(GainType.dac_level, prefs.getInt("dac_level", 0)));
			Sysfs.write(new AudioSysfs(GainType.hp_analog_gain, 255 - hp_gain));
			Sysfs.write(new AudioSysfs(GainType.spkr_analog_gain, 255 - spkr_gain));
			Log.i(TAG, "dac_level set to " + dac);
			Log.i(TAG, "hp gain set to " + hp_gain);
			Log.i(TAG, "speaker gain set to " + spkr_gain);
			
		}
		if (prefs.getBoolean("cpu_set_on_boot", false)) {
			int opp1 = prefs.getInt("mpu_opp1", Integer.parseInt(Sysfs.read(new CPUSysfs(1, 0))));
			int opp2 = prefs.getInt("mpu_opp2", Integer.parseInt(Sysfs.read(new CPUSysfs(2, 0))));
			int opp3 = prefs.getInt("mpu_opp3", Integer.parseInt(Sysfs.read(new CPUSysfs(3, 0))));
			int opp4 = prefs.getInt("mpu_opp4", Integer.parseInt(Sysfs.read(new CPUSysfs(4, 0))));
			int opp5 = prefs.getInt("mpu_opp5", Integer.parseInt(Sysfs.read(new CPUSysfs(5, 0))));
			
			if(Sysfs.write(new CPUSysfs(1, opp1)))
				Log.i(TAG, "MPU OPP1 set: " + opp1);
			else
				Log.i(TAG, "MPU OPP1 could not be set!");
			
			if(Sysfs.write(new CPUSysfs(2, opp2)))
				Log.i(TAG, "MPU OPP2 set: " + opp2);
			else
				Log.i(TAG, "MPU OPP2 could not be set!");
			
			if(Sysfs.write(new CPUSysfs(3, opp3)))
				Log.i(TAG, "MPU OPP3 set: " + opp3);
			else
				Log.i(TAG, "MPU OPP3 could not be set!");
			
			if(Sysfs.write(new CPUSysfs(4, opp4)))
				Log.i(TAG, "MPU OPP4 set: " + opp4);
			else
				Log.i(TAG, "MPU OPP4 could not be set!");
			
			if(Sysfs.write(new CPUSysfs(5, opp5)))
				Log.i(TAG, "MPU OPP5 set: " + opp5);
			else
				Log.i(TAG, "MPU OPP5 could not be set!");			
		}
	}
}
