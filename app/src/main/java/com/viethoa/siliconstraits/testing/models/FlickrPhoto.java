package com.viethoa.siliconstraits.testing.models;

import com.googlecode.flickrjandroid.photos.Photo;

import java.io.Serializable;
import java.util.Random;

public class FlickrPhoto implements Serializable {
	//https://farm{farm-id}.staticflickr.com/{server-id}/{id}_{secret}.jpg

	private static final String IMAGE_URL = "https://farm";
	private static final String STATIC_FLICKR = ".staticflickr.com/";

	private String uid;
	private String name;
	private String url;
	private int likeSum;
	private boolean like;

	public FlickrPhoto(Photo photo) {
		this.uid = photo.getId();
		this.name = photo.getTitle();

		Random rand = new Random();
		this.likeSum = rand.nextInt(1000) + 1;
		this.like = false;

		StringBuilder sbUrl = new StringBuilder(IMAGE_URL);
		sbUrl.append(photo.getFarm());
		sbUrl.append(STATIC_FLICKR);
		sbUrl.append(photo.getServer());
		sbUrl.append("/");
		sbUrl.append(uid);
		sbUrl.append("_");
		sbUrl.append(photo.getSecret());
		sbUrl.append(".");
		sbUrl.append(photo.getOriginalFormat());

		this.url = sbUrl.toString();
	}

	public boolean isLike() {
		return like;
	}

	public void setLike(boolean like) {
		this.like = like;
	}

	public void setLikeSum(int likeCountSum) {
		this.likeSum = likeCountSum;
	}

	public int getLikeSum() {
		return likeSum;
	}

	public String getUrl() {
		return url;
	}
	
	public String getName() {
		return name;
	}
}
