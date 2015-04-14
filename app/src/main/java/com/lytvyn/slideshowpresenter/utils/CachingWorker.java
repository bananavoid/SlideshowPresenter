package com.lytvyn.slideshowpresenter.utils;

import android.os.Environment;

import java.io.File;
import java.util.ArrayList;

public final class CachingWorker {
    public static File STORAGE_DIR = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/SlideshowImages/");

    public static ArrayList<String> getFromSdcard() {

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

    public static void clearCacheDirectory() {
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
}
