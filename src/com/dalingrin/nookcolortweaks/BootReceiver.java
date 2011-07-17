package com.dalingrin.nookcolortweaks;

import com.dalingrin.nookcolortweaks.sysfs.*;
import com.dalingrin.nookcolortweaks.sysfs.AudioSysfs.GainType;
import com.dalingrin.nookcolortweaks.voltage.OmapOPP;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;


public class BootReceiver extends BroadcastReceiver {
	private static final String TAG = "NookColorTweaks";
	SharedPreferences prefs;
	
	@Override 
	public void onReceive (Context context, Intent intent) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		
		if (prefs.getBoolean("audio_set_on_boot", false) && AudioSysfs.isKernelCompat()) {
			
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
		if (prefs.getBoolean("cpu_set_on_boot", false) && CPUSysfs.isKernelCompat()) {
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
		if (prefs.getBoolean("volt_set_on_boot", false)) {
			if (!prefs.getBoolean("volt_set_on_boot_confirmed", false) 
					&& prefs.getBoolean("volt_confirmation_attempted", false)) {
				//Voltages may not be stable. Need to reset
				Log.e(TAG, "Failed voltage settings detected!");
				SharedPreferences.Editor editor = prefs.edit();
				
				for(int i=1; i<=5; i++) {
					OmapOPP opp = new OmapOPP(i);
					editor.putInt("seekBarOPP" + i, opp.getProgress());
					Log.i(TAG, "OPP" + i + " voltage reset to: " + opp.getVoltage());
				}
				editor.putBoolean("volt_confirmation_attempted", false);
				editor.putBoolean("volt_set_on_boot", false);
				editor.commit();
				Toast popupText = Toast.makeText(context, "Nook Color Tweaks recovered from unstable voltage settings", Toast.LENGTH_LONG);
				popupText.show();
			} else {
				//Keep record that we will attempt to get confirmation of voltage settings
				if (!prefs.getBoolean("volt_set_on_boot_confirmed", false)) {
					SharedPreferences.Editor attemptEditor = prefs.edit();
					attemptEditor.putBoolean("volt_confirmation_attempted", true);
					attemptEditor.commit();
				}
				
				//Apply voltages
				for(int i=1; i<=5; i++) {
					OmapOPP opp = new OmapOPP(i);
					int progress = prefs.getInt("seekBarOPP" + i, -1);
					if(progress != -1) {
						opp.setProgress(progress);
						opp.setKernelVSel();
						Log.i(TAG, "OPP" + i + " voltage set to: " + opp.getVoltage());
					} else 
						Log.i(TAG, "No shared preference data found for OPP" + i);
				}
				
				//Ask for confirmation of voltages
				if (!prefs.getBoolean("volt_set_on_boot_confirmed", false)) {
					//Give a little time for the system to potentially lock up
					//before letting the user confirm
					android.os.SystemClock.sleep(5000);
					
					Intent confirmationIntent = new Intent(context, VoltageConfirmationActivity.class);
					confirmationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
					context.startActivity(confirmationIntent);
				}
			}
		}
	}
}
