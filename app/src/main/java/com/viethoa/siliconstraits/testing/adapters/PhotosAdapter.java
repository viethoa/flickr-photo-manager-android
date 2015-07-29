package com.viethoa.siliconstraits.testing.adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextSwitcher;

import com.viethoa.siliconstraits.testing.R;
import com.viethoa.siliconstraits.testing.images.loader.ImageLoader;
import com.viethoa.siliconstraits.testing.models.FlickrPhoto;
import com.viethoa.siliconstraits.testing.utils.DeviceUtils;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by VietHoa on 28/07/15.
 */
public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.ViewHolder> {

    private static final int ANIMATION_DURATION = 300;

    private static int imageHeight = 0;
    private ImageLoader mImageLoader;

    private OnPhotoItemClickListener listener;
    private ArrayList<FlickrPhoto> mDataArray;

    public PhotosAdapter(Context context, ImageLoader imageLoader, ArrayList<FlickrPhoto> data) {
        this.mDataArray = data;
        this.mImageLoader = imageLoader;
        this.listener = (OnPhotoItemClickListener) context;
        this.imageHeight = DeviceUtils.getDeviceScreenHeight(context) / 3;
    }

    public void updateDataArray(ArrayList<FlickrPhoto> newDataArray) {
        this.mDataArray = newDataArray;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mDataArray == null)
            return 0;
        return mDataArray.size();
    }

    @Override
    public PhotosAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (position < 0 || position >= mDataArray.size())
            return;

        final FlickrPhoto photo = mDataArray.get(position);
        String imageUrl = "";
        int likeSum = 0;

        if (photo != null) {
            imageUrl = photo.getUrl();
            likeSum = photo.getLikeSum();
        }

        //Config
        ViewGroup.LayoutParams params = holder.ivPhoto.getLayoutParams();
        params.height = imageHeight;
        holder.ivPhoto.setLayoutParams(params);
        holder.ivPhotoHolder.setLayoutParams(params);

        //Setting
        mImageLoader.loadImage(imageUrl, holder.ivPhoto, null);
        holder.switFavorite.setText(likeSum + " likes");
        holder.btnLike.setImageResource(photo.isLike() ? R.mipmap.ic_heart : R.mipmap.ic_heart_gray_cover);

        //Event
        holder.btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (photo.isLike())
                    return;

                photo.setLike(true);
                takeLikedAnimate(holder);
                onFavourteClicked(holder, photo);
            }
        });
        holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnPhotoItemClicked(position);
            }
        });
    }

    protected void onFavourteClicked(ViewHolder holder, FlickrPhoto photo) {
        int currFavorite = photo.getLikeSum() + 1;
        photo.setLikeSum(currFavorite);

        holder.switFavorite.setText(currFavorite + " likes");
    }

    protected void takeLikedAnimate(final ViewHolder holder) {
        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(holder.btnLike, "rotation", 0f, 360f);
        rotationAnim.setDuration(ANIMATION_DURATION);
        rotationAnim.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator bounceAnimX = ObjectAnimator.ofFloat(holder.btnLike, "scaleX", 0.2f, 1f);
        bounceAnimX.setDuration(ANIMATION_DURATION);
        bounceAnimX.setInterpolator(new OvershootInterpolator());

        ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(holder.btnLike, "scaleY", 0.2f, 1f);
        bounceAnimY.setDuration(ANIMATION_DURATION);
        bounceAnimY.setInterpolator(new OvershootInterpolator());
        bounceAnimY.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                holder.btnLike.setImageResource(R.mipmap.ic_heart);
            }
        });

        animatorSet.play(rotationAnim);
        animatorSet.play(bounceAnimX).with(bounceAnimY).after(rotationAnim);
        animatorSet.start();
    }

    public interface OnPhotoItemClickListener {
        void OnPhotoItemClicked(int position);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.image_default_holder)
        ImageView ivPhotoHolder;
        @InjectView(R.id.image_view)
        ImageView ivPhoto;
        @InjectView(R.id.iv_like)
        ImageView btnLike;
        @InjectView(R.id.switcher_favorite)
        TextSwitcher switFavorite;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }
}
