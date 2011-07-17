package com.dalingrin.nookcolortweaks;

/* The following code was written by Matthew Wiggins and extended by Erik Hardesty
 * and is released under the APACHE 2.0 license 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 */

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.preference.DialogPreference;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.LinearLayout;

public class SeekBarPreference extends DialogPreference implements
		SeekBar.OnSeekBarChangeListener {
	private static final String androidns = "http://schemas.android.com/apk/res/android";

	private SeekBar mSeekBar;
	private TextView mSplashText, mValueText;
	private Context mContext;

	private String mDialogMessage, mSuffix;
	private int mDefault, mMin, mMax, mValue, mStepSize = 0;
	private boolean mShowText, mDelayedSet = false;

	public SeekBarPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		
		TypedArray ta = context.obtainStyledAttributes(attrs,
				R.styleable.SeekBarPreference);
		
		mDialogMessage = attrs.getAttributeValue(androidns, "dialogMessage");
		mSuffix = attrs.getAttributeValue(androidns, "text");
		mDefault = attrs.getAttributeIntValue(androidns, "defaultValue", 0);
		mMin = ta.getInteger(R.styleable.SeekBarPreference_min, 0);
		mMax = attrs.getAttributeIntValue(androidns, "max", 100);
		mStepSize = ta.getInteger(R.styleable.SeekBarPreference_stepSize, 0);
		mShowText = ta.getBoolean(R.styleable.SeekBarPreference_showText, true);
		mDelayedSet = ta.getBoolean(R.styleable.SeekBarPreference_delayedSet, false);
		
		
		if(mDefault < mMin)
			mDefault = mMin;
	}

	@Override
	protected View onCreateDialogView() {
		LinearLayout.LayoutParams params;
		LinearLayout layout = new LinearLayout(mContext);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setPadding(6, 6, 6, 6);

		mSplashText = new TextView(mContext);
		if (mDialogMessage != null)
			mSplashText.setText(mDialogMessage);
		layout.addView(mSplashText);

		mValueText = new TextView(mContext);
		mValueText.setGravity(Gravity.CENTER_HORIZONTAL);
		mValueText.setTextSize(32);
		params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		if (mShowText)
			layout.addView(mValueText, params);

		mSeekBar = new SeekBar(mContext);
		mSeekBar.setOnSeekBarChangeListener(this);
		mSeekBar.setKeyProgressIncrement(mStepSize);
		layout.addView(mSeekBar, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));

		if (shouldPersist()) {
			mValue = getPersistedInt(mDefault) - mMin;
			Log.i("SeekBarPreference", "Persist! " + (mValue + mMin));
		}

		mSeekBar.setMax(mMax - mMin);
		mSeekBar.setProgress(mValue);
		return layout;
	}

	@Override
	protected void onBindDialogView(View v) {
		super.onBindDialogView(v);
		mSeekBar.setMax(mMax);
		mSeekBar.setProgress(mValue);
	}
	
	@Override 
	protected void onDialogClosed (boolean positiveResult) {
		if (mDelayedSet && positiveResult) {
			Log.i("SeekBarPreference", "value:" + (mValue + mMin));
			callChangeListener(mValue + mMin);
			
			if (shouldPersist()) {
				//Ugly workaround for the cases when the value accepted is 
				//not changed from persistent value but should still signal a change
				persistInt(mValue + mMin + 1);
				persistInt(mValue + mMin);
			}			
		}
	}

	@Override
	protected void onSetInitialValue(boolean restore, Object defaultValue) {
		super.onSetInitialValue(restore, defaultValue);
		if (restore)
			mValue = shouldPersist() ? getPersistedInt(mDefault) : 0;
		else
			mValue = (Integer) defaultValue;
	}

	public void onProgressChanged(SeekBar seek, int value, boolean fromTouch) {
		if(mStepSize > 1) 
			value = Math.round(value/mStepSize)*mStepSize;

		String t = String.valueOf(value + mMin);
		if (mShowText) 
			mValueText.setText(mSuffix == null ? t : t.concat(mSuffix));
		
		if (!mDelayedSet) {
			if (shouldPersist())
				persistInt(value + mMin);
			callChangeListener(new Integer(value));
		} else if (fromTouch)
			mValue = value;
	}

	public void onStartTrackingTouch(SeekBar seek) {
	}

	public void onStopTrackingTouch(SeekBar seek) {
	}
	
	public void setMin(int min) {
		mMin = min;
	}
	
	public int getMin() {
		return mMin;
	}

	public void setMax(int max) {
		mMax = max;
	}

	public int getMax() {
		return mMax;
	}
	
	public void setStepSize(int stepSize) {
		mStepSize = stepSize;
	}
	
	public int getStepSize() {
		return mStepSize;
	}

	public void setProgress(int progress) {
		mValue = progress;
		if (mSeekBar != null)
			mSeekBar.setProgress(progress);
	}

	public int getProgress() {
		return mValue;
	}
}
