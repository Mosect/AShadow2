package com.mosect.ashadow;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

/**
 * 快速渲染的子视图阴影
 */
public abstract class FastShadow implements ChildShadow {

    private Map<Object, ShadowImage> cache;
    private RectF rect = new RectF();
    private Path path = new Path();

    @Override
    public void drawChild(@NonNull Canvas canvas, @NonNull ViewGroup parent, @NonNull View child) {
        Object key = getChildShadowKey(parent, child);
        if (null == key) return;

        rect.setEmpty();
        getChildBounds(parent, child, rect);
        float width = rect.width();
        float height = rect.height();
        if (width > 0 && height > 0) {
            ShadowImage image = getShadowImage(key);
            if (null == image) {
                ShadowInfo shadowInfo = getShadowInfo(parent, child);
                if (null != shadowInfo) {
                    path.reset();
                    float[] rounds = getChildRounds(parent, child);
                    if (null == rounds) {
                        path.addRect(rect, Path.Direction.CW);
                    } else {
                        path.addRoundRect(rect, rounds, Path.Direction.CW);
                    }
                    image = new ShadowImage(path, shadowInfo);
                    putShadowImage(key, image);
                }
            }
            if (null != image) {
                int sc = canvas.save();
                canvas.translate(rect.left, rect.top);
                image.draw(canvas, child.getMatrix(), null);
                canvas.restoreToCount(sc);
            }
        }
    }

    @Override
    public void close() {
        if (null != cache) {
            for (ShadowImage image : cache.values()) {
                image.close();
            }
            cache = null;
        }
        rect = null;
        path = null;
    }

    /**
     * 获取子视图边界
     *
     * @param parent 父视图
     * @param child  子视图
     * @param out    边界，输出
     */
    protected void getChildBounds(@NonNull ViewGroup parent, @NonNull View child, @NonNull RectF out) {
        out.set(child.getLeft(), child.getTop(), child.getRight(), child.getBottom());
    }

    /**
     * 返回key的备份
     *
     * @param key key的备份
     * @return key的备份
     */
    @NonNull
    protected Object copyKey(@NonNull Object key) {
        return key;
    }

    private ShadowImage getShadowImage(Object key) {
        if (null != cache) {
            return cache.get(key);
        }
        return null;
    }

    private void putShadowImage(Object key, ShadowImage image) {
        if (null == cache) {
            cache = new HashMap<>();
        }
        cache.put(copyKey(key), image);
    }

    /**
     * 获取子视图阴影唯一标识
     *
     * @param parent 父视图
     * @param child  子视图
     * @return 唯一标识，返回null，表示子视图没有阴影
     */
    @Nullable
    protected abstract Object getChildShadowKey(@NonNull ViewGroup parent, @NonNull View child);

    /**
     * 获取子视图圆角
     *
     * @param parent 父视图
     * @param child  子视图
     * @return 圆角，长度为8，排序为：左上，右上，右下，左下，每个角占两个，
     * 分别是X轴圆角半径和Y轴圆角半径，可以为null，表示没有圆角
     */
    protected abstract float[] getChildRounds(@NonNull ViewGroup parent, @NonNull View child);

    /**
     * 获取阴影信息
     *
     * @param parent 父视图
     * @param child  子视图
     * @return 阴影信息
     */
    protected abstract ShadowInfo getShadowInfo(@NonNull ViewGroup parent, @NonNull View child);
}
