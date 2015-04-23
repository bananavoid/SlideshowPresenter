package com.lytvyn.slideshowpresenter.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.lytvyn.slideshowpresenter.network.TaskCallback;

import java.util.Calendar;

public class UpdateAlarm {
    public Context appContext;
    public Intent intent;
    public PendingIntent pendingIntent;
    public AlarmManager alarmManager;
    public Calendar calendar;

    public UpdateAlarm(Context context) {
        this.appContext = context;
        UpdateReceiver receiver = new UpdateReceiver(new TaskCallback() {
            @Override

            public void onSuccess() {
                Log.d("UpdateAlarm separate", "UPDATE RECEIVED");
            }

            @Override
            public void onError(String error) {

            }
        });
        intent = new Intent(context, receiver.getClass());
        pendingIntent = PendingIntent.getBroadcast(context, 11, intent, 0);
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public void runSheduledTask() {
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 13);
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
