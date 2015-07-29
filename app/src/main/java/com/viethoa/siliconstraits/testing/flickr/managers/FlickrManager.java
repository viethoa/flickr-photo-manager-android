package com.viethoa.siliconstraits.testing.flickr.managers;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.FlickrException;
import com.googlecode.flickrjandroid.REST;
import com.googlecode.flickrjandroid.RequestContext;
import com.googlecode.flickrjandroid.interestingness.InterestingnessInterface;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.people.PeopleInterface;
import com.googlecode.flickrjandroid.photos.Photo;
import com.googlecode.flickrjandroid.photos.PhotoList;
import com.googlecode.flickrjandroid.photos.PhotosInterface;
import com.lorem_ipsum.managers.CacheManager;
import com.lorem_ipsum.utils.StringUtils;

import org.json.JSONException;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

public final class FlickrManager {

    private static FlickrManager instance = null;
    public static final String API_KEY = "28f379725ea3ee4184223cf3137ca2c6"; //$NON-NLS-1$
    public static final String API_SEC = "69a5ddd13a112d34"; //$NON-NLS-1$

    private FlickrManager() {

    }

    public static FlickrManager getInstance() {
        if (instance == null) {
            instance = new FlickrManager();
        }
        return instance;
    }

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

    public InterestingnessInterface getInterestingInterface() {
        Flickr f = getFlickr();
        if (f != null) {
            return f.getInterestingnessInterface();
        } else {
            return null;
        }
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
