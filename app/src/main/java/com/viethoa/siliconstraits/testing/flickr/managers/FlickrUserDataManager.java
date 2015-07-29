package com.viethoa.siliconstraits.testing.flickr.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.googlecode.flickrjandroid.people.User;
import com.viethoa.siliconstraits.testing.utils.AppUtils;
import com.viethoa.siliconstraits.testing.utils.StringUtils;

/**
 * Created by VietHoa on 27/07/15.
 */
public class FlickrUserDataManager {

    private static final String LOG_TAG = "FlickrUserDataManager";
    private static final String PREF_KEY_USER_INFO = "flickr_key_user_info";
    private static User cacheUser;

    private static SharedPreferences getSharedPreferences() {
        Context context = AppUtils.getAppContext();
        return context.getSharedPreferences("pref_user_session_data", Context.MODE_PRIVATE);
    }

    /**
     * Clear all user-related cached data before logging out
     */
    public static void clearAllSavedUserData() {
        cacheUser = null;
        getSharedPreferences().edit().clear().commit();
    }

    /**
     * Save user info from cache
     */
    public static void saveCurrentUser(User user) {
        if (user == null)
            return;

        // memory cache
        cacheUser = user;

        String json = (new Gson()).toJson(user);
        if (StringUtils.isNull(json))
            return;

        getSharedPreferences().edit().putString(PREF_KEY_USER_INFO, json).commit();
    }

    /**
     * Get user info from cache
     */
    public static User getCurrentUser() {
        if (cacheUser != null)
            return cacheUser;

        User user;
        String jsonData = getSharedPreferences().getString(PREF_KEY_USER_INFO, "");

        //Convert back to User data model
        try {
            user = (new Gson()).fromJson(jsonData, User.class);
        } catch (Exception e) {
            String message = e.getMessage();
            Log.e(LOG_TAG, "getCurrentUserInfo error: " + message);
            user = null;
        }

        cacheUser = user;
        return cacheUser;
    }

    /**
     * Get user id from cache
     */
    public static String getCurrentUserID() {
        User currentUser = getCurrentUser();
        if (currentUser != null)
            return currentUser.getId();
        return null;
    }
}
