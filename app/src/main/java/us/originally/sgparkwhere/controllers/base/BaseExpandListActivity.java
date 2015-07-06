package us.originally.sgparkwhere.controllers.base;

import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;

import com.google.gson.reflect.TypeToken;
import com.lorem_ipsum.managers.CacheManager;
import com.lorem_ipsum.requests.MyDataCallback;

import java.lang.reflect.Type;
import java.util.ArrayList;

import retrofit.RetrofitError;
import us.originally.sgparkwhere.R;
import us.originally.sgparkwhere.adapters.MyBaseExpandListAdapter;
import us.originally.sgparkwhere.models.ExpandListModels.Parent;

/**
 * Created by VietHoa on 24/04/15.
 */
public abstract class BaseExpandListActivity<T extends Parent> extends MDLBaseActivity {


    protected ExpandableListView mExpandListView;
    protected MyBaseExpandListAdapter<T> mExpandListAdapter;
    protected View noDataView;
    protected ArrayList<T> mDataArray;
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
    public void onResume() {
        super.onResume();

        //Show loading indicator when there is no data
        if (mDataArray == null || mDataArray.size() <= 0)
            showLoadingDialog();
        loadDataFromServer();
    }

    @Override
    public void onDestroy() {
        mDataArray = null;
        mExpandListAdapter = null;
        mType = null;
        mListType = null;
        mCacheKey = null;
        mExpandListView = null;
        noDataView = null;
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

        this.mDataArray = restoreCacheList();
    }

    protected void initialiseUI() {
        noDataView = findViewById(R.id.no_data_view);
        if (noDataView != null)
            noDataView.setVisibility(View.GONE);

        mExpandListView = (ExpandableListView) findViewById(R.id.expand_list_view);
        if (mExpandListView == null) {
            logError("Make sure the id of listview in XML is +id/listview");
            return;
        }

        mExpandListView.setAdapter(mExpandListAdapter);
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
        logDebug("saveCacheList with key: " + cacheKey + " -> " + count + " objects");

        CacheManager.saveListCacheData(cacheKey, mDataArray);
    }

    //--------------------------------------------------------------------------------
    // Server API
    //--------------------------------------------------------------------------------

    protected abstract void loadDataFromServer();

    protected void showNoDataView(boolean show) {
        if (mExpandListView != null)
            mExpandListView.setVisibility(show ? View.GONE : View.VISIBLE);

        if (noDataView != null)
            noDataView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    protected MyDataCallback<ArrayList<T>> mGetServerListDataCallback = new MyDataCallback<ArrayList<T>>() {
        @Override
        public void success(ArrayList<T> list) {
            dismissLoadingDialog();

            //Handle cases when the activity has gone away
            if (mExpandListView == null)
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

            if (mExpandListAdapter != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mExpandListAdapter.refreshDataChange(mDataArray);
                        mExpandListView.invalidateViews();
                    }
                });
            }

            updateUIAfterLoadingList();
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            handleRetrofitError(retrofitError);
        }
    };

    protected void updateUIAfterLoadingList() {
        //Subclass can optionally override this to perform additional setup
    }
}
