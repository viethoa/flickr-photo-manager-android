package com.viethoa.siliconstraits.testing.services;

import android.app.IntentService;
import android.content.Intent;

import com.googlecode.flickrjandroid.photos.Photo;
import com.googlecode.flickrjandroid.photos.PhotoList;
import com.viethoa.siliconstraits.testing.MyApplication;
import com.viethoa.siliconstraits.testing.daggers.managers.FlickrManager;
import com.viethoa.siliconstraits.testing.daggers.modules.RequestModule;
import com.viethoa.siliconstraits.testing.models.FlickrPhoto;
import com.viethoa.siliconstraits.testing.models.events.FlickrPhotoEvent;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.ObjectGraph;
import de.greenrobot.event.EventBus;

/**
 * Created by VietHoa on 30/07/15.
 */
public class UserPhotoService extends IntentService {
    private static final String NAME = "UserPhotoService";

    @Inject
    FlickrManager flickrManager;
    @Inject
    EventBus eventBus;

    public UserPhotoService() {
        super(NAME);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MyApplication application = (MyApplication) getApplication();
        if (application != null) {
            ObjectGraph graph = application.extendScope(new RequestModule());
            graph.inject(this);
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        PhotoList photoList = flickrManager.getUserPhotos();
        if (photoList == null) {
            eventBus.getDefault().post(new FlickrPhotoEvent(null));
            return;
        }

        ArrayList<FlickrPhoto> mDataArray = convertDataToMyDataModel(photoList);
        eventBus.getDefault().post(new FlickrPhotoEvent(mDataArray));
    }


    protected ArrayList<FlickrPhoto> convertDataToMyDataModel(PhotoList photos) {
        if (photos == null) {
            return null;
        }

        ArrayList<FlickrPhoto> newDataArray = new ArrayList<>();
        for (Photo photo : photos) {
            FlickrPhoto mPhoto = new FlickrPhoto(photo);
            newDataArray.add(mPhoto);
        }

        return newDataArray;
    }
}
