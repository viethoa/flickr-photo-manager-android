package us.originally.sgparkwhere.managers;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.lorem_ipsum.managers.CredentialManager;
import com.lorem_ipsum.managers.UserSessionDataManager;
import com.lorem_ipsum.models.User;
import com.lorem_ipsum.requests.BaseRequest;
import com.lorem_ipsum.requests.MyDataCallback;
import com.lorem_ipsum.utils.DeviceUtils;

import java.lang.reflect.Type;
import java.util.HashMap;

import retrofit.RetrofitError;

public class UserApiManager extends BaseRequest {

    private static final String LOG_TAG = UserApiManager.class.getSimpleName();

    public static void userConnectDevice(final Context context, final MyDataCallback<User> myCallback) {

        //Parameters
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("os_type", "android");
        params.put("uuid", DeviceUtils.getDeviceUUID(context));

        String url = "users/connect_device";
        POST(context, url, params, User.class, null, new MyDataCallback<User>() {
            @Override
            public void success(User o) {
                CredentialManager.clearAllSavedUserData();
                CredentialManager.saveToken(o.secret);
                UserSessionDataManager.saveCurrentUser(o);

                //Callback to UI layer
                if (myCallback != null)
                    myCallback.success(o);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                myCallback.failure(retrofitError);
            }
        });
    }

    public static void logout(final Context context, final MyDataCallback<HashMap<String, Object>> myCallback) {
        String url = "users/logout";

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("os_type", "android");
        params.put("uuid", DeviceUtils.getDeviceUUID(context));

        Type type = new TypeToken<HashMap<String, Object>>() {
        }.getType();
        POST(context, url, params, type, null, myCallback);
    }

}
