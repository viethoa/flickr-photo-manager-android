/**
 *
 */
package com.viethoa.siliconstraits.testing.flickr.tasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthInterface;
import com.viethoa.siliconstraits.testing.flickr.base.BaseFlickrLoginActivity;
import com.viethoa.siliconstraits.testing.flickr.FlickrManager;

/**
 * @author Toby Yu(yuyang226@gmail.com)
 */
public class GetOAuthTokenTask extends AsyncTask<String, Integer, OAuth> {

    private static final String LOG_TAG = "GetOAuthTokenTask";
    private Activity mActivity;

    public GetOAuthTokenTask(Activity activity) {
        this.mActivity = activity;
    }

    @Override
    protected OAuth doInBackground(String... params) {
        String oauthToken = params[0];
        String oauthTokenSecret = params[1];
        String verifier = params[2];

        Flickr f = FlickrManager.getInstance().getFlickr();
        OAuthInterface oauthApi = f.getOAuthInterface();
        try {
            return oauthApi.getAccessToken(oauthToken, oauthTokenSecret, verifier);
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
            return null;
        }
    }

    @Override
    protected void onPostExecute(OAuth result) {
        if (mActivity == null || !(mActivity instanceof BaseFlickrLoginActivity))
            return;

        ((BaseFlickrLoginActivity) mActivity).onOAuthDone(result);
    }

}
