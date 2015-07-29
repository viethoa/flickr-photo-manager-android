package com.viethoa.siliconstraits.testing.controllers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.viethoa.siliconstraits.testing.R;
import com.viethoa.siliconstraits.testing.flickr.base.BaseFlickrLoginActivity;
import com.viethoa.siliconstraits.testing.flickr.managers.FlickrLoginManager;
import com.viethoa.siliconstraits.testing.flickr.tasks.FlickrLoginTask;

import butterknife.InjectView;
import butterknife.OnClick;

public class LoginActivity extends BaseFlickrLoginActivity {

    private static final String EXTRACT = "extract-logout";

    @InjectView(R.id.btn_flick_login)
    View btnFlickrLogin;

    public static Intent getInstance(Context context, boolean logout) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(EXTRACT, logout);
        return intent;
    }

    @Override
    protected void setLayoutResource() {
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        if (bundle == null)
            return;

        //Check logout ==> clear cache
        boolean isLogout = bundle.getBoolean(EXTRACT);
        if (isLogout) {
            onClearOAuthentication();
        }
    }

    //----------------------------------------------------------------------------------------------
    // Event
    //----------------------------------------------------------------------------------------------

    @OnClick(R.id.btn_flick_login)
    protected void onBtnFlickrLoginClicked() {
        if (FlickrLoginManager.hasLogin()) {
            performAfterLogin();
            return;
        }

        FlickrLoginTask task = new FlickrLoginTask(LoginActivity.this);
        task.execute();
    }

    @Override
    protected void performAfterLogin() {
        Intent intent = MainActivity.getInstance(this);
        startActivity(intent);
        finish();
    }
}
