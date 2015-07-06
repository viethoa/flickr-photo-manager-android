/*
 * Copyright 2014 Alex Curran
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.amlcurran.showcaseview;

import com.github.amlcurran.showcaseview.targets.Target;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.text.DynamicLayout;
import android.text.Layout;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import static com.github.amlcurran.showcaseview.AnimationFactory.AnimationEndListener;
import static com.github.amlcurran.showcaseview.AnimationFactory.AnimationStartListener;

/**
 * A view which allows you to showcase areas of your app with an explanation.
 */
public class ShowcaseView extends RelativeLayout
        implements View.OnTouchListener, ShowcaseViewApi {

    private static final int HOLO_BLUE = Color.parseColor("#33B5E5");

    private final Button mEndButton;
    private final ShowcaseAreaCalculator showcaseAreaCalculator;
    private final AnimationFactory animationFactory;
    private final ShotStateStore shotStateStore;

    private List<Target> viewTargets = new ArrayList<>();

    private float scaleMultiplier = 1f;

    // Touch items
    private boolean hasCustomClickListener = false;
    private boolean blockTouches = true;
    private boolean hideOnTouch = false;
    private OnShowcaseEventListener mEventListener = OnShowcaseEventListener.NONE;

    private boolean hasAlteredText = false;
    private boolean shouldCentreText;
    private Bitmap bitmapBuffer;

    // Animation items
    private long fadeInMillis;
    private long fadeOutMillis;
    private boolean isShowing;

    private TextPaint textPaint = new TextPaint();
    private TextAppearanceSpan mDetailSpan;
    private int showcaseColour;
    private int backgroundColour;

    protected ShowcaseView(Context context) {
        this(context, null, R.styleable.CustomTheme_showcaseViewStyle);
    }

    protected ShowcaseView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        ApiUtils apiUtils = new ApiUtils();
        animationFactory = new AnimatorAnimationFactory();
        showcaseAreaCalculator = new ShowcaseAreaCalculator();
        shotStateStore = new ShotStateStore(context);

        apiUtils.setFitsSystemWindowsCompat(this);
        getViewTreeObserver().addOnPreDrawListener(new CalculateTextOnPreDraw());
        getViewTreeObserver().addOnGlobalLayoutListener(new UpdateOnGlobalLayout());

        // Get the attributes for the ShowcaseView
        final TypedArray styled = context.getTheme()
                .obtainStyledAttributes(attrs, R.styleable.ShowcaseView, R.attr.showcaseViewStyle,
                        R.style.ShowcaseView);

        // Set the default animation times
        fadeInMillis = getResources().getInteger(android.R.integer.config_mediumAnimTime);
        fadeOutMillis = getResources().getInteger(android.R.integer.config_mediumAnimTime);

        mEndButton = (Button) LayoutInflater.from(context).inflate(R.layout.showcase_button, null);
//        if (newStyle) {
//            showcaseDrawer = new NewShowcaseDrawer(getResources());
//        } else {
//            showcaseDrawer = new StandardShowcaseDrawer(getResources());
//        }
//        textDrawer = new TextDrawer(getResources(), showcaseAreaCalculator, getContext());
        updateStyle(styled, false);

        init();
    }

    private void init() {
        textPaint = new TextPaint();
        textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        textPaint.setAntiAlias(true);

        setOnTouchListener(this);

        if (mEndButton.getParent() == null) {
            int margin = (int) getResources().getDimension(R.dimen.button_margin);
            RelativeLayout.LayoutParams lps = (LayoutParams) generateDefaultLayoutParams();
            lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            lps.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            lps.setMargins(margin, margin, margin, margin);
            mEndButton.setLayoutParams(lps);
            mEndButton.setText(android.R.string.ok);
            if (!hasCustomClickListener) {
                mEndButton.setOnClickListener(hideOnClickListener);
            }
            addView(mEndButton);
        }
        hideButton();

    }

    private boolean hasShot() {
        return shotStateStore.hasShot();
    }

    void setShowcasePosition(Point point) {
        setShowcasePosition(point.x, point.y);
    }

    void setShowcasePosition(int x, int y) {
        if (shotStateStore.hasShot()) {
            return;
        }
        //init();
        invalidate();
    }

    public void addTarget(final Target target) {
        setShowcase(target, false);
    }

    public void setShowcase(final Target target, final boolean animate) {
        postDelayed(new Runnable() {
            @Override
            public void run() {

                if (!shotStateStore.hasShot()) {

                    updateBitmap();
                    viewTargets.add(target);
                    Point targetPoint = target.getPoint();
                    if (targetPoint != null) {
                        if (animate) {
                            animationFactory.animateTargetToPoint(ShowcaseView.this, targetPoint);
                        } else {
                            setShowcasePosition(targetPoint);
                        }
                    } else {
                        invalidate();
                    }

                }
            }
        }, 100);
    }

    private void updateBitmap() {
        if ((bitmapBuffer == null || haveBoundsChanged()) && getMeasuredWidth() > 0) {
            if (bitmapBuffer != null) {
                bitmapBuffer.recycle();
            }
            bitmapBuffer = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        }
    }

    private boolean haveBoundsChanged() {
        return getMeasuredWidth() != bitmapBuffer.getWidth() ||
                getMeasuredHeight() != bitmapBuffer.getHeight();
    }

    public boolean hasShowcaseView() {
        return true;
    }

    /**
     * Override the standard button click event
     *
     * @param listener Listener to listen to on click events
     */
    public void overrideButtonClick(OnClickListener listener) {
        if (shotStateStore.hasShot()) {
            return;
        }
        if (mEndButton != null) {
            if (listener != null) {
                mEndButton.setOnClickListener(listener);
            } else {
                mEndButton.setOnClickListener(hideOnClickListener);
            }
        }
        hasCustomClickListener = true;
    }

    public void setOnShowcaseEventListener(OnShowcaseEventListener listener) {
        if (listener != null) {
            mEventListener = listener;
        } else {
            mEventListener = OnShowcaseEventListener.NONE;
        }
    }

    public void setButtonText(CharSequence text) {
        if (mEndButton != null) {
            mEndButton.setText(text);
        }
    }

//    private void recalculateText() {
//        boolean recalculatedCling = showcaseAreaCalculator.calculateShowcaseRect(showcaseX, showcaseY, showcaseDrawer);
//        boolean recalculateText = recalculatedCling || hasAlteredText;
//        hasAlteredText = false;
//    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (viewTargets == null || viewTargets.isEmpty() || shotStateStore.hasShot() || bitmapBuffer == null) {
            super.dispatchDraw(canvas);
            return;
        }

        //Draw background color
        bitmapBuffer.eraseColor(backgroundColour);
        Canvas bitmapBufferCanvas = new Canvas(bitmapBuffer);

        // Draw the showcase drawable
        if (viewTargets != null && !viewTargets.isEmpty()) {
            for (Target target : viewTargets) {
                Point point = target.getPoint();
                ShowcaseDrawer showcaseDrawer = target.getShowcaseDrawer();
                if (showcaseDrawer != null) {
                    showcaseDrawer.drawShowcase(bitmapBuffer, point.x, point.y, scaleMultiplier);
                    if (target.getDescription() != null) {
                        SpannableString ssbTitle = new SpannableString(target.getDescription());
                        ssbTitle.setSpan(mDetailSpan, 0, ssbTitle.length(), 0);
                        Point textPosition = null;
                        DynamicLayout mDynamicDetailLayout = null;
                        switch (target.getPositionOfText()) {
                            case Target.ABOVE:
                                mDynamicDetailLayout = new DynamicLayout(ssbTitle, textPaint,
                                        getWidth() - getPaddingLeft() - getPaddingRight(),
                                        Layout.Alignment.ALIGN_CENTER,
                                        1.2f, 1.0f, true);
                                textPosition = new Point(getPaddingLeft(),
                                        getHeight() - showcaseDrawer.getShowcaseHeight() - target
                                                .getDescriptionPadding() - mDynamicDetailLayout.getHeight());
                                break;
                            case Target.LEFT:
                                mDynamicDetailLayout = new DynamicLayout(ssbTitle, textPaint,
                                        getWidth() - getPaddingLeft() - getPaddingRight() - showcaseDrawer
                                                .getShowcaseWidth() - target.getDescriptionPadding(),
                                        Layout.Alignment.ALIGN_NORMAL,
                                        1.2f, 1.0f, true);
                                int maxLineWidth = 0;
                                for (int i = 0; i < mDynamicDetailLayout.getLineCount(); i++) {
                                    maxLineWidth = (int) Math.max(mDynamicDetailLayout.getLineWidth(i), maxLineWidth);
                                }
                                textPosition = new Point(
                                        Math.max(point.x - showcaseDrawer.getShowcaseWidth() / 2 - maxLineWidth, 0),
                                        Math.max(point.y - showcaseDrawer.getShowcaseHeight() / 2, 0));
                                break;
                            case Target.BELOW:
                                mDynamicDetailLayout = new DynamicLayout(ssbTitle, textPaint,
                                        getWidth() - getPaddingLeft() - getPaddingRight(),
                                        Layout.Alignment.ALIGN_NORMAL, 1.2f, 1.0f, true);
                                maxLineWidth = 0;
                                for (int i = 0; i < mDynamicDetailLayout.getLineCount(); i++) {
                                    maxLineWidth = (int) Math.max(mDynamicDetailLayout.getLineWidth(i), maxLineWidth);
                                }
                                int textPositionXPos = getWidth() - getPaddingLeft() - getPaddingRight() - maxLineWidth;
                                if (shouldCentreText) {
                                    textPositionXPos = point.x - maxLineWidth / 2;
                                }
                                textPosition = new Point(textPositionXPos,
                                        point.y + showcaseDrawer.getShowcaseHeight() / 2 + target.getDescriptionPadding());
                        }
                        if (mDynamicDetailLayout != null) {
                            bitmapBufferCanvas.save();
                            if (textPosition != null) {
                                bitmapBufferCanvas.translate(textPosition.x, textPosition.y);
                            }
                            mDynamicDetailLayout.draw(bitmapBufferCanvas);
                            bitmapBufferCanvas.restore();
                        }
                    }
                }
            }
            canvas.drawBitmap(bitmapBuffer, 0, 0, new Paint());
        }

        super.dispatchDraw(canvas);

    }

    @Override
    public void hide() {
        clearBitmap();
        // If the type is set to one-shot, store that it has shot
        shotStateStore.storeShot();
        mEventListener.onShowcaseViewHide(this);
        fadeOutShowcase();
    }

    private void clearBitmap() {
        if (bitmapBuffer != null && !bitmapBuffer.isRecycled()) {
            bitmapBuffer.recycle();
            bitmapBuffer = null;
        }
    }

    private void fadeOutShowcase() {
        animationFactory.fadeOutView(this, fadeOutMillis, new AnimationEndListener() {
            @Override
            public void onAnimationEnd() {
                setVisibility(View.GONE);
                isShowing = false;
                mEventListener.onShowcaseViewDidHide(ShowcaseView.this);
            }
        });
    }

    @Override
    public void show() {
        isShowing = true;
        mEventListener.onShowcaseViewShow(this);
        fadeInShowcase();
    }

    private void fadeInShowcase() {
        animationFactory.fadeInView(this, fadeInMillis,
                new AnimationStartListener() {
                    @Override
                    public void onAnimationStart() {
                        setVisibility(View.VISIBLE);
                    }
                }
        );
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        double distanceFromFocus = 0;
        if (viewTargets != null && !viewTargets.isEmpty()) {
            for (Target target : viewTargets) {
                int showcaseX = target.getPoint().x;
                int showcaseY = target.getPoint().y;
                float xDelta = Math.abs(motionEvent.getRawX() - showcaseX);
                float yDelta = Math.abs(motionEvent.getRawY() - showcaseY);
                distanceFromFocus = Math.sqrt(Math.pow(xDelta, 2) + Math.pow(yDelta, 2));
                if (MotionEvent.ACTION_UP == motionEvent.getAction()) {
                    this.hide();
                    return true;
                }
            }
        }

        return blockTouches && distanceFromFocus > 0;
    }

    private static void insertShowcaseView(ShowcaseView showcaseView, Activity activity) {
        ((ViewGroup) activity.getWindow().getDecorView()).addView(showcaseView);
        if (!showcaseView.hasShot()) {
            showcaseView.show();
        } else {
            showcaseView.hideImmediate();
        }
    }

    private void hideImmediate() {
        isShowing = false;
        setVisibility(GONE);
        hideButton();
    }

    private void setScaleMultiplier(float scaleMultiplier) {
        this.scaleMultiplier = scaleMultiplier;
    }

    public void hideButton() {
        mEndButton.setVisibility(GONE);
    }

    public void showButton() {
        mEndButton.setVisibility(VISIBLE);
    }

    public void setShowcaseColour(int showcaseColour) {
        this.showcaseColour = showcaseColour;
    }

    public void setBackgroundColour(int backgroundColour) {
        this.backgroundColour = backgroundColour;
    }


    /**
     * Builder class which allows easier creation of {@link com.github.amlcurran.showcaseview.ShowcaseView}s. It is recommended that you use this Builder
     * class.
     */
    public static class Builder {

        final ShowcaseView showcaseView;
        private final Activity activity;


        public Builder(Activity activity) {
            this.activity = activity;
            this.showcaseView = new ShowcaseView(activity);
            this.showcaseView.addTarget(Target.NONE);
        }

        /**
         * Create the {@link com.github.amlcurran.showcaseview.ShowcaseView} and show it.
         *
         * @return the created ShowcaseView
         */
        public ShowcaseView build() {
            insertShowcaseView(showcaseView, activity);
            return showcaseView;
        }

        /**
         * Set the target of the showcase.
         *
         * @param target a {@link com.github.amlcurran.showcaseview.targets.Target} representing the item to showcase
         *               (e.g., a button, or action item).
         */
        public Builder setTarget(Target target) {
            showcaseView.addTarget(target);
            return this;
        }

        /**
         * Set the style of the ShowcaseView. See the sample app for example styles.
         */
        public Builder setStyle(int theme) {
            showcaseView.setStyle(theme);
            return this;
        }

        /**
         * Set a listener which will override the button clicks. <p/> Note that you will have to manually hide the
         * ShowcaseView
         */
        public Builder setOnClickListener(OnClickListener onClickListener) {
            showcaseView.overrideButtonClick(onClickListener);
            return this;
        }

        /**
         * Don't make the ShowcaseView block touches on itself. This doesn't block touches in the showcased area. <p/>
         * By default, the ShowcaseView does block touches
         */
        public Builder doNotBlockTouches() {
            showcaseView.setBlocksTouches(false);
            return this;
        }

        /**
         * Make this ShowcaseView hide when the user touches outside the showcased area. This enables {@link
         * #doNotBlockTouches()} as well. <p/> By default, the ShowcaseView doesn't hide on touch.
         */
        public Builder hideOnTouchOutside() {
            showcaseView.setBlocksTouches(true);
            showcaseView.setHideOnTouchOutside(true);
            return this;
        }

        /**
         * Set the ShowcaseView to only ever show once.
         *
         * @param shotId a unique identifier (<em>across the app</em>) to store whether this ShowcaseView has been
         *               shown.
         */
        public Builder singleShot(String shotId) {
            showcaseView.setSingleShot(shotId);
            return this;
        }

        public Builder setShowcaseEventListener(OnShowcaseEventListener showcaseEventListener) {
            showcaseView.setOnShowcaseEventListener(showcaseEventListener);
            return this;
        }
    }

    /**
     * Set whether the text should be centred in the screen, or left-aligned (which is the default).
     */
    public void setShouldCentreText(boolean shouldCentreText) {
        this.shouldCentreText = shouldCentreText;
        hasAlteredText = true;
        invalidate();
    }

    /**
     * @see com.github.amlcurran.showcaseview.ShowcaseView.Builder#setSingleShot(long)
     */
    private void setSingleShot(String shotId) {
        shotStateStore.setSingleShot(shotId);
    }

    /**
     * Change the position of the ShowcaseView's button from the default bottom-right position.
     *
     * @param layoutParams a {@link android.widget.RelativeLayout.LayoutParams} representing the new position of the
     *                     button
     */
    @Override
    public void setButtonPosition(RelativeLayout.LayoutParams layoutParams) {
        mEndButton.setLayoutParams(layoutParams);
    }

    /**
     * Set the duration of the fading in and fading out of the ShowcaseView
     */
    private void setFadeDurations(long fadeInMillis, long fadeOutMillis) {
        this.fadeInMillis = fadeInMillis;
        this.fadeOutMillis = fadeOutMillis;
    }

    /**
     * @see com.github.amlcurran.showcaseview.ShowcaseView.Builder#hideOnTouchOutside()
     */
    @Override
    public void setHideOnTouchOutside(boolean hideOnTouch) {
        this.hideOnTouch = hideOnTouch;
    }

    /**
     * @see com.github.amlcurran.showcaseview.ShowcaseView.Builder#doNotBlockTouches()
     */
    @Override
    public void setBlocksTouches(boolean blockTouches) {
        this.blockTouches = blockTouches;
    }

    /**
     * @see com.github.amlcurran.showcaseview.ShowcaseView.Builder#setStyle(int)
     */
    @Override
    public void setStyle(int theme) {
        TypedArray array = getContext().obtainStyledAttributes(theme, R.styleable.ShowcaseView);
        updateStyle(array, true);
    }

    @Override
    public boolean isShowing() {
        return isShowing;
    }

    private void updateStyle(TypedArray styled, boolean invalidate) {
        int backgroundColor = styled.getColor(R.styleable.ShowcaseView_sv_backgroundColor, Color.argb(128, 80, 80, 80));
        int showcaseColor = styled.getColor(R.styleable.ShowcaseView_sv_showcaseColor, HOLO_BLUE);
        String buttonText = styled.getString(R.styleable.ShowcaseView_sv_buttonText);
        if (TextUtils.isEmpty(buttonText)) {
            buttonText = getResources().getString(android.R.string.ok);
        }
        boolean tintButton = styled.getBoolean(R.styleable.ShowcaseView_sv_tintButtonColor, true);

        int titleTextAppearance = styled.getResourceId(R.styleable.ShowcaseView_sv_titleTextAppearance,
                R.style.TextAppearance_ShowcaseView_Title);
        int detailTextAppearance = styled.getResourceId(R.styleable.ShowcaseView_sv_detailTextAppearance,
                R.style.TextAppearance_ShowcaseView_Detail);

        mDetailSpan = new TextAppearanceSpan(getContext(), detailTextAppearance);

        styled.recycle();

        setShowcaseColour(showcaseColor);
        setBackgroundColour(backgroundColor);
        tintButton(showcaseColor, tintButton);
        mEndButton.setText(buttonText);
        hasAlteredText = true;

        if (invalidate) {
            invalidate();
        }
    }

    private void tintButton(int showcaseColor, boolean tintButton) {
        if (tintButton) {
            mEndButton.getBackground().setColorFilter(showcaseColor, PorterDuff.Mode.MULTIPLY);
        } else {
            mEndButton.getBackground().setColorFilter(HOLO_BLUE, PorterDuff.Mode.MULTIPLY);
        }
    }

    private class UpdateOnGlobalLayout implements ViewTreeObserver.OnGlobalLayoutListener {

        @Override
        public void onGlobalLayout() {
            if (!shotStateStore.hasShot()) {
                updateBitmap();
            }
        }
    }

    private class CalculateTextOnPreDraw implements ViewTreeObserver.OnPreDrawListener {

        @Override
        public boolean onPreDraw() {
//            recalculateText();
            return true;
        }
    }

    private OnClickListener hideOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            hide();
        }
    };

}
