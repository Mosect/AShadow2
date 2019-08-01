package com.mosect.ashadow;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Arrays;

/**
 * 阴影的key，不同的阴影对应不同的key
 */
public class ShadowKey implements Cloneable {

    private int width;
    private int height;
    private int shadowColor;
    private float shadowRadius;
    private float shadowX;
    private float shadowY;
    private int solidColor;
    private float[] roundRadius;
    private int myHashCode;

    public void set(int width, int height, @NonNull ShadowInfo shadowInfo, @Nullable float[] roundRadius) {
        myHashCode = 0;
        this.width = width;
        this.height = height;
        this.shadowColor = shadowInfo.getShadowColor();
        this.shadowRadius = shadowInfo.getShadowRadius();
        this.shadowX = shadowInfo.getShadowX();
        this.shadowY = shadowInfo.getShadowY();
        this.solidColor = shadowInfo.getSolidColor();
        this.roundRadius = roundRadius;
        myHashCode += width + height +
                shadowColor +
                Float.floatToIntBits(shadowRadius) +
                Float.floatToIntBits(shadowX) +
                Float.floatToIntBits(shadowY) +
                solidColor +
                Arrays.hashCode(roundRadius);
    }

    @Override
    public int hashCode() {
        return myHashCode;
    }

    @Override
    public ShadowKey clone() {
        try {
            ShadowKey copy = (ShadowKey) super.clone();
            if (null != copy.roundRadius) {
                copy.roundRadius = Arrays.copyOf(copy.roundRadius, copy.roundRadius.length);
            }
            return copy;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (null != obj) {
            if (obj == this) return true;
            if (obj instanceof ShadowKey) {
                ShadowKey other = (ShadowKey) obj;
                return width == other.width &&
                        height == other.height &&
                        shadowColor == other.shadowColor &&
                        shadowRadius == other.shadowRadius &&
                        shadowX == other.shadowX &&
                        shadowY == other.shadowY &&
                        solidColor == other.solidColor &&
                        Arrays.equals(roundRadius, other.roundRadius);
            }
        }
        return false;
    }

    public boolean equals(int width, int height, ShadowInfo shadowInfo, float[] roundRadius) {
        return shadowColor == shadowInfo.getShadowColor() &&
                shadowRadius == shadowInfo.getShadowRadius() &&
                shadowX == shadowInfo.getShadowX() &&
                shadowY == shadowInfo.getShadowY() &&
                solidColor == shadowInfo.getSolidColor() &&
                Arrays.equals(this.roundRadius, roundRadius);
    }
}
