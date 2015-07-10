package com.lorem_ipsum.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;

import com.androidquery.AQuery;
import com.crittercism.app.Crittercism;
import com.lorem_ipsum.R;
import com.lorem_ipsum.requests.MyDataCallback;
import com.lorem_ipsum.utils.AnimationUtils;
import com.lorem_ipsum.utils.AppUtils;
import com.lorem_ipsum.utils.DialogUtils;
import com.lorem_ipsum.utils.RetrofitUtils;
import com.lorem_ipsum.utils.ToastUtils;

import java.util.HashMap;

import retrofit.RetrofitError;

/**
 * @author Originally.US
 */
public class BaseActivity extends AppCompatActivity {

    protected final String LOG_TAG = this.getClass().getSimpleName();
    protected AQuery mAQuery = new AQuery(this);
    private Dialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.mLoadingDialog = DialogUtils.createCustomDialogLoading(this);
        this.mAQuery = new AQuery(this);

        initializeNetworkManager();
    }

    @Override
    protected void onPause() {
        dismissLoadingDialog();
        super.onPause();
    }

    //----------------------------------------------------------------------------------------------------------
    // Network broadcast receiver
    //----------------------------------------------------------------------------------------------------------

    protected void initializeNetworkManager() {
        AppUtils.networkListener = new AppUtils.NetworkListener() {
            @Override
            public void networkAvailable() {
                onNetworkAvailable();
            }

            @Override
            public void networkUnavailable() {
                onNetworkUnavailable();
            }
        };
    }

    public void onNetworkAvailable() {

    }

    public void onNetworkUnavailable() {
        showToastErrorMessage("please check your internet connection");
    }

    //----------------------------------------------------------------------------------------------------------
    // Animation start activity
    //----------------------------------------------------------------------------------------------------------

    protected void startActivityWithAnimation(Intent intent) {
        startActivity(intent);
        AnimationUtils.slideInFromRightZoom(this);
    }

    protected void startActivityForResultWithAnimation(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
        AnimationUtils.slideInFromRightZoom(this);
    }

    //----------------------------------------------------------------------------------------------------------
    // UI Helpers
    //----------------------------------------------------------------------------------------------------------

    /**
     * Setting font to EditTexts
     */
    protected void setTypeFace(EditText... views) {
        for (EditText view : views) {
            view.setTypeface(Typeface.SANS_SERIF);
        }
    }

    //----------------------------------------------------------------------------------------------------------
    // Loading UI Helpers
    //----------------------------------------------------------------------------------------------------------

    /**
     * show dialog loading
     */
    protected void showLoadingDialog() {
        if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
            mLoadingDialog.show();
            AnimationUtils.AnimationWheelForDialog(this, mLoadingDialog.findViewById(R.id.loading_progress_wheel_view_container));
        }
    }

    protected boolean isShowLoadingDialog() {
        if (mLoadingDialog == null)
            return false;
        return mLoadingDialog.isShowing();
    }

    /**
     * Dismiss loading dialog if it's showing
     */
    protected void dismissLoadingDialog() {
        if (mLoadingDialog != null) {
            try {
                mLoadingDialog.dismiss();
            } catch (Exception e) {
                // dismiss dialog after destroy activity
            }
        }
    }

    /**
     * Convenient function to replace a fragment
     */
    protected void replaceFragmentHelper(final Fragment fg, final int containerResId, final boolean animated) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction tx = fm.beginTransaction();
        if (animated)
            tx.setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out);
        tx.replace(containerResId, fg);
        tx.addToBackStack(LOG_TAG);
        tx.commit();
        fm.executePendingTransactions();
    }


    //----------------------------------------------------------------------------------------------------------
    // Error & Logging Helpers
    //----------------------------------------------------------------------------------------------------------

    /**
     * shortcuts to logging methods so that we can disable everything all together
     * or redirect logging to a file or to server
     */
    protected void logError(String message) {
        Log.e(LOG_TAG, message);
    }

    protected void logDebug(String message) {
        Log.d(LOG_TAG, message);
    }

    protected void logWarning(String message) {
        Log.w(LOG_TAG, message);
    }

    protected void logInfo(String message) {
        Log.i(LOG_TAG, message);
    }

    /**
     * show a toast with message and log out to console as well
     */
    protected void showToastMessage(String message) {
        ToastUtils.showToastMessageWithSuperToast(message);
    }

    protected void showToastMessageDelay(String message, int timeDelay) {
        ToastUtils.showToastMessageWithSuperToastDelay(message, timeDelay);
    }

    protected void showToastErrorMessage(String message) {
        ToastUtils.showErrorMessageWithSuperToast(message, LOG_TAG);
    }

    /**
     * Standard function to handle error from Retrofit
     */
    protected void handleRetrofitError(final RetrofitError retrofitError) {
        dismissLoadingDialog();
        RetrofitUtils.handleRetrofitError(this, retrofitError, null, true);
    }

    /**
     * Standard function to handle error from Retrofit
     */
    protected void handleRetrofitErrorNoToast(final RetrofitError retrofitError) {
        dismissLoadingDialog();
        RetrofitUtils.handleRetrofitError(this, retrofitError, null, false);
    }


    //----------------------------------------------------------------------------------------------------------
    // Other utilities
    //----------------------------------------------------------------------------------------------------------

    protected void openUrl(String url, boolean useExternalBrowser) {
        if (url == null)
            return;

        //Use external browser
        if (useExternalBrowser) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
            return;
        }

        //Our internal webview
        Intent intent = new Intent(this, BaseWebviewActivity.class);
        intent.putExtra("url", url);
        this.startActivity(intent);
    }

    //----------------------------------------------------------------------------------------------------------
    // Server API
    //----------------------------------------------------------------------------------------------------------

    protected MyDataCallback<HashMap<String, Object>> mStandardApiResponse = new MyDataCallback<HashMap<String, Object>>() {
        @Override
        public void success(HashMap<String, Object> object) {
            dismissLoadingDialog();

            if (object == null)
                return;
            Object message = object.get("message");
            if (message == null || message instanceof String == false)
                return;

            if (((String) message).length() > 0)
                showToastMessage((String) message);
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            handleRetrofitError(retrofitError);
        }
    };
}