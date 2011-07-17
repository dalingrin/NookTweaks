package com.dalingrin.nookcolortweaks;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class VoltageConfirmationActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		final SharedPreferences.Editor editor = pref.edit();
	
		AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(this);
		confirmationDialog.setIcon(R.drawable.icon);
		confirmationDialog.setTitle("Nook Color Tweaks");
		confirmationDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				editor.putBoolean("volt_set_on_boot_confirmed", true);
				editor.putBoolean("volt_confirmation_attempted", false);
				editor.commit();
				finish();
			}
		});
		confirmationDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();			
			}
		});
		confirmationDialog.setMessage("Please confirm your voltage settings are working properly." 
				+ "\nConfirming will enable the voltage settings on every boot."
				+ "\n\nPress Ok to confirm your voltage settings." 
				+ "\nPress Cancel to remove them on boot.");
		confirmationDialog.show();
	}
}
