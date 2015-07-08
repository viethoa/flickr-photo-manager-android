package us.originally.sgparkwhere.controllers;

import android.os.Bundle;

import us.originally.sgparkwhere.R;
import us.originally.sgparkwhere.controllers.base.MDLBaseActivity;

public class MainActivity extends MDLBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialiseData();
        initialiseUI();
    }

    protected void initialiseData() {
    }

    protected void initialiseUI() {

    }

}
