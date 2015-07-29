package com.viethoa.siliconstraits.testing.controllers.base;

import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ImageView;

import com.viethoa.siliconstraits.testing.R;
import com.viethoa.siliconstraits.testing.utils.AppUtils;
import com.viethoa.siliconstraits.testing.utils.DialogUtils;
import com.viethoa.siliconstraits.testing.utils.ToastUtils;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {

    protected final String LOG_TAG = this.getClass().getSimpleName();
    private Dialog mLoadingDialog;

    protected abstract void setLayoutResource();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.mLoadingDialog = DialogUtils.createCustomDialogLoading(this);
        initializeNetworkManager();

        setLayoutResource();
        ButterKnife.inject(this);
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
    public void showLoadingDialog() {
        if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
            mLoadingDialog.show();

            View viewContainer = mLoadingDialog.findViewById(R.id.loading_progress_wheel_view_container);
            if (viewContainer == null)
                return;

            Animation rotation = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.loading_animate);
            ImageView imgLoading = (ImageView) viewContainer.findViewById(R.id.iv_loading);
            viewContainer.setVisibility(View.VISIBLE);
            imgLoading.startAnimation(rotation);
            rotation.setDuration(800);
        }
    }

    public boolean isShowLoadingDialog() {
        if (mLoadingDialog == null)
            return false;
        return mLoadingDialog.isShowing();
    }

    /**
     * Dismiss loading dialog if it's showing
     */
    public void dismissLoadingDialog() {
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
}