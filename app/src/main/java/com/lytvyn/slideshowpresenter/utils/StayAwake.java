package com.lytvyn.slideshowpresenter.utils;

import android.content.Context;
import android.os.PowerManager;


public final class StayAwake {

    static PowerManager powerManager;
    static PowerManager.WakeLock wakeLock;

    public static void startStayAwake(Context context) {
        powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "mytag");
        wakeLock.acquire();
    }

    public static void resumeWakeLock() {
        if ((wakeLock != null) &&
                (wakeLock.isHeld() == false)) {
            wakeLock.acquire();
        }
    }
}
