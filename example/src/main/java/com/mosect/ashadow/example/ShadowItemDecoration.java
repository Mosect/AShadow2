package com.mosect.ashadow.example;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mosect.ashadow.Shadow;
import com.mosect.ashadow.ShadowManager;
import com.mosect.ashadow.UnsupportedKeyException;

import java.io.Closeable;
import java.util.HashMap;
import java.util.Map;

/**
 * item阴影
 */
public abstract class ShadowItemDecoration
        extends RecyclerView.ItemDecoration
        implements Closeable {

    private Map<Object, Shadow> shadowMap;
    private ShadowInfo shadowInfo = new ShadowInfo();
    private Rect shadowRect = new Rect();

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child.getVisibility() != View.VISIBLE) continue;
            shadowInfo.key = null;
            shadowInfo.shadowX = 0;
            shadowInfo.shadowY = 0;
            getShadowInfo(parent, child, shadowInfo);
            if (null != shadowInfo.key) {
                Shadow shadow = null;
                if (null != shadowMap) {
                    shadow = shadowMap.get(shadowInfo.key);
                }
                if (null == shadow) {
                    try {
                        shadow = ShadowManager.getDefault().get(shadowInfo.key);
                        if (null == shadowMap) {
                            shadowMap = new HashMap<>();
                        }
                        shadowMap.put(shadow.getKey(), shadow);
                        draw(c, parent, child, shadow);
                    } catch (UnsupportedKeyException e) {
                        e.printStackTrace();
                    }
                } else {
                    draw(c, parent, child, shadow);
                }
            }
        }
    }

    private void draw(Canvas canvas, RecyclerView parent, View child, Shadow shadow) {
        shadowRect.setEmpty();
        getShadowRect(parent, child, shadowRect);
        if (!shadowRect.isEmpty()) {
            shadow.draw(canvas, shadowRect, null);
        }
    }

    @Override
    public void close() {
        if (null != shadowMap) {
            /*for (Shadow shadow : shadowMap.values()) {
                ShadowManager.getDefault().unbind(shadow);
            }*/
            shadowMap = null;
        }
    }

    protected abstract void getShadowInfo(@NonNull RecyclerView parent, @NonNull View child, @NonNull ShadowInfo out);

    protected abstract void getShadowRect(@NonNull RecyclerView parent, @NonNull View child, @NonNull Rect out);

    protected static class ShadowInfo {
        public Object key;
        public float shadowX;
        public float shadowY;
    }
}
