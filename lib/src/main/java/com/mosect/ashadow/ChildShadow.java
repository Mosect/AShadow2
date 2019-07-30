package com.mosect.ashadow;

import android.graphics.Canvas;
import android.graphics.Path;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

/**
 * 子视图阴影
 */
public abstract class ChildShadow {

    public void drawChild(@NonNull Canvas canvas, @NonNull ViewGroup parent, @NonNull View child) {
        Path shadowPath = getShadowPath(parent, child);
        Path clipPath = getClipPath(parent, child);
        ShadowInfo shadowInfo = getShadowInfo(parent, child);
        if (null != shadowPath && null != shadowInfo) {
            if (null != clipPath) {
                int sc = canvas.save();
                canvas.clipPath(clipPath);
                canvas.drawPath(shadowPath, shadowInfo.getPaint());
                canvas.restoreToCount(sc);
            } else {
                canvas.drawPath(shadowPath, shadowInfo.getPaint());
            }
        }
    }

    /**
     * 获取阴影信息
     *
     * @param parent 父视图
     * @param child  子视图
     * @return 阴影信息
     */
    @Nullable
    protected abstract ShadowInfo getShadowInfo(@NonNull ViewGroup parent, @NonNull View child);

    /**
     * 获取clip路径，用于选定画布区域，不在此区域的图像不会被画出来
     *
     * @param child 子视图
     * @return clip路径
     */
    @Nullable
    protected abstract Path getClipPath(@NonNull ViewGroup parent, @NonNull View child);

    /**
     * 获取阴影路径
     *
     * @param child 子视图
     * @return 阴影路径
     */
    @Nullable
    protected abstract Path getShadowPath(@NonNull ViewGroup parent, @NonNull View child);
}
