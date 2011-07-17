package com.dalingrin.nookcolortweaks;

import java.util.ArrayList;

import com.dalingrin.nookcolortweaks.voltage.OPPListAdapter;
import com.dalingrin.nookcolortweaks.voltage.OmapOPP;

import android.app.Activity;
import android.app.ListActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class VoltageActivity extends ListActivity {
	SharedPreferences pref;
	OPPListAdapter oppAdapter;
	ArrayList<OmapOPP> oppList = new ArrayList<OmapOPP>();
	
	private final static String TAG = "VoltageActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		pref = PreferenceManager.getDefaultSharedPreferences(this);//getSharedPreferences("voltagePrefs", 0);
		oppAdapter = new OPPListAdapter(this);

		oppList.add(new OmapOPP(1));
		oppList.add(new OmapOPP(2));
		oppList.add(new OmapOPP(3));
		oppList.add(new OmapOPP(4));
		oppList.add(new OmapOPP(5));
		oppAdapter.setOPPs(oppList);
		this.setListAdapter(oppAdapter);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.voltmenu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.apply:
			SharedPreferences.Editor editor = pref.edit();
			for(OmapOPP opp: oppList) {
				editor.putInt("seekBarOPP" + String.valueOf(opp.getStepping()), opp.getProgress());
				opp.setKernelVSel();
				Log.i(TAG, "OPP" + opp.getStepping() + " voltage set: " + opp.getVoltage());
			}
			editor.commit();
			Toast notifyText = Toast.makeText(this, "Saving and applying selected voltages", Toast.LENGTH_LONG);
			notifyText.show();
			break;
		case R.id.set_on_boot:
			SharedPreferences.Editor setBootEditor = pref.edit();
			if (pref.getBoolean("volt_set_on_boot", false)) {
				setBootEditor.putBoolean("volt_set_on_boot", false);
				Toast notifyBootText = Toast.makeText(this, "Voltage settings will no longer apply on boot", Toast.LENGTH_LONG);
				notifyBootText.show();
			} else {
				setBootEditor.putBoolean("volt_set_on_boot", true);
				setBootEditor.putBoolean("volt_set_on_boot_confirmed", false);
				setBootEditor.putBoolean("volt_confirmation_attempted", false);
				Toast notifyBootText = Toast.makeText(this, "Voltage settings will apply on boot", Toast.LENGTH_LONG);
				notifyBootText.show();
			}
			setBootEditor.commit();
			break;
		case R.id.reset:
			for(OmapOPP opp: oppList) {
				opp.resetToStock();
				oppAdapter.notifyDataSetChanged();
			}
			break;
		}
		return true;
	}
}
