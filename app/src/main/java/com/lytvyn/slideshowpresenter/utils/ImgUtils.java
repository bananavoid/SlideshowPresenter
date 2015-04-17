package com.lytvyn.slideshowpresenter.utils;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.io.File;
import java.util.ArrayList;

public final class ImgUtils {
    private static File STORAGE_DIR = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/SlideshowImages/");

    public static Bitmap decodeSampledBitmapByPath(Context context, String path) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        final int height = displayMetrics.heightPixels;
        final int width = displayMetrics.widthPixels;

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, width, height);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inDither = true;
        return BitmapFactory.decodeFile(path, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap scaleCenterCrop(Context context, String path) {
        Bitmap myBitmap = decodeSampledBitmapByPath(context, path);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        final int height = displayMetrics.heightPixels;
        final int width = displayMetrics.widthPixels;
        int newWidth = width;
        int newHeight = height;
        int sourceWidth = myBitmap.getWidth();
        int sourceHeight = myBitmap.getHeight();
        float xScale = (float) newWidth / sourceWidth;
        float yScale = (float) newHeight / sourceHeight;
        float scale = Math.max(xScale, yScale);
        float scaledWidth = scale * sourceWidth;
        float scaledHeight = scale * sourceHeight;
        float left = (newWidth - scaledWidth) / 2;
        float top = (newHeight - scaledHeight) / 2;
        RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);
        Bitmap scaled = Bitmap.createBitmap(newWidth, newHeight, myBitmap.getConfig());
        Canvas canvas = new Canvas(scaled);
        canvas.drawBitmap(myBitmap, null, targetRect, null);
        return scaled;
    }

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
