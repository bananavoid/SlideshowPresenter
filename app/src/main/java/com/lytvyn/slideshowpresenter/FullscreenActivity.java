package com.lytvyn.slideshowpresenter;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.util.ArrayList;

public class FullscreenActivity extends FragmentActivity {

    private static File STORAGE_DIR = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/SlideshowImages/");

    private static int UPDATE_IMAGES_INTERVAL = 5000;

    private LinearLayout emptyLayout;
    private FrameLayout fragmentLayout;

    private ArrayList<String> imgPaths;

    private ProgressDialog progress;
    private ImageButton refreshBtn;

    Handler handler = new Handler();
    ImageFragment imgFragment;

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

        progress = new ProgressDialog(this);
        progress.setTitle(getString(R.string.please_wait));
        progress.setMessage(getString(R.string.loading));
        progress.setIndeterminate(true);
        //progress.setMax(100);
        //progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        clearCacheDirectory();

        emptyLayout = (LinearLayout) findViewById(R.id.emptyLay);
        refreshBtn = (ImageButton)findViewById(R.id.refreshBtn);
        fragmentLayout = (FrameLayout) findViewById(R.id.fragmentLayout);

        new FtpTask().execute();
    }

    private void replaceFragment(int count) {
        imgFragment = new ImageFragment().newInstance(imgPaths.get(count));

        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
                .replace(R.id.fragmentLayout, imgFragment).commit();
    }

    public void toggleHideyBar() {
        int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;

        boolean isImmersiveModeEnabled =
                ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
        if (isImmersiveModeEnabled) {
            Log.i("BEBEBE", "Turning immersive mode mode off. ");
        } else {
            Log.i("BEBEBE", "Turning immersive mode mode on.");
        }

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
        imgPaths = getFromSdcard();
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

    private ArrayList<String> getFromSdcard() {

        ArrayList<String> paths = new ArrayList<>();
        File[] listFile;

        if (STORAGE_DIR.isDirectory()) {
            listFile = STORAGE_DIR.listFiles();

            for (int i = 0; i < listFile.length; i++) {
                paths.add(listFile[i].getAbsolutePath());
            }
        }

        return paths;
    }

    public void clearCacheDirectory() {
        if (!STORAGE_DIR.exists()) {
            STORAGE_DIR.mkdirs();
        } else {
            if (STORAGE_DIR.isDirectory()) {
                String[] children = STORAGE_DIR.list();
                for (int i = 0; i < children.length; i++) {
                    new File(STORAGE_DIR, children[i]).delete();
                }
            }
        }
    }

    public void doRefresh(View v) {
        progress = ProgressDialog.show(this, "Please, wait",
                "Loading files from server", true);
        clearCacheDirectory();
        new FtpTask().execute();
    }



    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        handler.removeCallbacks(runnable);
        super.onPause();
    }

    @Override
    protected void onResume() {
        //handler.postDelayed(runnable, UPDATE_IMAGES_INTERVAL);
        super.onResume();
        toggleHideyBar();
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
