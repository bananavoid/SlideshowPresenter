package com.lytvyn.slideshowpresenter.utils;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.provider.Settings;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.lytvyn.slideshowpresenter.FullscreenActivity;
import com.lytvyn.slideshowpresenter.SlideShowApp;
import com.lytvyn.slideshowpresenter.network.ServerRequests;

import org.json.JSONException;
import org.json.JSONObject;

public class CheckStatusWakeReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Location location = SlideShowApp.getGpsTracker().getLocation();
        float batteryLevel = SlideShowApp.getBatteryTracker().getBatteryLevel();

        JSONObject deviceStatusJson = new JSONObject();
        try {
            deviceStatusJson.put("device_id", Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
            deviceStatusJson.put("battery_level", batteryLevel);
            deviceStatusJson.put("location_longitude", location.getLongitude());
            deviceStatusJson.put("location_latitude", location.getLatitude());
            deviceStatusJson.put("is_slideshow_running", FullscreenActivity.IS_SLIDESHOW_RUNNING);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ServerRequests.postDeviceStatusRequest(context, deviceStatusJson);
    }
}
