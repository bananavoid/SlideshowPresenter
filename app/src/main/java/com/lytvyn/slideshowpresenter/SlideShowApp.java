package com.lytvyn.slideshowpresenter;

import android.app.Application;
import android.content.IntentSender;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.lytvyn.slideshowpresenter.logger.Log;

public class SlideShowApp extends Application {

    GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate() {
        super.onCreate();
//
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//            .addApi(Drive.API)
//            .addScope(Drive.SCOPE_FILE)
//            .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) getApplicationContext())
//            .addOnConnectionFailedListener(this)
//            .build();
//
//        mGoogleApiClient.connect();
    }

//    @Override
//    public void onConnectionFailed(ConnectionResult connectionResult) {
//        Log.d("APP", "onConnectionFailed");
//    }
//
//    @Override
//    public void onConnected(Bundle bundle) {
//        Log.d("APP", "onConnected");
//        //mGoogleApiClient.connect();
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//        Log.d("APP", "onConnectionSuspended");
//    }
//
//    public GoogleApiClient getGoogleAPIClient() {
//        return mGoogleApiClient;
//    }
}
