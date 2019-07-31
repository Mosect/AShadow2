package com.mosect.ashadow;

import android.graphics.Color;
import android.graphics.Paint;

/**
 * 阴影信息
 */
public class ShadowInfo {

    private float shadowRadius; // 阴影半径
    private float shadowX; // 阴影X轴偏移量
    private float shadowY; // 阴影Y轴偏移量
    private int shadowColor; // 阴影颜色
    private int solidColor; // 填充颜色
    private Paint paint; // 阴影画笔

    public ShadowInfo() {
        shadowColor = Color.BLACK;
        solidColor = Color.BLACK;
        paint = new Paint();
        updatePaint();
    }

    public float getShadowRadius() {
        return shadowRadius;
    }

    public void setShadowRadius(float shadowRadius) {
        this.shadowRadius = shadowRadius;
        updatePaint();
    }

    public float getShadowX() {
        return shadowX;
    }

    public void setShadowX(float shadowX) {
        this.shadowX = shadowX;
        updatePaint();
    }

    public float getShadowY() {
        return shadowY;
    }

    public void setShadowY(float shadowY) {
        this.shadowY = shadowY;
        updatePaint();
    }

    public int getShadowColor() {
        return shadowColor;
    }

    public void setShadowColor(int shadowColor) {
        this.shadowColor = shadowColor;
        updatePaint();
    }

    public void setSolidColor(int solidColor) {
        this.solidColor = solidColor;
        updatePaint();
    }

    public int getSolidColor() {
        return solidColor;
    }

    public Paint getPaint() {
        return paint;
    }

    private void updatePaint() {
        paint.reset();
        paint.setAntiAlias(true);
        paint.setShadowLayer(shadowRadius, shadowX, shadowY, shadowColor);
        paint.setColor(solidColor);
        paint.setStyle(Paint.Style.FILL);
    }
}
