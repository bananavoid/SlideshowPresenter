package com.lytvyn.slideshowpresenter.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class UpdateAlarm {
    public Context appContext;
    public Intent intent;
    public PendingIntent pendingIntent;
    public AlarmManager alarmManager;
    public Calendar calendar;

    public UpdateAlarm(Context context) {
        this.appContext = context;

        intent = new Intent(context, UpdateReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, 11, intent, 0);
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public void runSheduledTask() {
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent);
    }

    public void stopSheduledTask() {
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }
}
