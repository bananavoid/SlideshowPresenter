package com.lytvyn.slideshowpresenter;

import com.lytvyn.slideshowpresenter.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class FullscreenActivity extends Activity {

    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 1000;
    //private static final boolean TOGGLE_ON_CLICK = true;
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;
    private SystemUiHider mSystemUiHider;

    private AutoScrollViewPager viewPager;
    //private LoopViewPager viewPager;

    private LinearLayout emptyLayout;

    private ArrayList<String> imgPaths;

    static private final String IMAGES_FOLDER = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES)
            + "/MySlideShow";

    private boolean pagerMoved = false;
    private static final long ANIM_VIEWPAGER_DELAY = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_fullscreen);

        final View contentView = findViewById(R.id.pager);
        emptyLayout = (LinearLayout) findViewById(R.id.emptyLay);

        viewPager = (AutoScrollViewPager) findViewById(R.id.pager);
        viewPager.setCycle(true);
        viewPager.setAutoScrollDurationFactor(3);
        viewPager.setSlideBorderMode(AutoScrollViewPager.SLIDE_BORDER_MODE_CYCLE);
        viewPager.setBorderAnimation(true);
        viewPager.startAutoScroll();

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

//        contentView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (TOGGLE_ON_CLICK) {
//                    mSystemUiHider.toggle();
//                } else {
//                    mSystemUiHider.show();
//                }
//            }
//        });
    }

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

    private void setUpImages() {
        if (viewPager != null) {
            imgPaths = getFromSdcard();
            if (imgPaths.size() == 0) {
                viewPager.setVisibility(View.GONE);
                emptyLayout.setVisibility(View.VISIBLE);
            } else {
                if (viewPager.getChildCount() != 0) {
                    viewPager.removeAllViews();
                }

                ImageAdapter pagerAdapter = new ImageAdapter(this, imgPaths);

                viewPager.setAdapter(pagerAdapter);
                viewPager.setVisibility(View.VISIBLE);
                emptyLayout.setVisibility(View.GONE);

                //viewPager.startAutoScroll();
            }
        }
    }

    private ArrayList<String> getFromSdcard() {
        File storageDir = new File(IMAGES_FOLDER);
        if (!storageDir.exists()) {
            storageDir.mkdir();
        }

        ArrayList<String> paths = new ArrayList<>();
        File[] listFile;

        if (storageDir.isDirectory()) {
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
        //viewPager.stopAutoScroll();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //viewPager.stopAutoScroll();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (viewPager != null) {
//            setUpImages();
//        }
    }
//
//    final Handler handler = new Handler();
//    Timer swipeTimer = new Timer();
//    int currentPage = 0;
//
//    final Runnable Update = new Runnable() {
//        public void run() {
//            if (currentPage == imgPaths.size()) {
//                currentPage = 0;
//            }
//            viewPager.setCurrentItem(currentPage++, true);
//        }
//    };

    String serverAddress = "snackmonsterz.com";
    String userId = "nastya@snackmonsterz.com";
    String password = "Sl1desh0w";
    String remoteDirectory = "/images/monsterz";

    private class FtpTask extends AsyncTask<Void, Void, FTPClient> {
        protected FTPClient doInBackground(Void... args) {
            boolean status = false;
            FTPClient mFtpClient = new FTPClient();
            try {
                //FTPClient mFtpClient = new FTPClient();
                mFtpClient.setConnectTimeout(10 * 1000);
                mFtpClient.connect(InetAddress.getByName(serverAddress));

                boolean answer = mFtpClient.sendNoOp();

                status = mFtpClient.login(userId, password);
                Log.e("isFTPConnected", String.valueOf(status));
                if (FTPReply.isPositiveCompletion(mFtpClient.getReplyCode())) {
                    mFtpClient.setFileType(FTP.ASCII_FILE_TYPE);
                    mFtpClient.enterLocalPassiveMode();
                    FTPFile[] mFileArray = mFtpClient.listFiles();
                    Log.e("Size", String.valueOf(mFileArray.length));
                }
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return mFtpClient;
        }

        protected void onPostExecute(FTPClient result) {
            Log.v("FTPTask","FTP connection complete");
            //ftpClient = result;
            //Where ftpClient is a instance variable in the main activity
        }
    }
}
