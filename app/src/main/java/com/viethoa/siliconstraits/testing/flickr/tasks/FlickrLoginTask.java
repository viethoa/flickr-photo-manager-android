package com.viethoa.siliconstraits.testing.flickr.tasks;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.auth.Permission;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.viethoa.siliconstraits.testing.flickr.managers.FlickrLoginManager;
import com.viethoa.siliconstraits.testing.flickr.managers.FlickrManager;

import java.net.URL;

/**
 * Created by VietHoa on 27/07/15.
 */
public class FlickrLoginTask extends AsyncTask<Void, Integer, String> {

    private static final Uri OAUTH_CALLBACK_URI = Uri.parse(FlickrLoginManager.CALLBACK_SCHEME + "://oauth");
    private ProgressDialog mProgressDialog;
    private Context mContext;

    public FlickrLoginTask(Context context) {
        super();
        this.mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog = new ProgressDialog(mContext, AlertDialog.THEME_HOLO_LIGHT);
        mProgressDialog.setTitle("");
        mProgressDialog.setMessage("Generating the authorization request...");
        mProgressDialog.setCanceledOnTouchOutside(true);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dlg) {
                FlickrLoginTask.this.cancel(true);
            }
        });

        mProgressDialog.show();
    }

    @Override
    protected String doInBackground(Void... params) {
        try {

            Flickr f = FlickrManager.getInstance().getFlickr();
            if (f == null) {
                return null;
            }

            OAuthToken oauthToken = f.getOAuthInterface().getRequestToken(OAUTH_CALLBACK_URI.toString());
            saveCurrentToken(oauthToken.getOauthTokenSecret());
            URL oauthUrl = f.getOAuthInterface().buildAuthenticationUrl(Permission.WRITE, oauthToken);
            return oauthUrl.toString();

        } catch (Exception e) {
            Log.e("Error to oauth", e.getMessage()); //$NON-NLS-1$
            return "error:" + e.getMessage(); //$NON-NLS-1$
        }
    }

    private void saveCurrentToken(String tokenSecret) {
        FlickrLoginManager.saveOAuthToken(null, null, null, tokenSecret);
    }

    @Override
    protected void onPostExecute(String result) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }

        if (result == null || result.startsWith("error")) {
            Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
            return;
        }

        mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(result)));
    }

}
