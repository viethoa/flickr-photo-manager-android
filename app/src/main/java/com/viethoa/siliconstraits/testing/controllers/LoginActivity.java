package com.viethoa.siliconstraits.testing.controllers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.viethoa.siliconstraits.testing.R;
import com.viethoa.siliconstraits.testing.daggers.managers.FlickrManager;
import com.viethoa.siliconstraits.testing.flickr.base.BaseFlickrLoginActivity;
import com.viethoa.siliconstraits.testing.flickr.managers.FlickrLoginManager;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

public class LoginActivity extends BaseFlickrLoginActivity {

    private static final String EXTRACT = "extract-logout";

    @Inject
    FlickrManager flickrManager;

    @InjectView(R.id.btn_flick_login)
    View btnFlickrLogin;

    public static Intent newInstance(Context context, boolean logout) {
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

    //----------------------------------------------------------------------------O------------------
    // Event
    //----------------------------------------------------------------------------------------------

    @OnClick(R.id.btn_flick_login)
    protected void onBtnFlickrLoginClicked() {
        if (FlickrLoginManager.hasLogin()) {
            performAfterLogin();
            return;
        }

        flickrManager.getAuthorization(this);
    }

    @Override
    protected void performAfterLogin() {
        Intent intent = MainActivity.newInstance(this);
        startActivity(intent);
        finish();
    }
}
