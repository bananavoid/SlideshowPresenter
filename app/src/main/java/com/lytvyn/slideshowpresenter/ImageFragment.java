package com.lytvyn.slideshowpresenter;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


public class ImageFragment extends Fragment {

    private static final String IMG_PATH = "param1";

    private String path;
    private ImageView contentView;

    public static ImageFragment newInstance(String path) {
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putString(IMG_PATH, path);
        fragment.setArguments(args);
        return fragment;
    }

    public ImageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            path = getArguments().getString(IMG_PATH);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.pager_item, null, false);

        ImageView contentView = (ImageView)view.findViewById(R.id.slideImage);
        Bitmap imgBitmap = ImgUtils.decodeSampledBitmapByPath(getActivity(), path);
        contentView.setImageBitmap(imgBitmap);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (contentView != null) {
            ViewGroup parentViewGroup = (ViewGroup) contentView.getParent();
            if (parentViewGroup != null) {
                parentViewGroup.removeAllViews();
            }
        }
    }
}
