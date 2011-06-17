package com.dalingrin.nookcolortweaks;

import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.dalingrin.nookcolortweaks.sysfs.*;
import com.dalingrin.nookcolortweaks.sysfs.AudioSysfs.GainType;

public class PrefsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	private static final String TAG = "NookColorTweaks";
	SharedPreferences pref;
	
	  @Override
	  protected void onCreate(Bundle savedInstanceState) { 
	    super.onCreate(savedInstanceState);
	    addPreferencesFromResource(R.xml.prefs); 
	    
	    compatibilityChecks();

	    pref = PreferenceManager.getDefaultSharedPreferences(this);
	    
	    if(!pref.contains("appInitialized")) {
	    	Log.i(TAG, "first run detected");
	    	setInitalSettings();
	    }
	    
	    pref.registerOnSharedPreferenceChangeListener(this);
	    
	    Preference myPref = (Preference) findPreference("donate");
	    myPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
            	String url = "https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=S8ZMTYR2J2JFU";
            	Intent i = new Intent(Intent.ACTION_VIEW);
            	i.setData(Uri.parse(url));
            	startActivity(i);
            	return false;
            }
        });
	    
	    Preference shiftPref = (Preference) findPreference("shift");
	    shiftPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
            	String url = "https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=DTYD9SHGLGJXC&lc=US&item_name=shift%7cdesigns&currency_code=USD&bn=PP%2dDonationsBF%3abtn_donate_SM%2egif%3aNonHosted";
            	Intent i = new Intent(Intent.ACTION_VIEW);
            	i.setData(Uri.parse(url));
            	startActivity(i);
            	return false;
            }
        });
	    
	  }
	  
	  public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
		  Log.i(TAG, "Key: " + key);
		  if (key != null) {
			  if (key.equals(GainType.dac_level.toString())) {
				  int level = prefs.getInt(key, -1);
				  
				  if (level >= 0)
					  Sysfs.write(new AudioSysfs(GainType.dac_level, level));
				  else
					  Log.i(TAG, "Invalid DAC level: " + level);
			  } else if (key.equals(GainType.spkr_analog_gain.toString())) {
				  int gain = prefs.getInt(key, -1);
				  
				  if (gain >= 0)
					  Sysfs.write(new AudioSysfs(GainType.spkr_analog_gain, 255 - gain));
				  else
					  Log.i(TAG, "Invalid speaker gain: " + gain);
			  } else if (key.equals(GainType.hp_analog_gain.toString())) {
				  int gain = prefs.getInt(key, -1);
				  
				  if (gain >= 0)
					  Sysfs.write(new AudioSysfs(GainType.hp_analog_gain, 255 - gain));
				  else
					  Log.i(TAG, "Invalid hp gain: " + gain);
			  } else if (key.equals("mpu_opp1")) {
				  int freq = prefs.getInt(key, 300);
				  Sysfs.write(new CPUSysfs(1, freq));
			  } else if (key.equals("mpu_opp2")) {
				  int freq = prefs.getInt(key, 600);
				  Sysfs.write(new CPUSysfs(2, freq));
			  } else if (key.equals("mpu_opp3")) {
				  int freq = prefs.getInt(key, 800);
				  Sysfs.write(new CPUSysfs(3, freq));
			  } else if (key.equals("mpu_opp4")) {
				  int freq = prefs.getInt(key, 925);
				  Sysfs.write(new CPUSysfs(4, freq));
			  } else if (key.equals("mpu_opp5")) {
				  int freq = prefs.getInt(key, 925);
				  Sysfs.write(new CPUSysfs(5, freq));
			  }
		  }
	  }
	  
	  private void setInitalSettings() {
		  Editor editor = pref.edit();
		  try {
			  if(AudioSysfs.isKernelCompat()) {
				  editor.putInt(GainType.dac_level.toString(), 0);
				  editor.putInt(GainType.hp_analog_gain.toString(),
						  255 - Integer.parseInt(Sysfs.read(new AudioSysfs(GainType.hp_analog_gain, 0))));
				  editor.putInt(GainType.spkr_analog_gain.toString(),
						  255 - Integer.parseInt(Sysfs.read(new AudioSysfs(GainType.spkr_analog_gain, 0))));
			  }
			  if(CPUSysfs.isKernelCompat()) {
				  editor.putInt("mpu_opp1", Integer.parseInt(Sysfs.read(new CPUSysfs(1, 0))));
				  editor.putInt("mpu_opp2", Integer.parseInt(Sysfs.read(new CPUSysfs(2, 0))));
				  editor.putInt("mpu_opp3", Integer.parseInt(Sysfs.read(new CPUSysfs(3, 0))));
				  editor.putInt("mpu_opp4", Integer.parseInt(Sysfs.read(new CPUSysfs(4, 0))));
				  editor.putInt("mpu_opp5", Integer.parseInt(Sysfs.read(new CPUSysfs(5, 0))));
				  editor.putBoolean("appInitialized", true);
			  }
		  } catch (NumberFormatException nfe) {
			  AlertDialog.Builder notCompatDialog = new AlertDialog.Builder(this);
			  notCompatDialog.setMessage("Unable to establish initial settings");
			  notCompatDialog.setNeutralButton("Ok", null);
			  notCompatDialog.show();
		  }
		  
		  
		  editor.commit();
	  }	  
	  
	  private void compatibilityChecks() {
		    if(!AudioSysfs.isKernelCompat()) {
		    	//Remove CPU preferences due to no kernel support
		    	PreferenceScreen cpuPrefs = (PreferenceScreen) findPreference("audio_settings");
		    	cpuPrefs.removeAll();
		    	
		    	//If you clicks on CPU preference screen then show warning
		    	cpuPrefs.setOnPreferenceClickListener(new OnPreferenceClickListener() {
		            public boolean onPreferenceClick(Preference preference) {
		            	showKernelNotCompatCPU("Audio");
		            	return false;
		            }
		    	});
		    }	 
		    if(!CPUSysfs.isKernelCompat()) {
		    	//Remove CPU preferences due to no kernel support
		    	PreferenceScreen cpuPrefs = (PreferenceScreen) findPreference("cpu_settings");
		    	cpuPrefs.removeAll();
		    	
		    	//If you clicks on CPU preference screen then show warning
		    	cpuPrefs.setOnPreferenceClickListener(new OnPreferenceClickListener() {
		            public boolean onPreferenceClick(Preference preference) {
		            	showKernelNotCompatCPU("CPU");
		            	return false;
		            }
		    	});
		    }	    
	  }
	  
	  private void showKernelNotCompatCPU(String settings) {
		  StringBuilder sb = new StringBuilder("Kernel is not compatible with ");
		  sb.append(settings);
		  sb.append(" settings.\n");
		  if(settings.equalsIgnoreCase("audio"))
			  sb.append("A newer build of CM7 or Dalingrin's overclock kernel is required");
		  else if(settings.equalsIgnoreCase("cpu")) 
			  sb.append("A newer build of Dalingrin's overclock kernel is required");
		  
		  AlertDialog.Builder notCompatDialog = new AlertDialog.Builder(this);
		  notCompatDialog.setMessage(sb);
		  notCompatDialog.setNeutralButton("Ok", null);
		  notCompatDialog.show();
	  }
}
