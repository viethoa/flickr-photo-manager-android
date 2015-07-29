package com.viethoa.siliconstraits.testing.controllers.base;

import android.os.Bundle;

import com.crittercism.app.Crittercism;
import com.lorem_ipsum.activities.BaseActivity;
import com.viethoa.siliconstraits.testing.R;

import butterknife.ButterKnife;

/**
 * Created by VietHoa on 15/04/15.
 */
public abstract class MDLBaseActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeCritercism(getString(R.string.crittercism_key));

        setLayoutResource();
        ButterKnife.inject(this);
    }

    protected abstract void setLayoutResource();

    protected void initializeCritercism(String critercismAppId) {
        if (critercismAppId != null && critercismAppId.length() > 5)
            Crittercism.initialize(getApplicationContext(), critercismAppId);
    }

}
