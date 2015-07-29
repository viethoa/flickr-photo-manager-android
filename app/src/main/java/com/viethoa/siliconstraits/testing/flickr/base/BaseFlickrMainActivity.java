package com.viethoa.siliconstraits.testing.flickr.base;

import android.net.Uri;

import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.photos.Photo;
import com.googlecode.flickrjandroid.photos.PhotoList;
import com.viethoa.siliconstraits.testing.controllers.base.MDLBaseActivity;
import com.viethoa.siliconstraits.testing.flickr.managers.FlickrManager;
import com.viethoa.siliconstraits.testing.flickr.managers.FlickrLoginManager;
import com.viethoa.siliconstraits.testing.flickr.tasks.UploadPhotoTask;
import com.viethoa.siliconstraits.testing.models.FlickrPhoto;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by VietHoa on 28/07/15.
 */
public abstract class BaseFlickrMainActivity extends MDLBaseActivity implements
        UploadPhotoTask.onUploadDone {

    /**
     * Get list user's photo on Flickr
     */
    protected void performGetUserPhotos() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                FlickrManager flickrHelper = FlickrManager.getInstance();
                PhotoList photoList = flickrHelper.getPhotoList();
                convertDataToModel(photoList);

            }
        }).start();
    }

    protected void convertDataToModel(PhotoList photos) {
        if (photos == null) {
            performShowData(new ArrayList<FlickrPhoto>());
            return;
        }

        // Convert to my photo model.
        ArrayList<FlickrPhoto> newDataArray = new ArrayList<>();
        for (Photo photo : photos) {
            FlickrPhoto mPhoto = new FlickrPhoto(photo);
            newDataArray.add(mPhoto);
        }

        performShowData(newDataArray);
    }

    protected abstract void performShowData(ArrayList<FlickrPhoto> dataArray);

    /**
     * Upload images into Flickr
     */
    protected void onProcessedImageFinished(Uri[] uris) {
        if (uris == null)
            return;

        OAuth oauth = FlickrLoginManager.getOAuthToken();
        if (oauth == null || oauth.getUser() == null)
            return;

        List<File> files = new ArrayList<>();
        for (Uri imagePath : uris) {
            files.add(new File(imagePath.getPath()));
        }

        UploadPhotoTask taskUpload = new UploadPhotoTask(this, this, files);
        taskUpload.execute(oauth);
    }

    @Override
    public void onComplete(ArrayList<Photo> photos) {
        //Todo override
    }

}
