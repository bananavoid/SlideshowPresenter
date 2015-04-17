package com.lytvyn.slideshowpresenter.network;

import android.content.Context;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

public class ServerRequests {
    private static String GET_ARTICLES_URL = "http://87.251.89.41/application/11424/article/get_articles_list ";

    public static void getAllArticlesRequest(Context context, final ServerRequestCallback callback) {
        JsonArrayRequest jsObjRequest = new JsonArrayRequest(GET_ARTICLES_URL, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                if (callback != null) {
                    callback.onSuccess(response);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (callback != null) {
                    callback.onError(error);
                }
            }
        });

        VolleyQueue.add(context, jsObjRequest);
    }
}
