/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lytvyn.slideshowpresenter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lytvyn.slideshowpresenter.imgutils.ImageFetcher;
import com.lytvyn.slideshowpresenter.imgutils.ImageWorker;
import com.lytvyn.slideshowpresenter.imgutils.Utils;


public class ImageDetailFragment extends Fragment {
    private static final String IMAGE_DATA_EXTRA = "extra_image_data";
    private String mImageUrl;
    private ImageView mImageView;
    private ImageFetcher mImageFetcher;


    public static ImageDetailFragment newInstance(String imagePath) {
        final ImageDetailFragment f = new ImageDetailFragment();

        final Bundle args = new Bundle();
        args.putString(IMAGE_DATA_EXTRA, imagePath);
        f.setArguments(args);

        return f;
    }


    public ImageDetailFragment() {}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageUrl = getArguments() != null ? getArguments().getString(IMAGE_DATA_EXTRA) : null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate and locate the main ImageView
        final View v = inflater.inflate(R.layout.pager_item, container, false);
        mImageView = (ImageView) v.findViewById(R.id.slideImage);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        if (FullscreenActivity.class.isInstance(getActivity())) {
//            mImageFetcher = ((FullscreenActivity) getActivity()).getImageFetcher();
//            mImageFetcher.loadImage(mImageUrl, mImageView);
//        }
//
//        // Pass clicks on the ImageView to the parent activity to handle
//        if (OnClickListener.class.isInstance(getActivity()) && Utils.hasHoneycomb()) {
//            mImageView.setOnClickListener((OnClickListener) getActivity());
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mImageView != null) {
            // Cancel any pending image work
            ImageWorker.cancelWork(mImageView);
            mImageView.setImageDrawable(null);
        }
    }
}
