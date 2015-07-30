package com.viethoa.siliconstraits.testing.flickr.base;

import android.net.Uri;

import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.photos.Photo;
import com.viethoa.siliconstraits.testing.controllers.base.BaseDaggerActivity;
import com.viethoa.siliconstraits.testing.flickr.managers.FlickrLoginManager;
import com.viethoa.siliconstraits.testing.flickr.tasks.UploadPhotoTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by VietHoa on 28/07/15.
 */
public abstract class BaseFlickrMainActivity extends BaseDaggerActivity implements
        UploadPhotoTask.onUploadDone {

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
