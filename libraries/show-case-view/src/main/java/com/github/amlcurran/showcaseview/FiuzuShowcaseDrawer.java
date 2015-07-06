package com.github.amlcurran.showcaseview;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

/**
 * Created by Jupiter (vu.cao.duy@gmail.com) on 3/11/15.
 */
public class FiuzuShowcaseDrawer implements ShowcaseDrawer {

    private static final int ALPHA_60_PERCENT = 153;
    private final Paint eraserPaint;
    private final Paint basicPaint;
    private float innerRadius;
    private int backgroundColour;

    public FiuzuShowcaseDrawer(int innerRadius) {
        PorterDuffXfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY);
        eraserPaint = new Paint();
        eraserPaint.setColor(0xFFFFFF);
        eraserPaint.setAlpha(0);
        eraserPaint.setXfermode(xfermode);
        eraserPaint.setAntiAlias(true);
        basicPaint = new Paint();
        this.innerRadius = innerRadius;
    }

    @Override
    public void setShowcaseColour(int color) {
        eraserPaint.setColor(color);
    }

    @Override
    public void drawShowcase(Bitmap buffer, float x, float y, float scaleMultiplier) {
        Canvas bufferCanvas = new Canvas(buffer);
        eraserPaint.setAlpha(0);
        bufferCanvas.drawCircle(x, y, innerRadius, eraserPaint);
    }

    @Override
    public int getShowcaseWidth() {
        return (int) (innerRadius * 2);
    }

    @Override
    public int getShowcaseHeight() {
        return (int) (innerRadius * 2);
    }

    @Override
    public float getBlockedRadius() {
        return 0;
    }

    @Override
    public void setBackgroundColour(int backgroundColor) {
        this.backgroundColour = backgroundColor;
    }

    @Override
    public void erase(Bitmap bitmapBuffer) {
        bitmapBuffer.eraseColor(backgroundColour);
    }

    @Override
    public void drawToCanvas(Canvas canvas, Bitmap bitmapBuffer) {
        canvas.drawBitmap(bitmapBuffer, 0, 0, basicPaint);
    }
}
