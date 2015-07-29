package com.viethoa.siliconstraits.testing.images.loader;

import android.support.v4.app.FragmentManager;
import android.widget.ImageView;

import com.viethoa.siliconstraits.testing.MyApplication;

/**
 * Created by VietHoa on 29/07/15.
 */
public class ImageLoader {

    private static final int THUMB_TARGET_HEIGHT = 500;
    private static final String DiskCacheDirectoryName = "iamges";

    private static ImageLoader instance = null;
    private static ImageFetcher mImageFetcher;

    public static ImageLoader getInstance(FragmentManager manager) {
        configImageFetcher(manager);

        if (instance == null) {
            instance = new ImageLoader();
        }
        return instance;
    }

    private static void configImageFetcher(FragmentManager manager) {
        ImageCache.ImageCacheParams cacheParams =
                new ImageCache.ImageCacheParams(MyApplication.getAppContext(), DiskCacheDirectoryName);
        // Set memory cache to 25% of app memory
        cacheParams.setMemCacheSizePercent(0.25f);

        mImageFetcher = new ImageFetcher(MyApplication.getAppContext(), THUMB_TARGET_HEIGHT);
        //mImageFetcher.setLoadingImage(R.drawable.default_image_holder);
        mImageFetcher.addImageCache(manager, cacheParams);
    }

    public void loadImage(String imageUrl, ImageView image, ImageWorker.OnToDoAfterHasDoneListener callback) {
        mImageFetcher.loadImage(MyApplication.getAppContext(), imageUrl, image, callback);
    }
}

