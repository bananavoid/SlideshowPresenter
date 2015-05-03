package com.lytvyn.slideshowpresenter.utils;


import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

public final class BatteryTracker {

    public static float getBatteryLevel(Context context) {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent statusIntent = context.registerReceiver(null, ifilter);

        String batteryStatus = "";
        int status = statusIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        int chargePlug = statusIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

        int level = statusIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = statusIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float currentLevel = (level / (float)scale);

        return currentLevel;
    }
}
