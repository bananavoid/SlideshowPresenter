package com.lytvyn.slideshowpresenter;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.lytvyn.slideshowpresenter.network.FTPWorker;
import com.lytvyn.slideshowpresenter.utils.CheckStatusAlarm;
import com.lytvyn.slideshowpresenter.utils.ImgUtils;
import com.lytvyn.slideshowpresenter.utils.StayAwake;

import org.apache.commons.net.ftp.FTPClient;

import java.util.ArrayList;

public class FullscreenActivity extends FragmentActivity {
    private static int UPDATE_IMAGES_INTERVAL = 5000;

    private LinearLayout emptyLayout;
    private FrameLayout fragmentLayout;
    private ArrayList<String> imgPaths;
    private ProgressDialog progress;
    private ImageButton refreshBtn;
    private Handler handler = new Handler();
    private ImageFragment imgFragment;

    public static boolean IS_SLIDESHOW_RUNNING = false;

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

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_fullscreen);

        SlideShowApp.startCheckAlarm();

        progress = new ProgressDialog(this);
        progress.setTitle(getString(R.string.please_wait));
        progress.setMessage(getString(R.string.loading));
        progress.setIndeterminate(true);

        //clearCacheDirectory();

        emptyLayout = (LinearLayout) findViewById(R.id.emptyLay);
        refreshBtn = (ImageButton)findViewById(R.id.refreshBtn);
        fragmentLayout = (FrameLayout) findViewById(R.id.fragmentLayout);

        //new FtpTask().execute();

        setUpImages();
    }

    private void replaceFragment(int count) {
        imgFragment = new ImageFragment().newInstance(imgPaths.get(count));

        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                .replace(R.id.fragmentLayout, imgFragment).commit();
    }

    public void hideHideyBar() {
        int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;

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
        imgPaths = ImgUtils.getFromSdcard();
        if (imgPaths.size() == 0) {
            fragmentLayout.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
            refreshBtn.clearAnimation();
        } else {
            fragmentLayout.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);

            replaceFragment(0);

            handler.postDelayed(runnable, UPDATE_IMAGES_INTERVAL);

            IS_SLIDESHOW_RUNNING = true;

        }

        if(progress.isShowing()) {
            progress.dismiss();
        }
    }

    public void doRefresh(View v) {
        progress = ProgressDialog.show(this, "Please, wait",
                "Loading files from server", true);
        ImgUtils.clearCacheDirectory();
        new FtpTask().execute();
    }

    @Override
    protected void onStop() {
        super.onStop();
        StayAwake.resumeWakeLock();
        IS_SLIDESHOW_RUNNING = false;
    }

    @Override
    protected void onPause() {
        handler.removeCallbacks(runnable);
        super.onPause();
        StayAwake.resumeWakeLock();
    }

    @Override
    protected void onResume() {
        super.onResume();
        StayAwake.resumeWakeLock();
    }

    @Override
    protected void onStart() {
        super.onStart();
        StayAwake.resumeWakeLock();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SlideShowApp.stopCheckAlarm();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        hideHideyBar();
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
            if (progress.isShowing()) {
                progress.hide();
                Toast.makeText(getApplicationContext(), getString(R.string.connection_error), Toast.LENGTH_LONG).show();
            }
        }
    }
}
