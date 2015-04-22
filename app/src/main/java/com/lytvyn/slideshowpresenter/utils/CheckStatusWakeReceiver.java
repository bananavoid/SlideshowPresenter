package com.lytvyn.slideshowpresenter.utils;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.android.volley.VolleyError;
import com.google.gson.JsonObject;
import com.lytvyn.slideshowpresenter.FullscreenActivity;
import com.lytvyn.slideshowpresenter.SlideShowApp;
import com.lytvyn.slideshowpresenter.network.ServerRequestCallback;
import com.lytvyn.slideshowpresenter.network.ServerRequests;

import org.json.JSONArray;

public class CheckStatusWakeReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Location location = SlideShowApp.getGpsTracker().getLocation();
        float batteryLevel = SlideShowApp.getBatteryTracker().getBatteryLevel();

        JsonObject deviceStatusJson = new JsonObject();
        deviceStatusJson.addProperty("device_id", Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
        deviceStatusJson.addProperty("battery_level", batteryLevel);
        deviceStatusJson.addProperty("location_longitude", location.getLongitude());
        deviceStatusJson.addProperty("location_latitude", location.getLatitude());
        deviceStatusJson.addProperty("is_slideshow_running", FullscreenActivity.IS_SLIDESHOW_RUNNING);

        ServerRequests.postDeviceStatusRequest(context, deviceStatusJson);
    }
}
