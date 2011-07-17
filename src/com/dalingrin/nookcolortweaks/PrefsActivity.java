package com.dalingrin.nookcolortweaks;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;

import com.dalingrin.nookcolortweaks.romfs.DeviceRomfs;
import com.dalingrin.nookcolortweaks.romfs.Romfs;
import com.dalingrin.nookcolortweaks.sysfs.AudioSysfs;
import com.dalingrin.nookcolortweaks.sysfs.AudioSysfs.GainType;
import com.dalingrin.nookcolortweaks.sysfs.CPUSysfs;
import com.dalingrin.nookcolortweaks.sysfs.Sysfs;
import com.dalingrin.nookcolortweaks.sysfs.UsbSysfs;
import com.dalingrin.nookcolortweaks.sysfs.UsbSysfs.USBMode;
import com.dalingrin.nookcolortweaks.sysfs.VBusSysfs;
import com.dalingrin.nookcolortweaks.sysfs.VBusSysfs.VBusMode;
import com.dalingrin.nookcolortweaks.voltage.OmapOPP;

public class PrefsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	private static final String TAG = "NookColorTweaks";
	SharedPreferences pref;
	DisplaySettings dSettings;
	boolean isUSBCompat = false;

	
	  @Override
	  protected void onCreate(Bundle savedInstanceState) { 
	    super.onCreate(savedInstanceState);
	    addPreferencesFromResource(R.xml.prefs); 
	    
	    pref = PreferenceManager.getDefaultSharedPreferences(this);
	    dSettings = new DisplaySettings(this);
	    
	    compatibilityChecks();
	    
	    if(!pref.contains("appInitialized")) {
	    	Log.i(TAG, "first run detected");
	    	setFirstRunSettings();
	    }

	    initSettings();

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
	    
	    if (isUSBCompat) {
		    Preference usbRefreshPref = (Preference) findPreference("usb_refresh");
		    usbRefreshPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
				
				@Override
				public boolean onPreferenceClick(Preference preference) {
					refreshUSBSettings();
					return false;
				}
			});
	    }
	    
	    Preference voltPref = (Preference) findPreference("voltage_control");
	    if (voltPref != null) {
		    voltPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
		    	
		    	@Override
				public boolean onPreferenceClick(Preference preference) {
		    		if(OmapOPP.isKernelCompat()) {
		    			Intent i = new Intent(PrefsActivity.this, VoltageActivity.class);
						startActivity(i);
		    		} else {
		    			showKernelNotCompat("Voltage");
		    		}
					return false;
				}
		    });
	    } else {
	    	Log.e(TAG, "Unable to find voltage preference");
	    }

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
				  Log.i(TAG, "MPU OPP1 set: " + freq);
			  } else if (key.equals("mpu_opp2")) {
				  int freq = prefs.getInt(key, 600);
				  Sysfs.write(new CPUSysfs(2, freq));
				  Log.i(TAG, "MPU OPP2 set: " + freq);
			  } else if (key.equals("mpu_opp3")) {
				  int freq = prefs.getInt(key, 800);
				  Sysfs.write(new CPUSysfs(3, freq));
				  Log.i(TAG, "MPU OPP3 set: " + freq);
			  } else if (key.equals("mpu_opp4")) {
				  int freq = prefs.getInt(key, 925);
				  Sysfs.write(new CPUSysfs(4, freq));
				  Log.i(TAG, "MPU OPP4 set: " + freq);
			  } else if (key.equals("mpu_opp5")) {
				  int freq = prefs.getInt(key, 925);
				  Sysfs.write(new CPUSysfs(5, freq));
				  Log.i(TAG, "MPU OPP5 set: " + freq);
			  } else if (key.equals("boot_device")) {
				  boolean value = prefs.getBoolean(key, false);
				  if (value) {Romfs.write(new DeviceRomfs("u-boot.device", "1")); }
				  else {Romfs.delete(new DeviceRomfs("u-boot.device", "0")); }
			  } else if (key.equals("boot_alt")) {
				  boolean value = prefs.getBoolean(key, false);
				  if (value) {Romfs.write(new DeviceRomfs("u-boot.altboot", "1")); }
				  else {Romfs.delete(new DeviceRomfs("u-boot.altboot", "0")); }
			  } else if (key.equals("usbhost_mode")) {
				  boolean value = prefs.getBoolean(key, false);
				  if (value)
					  Sysfs.write(new UsbSysfs(USBMode.host));
				  else
					  Sysfs.write(new UsbSysfs(USBMode.peripheral));
				  refreshUSBSettings();
			  } else if (key.equals("vbus_mode")) {
				  boolean value = prefs.getBoolean(key, false);
				  if (value)
					  Sysfs.write(new VBusSysfs(VBusMode.external));
				  else
					  Sysfs.write(new VBusSysfs(VBusMode.internal));
				  refreshUSBSettings();
			  } else if (key.equals("32bpp")) {
				  boolean value = prefs.getBoolean(key, true);
				  dSettings.setKernel(value);
				  dSettings.promptReboot();
			  }
		  }
	  }

	  private void initSettings() {
		  if(isUSBCompat)
			  refreshUSBSettings();
		  
		  Editor editor = pref.edit();
		  
		  String dev = Romfs.read(new DeviceRomfs("u-boot.device", "0"));
		  String ab = Romfs.read(new DeviceRomfs("u-boot.altboot", "0"));
		  Log.i(TAG, "Read u-boot.device file  : " + dev);
		  Log.i(TAG, "Read u-boot.altboot file : " + ab);
		  if (dev != null) {
			  if (dev.startsWith("1")) {
				  	editor.putBoolean("boot_device", true);
	
				  	Log.i(TAG, "setting boot pref device to true");
			  } else {
				  	editor.putBoolean("boot_device", false);
				  	Log.i(TAG, "setting boot pref device to false");
			  }
		  }
		  if (ab != null) {
			  if (ab.startsWith("1")) {
			  	editor.putBoolean("boot_alt", true);
			  	Log.i(TAG, "setting alt boot pref to true");
			  } else {
			  	editor.putBoolean("boot_alt", false);
			  	Log.i(TAG, "setting alt boot pref to false");
			  }
		  }
		  if (dSettings.isCompat()) { 
			  editor.putBoolean("32bpp", dSettings.is32BitFramebuffer());
			  CheckBoxPreference framebufferCheckBox = (CheckBoxPreference)findPreference("32bpp");
			  framebufferCheckBox.setChecked(dSettings.is32BitFramebuffer());
		  }
		
		  editor.commit();
}
	  private void refreshUSBSettings() {
		  Editor editor = pref.edit();
		  
		  //USB Settings
		  String usbMode = Sysfs.read(new UsbSysfs());
		  String vbusMode = Sysfs.read(new VBusSysfs());
		  Log.i(TAG, "Read USB Mode sysfs file  : " + usbMode);
		  Log.i(TAG, "Read vbus mode sysfs file  : " + vbusMode);
		  if (usbMode != null) {
			  boolean isUSBHost = usbMode.startsWith("a_");
			  editor.putBoolean("usbhost_mode", isUSBHost);
			  CheckBoxPreference usbHostPref = (CheckBoxPreference)findPreference("usbhost_mode");
			  usbHostPref.setChecked(isUSBHost);
		  }

		  if (vbusMode != null) {
			  boolean isVBusExternal = vbusMode.contains(VBusMode.external.toString());
			  editor.putBoolean("vbus_mode", isVBusExternal);
			  CheckBoxPreference vbusPref = (CheckBoxPreference)findPreference("vbus_mode");
			  vbusPref.setChecked(isVBusExternal);
		  }
		  editor.commit();
	  }
	  
	  private void setFirstRunSettings() {
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
		            	showKernelNotCompat("Audio");
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
		            	showKernelNotCompat("CPU");
		            	return false;
		            }
		    	});
		    }
		    if(!CPUSysfs.isKernelCompat() || !VBusSysfs.isKernelCompat()) {
		    	//Remove USB preferences due to no kernel support
		    	PreferenceScreen usbPrefs = (PreferenceScreen) findPreference("usb_settings");
		    	usbPrefs.removeAll();
		    	
		    	//If you clicks on CPU preference screen then show warning
		    	usbPrefs.setOnPreferenceClickListener(new OnPreferenceClickListener() {
		            public boolean onPreferenceClick(Preference preference) {
		            	showKernelNotCompat("USB");
		            	return false;
		            }
		    	});
		    } else
		    	isUSBCompat = true;
		    if(!dSettings.isCompat()) {
		    	PreferenceScreen displayPrefs = (PreferenceScreen) findPreference("display_settings");
		    	displayPrefs.removeAll();
		    	
		    	//If you clicks on CPU preference screen then show warning
		    	displayPrefs.setOnPreferenceClickListener(new OnPreferenceClickListener() {
		            public boolean onPreferenceClick(Preference preference) {
		            	showKernelNotCompat("display");
		            	return false;
		            }
		    	});
		    }
	  }
	  
	  private void showKernelNotCompat(String settings) {
		  StringBuilder sb = new StringBuilder("Kernel is not compatible with ");
		  sb.append(settings);
		  sb.append(" settings.\n");
		  if(settings.equalsIgnoreCase("audio") || settings.equalsIgnoreCase("USB"))
			  sb.append("A newer build of CM7 or Dalingrin's overclock kernel is required");
		  else if(settings.equalsIgnoreCase("cpu") || settings.equalsIgnoreCase("Voltage")) 
			  sb.append("A newer build of Dalingrin's overclock kernel is required");
		  else if(settings.equalsIgnoreCase("display"))
			  sb.append("A newer build of CM7 is required");
		  
		  AlertDialog.Builder notCompatDialog = new AlertDialog.Builder(this);
		  notCompatDialog.setIcon(R.drawable.icon);
		  notCompatDialog.setTitle("Nook Color Tweaks");
		  notCompatDialog.setMessage(sb);
		  notCompatDialog.setNeutralButton("Ok", null);
		  notCompatDialog.show();
	  }
}
