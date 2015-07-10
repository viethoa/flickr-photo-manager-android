package com.originally.shape.controllers;

import android.os.Bundle;
import com.originally.shape.R;

import com.originally.shape.controllers.base.MDLBaseActivity;
import com.originally.shape.countroll.modules.SlidingTabsColors.SlidingTabsColorActivity;

public class MainActivity extends MDLBaseActivity {

    @Override
    protected void setLayoutResource() {
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startActivity(SlidingTabsColorActivity.getInstance(this));

        initialiseData();
        initialiseUI();
    }

    //----------------------------------------------------------------------------------------------
    // Setup
    //----------------------------------------------------------------------------------------------

    protected void initialiseData() {

    }

    protected void initialiseUI() {

    }

}
