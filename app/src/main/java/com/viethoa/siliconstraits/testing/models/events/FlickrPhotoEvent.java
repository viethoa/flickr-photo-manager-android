package com.viethoa.siliconstraits.testing.models.events;

import com.viethoa.siliconstraits.testing.models.FlickrPhoto;

import java.util.ArrayList;

/**
 * Created by VietHoa on 30/07/15.
 */
public class FlickrPhotoEvent {

    private ArrayList<FlickrPhoto> mDataArray;

    public FlickrPhotoEvent(ArrayList<FlickrPhoto> data) {
        this.mDataArray = data;
    }

    public ArrayList<FlickrPhoto> getmDataArray() {
        return mDataArray;
    }
}
