package com.viethoa.siliconstraits.testing.daggers.managers;

import android.content.Context;
import android.net.Uri;

import com.googlecode.flickrjandroid.photos.PhotoList;
import com.viethoa.siliconstraits.testing.models.FlickrPhoto;

import java.util.ArrayList;

/**
 * Created by VietHoa on 30/07/15.
 */
public interface FlickrManager {

    void getAuthorization(Context context);

    PhotoList getUserPhotos();

    ArrayList<FlickrPhoto> uploadPhotos(Uri[] uris);
}
