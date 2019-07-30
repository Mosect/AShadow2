package com.mosect.ashadow;

import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

public abstract class RectChildShadow extends ChildShadow {

    private Path shadowPath = new Path();
    private Path childPath = new Path();
    private Path parentPath = new Path();
    private Path clipPath = new Path();
    private RectF rect = new RectF();
    private Matrix matrix = new Matrix();

    @Override
    protected Path getClipPath(@NonNull ViewGroup parent, @NonNull View child) {
        clipPath.reset();
        childPath.reset();
        parentPath.reset();

        // 计算父视图路径
        computeParentRect(parent, child, rect);
        parentPath.addRect(rect, Path.Direction.CW);

        // 计算子视图路径
        computeChildRect(parent, child, rect);
        float[] roundRadius = getRoundRadius(parent, child);
        if (null != roundRadius && roundRadius.length == 8) {
            childPath.addRoundRect(rect, roundRadius, Path.Direction.CW);
        } else {
            childPath.addRect(rect, Path.Direction.CW);
        }

        // 计算出clip路径，需要排除子视图内容的部分，剩余部分画阴影
        clipPath.op(parentPath, childPath, Path.Op.DIFFERENCE);

        // 因为视图本身也有偏移缩放等，需要加上子视图本身的偏移、缩放等
        Matrix matrix = child.getMatrix();
        if (null != matrix) {
            this.matrix.reset();
            this.matrix.postTranslate(-rect.left, -rect.top);
            this.matrix.postConcat(matrix);
            this.matrix.postTranslate(rect.left, rect.top);
            clipPath.transform(this.matrix);
        }
        return clipPath;
    }

    @Override
    protected Path getShadowPath(@NonNull ViewGroup parent, @NonNull View child) {
        shadowPath.reset();
        computeChildRect(parent, child, rect);
        float[] roundRadius = getRoundRadius(parent, child);
        if (null != roundRadius && roundRadius.length == 8) {
            shadowPath.addRoundRect(rect, roundRadius, Path.Direction.CW);
        } else {
            shadowPath.addRect(rect, Path.Direction.CW);
        }
        Matrix matrix = child.getMatrix();
        if (null != matrix) {
            this.matrix.reset();
            this.matrix.postTranslate(-rect.left, -rect.top);
            this.matrix.postConcat(matrix);
            this.matrix.postTranslate(rect.left, rect.top);
            shadowPath.transform(this.matrix);
        }
        return shadowPath;
    }

    /**
     * 获取圆角半径
     *
     * @return 圆角半径数组，排列顺序为：左上（1），右上（2），右下（3），左下（4）
     * 示例：float[] radius = {x1,y1,x2,y2,x3,y3,x4,y4};
     * 返回的数组长度必须为8，否则当成返回null，当返回null时，表示没有圆角
     */
    public float[] getRoundRadius(@NonNull ViewGroup parent, @NonNull View child) {
        return null;
    }

    /**
     * 计算父视图的边界，需要包含阴影部分
     * 当parent与child是间接关系时，需要计算出child相对于parent中的位置的父视图边界
     *
     * @param parent 父视图
     * @param child  子视图
     * @param out    输出，父视图边界
     */
    protected void computeParentRect(@NonNull ViewGroup parent, @NonNull View child, @NonNull RectF out) {
        ShadowInfo shadowInfo = getShadowInfo(parent, child);
        if (null == shadowInfo) {
            out.setEmpty();
        } else {
            // 以子视图为中心，取阴影半径的周边
            out.set(
                    child.getLeft() - shadowInfo.getShadowRadius(),
                    child.getTop() - shadowInfo.getShadowRadius(),
                    child.getRight() + shadowInfo.getShadowRadius(),
                    child.getBottom() + shadowInfo.getShadowRadius()
            );
            // 需要加上阴影的偏移
            out.offset(shadowInfo.getShadowX(), shadowInfo.getShadowY());
        }
    }

    /**
     * 计算子视图的边界
     * 当parent与child是间接关系时，需要计算出child相对于parent中的位置
     *
     * @param parent 父视图
     * @param child  子视图
     * @param out    输出，子视图边界
     */
    protected void computeChildRect(@NonNull ViewGroup parent, @NonNull View child, @NonNull RectF out) {
        if (child.getParent() == parent) {
            // 直接关系
            out.set(child.getLeft(), child.getTop(), child.getRight(), child.getBottom());
        } else {
            // 间接关系
            View cur = child;
            int ox = 0;
            int oy = 0;
            do {
                ox += cur.getLeft();
                oy += cur.getTop();
                cur = (View) cur.getParent();
            } while (cur.getParent() != parent);
            out.set(ox, oy, ox + child.getWidth(), oy + child.getHeight());
        }
    }
}
