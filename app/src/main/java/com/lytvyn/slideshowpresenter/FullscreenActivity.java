package com.lytvyn.slideshowpresenter;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PersistableBundle;
import android.os.PowerManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.commonsware.cwac.wakeful.WakefulIntentService;
import com.lytvyn.slideshowpresenter.network.FTPWorker;
import com.lytvyn.slideshowpresenter.util.SystemUiHider;
import com.lytvyn.slideshowpresenter.utils.CachingWorker;
import com.lytvyn.slideshowpresenter.utils.StayAwake;

import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.util.ArrayList;

public class FullscreenActivity extends FragmentActivity {
    private static int UPDATE_IMAGES_INTERVAL = 5000;
    private static int SEND_STATUS_INTERVAL = 60000;

    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link com.lytvyn.slideshowpresenter.util.SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;


    private LinearLayout emptyLayout;
    private FrameLayout fragmentLayout;

    private ArrayList<String> imgPaths;

    private ProgressDialog progress;
    private ImageButton refreshBtn;

    private Handler handler = new Handler();
    private ImageFragment imgFragment;

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };


    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    final Runnable runnable = new Runnable() {
        int count = 1;

        public void run() {
            if (imgPaths.size() != 0) {
                if (count == imgPaths.size()) {
                    count = 0;
                }

                replaceFragment(count);

                ++count;

                handler.postDelayed(runnable, UPDATE_IMAGES_INTERVAL);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("Activity", "onCreate");

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_fullscreen);

        final View contentView = findViewById(R.id.content);

        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider
                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
                    int mShortAnimTime;

                    @Override
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
                    public void onVisibilityChange(boolean visible) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {

                            if (mShortAnimTime == 0) {
                                mShortAnimTime = getResources().getInteger(
                                        android.R.integer.config_shortAnimTime);
                            }
                        }

                        if (visible && AUTO_HIDE) {
                            // Schedule a hide().
                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
                        }
                    }
                });

        // Set up the user interaction to manually show or hide the system UI.
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TOGGLE_ON_CLICK) {
                    mSystemUiHider.toggle();
                } else {
                    mSystemUiHider.show();
                }
            }
        });

        StayAwake.startStayAwake(this);

        progress = new ProgressDialog(this);
        progress.setTitle(getString(R.string.please_wait));
        progress.setMessage(getString(R.string.loading));
        progress.setIndeterminate(true);

        CachingWorker.clearCacheDirectory();

        emptyLayout = (LinearLayout) findViewById(R.id.emptyLay);
        refreshBtn = (ImageButton)findViewById(R.id.refreshBtn);
        fragmentLayout = (FrameLayout) findViewById(R.id.fragmentLayout);

        new FtpTask().execute();

        //setUpImages();
    }

    @Override
    protected void onPause() {
        handler.removeCallbacks(runnable);
        super.onPause();
        StayAwake.resumeWakeLock();
        Log.d("Activity", "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        StayAwake.resumeWakeLock();
        //removeSystemBar();
        Log.d("Activity", "onResume");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Activity", "onDestroy");
    }

    @Override
    protected void onStart() {
        super.onStart();
        StayAwake.resumeWakeLock();
        Log.d("Activity", "onStart");

    }

    @Override
    protected void onStop() {
        handler.removeCallbacks(runnable);
        super.onStop();
        StayAwake.resumeWakeLock();
        Log.d("Activity", "onStop");

    }

    private void replaceFragment(int count) {
        imgFragment = new ImageFragment().newInstance(imgPaths.get(count));

        getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_ENTER_MASK)
                //.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                .replace(R.id.fragmentLayout, imgFragment).commit();
    }

    public void removeSystemBar() {
        int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
//
//        boolean isImmersiveModeEnabled =
//                ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
//        if (isImmersiveModeEnabled) {
//            Log.i("BEBEBE", "Turning immersive mode mode off. ");
//        } else {
//            Log.i("BEBEBE", "Turning immersive mode mode on.");
//        }

        if (Build.VERSION.SDK_INT >= 14) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }

        if (Build.VERSION.SDK_INT >= 16) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        }

        if (Build.VERSION.SDK_INT >= 18) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }

        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
    }

    private void setUpImages() {
        imgPaths = CachingWorker.getFromSdcard();
        if (imgPaths.size() == 0) {
            fragmentLayout.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
            refreshBtn.clearAnimation();
        } else {
            fragmentLayout.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);

            replaceFragment(0);

            handler.postDelayed(runnable, UPDATE_IMAGES_INTERVAL);

        }

        if(progress.isShowing()) {
            progress.dismiss();
        }
    }

    public void doRefresh(View v) {
        progress = ProgressDialog.show(this, getString(R.string.please_wait),
                getString(R.string.loading), true);
        CachingWorker.clearCacheDirectory();
        new FtpTask().execute();
    }

    private class FtpTask extends AsyncTask<Void, Void, FTPClient> {
        protected FTPClient doInBackground(Void... args) {
            return FTPWorker.cacheImagesFromServer();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.show();
        }

        protected void onPostExecute(FTPClient result) {
            setUpImages();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
}
