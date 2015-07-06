package us.originally.sgparkwhere.controllers.base;

import android.os.Bundle;

import com.crittercism.app.Crittercism;
import com.lorem_ipsum.activities.BaseActivity;
import com.lorem_ipsum.utils.AppUtils;

import butterknife.ButterKnife;
import us.originally.sgparkwhere.R;

/**
 * Created by VietHoa on 15/04/15.
 */
public class MDLBaseActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crittercism.initialize(getApplicationContext(), getString(R.string.crittercism_key));
        ButterKnife.inject(this);

        initializeNetworkManager();
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
}
