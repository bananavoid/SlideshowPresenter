package com.lytvyn.slideshowpresenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;


public class ImageViewRecyclable extends ImageView {
    private Bitmap bitmap;

    public ImageViewRecyclable(Context context)
    {
        super(context);
    }

    @Override
    public void setImageBitmap(Bitmap bm)
    {
        super.setImageBitmap(bm);
        if (bitmap != null) bitmap.recycle();
        this.bitmap = bm;
    }
}
