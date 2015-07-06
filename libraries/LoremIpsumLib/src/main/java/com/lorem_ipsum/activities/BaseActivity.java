package com.lorem_ipsum.activities;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.androidquery.AQuery;
import com.crittercism.app.Crittercism;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.Primitives;
import com.lorem_ipsum.R;
import com.lorem_ipsum.requests.MyDataCallback;
import com.lorem_ipsum.utils.AnimationUtils;
import com.lorem_ipsum.utils.CustomDateParser;
import com.lorem_ipsum.utils.DialogUtils;
import com.lorem_ipsum.utils.RetrofitUtils;
import com.lorem_ipsum.utils.ToastUtils;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import retrofit.RetrofitError;

/**
 * @author Originally.US
 */
public class BaseActivity extends AppCompatActivity {

    protected final String LOG_TAG = this.getClass().getSimpleName();

    protected ActionBar mActionBar;
    protected AQuery mAQuery = new AQuery(this);

    private Dialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.mLoadingDialog = DialogUtils.createCustomDialogLoading(this);

        this.mActionBar = getActionBar();
        this.mAQuery = new AQuery(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {

        dismissLoadingDialog();
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // This code allow child Fragment to control the hardware Back button behavior
    /*
    @Override
    public void onBackPressed() {
        final BaseFragment fragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(LOG_TAG);

        //Custom behavior from the Fragment implementation
        Boolean canGoBack = true;
        if (fragment != null)
            canGoBack = fragment.allowBackPressed();

        if (canGoBack)
            super.onBackPressed();
    }
    */

    protected void initializeCritercism(String critercismAppId) {
        if (critercismAppId != null && critercismAppId.length() > 5)
            Crittercism.initialize(getApplicationContext(), critercismAppId);
    }


    //----------------------------------------------------------------------------------------------------------
    // Action Bar
    //----------------------------------------------------------------------------------------------------------

    protected void setupCustomActionBar() {
        ActionBar actionBar = getActionBar();
        if (actionBar == null)
            return;

        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
    }

    protected void setupCustomActionBar(String title, int bg_color_res_id, int text_color_res_id) {
        setupCustomActionBar(title, bg_color_res_id, text_color_res_id, true);
    }

    protected void setupCustomActionBar(String title, int bg_color_res_id, int text_color_res_id, boolean showHome) {
        ActionBar actionBar = getActionBar();
        if (actionBar == null)
            return;

        actionBar.setDisplayHomeAsUpEnabled(showHome);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(bg_color_res_id)));
        actionBar.setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));

        //Awesome way to set text color, works with all flavors of ActionBar
        int actionBarTitleColor = getResources().getColor(text_color_res_id);
        String htmlColor = String.format(Locale.US, "#%06X", (0xFFFFFF & Color.argb(0, Color.red(actionBarTitleColor), Color.green(actionBarTitleColor), Color.blue(actionBarTitleColor))));
        actionBar.setTitle(Html.fromHtml("<font color=\"" + htmlColor + "\">" + title + "</font>"));
    }

    protected void hideHomeButtonInActionBar() {
        ActionBar actionBar = getActionBar();
        if (actionBar == null)
            return;

        actionBar.setHomeButtonEnabled(false);          // disable the button
        actionBar.setDisplayHomeAsUpEnabled(false);     // remove the left caret
        actionBar.setDisplayShowHomeEnabled(false);     // remove the icon
    }


    //----------------------------------------------------------------------------------------------------------
    // Activity life cycle helpers
    //----------------------------------------------------------------------------------------------------------

    //Override finish animation
    @Override
    public void finish() {
        super.finish();
//        if (mIsFinishWithAnimation)
//            AnimationUtils.slideOutFromLeftZoom(this);
    }

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

    /**
     * hide keyboard
     */
    protected void dismissKeyboard() {
        if (this.getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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
            AnimationUtils.AnimationWheelForDialog(mLoadingDialog.findViewById(R.id.loading_progress_wheel_view_container));
        }
    }

    protected boolean isShowingLoadingDialog() {
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
    // Show/Hide view within fade in/out animation
    //----------------------------------------------------------------------------------------------------------

    protected void showView(View view) {
        showView(view, 200);
    }

    protected void hideView(View view) {
        hideView(view, 200);
    }

    protected void showView(View view, int duration) {
        AnimationUtils.fadeInViewWithDuration(view, duration, true, 0);
    }

    protected void hideView(View view, int duration) {
        AnimationUtils.fadeOutViewWithDuration(view, duration, true, 0);
    }


    //----------------------------------------------------------------------------------------------------------
    // Passing data between activities
    //----------------------------------------------------------------------------------------------------------

    protected Intent getIntentWithObjectAndKey(Object obj, String key) {
        return getIntentWithObjectAndKey(null, obj, key);
    }

    protected Intent getIntentWithObjectAndKey(Class<?> activityClass, Object obj, String key) {
        String modelString = getSerializedModelString(obj);

        Intent intent;
        if (activityClass == null)
            intent = new Intent();
        else
            intent = new Intent(this, activityClass);
        intent.putExtra(key, modelString);
        return intent;
    }

    protected String getSerializedModelString(Object obj) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new CustomDateParser());
        Gson gson = gsonBuilder.create();
        return gson.toJson(obj);
    }

    protected <T> T getSerializedModelFromIntent(String key, Class<T> classOfT) {
        return getSerializedModelFromIntent(getIntent(), key, classOfT);
    }

    protected <T> T getSerializedModelFromIntent(Intent data, String key, Class<T> classOfT) {

        if (data == null)
            data = getIntent();
        if (data == null)
            return null;

        String modelString = data.getStringExtra(key);
        if (modelString == null || modelString.length() <= 0)
            return null;

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new CustomDateParser());
        Gson gson = gsonBuilder.create();

        Object object = gson.fromJson(modelString, (Type) classOfT);
        return Primitives.wrap(classOfT).cast(object);
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