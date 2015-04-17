package com.lytvyn.slideshowpresenter;

import android.app.Application;
import android.provider.Settings;

import com.lytvyn.slideshowpresenter.utils.BatteryTracker;
import com.lytvyn.slideshowpresenter.utils.CheckStatusAlarm;
import com.lytvyn.slideshowpresenter.utils.GPSTracker;

public class SlideShowApp extends Application {
    public static CheckStatusAlarm statusAlarm;
    public static GPSTracker gpsTracker;
    public static BatteryTracker batteryTracker;
    public static String DEVICE_ID;

    @Override
    public void onCreate() {
        super.onCreate();

        statusAlarm = new CheckStatusAlarm(this);
        gpsTracker = new GPSTracker(this);
        batteryTracker = new BatteryTracker(this);

        DEVICE_ID = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    public static void startCheckAlarm() {
        statusAlarm.runSheduledTask();
    }

    public static void stopCheckAlarm() {
        statusAlarm.stopSheduledTask();
    }

    public static GPSTracker getGpsTracker() {
        return gpsTracker;
    }

    public static BatteryTracker getBatteryTracker() {
        return batteryTracker;
    }
}
