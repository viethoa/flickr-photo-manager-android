package com.viethoa.siliconstraits.testing.flickr.base;

import android.content.Intent;
import android.net.Uri;

import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.people.User;
import com.viethoa.siliconstraits.testing.controllers.base.BaseDaggerActivity;
import com.viethoa.siliconstraits.testing.flickr.managers.FlickrLoginManager;
import com.viethoa.siliconstraits.testing.flickr.tasks.GetOAuthTokenTask;

import java.util.Locale;

/**
 * Created by VietHoa on 28/07/15.
 */
public abstract class BaseFlickrLoginActivity extends BaseDaggerActivity {

    @Override
    protected void onNewIntent(Intent intent) {
        // this is very important, otherwise you would get a null Scheme in the onResume later on.
        setIntent(intent);
    }

    @Override
    public void onResume() {
        super.onResume();

        Intent intent = getIntent();
        String scheme = intent.getScheme();
        if (!FlickrLoginManager.CALLBACK_SCHEME.equals(scheme))
            return;

        showLoadingDialog();
        onGetOAuthToken(intent);
    }

    //----------------------------------------------------------------------------------------------
    // Event
    //----------------------------------------------------------------------------------------------

    protected abstract void performAfterLogin();

    protected void onClearOAuthentication() {
        FlickrLoginManager.saveOAuthToken("", "", "", "");
        FlickrLoginManager.clearOAuthData();
    }

    protected void onGetOAuthToken(Intent intent) {
        Uri uri = intent.getData();
        String query = uri.getQuery();
        String[] data = query.split("&");
        if (data == null || data.length != 2) {
            dismissLoadingDialog();
            showToastErrorMessage("Authorization failed");
            return;
        }

        String oauthToken = data[0].substring(data[0].indexOf("=") + 1);
        String oauthVerifier = data[1].substring(data[1].indexOf("=") + 1);
        OAuth oauth = FlickrLoginManager.getOAuthToken();
        if (oauth == null || oauth.getToken() == null || oauth.getToken().getOauthTokenSecret() == null) {
            dismissLoadingDialog();
            showToastErrorMessage("Authorization failed");
            return;
        }

        GetOAuthTokenTask task = new GetOAuthTokenTask(this);
        task.execute(oauthToken, oauth.getToken().getOauthTokenSecret(), oauthVerifier);
    }

    public void onOAuthDone(OAuth result) {
        if (result == null) {
            dismissLoadingDialog();
            showToastErrorMessage("Authorization failed");
            return;
        }

        User user = result.getUser();
        if (user == null || user.getId() == null) {
            dismissLoadingDialog();
            showToastErrorMessage("Authorization failed");
            return;
        }
        OAuthToken token = result.getToken();
        if (token == null || token.getOauthToken() == null || token.getOauthTokenSecret() == null) {
            dismissLoadingDialog();
            showToastErrorMessage("Authorization failed");
            return;
        }

        //Debug
        String message = String.format(Locale.US, "Authorization Succeed: user=%s, userId=%s, oauthToken=%s, tokenSecret=%s",
                user.getUsername(), user.getId(), token.getOauthToken(), token.getOauthTokenSecret());
        logDebug(message);

        FlickrLoginManager.saveOAuthToken(user.getUsername(), user.getId(), token.getOauthToken(), token.getOauthTokenSecret());
        dismissLoadingDialog();
        performAfterLogin();
    }
}
