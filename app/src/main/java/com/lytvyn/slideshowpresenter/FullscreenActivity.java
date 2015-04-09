package com.lytvyn.slideshowpresenter;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.lytvyn.slideshowpresenter.imgUtils.ImageCache;
import com.lytvyn.slideshowpresenter.imgUtils.ImageFetcher;
import com.lytvyn.slideshowpresenter.logger.Log;
import com.lytvyn.slideshowpresenter.util.SystemUiHider;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import java.util.ArrayList;

import com.google.android.gms.drive.Drive;

public class FullscreenActivity extends BaseActivity {

    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 1000;
    //private static final boolean TOGGLE_ON_CLICK = true;
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;
    private SystemUiHider mSystemUiHider;

    private AutoScrollViewPager mViewPager;

    private LinearLayout emptyLayout;

    private static final String IMAGE_CACHE_DIR = "images";
    private ImageFetcher mImageFetcher;
    private ArrayList<String> mLoadedImagesLinks = new ArrayList<>();
    private ImagePagerAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_fullscreen);

        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int height = displayMetrics.heightPixels;
        final int width = displayMetrics.widthPixels;

        final View contentView = findViewById(R.id.pager);
        emptyLayout = (LinearLayout) findViewById(R.id.emptyLay);

        final int longest = (height > width ? height : width) / 2;

        ImageCache.ImageCacheParams cacheParams =
                new ImageCache.ImageCacheParams(this, IMAGE_CACHE_DIR);
        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

        mImageFetcher = new ImageFetcher(this, longest);
        mImageFetcher.addImageCache(getSupportFragmentManager(), cacheParams);
        mImageFetcher.setImageFadeIn(false);

        mViewPager = (AutoScrollViewPager) findViewById(R.id.pager);
        mViewPager.setCycle(true);
        mViewPager.setAutoScrollDurationFactor(3);
        mViewPager.setSlideBorderMode(AutoScrollViewPager.SLIDE_BORDER_MODE_CYCLE);
        mViewPager.setBorderAnimation(true);

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
                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
                        }
                    }
                });
    }

    @Override
    public void onConnected(Bundle connectionHint) {
  //      super.onCreate(connectionHint);
//        setContentView(R.layout.activity_fullscreen);
//        mResultsListView = (ListView) findViewById(R.id.listViewResults);
//        mResultsAdapter = new ResultsAdapter(this);
//        mResultsListView.setAdapter(mResultsAdapter);

        Drive.DriveApi.fetchDriveId(getGoogleApiClient(), EXISTING_FOLDER_ID)
                .setResultCallback(idCallback);

//        DriveId FOLDER_ID = DriveId.decodeFromString("DriveId:0BzHIUN_rhur0fjh3Nk41VkVaaWVMem5GdHQ0aUNoTm84bE9PQ08ydTBEcXVFbVRYYnVWcGc");
//
//
//        DriveFolder folder = Drive.DriveApi.getFolder(getGoogleApiClient(), FOLDER_ID);
//        folder.listChildren(getGoogleApiClient()).setResultCallback(metadataResult);
    }


    final private ResultCallback<DriveApi.DriveIdResult> idCallback = new ResultCallback<DriveApi.DriveIdResult>() {
        @Override
        public void onResult(DriveApi.DriveIdResult result) {
            if (!result.getStatus().isSuccess()) {
                showMessage("Cannot find DriveId. Are you authorized to view this file?");
                return;
            }
            DriveFolder folder = Drive.DriveApi.getFolder(getGoogleApiClient(), result.getDriveId());
            Query query = new Query.Builder()
                    .addFilter(Filters.eq(SearchableField.MIME_TYPE, "text/plain"))
                    .build();
            folder.queryChildren(getGoogleApiClient(), query)
                    .setResultCallback(metadataResult);
            //folder.listChildren(getGoogleApiClient()).setResultCallback(metadataResult);

        }
        //CAESSDBCekhJVU5fcmh1cjBmamgzTms0MVZrVmFhV1ZNZW01R2RIUTBhVU5vVG04NGJFOVBRMDh5ZFRCRWNYVkZiVlJZWW5WV2NHYximCyD68o-kiFIoAQ==
    };

    final private ResultCallback<DriveApi.MetadataBufferResult> metadataResult = new
            ResultCallback<DriveApi.MetadataBufferResult>() {
                @Override
                public void onResult(DriveApi.MetadataBufferResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("Problem while retrieving files");
                        return;
                    }

                    Log.d("MEtadataSIZE", "SIZE: " + result.getMetadataBuffer().getCount());

                    for (int i=0; i < result.getMetadataBuffer().getCount(); ++i) {
                        MetadataBuffer metadata = result.getMetadataBuffer();
                        mLoadedImagesLinks.add(metadata.get(i).getWebContentLink());
                    }

                    mAdapter = new ImagePagerAdapter(getSupportFragmentManager(), mLoadedImagesLinks.size());
                    mViewPager.setAdapter(mAdapter);
                    mViewPager.startAutoScroll();
                }
            };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ECLAIR
                && (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME)
                && event.getRepeatCount() == 0) {
            //onBackPressed();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        // Do nothing
        return;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        delayedHide(50);
    }

    @Override
    public void onAttachedToWindow() {

        super.onAttachedToWindow();
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


    public void doRefresh(View v) {

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mViewPager.stopAutoScroll();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

//        Drive.DriveApi.fetchDriveId(getGoogleApiClient(), EXISTING_FOLDER_ID)
//        .setResultCallback(idCallback);
    }

    public ImageFetcher getImageFetcher() {
        return mImageFetcher;
    }

    private class ImagePagerAdapter extends FragmentStatePagerAdapter {
        private final int mSize;

        public ImagePagerAdapter(FragmentManager fm, int size) {
            super(fm);
            mSize = size;
        }

        @Override
        public int getCount() {
            return mSize;
        }

        @Override
        public Fragment getItem(int position) {
            return ImageDetailFragment.newInstance(mLoadedImagesLinks.get(position));
        }
    }
}
