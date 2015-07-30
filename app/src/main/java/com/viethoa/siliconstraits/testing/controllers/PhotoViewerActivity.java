package com.viethoa.siliconstraits.testing.controllers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.viethoa.siliconstraits.testing.R;
import com.viethoa.siliconstraits.testing.adapters.ImageViewerAdapter;
import com.viethoa.siliconstraits.testing.controllers.base.BaseActivity;
import com.viethoa.siliconstraits.testing.images.loader.ImageLoader;
import com.viethoa.siliconstraits.testing.models.FlickrPhoto;
import com.viethoa.siliconstraits.testing.view.ui.ImageViewerViewpager;

import java.util.ArrayList;

import butterknife.InjectView;
import butterknife.OnClick;

public class PhotoViewerActivity extends BaseActivity {

    private static final String EXTRACT_DATA = "data-extract";
    private static final String EXTRACT_POSITION = "position-extract";

    protected ArrayList<FlickrPhoto> mDataArray;
    protected ImageViewerAdapter mAdapter;
    protected int position;

    @InjectView(R.id.vp_images_slider)
    ImageViewerViewpager viewpager;
    @InjectView(R.id.iv_back_icon)
    View ivBackIcon;

    public static Intent newInstance(Context context, ArrayList<FlickrPhoto> mDataArray, int position) {
        Intent intent = new Intent(context, PhotoViewerActivity.class);
        intent.putExtra(EXTRACT_DATA, mDataArray);
        intent.putExtra(EXTRACT_POSITION, position);
        return intent;
    }

    @Override
    protected void setLayoutResource() {
        setContentView(R.layout.activity_photo_viewer);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        if (bundle == null)
            finish();

        position = bundle.getInt(EXTRACT_POSITION);
        mDataArray = (ArrayList<FlickrPhoto>) bundle.getSerializable(EXTRACT_DATA);

        ImageLoader imageLoader = ImageLoader.getInstance(getSupportFragmentManager());
        mAdapter = new ImageViewerAdapter(this, imageLoader, mDataArray);

        viewpager.setAdapter(mAdapter);
        viewpager.setCurrentItem(position);
    }

    //----------------------------------------------------------------------------------------------
    // Event
    //----------------------------------------------------------------------------------------------

    @OnClick(R.id.iv_back_icon)
    protected void onBackIconClicked() {
        onBackPressed();
    }
}
