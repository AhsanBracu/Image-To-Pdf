package com.example.ahsan.uipharmecy;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class MainFragment extends Fragment {
    ImageView mImageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        mImageView = (ImageView) view.findViewById(R.id.image_taken);
        return view;
    }

    public void setImageView(Bitmap bitmap) {
        mImageView.setImageBitmap(bitmap);
    }
}
