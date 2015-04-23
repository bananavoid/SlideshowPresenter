package com.lytvyn.slideshowpresenter.network;


public interface TaskCallback {
    void onSuccess();
    void onError(String error);
}
