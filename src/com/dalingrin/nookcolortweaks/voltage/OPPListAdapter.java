package com.dalingrin.nookcolortweaks.voltage;

import java.util.ArrayList;

import com.dalingrin.nookcolortweaks.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class OPPListAdapter extends BaseAdapter {
	Context context;
	ArrayList<OmapOPP> oppList = new ArrayList<OmapOPP>();

	public OPPListAdapter(Context context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		return oppList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return oppList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LinearLayout voltageLayout = (LinearLayout) LayoutInflater
				.from(context).inflate(R.layout.voltage, parent, false);

		final OmapOPP opp = oppList.get(position);
		final TextView steppingLabel = (TextView) voltageLayout
				.findViewById(R.id.TextViewStepping);
		final SeekBar seekBarVolt = (SeekBar) voltageLayout
				.findViewById(R.id.SeekBarVolt);
		final TextView seekBarText = (TextView) voltageLayout
				.findViewById(R.id.TextViewVolt);
		final TextView stockVoltText = (TextView) voltageLayout
				.findViewById(R.id.TextStockVolt);

		steppingLabel.setText("Stepping " + String.valueOf(position + 1));
		stockVoltText.setText(String.valueOf(opp.getStockVoltage()));

		int seekBarPos = opp.getProgress();
		seekBarText.setText(String.valueOf(opp.getVoltage()) + "v");
		seekBarVolt.setProgress(seekBarPos);
		seekBarVolt
				.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						if (opp != null) {
							opp.setProgress(progress);
							seekBarText.setText(String.valueOf(opp.getVoltage())
									+ "v");
						}
					}
				});

		return voltageLayout;
	}

	public void setOPPs(ArrayList<OmapOPP> oppList) {
		this.oppList = oppList;
	}

}
