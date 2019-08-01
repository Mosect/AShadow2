package com.mosect.ashadow;

import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import java.io.Closeable;

/**
 * 子视图阴影
 */
public interface ChildShadow extends Closeable {

    /**
     * 给子视图画阴影
     *
     * @param canvas 画布
     * @param parent 父视图
     * @param child  子视图
     */
    void drawChild(@NonNull Canvas canvas, @NonNull ViewGroup parent, @NonNull View child);

    /**
     * 释放此对象
     */
    @Override
    void close();
}
