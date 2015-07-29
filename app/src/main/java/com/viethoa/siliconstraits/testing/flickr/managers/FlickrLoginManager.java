package com.viethoa.siliconstraits.testing.flickr.managers;

import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.people.User;
import com.viethoa.siliconstraits.testing.managers.CacheManager;
import com.viethoa.siliconstraits.testing.utils.StringUtils;

/**
 * Created by VietHoa on 27/07/15.
 */
public class FlickrLoginManager {

    private static final String TAG = "FlickrLogin";

    public static final String CALLBACK_SCHEME = "flickrj-android-sample-oauth"; //$NON-NLS-1$
    public static final String PREFS_NAME = "flickrj-android-sample-pref"; //$NON-NLS-1$
    public static final String KEY_OAUTH_TOKEN = "flickrj-android-oauthToken"; //$NON-NLS-1$
    public static final String KEY_TOKEN_SECRET = "flickrj-android-tokenSecret"; //$NON-NLS-1$
    public static final String KEY_USER_NAME = "flickrj-android-userName"; //$NON-NLS-1$
    public static final String KEY_USER_ID = "flickrj-android-userId"; //$NON-NLS-1$

    public static boolean hasLogin() {
        OAuth oauth = getOAuthToken();
        if (oauth == null || oauth.getUser() == null)
            return false;
        return true;
    }

    public static OAuth getOAuthToken() {
        String oauthTokenString = CacheManager.getStringCacheData(KEY_OAUTH_TOKEN);
        String tokenSecret = CacheManager.getStringCacheData(KEY_TOKEN_SECRET);
        if (StringUtils.isNull(oauthTokenString) && StringUtils.isNull(tokenSecret)) {
            return null;
        }

        OAuth oauth = new OAuth();
        String userName = CacheManager.getStringCacheData(KEY_USER_NAME);
        String userId = CacheManager.getStringCacheData(KEY_USER_ID);
        if (StringUtils.isNotNull(userId)) {
            User user = new User();
            user.setUsername(userName);
            user.setId(userId);
            oauth.setUser(user);
        }

        OAuthToken oauthToken = new OAuthToken();
        oauth.setToken(oauthToken);
        oauthToken.setOauthToken(oauthTokenString);
        oauthToken.setOauthTokenSecret(tokenSecret);
        return oauth;
    }

    public static void saveOAuthToken(String userName, String userId, String token, String tokenSecret) {
        CacheManager.saveStringCacheData(KEY_OAUTH_TOKEN, token);
        CacheManager.saveStringCacheData(KEY_TOKEN_SECRET, tokenSecret);
        CacheManager.saveStringCacheData(KEY_USER_NAME, userName);
        CacheManager.saveStringCacheData(KEY_USER_ID, userId);
    }

    public static void clearOAuthData() {
        CacheManager.saveStringCacheData(KEY_OAUTH_TOKEN, null);
        CacheManager.saveStringCacheData(KEY_TOKEN_SECRET, null);
        CacheManager.saveStringCacheData(KEY_USER_NAME, null);
        CacheManager.saveStringCacheData(KEY_USER_ID, null);
    }

    public static String getUserId() {
        return CacheManager.getStringCacheData(KEY_USER_ID);
    }

}
