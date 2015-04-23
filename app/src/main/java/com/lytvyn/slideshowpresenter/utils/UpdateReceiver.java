package com.lytvyn.slideshowpresenter.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.lytvyn.slideshowpresenter.network.TaskCallback;

public class UpdateReceiver extends BroadcastReceiver {
    TaskCallback taskCallback;

//    public UpdateReceiver(TaskCallback callback) {
//        this.taskCallback = callback;
//    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("UpdateReceiver separate", "UPDATE RECEIVED");

//        FtpTask loadImages = new FtpTask(new TaskCallback() {
//            @Override
//            public void onSuccess() {
//            }
//
//            @Override
//            public void onError(String error) {
//
//            }
//        });
//
//        loadImages.execute();
    }
}
