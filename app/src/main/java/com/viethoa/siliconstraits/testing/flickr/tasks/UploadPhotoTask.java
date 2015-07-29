/**
 *
 */
package com.viethoa.siliconstraits.testing.flickr.tasks;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.util.Log;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.photos.Photo;
import com.googlecode.flickrjandroid.uploader.UploadMetaData;
import com.lorem_ipsum.utils.StringUtils;
import com.viethoa.siliconstraits.testing.flickr.managers.FlickrManager;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class UploadPhotoTask extends AsyncTask<OAuth, Void, ArrayList<Photo>> {

    private static final String TAG = "UploadPhotoTask";
    private ProgressDialog mProgressDialog;
    private onUploadDone mCallback;
    private Activity mActivity;
    private List<File> files;

    public UploadPhotoTask(Activity activity, onUploadDone callback, List<File> files) {
        this.mActivity = activity;
        this.files = files;
        this.mCallback = callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog = new ProgressDialog(mActivity, AlertDialog.THEME_HOLO_LIGHT);
        mProgressDialog.setTitle("");
        mProgressDialog.setMessage("Uploading...");
        mProgressDialog.setCanceledOnTouchOutside(true);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dlg) {
                UploadPhotoTask.this.cancel(true);
            }
        });

        mProgressDialog.show();
    }

    @Override
    protected ArrayList<Photo> doInBackground(OAuth... params) {
        OAuth oauth = params[0];
        OAuthToken token = oauth.getToken();

        ArrayList<Photo> resultPhotos = new ArrayList<>();
        FlickrManager flickrHelper = FlickrManager.getInstance();
        Flickr f = flickrHelper.getFlickrAuthed(token.getOauthToken(), token.getOauthTokenSecret());

        try {

            for (File file : files) {
                UploadMetaData uploadMetaData = new UploadMetaData();
                uploadMetaData.setTitle("" + file.getName());

                String photoId = f.getUploader().upload(file.getName(), new FileInputStream(file), uploadMetaData);
                Log.e(TAG, photoId);
                if (StringUtils.isNull(photoId)) {
                    continue;
                }

                Photo photo = flickrHelper.getPhotoById(photoId);
                if (photo != null) {
                    resultPhotos.add(photo);
                }
            }

        } catch (Exception e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }

        return resultPhotos;
    }

    @Override
    protected void onPostExecute(ArrayList<Photo> photos) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        if (mCallback != null) {
            mCallback.onComplete(photos);
        }
    }

    public interface onUploadDone {
        void onComplete(ArrayList<Photo> photos);
    }
}