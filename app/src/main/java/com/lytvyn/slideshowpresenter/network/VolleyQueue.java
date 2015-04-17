package com.lytvyn.slideshowpresenter.network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleyQueue {
    public static final String TAG = VolleyQueue.class.getSimpleName();

    private static RequestQueue queue = null;

    private VolleyQueue() { }

    public static synchronized RequestQueue getQueue(Context context) {
        if (queue == null) {
            queue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return queue;
    }

    public static <T> void add(Context context, Request req) {
        getQueue(context).add(req);
    }
}