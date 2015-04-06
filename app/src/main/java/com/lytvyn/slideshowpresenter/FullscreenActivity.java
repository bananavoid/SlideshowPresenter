package com.lytvyn.slideshowpresenter;

import com.lytvyn.slideshowpresenter.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;

import java.io.File;
import java.util.ArrayList;

public class FullscreenActivity extends Activity  {

    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private static final boolean TOGGLE_ON_CLICK = true;
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;
    private SystemUiHider mSystemUiHider;

    private AutoScrollViewPager viewPager;
    private LinearLayout emptyLayout;

    static private final String IMAGES_FOLDER = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES)
            + "/MySlideShow";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);


        final View contentView = findViewById(R.id.pager);
        emptyLayout = (LinearLayout)findViewById(R.id.emptyLay);

        viewPager = (AutoScrollViewPager) findViewById(R.id.pager);
        viewPager.setInterval(5000);
        viewPager.setCycle(false);


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
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        delayedHide(50);
    }

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

    private void setUpImages() {
        if (viewPager != null) {
            ArrayList<String> imgPaths = getFromSdcard();
            if (imgPaths.size() == 0) {
                viewPager.setVisibility(View.GONE);
                emptyLayout.setVisibility(View.VISIBLE);
            } else {
                ImageAdapter pagerAdapter = new ImageAdapter(this, imgPaths);

                viewPager.setAdapter(pagerAdapter);
                viewPager.setVisibility(View.VISIBLE);
                emptyLayout.setVisibility(View.GONE);

                viewPager.startAutoScroll();
            }
        }
    }

    private ArrayList<String> getFromSdcard()
    {
        File storageDir = new File(IMAGES_FOLDER);
        if(!storageDir.exists()) {
            storageDir.mkdir();
        }

        ArrayList<String> paths = new ArrayList<>();
        File[] listFile;

        if (storageDir.isDirectory()){
            listFile = storageDir.listFiles();

            for (int i = 0; i < listFile.length; i++) {
                paths.add(listFile[i].getAbsolutePath());
            }
        }

        return paths;
    }

    public void doRefresh(View v) {
        setUpImages();
    }

    @Override
    protected void onStop() {
        super.onStop();
        viewPager.stopAutoScroll();
    }

    @Override
    protected void onPause() {
        super.onPause();
        viewPager.stopAutoScroll();
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (viewPager != null) {
            setUpImages();
        }
    }
}
