package com.originally.shape.controllers.base;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.google.gson.reflect.TypeToken;
import com.lorem_ipsum.customviews.ExpandedListView;
import com.lorem_ipsum.managers.CacheManager;
import com.lorem_ipsum.requests.MyDataCallback;
import com.originally.shape.R;
import com.originally.shape.adapters.base.MyBaseArrayListAdapter;

import java.lang.reflect.Type;
import java.util.ArrayList;

import retrofit.RetrofitError;

public abstract class BaseListActivity<T> extends MDLBaseActivity {

    //Main listing of shipments
    protected ListView mListView;
    protected View noDataView;
    protected MyBaseArrayListAdapter<T> mListAdapter;
    protected ArrayList mDataArray;
    protected Type mType;
    protected Type mListType;
    protected String mCacheKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initialiseData();
        initialiseUI();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Show loading indicator when there is no data
        if (mDataArray == null || mDataArray.size() <= 0)
            showLoadingDialog();
        loadDataFromServer();
    }

    @Override
    protected void onDestroy() {
        mDataArray = null;
        mListAdapter = null;
        mListView = null;
        noDataView = null;
        mType = null;
        mListType = null;
        mCacheKey = null;
        super.onDestroy();
    }

    //--------------------------------------------------------------------------------
    // Setup
    //--------------------------------------------------------------------------------

    protected void initialiseData() {
        if (mType == null)
            mType = new TypeToken<T>() {
            }.getType();
        if (mListType == null)
            mListType = new TypeToken<ArrayList<T>>() {
            }.getType();
        if (mCacheKey == null || mCacheKey.trim().length() <= 0)
            mCacheKey = this.getClass().getSimpleName();

        this.mDataArray = restoreCacheList();
    }

    protected void initialiseUI() {
        mListView = (ListView) findViewById(R.id.listview);
        if (mListView != null)
            mListView.setAdapter(mListAdapter);
        else
            logError("Make sure the id of listview in XML is +id/listview");

        noDataView = findViewById(R.id.no_data_view);
        if (noDataView != null)
            noDataView.setVisibility(View.GONE);

        // pull to refresh

    }


    //--------------------------------------------------------------------------------
    // Data Helpers
    //--------------------------------------------------------------------------------

    protected ArrayList<T> restoreCacheList() {
        String cacheKey = this.mCacheKey;
        if (cacheKey == null || cacheKey.length() <= 0)
            cacheKey = this.getClass().getSimpleName();

        ArrayList<T> array = CacheManager.getListCacheData(cacheKey, mListType);

        //Debug
        int count = 0;
        if (array != null)
            count = array.size();
        logDebug("restoreCacheList with key: " + cacheKey + " -> " + count + " objects");

        return array;
    }

    protected void saveCacheList() {
        String cacheKey = this.mCacheKey;
        if (cacheKey == null || cacheKey.length() <= 0)
            cacheKey = this.getClass().getSimpleName();

        //Debug
        int count = 0;
        if (mDataArray != null)
            count = mDataArray.size();
        logDebug("saveCacheList with key: " + cacheKey + " -> " + mDataArray.size() + " objects");

        CacheManager.saveListCacheData(cacheKey, mDataArray);
    }

    //--------------------------------------------------------------------------------
    // Server API
    //--------------------------------------------------------------------------------

    protected abstract void loadDataFromServer();

    protected void showNoDataView(boolean show) {
        if (mListView != null)
            mListView.setVisibility(show ? View.GONE : View.VISIBLE);

        if (noDataView != null)
            noDataView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    protected MyDataCallback<ArrayList<T>> mGetServerListDataCallback = new MyDataCallback<ArrayList<T>>() {
        @Override
        public void success(ArrayList<T> list) {
            dismissLoadingDialog();

            // dismiss full to refresh

            //Handle cases when the activity has gone away
            if (mListView == null)
                return;

            //Sanity check
            if (list == null || list.size() <= 0) {
                showNoDataView(true);
                return;
            }

            //Hide no-data view
            showNoDataView(false);

            //Cache it
            mDataArray = list;
            logDebug("received " + list.size() + " data objects from server");
            saveCacheList();

            //Force a refresh of the listView
            if (mListView instanceof ExpandedListView)
                ((ExpandedListView) mListView).invalidateCount();
            if (mListAdapter != null)
                mListAdapter.refreshData(mDataArray);

            updateUIAfterLoadingList();
        }

        @Override
        public void failure(RetrofitError retrofitError) {
//            if (mScreenFullToRefresh != null)
//                mScreenFullToRefresh.onRefreshComplete();
            handleRetrofitError(retrofitError);
        }
    };

    protected void updateUIAfterLoadingList() {
        //Subclass can optionally override this to perform additional setup
    }
}