package com.viethoa.siliconstraits.testing.daggers.managers.impl;

import android.content.Context;
import android.net.Uri;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.REST;
import com.googlecode.flickrjandroid.RequestContext;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.people.PeopleInterface;
import com.googlecode.flickrjandroid.photos.Photo;
import com.googlecode.flickrjandroid.photos.PhotoList;
import com.googlecode.flickrjandroid.photos.PhotosInterface;
import com.viethoa.siliconstraits.testing.daggers.managers.FlickrManager;
import com.viethoa.siliconstraits.testing.flickr.managers.FlickrLoginManager;
import com.viethoa.siliconstraits.testing.flickr.tasks.FlickrLoginTask;
import com.viethoa.siliconstraits.testing.managers.CacheManager;
import com.viethoa.siliconstraits.testing.models.FlickrPhoto;
import com.viethoa.siliconstraits.testing.utils.StringUtils;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by VietHoa on 30/07/15.
 */
public class FlickrManagerImpl implements FlickrManager {

    public static final String API_KEY = "28f379725ea3ee4184223cf3137ca2c6"; //$NON-NLS-1$
    public static final String API_SEC = "69a5ddd13a112d34"; //$NON-NLS-1$
    private Context mContext;

    public FlickrManagerImpl(Context applicationContext) {
        this.mContext = applicationContext;
    }

    @Override
    public void getAuthorization(Context context) {
        FlickrLoginTask task = new FlickrLoginTask(context);
        task.execute();
    }

    @Override
    public PhotoList getUserPhotos() {
        final PeopleInterface peopleInterface = getPeopleInterface();
        if (peopleInterface == null) {
            return null;
        }

        final String userId = CacheManager.getStringCacheData(FlickrLoginManager.KEY_USER_ID);
        if (StringUtils.isNull(userId)) {
            return null;
        }

        Set<String> extras = new HashSet();
        extras.add("url_o");                //get the link of origin image.
        extras.add("original_format");      //get image with origin format.
        PhotoList photoList = null;
        try {

            photoList = peopleInterface.getPhotos(userId, extras, 0, 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FlickrException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return photoList;
    }

    @Override
    public ArrayList<FlickrPhoto> uploadPhotos(Uri[] uris) {
        OAuth oauth = FlickrLoginManager.getOAuthToken();
        if (oauth == null || oauth.getUser() == null)
            return null;

        List<File> files = new ArrayList<>();
        for (Uri imagePath : uris) {
            files.add(new File(imagePath.getPath()));
        }

        //UploadPhotoTask taskUpload = new UploadPhotoTask(this, this, files);
        //taskUpload.execute(oauth);

        return null;
    }

    //----------------------------------------------------------------------------------------------
    // Private function
    //----------------------------------------------------------------------------------------------

    public Flickr getFlickr() {
        try {
            Flickr f = new Flickr(API_KEY, API_SEC, new REST());
            return f;
        } catch (ParserConfigurationException e) {
            return null;
        }
    }

    public Flickr getFlickrAuthed(String token, String secret) {
        Flickr f = getFlickr();
        RequestContext requestContext = RequestContext.getRequestContext();
        OAuth auth = new OAuth();
        auth.setToken(new OAuthToken(token, secret));
        requestContext.setOAuth(auth);
        return f;
    }

    public PeopleInterface getPeopleInterface() {
        String token = CacheManager.getStringCacheData(FlickrLoginManager.KEY_OAUTH_TOKEN);
        if (StringUtils.isNull(token))
            return null;

        String secret = CacheManager.getStringCacheData(FlickrLoginManager.KEY_TOKEN_SECRET);
        if (StringUtils.isNull(token))
            return null;

        Flickr f = getFlickrAuthed(token, secret);
        if (f != null) {
            return f.getPeopleInterface();
        } else {
            return null;
        }
    }

    public PhotoList getPhotoList() {
        PeopleInterface peopleInterface = getPeopleInterface();
        if (peopleInterface == null)
            return null;

        String userId = CacheManager.getStringCacheData(FlickrLoginManager.KEY_USER_ID);
        if (StringUtils.isNull(userId))
            return null;

        // A set of extra info we want Flickr to give back. Go to the API page to see the other size options available.
        Set<String> extras = new HashSet();
        extras.add("url_o");
        extras.add("original_format");
        PhotoList photoList = null;
        try {

            photoList = peopleInterface.getPhotos(userId, extras, 0, 0);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (FlickrException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return photoList;
    }

    public PhotosInterface getPhotosInterface() {
        String token = CacheManager.getStringCacheData(FlickrLoginManager.KEY_OAUTH_TOKEN);
        if (StringUtils.isNull(token))
            return null;

        String secret = CacheManager.getStringCacheData(FlickrLoginManager.KEY_TOKEN_SECRET);
        if (StringUtils.isNull(token))
            return null;

        Flickr f = getFlickrAuthed(token, secret);
        if (f != null) {
            return f.getPhotosInterface();
        } else {
            return null;
        }
    }

    public Photo getPhotoById(String photoId) {
        PhotosInterface photosInterface = getPhotosInterface();
        if (photosInterface == null)
            return null;

        Photo photo = null;
        try {
            photo = photosInterface.getPhoto(photoId);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FlickrException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return photo;
    }
}
