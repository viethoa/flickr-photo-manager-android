package com.viethoa.siliconstraits.testing.controllers.base;

import android.os.Bundle;

import com.crittercism.app.Crittercism;
import com.lorem_ipsum.activities.BaseActivity;
import com.lorem_ipsum.managers.UserSessionDataManager;
import com.lorem_ipsum.models.User;
import com.lorem_ipsum.requests.MyDataCallback;
import com.lorem_ipsum.utils.StringUtils;
import com.viethoa.siliconstraits.testing.managers.UserApiManager;

import retrofit.RetrofitError;

public class BaseLoginActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected boolean hasAutologin() {
        //We use this as a flag to indicate user has logged in before
        String accessToken = UserSessionDataManager.getCurrentAccessToken();
        return StringUtils.isNotNull(accessToken);
    }

    protected void autoLoginLogic() {
        if (!isShowLoadingDialog()) {
            showLoadingDialog();
        }

        UserApiManager.userConnectDevice(this, mUsernameLoginCallback);
    }

    protected MyDataCallback<User> mUsernameLoginCallback = new MyDataCallback<User>() {

        @Override
        public void success(User user) {
            dismissLoadingDialog();

            //Sanity check
            if (user == null) {
                dismissLoadingDialog();
                return;
            }

            //Critercism meta data
            Crittercism.setUsername("User");
            performAfterLogin();
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            handleRetrofitError(retrofitError);
        }
    };

    protected void performAfterLogin() {

    }
}
