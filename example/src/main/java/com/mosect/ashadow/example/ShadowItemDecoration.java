package com.mosect.ashadow.example;

import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.mosect.ashadow.FastShadow;
import com.mosect.ashadow.ShadowInfo;
import com.mosect.ashadow.ShadowKey;

/**
 * item阴影
 */
public abstract class ShadowItemDecoration extends RecyclerView.ItemDecoration {

    private FastShadow fastShadow = new FastShadow() {
        ShadowKey shadowKey = new ShadowKey();

        @Nullable
        @Override
        protected Object getChildShadowKey(@NonNull ViewGroup parent, @NonNull View child) {
            ShadowInfo shadowInfo = getShadowInfo(parent, child);
            float[] rounds = getChildRounds(parent, child);
            if (null != shadowInfo) {
                shadowKey.set(child.getWidth(), child.getHeight(), shadowInfo, rounds);
                return shadowKey;
            }
            return null;
        }

        @Override
        protected float[] getChildRounds(@NonNull ViewGroup parent, @NonNull View child) {
            return ShadowItemDecoration.this.getChildRounds((RecyclerView) parent, child);
        }

        @Override
        protected ShadowInfo getShadowInfo(@NonNull ViewGroup parent, @NonNull View child) {
            return ShadowItemDecoration.this.getShadowInfo((RecyclerView) parent, child);
        }

        @NonNull
        @Override
        protected Object copyKey(@NonNull Object key) {
            return ((ShadowKey) key).clone();
        }
    };

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child.getVisibility() != View.VISIBLE) continue;
            fastShadow.drawChild(c, parent, child);
        }
    }

    /**
     * 获取子视图阴影信息
     *
     * @param parent 父视图
     * @param child  子视图
     * @return 阴影信息
     */
    @Nullable
    protected abstract ShadowInfo getShadowInfo(@NonNull RecyclerView parent, @NonNull View child);

    /**
     * 获取子视图圆角
     *
     * @param parent 父视图
     * @param child  子视图
     * @return 圆角
     */
    @Nullable
    protected abstract float[] getChildRounds(@NonNull RecyclerView parent, @NonNull View child);
}
