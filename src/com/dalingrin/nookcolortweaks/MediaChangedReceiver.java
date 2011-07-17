package com.dalingrin.nookcolortweaks;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;

public class MediaChangedReceiver extends BroadcastReceiver {
	
	private Resources mRes;
	private NotificationManager mNM;
	private Context mContext;
    // Unique Identification Number for the Notification.
    // We use it on Notification start, and to cancel it.
    private int NOTIFICATION = 123;
    private final static String TAG = "NookColorTweaks";

	@Override
	public void onReceive(Context context, Intent intent) {
		mRes = context.getResources();
		mNM = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		if (intent != null) {
			String path = intent.getDataString();
			if (intent.getAction().contains("MEDIA_MOUNTED")) {
				if (path != null) {
					if (path.contains(mRes.getString(R.string.usb_storage_path))) {
						Log.i(TAG, "Media mounted at: " + path);
						mContext = context;
				
				        // Display a notification for easy access to unmount usb disk
				        showNotification();
					}
				}
			} else if (intent.getAction().contains("MEDIA_REMOVED")) {
				Log.i(TAG, "Media removed at: " + path);
				if (intent.getDataString().contains(mRes.getString(R.string.usb_storage_path))) {
					mNM.cancel(NOTIFICATION);
				}
			} else if (intent.getAction().contains("MEDIA_UNMOUNTED")) {
				Log.i(TAG, "Media unmounted at " + path);
				if (intent.getDataString().contains(mRes.getString(R.string.usb_storage_path))) {
					mNM.cancel(NOTIFICATION);
				}
			}
		}
	}
	
    private void showNotification() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = "Select to unmount USB storage device";

        // Set the icon, scrolling text and timestamp
        Notification notification = new Notification(R.drawable.icon, text,
                System.currentTimeMillis());

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0,
        		new Intent(android.provider.Settings.ACTION_MEMORY_CARD_SETTINGS), 0);
        
        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(mContext, "USB storage device mounted",
                       text, contentIntent);

        // Send the notification.
        mNM.notify(NOTIFICATION, notification);
    }

}
