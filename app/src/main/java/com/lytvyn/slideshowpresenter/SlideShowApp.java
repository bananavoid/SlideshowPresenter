package com.lytvyn.slideshowpresenter;

import android.app.Application;

import com.lytvyn.slideshowpresenter.utils.BatteryTracker;
import com.lytvyn.slideshowpresenter.utils.CheckStatusAlarm;
import com.lytvyn.slideshowpresenter.utils.GPSTracker;
import com.lytvyn.slideshowpresenter.utils.UpdateAlarm;

public class SlideShowApp extends Application {
    public static CheckStatusAlarm statusAlarm;
    public static UpdateAlarm updateAlarm;
    public static GPSTracker gpsTracker;
    public static BatteryTracker batteryTracker;

    @Override
    public void onCreate() {
        super.onCreate();

        statusAlarm = new CheckStatusAlarm(this);
        updateAlarm = new UpdateAlarm(this);
        gpsTracker = new GPSTracker(this);
        //batteryTracker = new BatteryTracker(this);
    }

    public static void startCheckAlarm() {
        statusAlarm.runSheduledTask();
    }

    public static void stopCheckAlarm() {
        statusAlarm.stopSheduledTask();
    }

    public static void startUpdateAlarm() {
        updateAlarm.runSheduledTask();
    }

    public static void stopUpdateAlarm() {
        updateAlarm.stopSheduledTask();
    }

    public static GPSTracker getGpsTracker() {
        return gpsTracker;
    }

//    public static BatteryTracker getBatteryTracker() {
//        return batteryTracker;
//    }
}
