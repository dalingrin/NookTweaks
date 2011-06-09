package com.dalingrin.nookcolortweaks;

import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.dalingrin.nookcolortweaks.dac3100.DAC3100sysfs;
import com.dalingrin.nookcolortweaks.dac3100.DAC3100sysfs.GainType;

public class PrefsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	private static final String TAG = "NookColorTweaks";
	SharedPreferences pref;
	
	  @Override
	  protected void onCreate(Bundle savedInstanceState) { // 
	    super.onCreate(savedInstanceState);
	    addPreferencesFromResource(R.xml.prefs); //
	    
	    if(!DAC3100sysfs.isKernelCompat()) {
	    	AlertDialog.Builder notCompatDialog = new AlertDialog.Builder(this);
	    	notCompatDialog.setMessage("Kernel is not compatible. A newer build of CM7 or Dalingrin's overclock kernel is required");
	    	notCompatDialog.setNeutralButton("Ok", null);
	    	notCompatDialog.show();
	    }

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
		  if (key != null) {
			  if (key.equals(GainType.dac_level.toString())) {
				  int level = prefs.getInt(key, -1);
				  
				  if (level >= 0)
					  DAC3100sysfs.setGain(GainType.dac_level ,level);
				  else
					  Log.i(TAG, "Invalid DAC level: " + level);
			  } else if (key.equals(GainType.spkr_analog_gain.toString())) {
				  int gain = prefs.getInt(key, -1);
				  
				  if (gain >= 0)
					  DAC3100sysfs.setGain(GainType.spkr_analog_gain, 255 - gain);
				  else
					  Log.i(TAG, "Invalid speaker gain: " + gain);
			  } else if (key.equals(GainType.hp_analog_gain.toString())) {
				  int gain = prefs.getInt(key, -1);
				  
				  if (gain >= 0)
					  DAC3100sysfs.setGain(GainType.hp_analog_gain, 255 - gain);
				  else
					  Log.i(TAG, "Invalid hp gain: " + gain);
			  } 
		  }
	  }
	  
	  private void setInitalSettings() {
		  Editor editor = pref.edit();
		  editor.putInt(GainType.dac_level.toString(), 0);
		  editor.putInt(GainType.hp_analog_gain.toString(), 255 - DAC3100sysfs.read_level(GainType.hp_analog_gain));
		  editor.putInt(GainType.spkr_analog_gain.toString(), 255 - DAC3100sysfs.read_level(GainType.spkr_analog_gain));
		  editor.putBoolean("appInitialized", true);
		  editor.commit();
	  }
	  
}
