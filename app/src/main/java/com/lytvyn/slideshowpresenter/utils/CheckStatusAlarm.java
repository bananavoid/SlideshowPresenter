package com.lytvyn.slideshowpresenter.utils;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class CheckStatusAlarm {
    public static final int REPEAT_INTERVAL = 1000 * 60;
    public Context appContext;
    public Intent intent;
    public PendingIntent pendingIntent;
    public AlarmManager alarmManager;

    public CheckStatusAlarm(Context context) {
        this.appContext = context;
        intent = new Intent(context, CheckStatusWakeReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, 12, intent, 0);
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public void runSheduledTask() {
        alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(),
                REPEAT_INTERVAL,
                pendingIntent);
    }

    public void stopSheduledTask() {
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }
}
