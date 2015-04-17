package com.lytvyn.slideshowpresenter.network;

import com.android.volley.VolleyError;

import org.json.JSONArray;

public interface ServerRequestCallback {
    void onSuccess(JSONArray result);
    void onError(VolleyError error);
}
