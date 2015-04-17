package com.lytvyn.slideshowpresenter.utils;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.SystemClock;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.lytvyn.slideshowpresenter.FullscreenActivity;
import com.lytvyn.slideshowpresenter.SlideShowApp;

public class CheckStatusWakeReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("SimpleWakefulReceiver", "Starting service @ " + SystemClock.elapsedRealtime());

        Location location = SlideShowApp.getGpsTracker().getLocation();

        Log.i("SimpleWakefulReceiver", "Current location " + location);

        Log.i("SimpleWakefulReceiver", "Device id: " + SlideShowApp.DEVICE_ID);

        float batteryLevel = SlideShowApp.getBatteryTracker().getBatteryLevel();

        Log.i("SimpleWakefulReceiver", "Current battery status: " + batteryLevel);

        Log.i("SimpleWakefulReceiver", "Is slideshow running: " + FullscreenActivity.IS_SLIDESHOW_RUNNING);
    }
}
