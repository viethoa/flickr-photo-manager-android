package com.viethoa.siliconstraits.testing.view.ui;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by VietHoa on 29/07/15.
 */
public class ImageViewerViewpager extends ViewPager {

    public ImageViewerViewpager(Context context) {
        super(context);
    }

    public ImageViewerViewpager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        if (v instanceof ImageViewer) {
            return ((ImageViewer) v).canScrollHorizontallyFroyo(-dx);
        } else {
            return super.canScroll(v, checkV, dx, x, y);
        }
    }
}

