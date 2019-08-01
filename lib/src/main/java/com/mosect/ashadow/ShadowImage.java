package com.mosect.ashadow;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.Closeable;

/**
 * 阴影图像
 */
public class ShadowImage implements Closeable {

    private Bitmap bitmap;
    private float shadowX;
    private float shadowY;
    private float shadowRadius;
    private Matrix matrix;

    public ShadowImage(@NonNull Path path, @NonNull ShadowInfo shadowInfo) {
        RectF out = new RectF();
        path.computeBounds(out, false);
        float width = out.width();
        float height = out.height();
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException(String.format(
                    "Invalid path:width=%s,height=%s", width, height));
        }
        int imgWidth = (int) (width + shadowInfo.getShadowRadius() * 2 + Math.abs(shadowInfo.getShadowX()));
        int imgHeight = (int) (height + shadowInfo.getShadowRadius() * 2 + Math.abs(shadowInfo.getShadowY()));
        bitmap = Bitmap.createBitmap(imgWidth, imgHeight, Bitmap.Config.ARGB_8888);
        shadowX = shadowInfo.getShadowX();
        shadowY = shadowInfo.getShadowY();
        shadowRadius = shadowInfo.getShadowRadius();
        matrix = new Matrix();
        Canvas canvas = new Canvas(bitmap);
        float ox = shadowInfo.getShadowRadius() - out.left;
        if (shadowX < 0) {
            ox -= shadowX;
        }
        float oy = shadowInfo.getShadowRadius() - out.top;
        if (shadowY < 0) {
            oy -= shadowY;
        }
        canvas.translate(ox, oy);
        canvas.drawPath(path, shadowInfo.getPaint());
    }

    /**
     * 将阴影画在画布上
     *
     * @param canvas 画布
     * @param matrix 转换
     * @param paint  画笔
     */
    public void draw(@NonNull Canvas canvas, @Nullable Matrix matrix, @Nullable Paint paint) {
        if (null != bitmap) {
            this.matrix.reset();
            float ox = -shadowRadius;
            float oy = -shadowRadius;
            if (shadowX < 0) {
                ox += shadowX;
            }
            if (shadowY < 0) {
                oy += shadowY;
            }
            this.matrix.postTranslate(ox, oy);
            if (null != matrix) {
                this.matrix.postConcat(matrix);
            }
            canvas.drawBitmap(bitmap, this.matrix, paint);
        }
    }

    @Override
    public void close() {
        if (null != bitmap) {
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }
            bitmap = null;
        }
    }
}
