package com.dalingrin.nookcolortweaks;

import com.dalingrin.nookcolortweaks.dac3100.DAC3100sysfs;
import com.dalingrin.nookcolortweaks.dac3100.DAC3100sysfs.GainType;

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
		
		if (prefs.getBoolean("set_on_boot", false)) {
			int dac = prefs.getInt("dac_level", DAC3100sysfs.read_level(GainType.dac_level));
			int hp_gain = prefs.getInt("hp_analog_gain", DAC3100sysfs.read_level(GainType.hp_analog_gain));
			int spkr_gain = prefs.getInt("spkr_analog_gain", DAC3100sysfs.read_level(GainType.spkr_analog_gain));
			
			DAC3100sysfs.setGain(GainType.dac_level, prefs.getInt("dac_level", 0));
			DAC3100sysfs.setGain(GainType.hp_analog_gain, 255 - hp_gain);
			DAC3100sysfs.setGain(GainType.spkr_analog_gain, 255 - spkr_gain);
			Log.i(TAG, "dac_level set to " + dac);
			Log.i(TAG, "hp gain set to " + hp_gain);
			Log.i(TAG, "speaker gain set to " + spkr_gain);
		}
	}
}
