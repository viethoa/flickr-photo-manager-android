package com.viethoa.siliconstraits.testing.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.viethoa.siliconstraits.testing.R;
import com.viethoa.siliconstraits.testing.images.loader.ImageLoader;
import com.viethoa.siliconstraits.testing.images.loader.ImageWorker;
import com.viethoa.siliconstraits.testing.models.FlickrPhoto;

import java.util.ArrayList;

/**
 * Created by VietHoa on 29/07/15.
 */
public class ImageViewerAdapter extends PagerAdapter {

    private Context context;
    private SparseArray<View> views;
    private ArrayList<FlickrPhoto> mDataArray;
    private ImageLoader mImageLoader;

    public ImageViewerAdapter(Context context, ImageLoader imageLoader, ArrayList<FlickrPhoto> dataArray) {
        this.mDataArray = dataArray;
        this.context = context;
        this.mImageLoader = imageLoader;
        this.views = new SparseArray<>();
    }

    @Override
    public int getCount() {
        if (mDataArray == null) {
            return 0;
        }
        return mDataArray.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }

    @Override
    public int getItemPosition(Object object) {
        View view = (View) object;
        return mDataArray.indexOf(view.getTag());
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        views.remove(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image_viewer, container, false);

        ImageView imageView = (ImageView) view.findViewById(R.id.image_view);
        View ivImageHolder = view.findViewById(R.id.image_default_holder);
        View ivProgress = view.findViewById(R.id.image_progress);

        ivImageHolder.setVisibility(View.VISIBLE);
        ivProgress.setVisibility(View.VISIBLE);

        FlickrPhoto photo = mDataArray.get(position);
        if (photo != null) {
            mImageLoader.loadImage(photo.getUrl(), imageView, new OnHideLoaderHolder(ivImageHolder, ivProgress));
        }

        container.addView(view);
        view.setTag(photo);
        views.put(position, view);
        return view;
    }

    protected class OnHideLoaderHolder implements ImageWorker.OnToDoAfterHasDoneListener {
        private View ivImageHolder;
        private View ivProgress;

        public OnHideLoaderHolder(View ivImageHolder, View ivProgress) {
            this.ivImageHolder = ivImageHolder;
            this.ivProgress = ivProgress;
        }

        @Override
        public void onToDoAfterHasDone() {
            if (ivImageHolder != null)
                ivImageHolder.setVisibility(View.GONE);

            if (ivProgress != null)
                ivProgress.setVisibility(View.GONE);
        }
    }
}