package com.lytvyn.slideshowpresenter;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.lytvyn.slideshowpresenter.network.FtpAsyncTask;
import com.lytvyn.slideshowpresenter.network.TaskCallback;
import com.lytvyn.slideshowpresenter.utils.ImgUtils;
import com.lytvyn.slideshowpresenter.utils.StayAwake;
import com.lytvyn.slideshowpresenter.utils.UpdateReceiver;

import org.apache.commons.net.ftp.FTPClient;

import java.util.ArrayList;
import java.util.Calendar;

public class FullscreenActivity extends FragmentActivity {
    private static int UPDATE_IMAGES_INTERVAL = 5000;

    private LinearLayout emptyLayout;
    private FrameLayout fragmentLayout;
    private ArrayList<String> imgPaths;
    private ProgressDialog progress;
    private Handler handler = new Handler();
    private ImageFragment imgFragment;
    private FtpAsyncTask loadImages;

    public static boolean IS_SLIDESHOW_RUNNING = false;

    final Runnable runnable = new Runnable() {
        int count = 0;

        public void run() {
            if (imgPaths.size() != 0) {
                if (count == imgPaths.size()) {
                    count = 0;
                }

                replaceFragment(count);


                if(progress.isShowing()) {
                    progress.dismiss();
                }

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

        createFTPAsyncTask();

        SlideShowApp.startCheckAlarm();
        SlideShowApp.startUpdateAlarm();

        createFTPAsyncTask();

        createProgressDialog();

        progress.show();

        emptyLayout = (LinearLayout) findViewById(R.id.emptyLay);
        fragmentLayout = (FrameLayout) findViewById(R.id.fragmentLayout);
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

    public void createProgressDialog() {
        progress = new ProgressDialog(this);
        progress.setTitle(getString(R.string.please_wait));
        progress.setMessage(getString(R.string.loading));
        progress.setIndeterminate(true);
    }

    public void setUpImages() {
        imgPaths = ImgUtils.getFromSdcard();
        if (imgPaths.size() == 0) {
            fragmentLayout.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
        } else {
            fragmentLayout.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);

            handler.postDelayed(runnable, UPDATE_IMAGES_INTERVAL);

            IS_SLIDESHOW_RUNNING = true;
        }
    }

    public void doRefresh(View v) {
        getImagesFromFTP();
    }

    private void createFTPAsyncTask() {
        if (loadImages == null) {
            loadImages = new FtpAsyncTask(new TaskCallback() {
                @Override
                public void onSuccess() {
                    setUpImages();
                }

                @Override
                public void onError(String error) {
                    if(progress.isShowing()) {
                        progress.dismiss();
                    }

                    Toast.makeText(getBaseContext(), "Error while loading files", Toast.LENGTH_LONG);
                }
            });
        }
    }

    public void getImagesFromFTP() {
        if (loadImages.getStatus() != AsyncTask.Status.RUNNING) {
            if (progress != null) {
                progress.show();
            }
            loadImages = null;
            createFTPAsyncTask();
            loadImages.execute();
        }
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
        IS_SLIDESHOW_RUNNING = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        StayAwake.resumeWakeLock();
        IS_SLIDESHOW_RUNNING = false;
        getImagesFromFTP();
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
        SlideShowApp.stopUpdateAlarm();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        hideHideyBar();
    }
}
