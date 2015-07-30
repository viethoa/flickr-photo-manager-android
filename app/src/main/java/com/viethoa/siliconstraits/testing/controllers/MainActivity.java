package com.viethoa.siliconstraits.testing.controllers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.googlecode.flickrjandroid.photos.Photo;
import com.viethoa.siliconstraits.testing.R;
import com.viethoa.siliconstraits.testing.adapters.PhotosAdapter;
import com.viethoa.siliconstraits.testing.daggers.managers.FlickrManager;
import com.viethoa.siliconstraits.testing.flickr.base.BaseFlickrMainActivity;
import com.viethoa.siliconstraits.testing.flickr.managers.FlickrLoginManager;
import com.viethoa.siliconstraits.testing.images.loader.ImageLoader;
import com.viethoa.siliconstraits.testing.managers.CacheManager;
import com.viethoa.siliconstraits.testing.models.FlickrPhoto;
import com.viethoa.siliconstraits.testing.models.events.FlickrPhotoEvent;
import com.viethoa.siliconstraits.testing.services.UserPhotoService;

import java.lang.reflect.Type;
import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.InjectView;
import de.greenrobot.event.EventBus;
import nl.changer.polypicker.Config;
import nl.changer.polypicker.ImagePickerActivity;

public class MainActivity extends BaseFlickrMainActivity implements
        PhotosAdapter.OnPhotoItemClickListener,
        SwipeRefreshLayout.OnRefreshListener {

    private static final int REQUEST_GET_IMAGES = 0x101;
    private static final int SECTION_LIMIT_IMAGES = 5;

    protected Type mListType;
    protected String mCacheKey;

    protected ArrayList<FlickrPhoto> mDataArray;
    protected RecyclerView.Adapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;

    @Inject
    FlickrManager flickrManager;
    @Inject
    EventBus eventBus;

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @InjectView(R.id.refresh_view)
    SwipeRefreshLayout refreshLayout;
    @InjectView(R.id.no_data_view)
    View vNoDataView;

    public static Intent newInstance(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        return intent;
    }

    @Override
    protected void setLayoutResource() {
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!FlickrLoginManager.hasLogin()) {
            goToLoginActivity(true);
            return;
        }

        initialiseData();
        initialiseUI();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        eventBus.getDefault().unregister(this);
    }

    //----------------------------------------------------------------------------------------------
    // Setup
    //----------------------------------------------------------------------------------------------

    protected void initialiseData() {
        if (mListType == null)
            mListType = new TypeToken<ArrayList<FlickrPhoto>>() {
            }.getType();
        if (mCacheKey == null || mCacheKey.trim().length() <= 0)
            mCacheKey = this.getClass().getSimpleName();

        this.mDataArray = restoreCacheList();

        //pull data from server
        getDataFromServer(false);
    }

    protected void initialiseUI() {
        //Toolbar
        //mToolbar.setLogo(R.mipmap.ic_launcher);
        mToolbar.inflateMenu(R.menu.main_menu);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_upload:
                        goToUploadPhotoScreen();
                        return true;
                    case R.id.action_logout:
                        goToLoginActivity(false);
                        return true;
                    case R.id.action_feedback:
                        return true;
                    default:
                        return true;
                }
            }
        });

        //Swipe refresh
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(
                R.color.swipe_color_1,
                R.color.swipe_color_2,
                R.color.swipe_color_3,
                R.color.swipe_color_4);

        //Recycler View
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        ImageLoader imageLoader = ImageLoader.getInstance(getSupportFragmentManager());
        mAdapter = new PhotosAdapter(this, imageLoader, mDataArray);
        mRecyclerView.setAdapter(mAdapter);

        //NoDataView
        refreshNoDataView();
    }

    //----------------------------------------------------------------------------------------------
    // Event
    //----------------------------------------------------------------------------------------------

    @Override
    public void onRefresh() {
        getDataFromServer(true);
    }

    @Override
    public void OnPhotoItemClicked(int position) {
        Intent intent = PhotoViewerActivity.newInstance(this, mDataArray, position);
        startActivity(intent);
    }

    protected void goToLoginActivity(boolean hasOverride) {
        Intent intent = LoginActivity.newInstance(this, !hasOverride);
        startActivity(intent);

        if (hasOverride) {
            overridePendingTransition(0, 0);
        }
        finish();
    }

    protected void refreshNoDataView() {
        boolean isShowing = (mDataArray == null || mDataArray.size() <= 0);
        vNoDataView.setVisibility(isShowing ? View.VISIBLE : View.GONE);
        mRecyclerView.setVisibility(isShowing ? View.GONE : View.VISIBLE);
    }

    //----------------------------------------------------------------------------------------------
    // Upload images section
    //----------------------------------------------------------------------------------------------

    protected void goToUploadPhotoScreen() {
        Intent intent = new Intent(this, ImagePickerActivity.class);
        Config config = new Config.Builder()
                .setTabBackgroundColor(R.color.text_color_bright)
                .setTabSelectionIndicatorColor(R.color.app_primary_color)
                .setCameraButtonColor(R.color.text_color_dark)
                .setSelectionLimit(SECTION_LIMIT_IMAGES)
                .build();
        ImagePickerActivity.setConfig(config);
        startActivityForResult(intent, REQUEST_GET_IMAGES);
    }

    @Override
    public void onComplete(ArrayList<Photo> photos) {
        if (photos == null) {
            showToastErrorMessage("upload image error");
            return;
        }

        for (Photo photo : photos) {
            FlickrPhoto mPhoto = new FlickrPhoto(photo);
            mDataArray.add(0, mPhoto);
        }

        if (mAdapter != null && mAdapter instanceof PhotosAdapter) {
            ((PhotosAdapter) mAdapter).updateDataArray(mDataArray);
        }

        saveCacheList();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_GET_IMAGES) {

            Parcelable[] parcelableUris = data.getParcelableArrayExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);

            if (parcelableUris == null) {
                return;
            }

            Uri[] uris = new Uri[parcelableUris.length];
            System.arraycopy(parcelableUris, 0, uris, 0, parcelableUris.length);
            onProcessedImageFinished(uris);
        }
    }

    //----------------------------------------------------------------------------------------------
    // Data Photo Section
    //----------------------------------------------------------------------------------------------

    protected void getDataFromServer(boolean isSwipeRefresh) {
        if (!isSwipeRefresh) {
            showLoadingDialog();
        }

        Intent intent = new Intent(this, UserPhotoService.class);
        startService(intent);
    }

    public void onEventMainThread(FlickrPhotoEvent event) {
        logDebug("get user photo done");
        dismissLoadingDialog();
        if (event == null)
            return;

        mDataArray = event.getmDataArray();
        if (mDataArray != null) {
            logDebug("get user photo done: " + mDataArray.size());
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mAdapter != null && mAdapter instanceof PhotosAdapter)
                    ((PhotosAdapter) mAdapter).updateDataArray(mDataArray);
                refreshLayout.setRefreshing(false);
                refreshNoDataView();
            }
        });

        saveCacheList();
    }

    protected ArrayList<FlickrPhoto> restoreCacheList() {
        String cacheKey = this.mCacheKey;
        if (cacheKey == null || cacheKey.length() <= 0) {
            cacheKey = this.getClass().getSimpleName();
        }

        return CacheManager.getListCacheData(cacheKey, mListType);
    }

    protected void saveCacheList() {
        String cacheKey = this.mCacheKey;
        if (cacheKey == null || cacheKey.length() <= 0) {
            cacheKey = this.getClass().getSimpleName();
        }

        CacheManager.saveListCacheData(cacheKey, mDataArray);
    }
}
