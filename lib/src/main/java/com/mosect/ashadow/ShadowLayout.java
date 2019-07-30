package com.mosect.ashadow;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.mosect.viewutils.MeasureUtils;

public class ShadowLayout extends FrameLayout {

    private Rect layoutContainer = new Rect();
    private Rect layoutOut = new Rect();
    private RectChildShadow priChildShadow = new RectChildShadow() {
        @Nullable
        @Override
        protected ShadowInfo getShadowInfo(@NonNull ViewGroup parent, @NonNull View child) {
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (null != lp) {
                return lp.shadowInfo;
            }
            return null;
        }

        @Override
        public float[] getRoundRadius(@NonNull ViewGroup parent, @NonNull View child) {
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (null != lp) {
                return lp.roundRadius;
            }
            return null;
        }
    };

    public ShadowLayout(@NonNull Context context) {
        super(context);
        init();
    }

    public ShadowLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ShadowLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // 关闭硬件加速
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        return new LayoutParams(lp);
    }

    @Override
    protected FrameLayout.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int paddingWidth = getPaddingLeft() + getPaddingRight();
        int paddingHeight = getPaddingTop() + getPaddingBottom();
        int cws = MeasureUtils.makeSelfMeasureSpec(widthMeasureSpec, paddingWidth);
        int chs = MeasureUtils.makeSelfMeasureSpec(heightMeasureSpec, paddingHeight);
        int contentWidth = 0;
        int contentHeight = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) continue;
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            int hmar = (int) (lp.leftMargin + lp.rightMargin + lp.shadowInfo.getShadowRadius() * 2);
            int vmar = (int) (lp.topMargin + lp.bottomMargin + lp.shadowInfo.getShadowRadius() * 2);
            int ws = MeasureUtils.makeChildMeasureSpec(cws, lp.width, hmar);
            int hs = MeasureUtils.makeChildMeasureSpec(chs, lp.height, vmar);
            child.measure(ws, hs);
            int width = child.getMeasuredWidth() + hmar;
            int height = child.getMeasuredHeight() + vmar;
            if (width > contentWidth) contentWidth = width;
            if (height > contentHeight) contentHeight = height;
        }
        int width = MeasureUtils.getMeasuredDimension(contentWidth, widthMeasureSpec);
        int height = MeasureUtils.getMeasuredDimension(contentHeight, heightMeasureSpec);
        setMeasuredDimension(width, height);
        layoutContainer.set(0, 0, width, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) continue;
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            int gravity = lp.gravity == Gravity.NO_GRAVITY ? Gravity.LEFT | Gravity.TOP : lp.gravity;
            int width = (int) (child.getMeasuredWidth() + lp.leftMargin +
                    lp.rightMargin + lp.shadowInfo.getShadowRadius() * 2);
            int height = (int) (child.getMeasuredHeight() + lp.topMargin +
                    lp.bottomMargin + lp.shadowInfo.getShadowRadius() * 2);
            Gravity.apply(gravity, width, height, layoutContainer, layoutOut);
            int cl = (int) (layoutOut.left + lp.leftMargin +
                    lp.shadowInfo.getShadowRadius() + lp.shadowInfo.getShadowX());
            int ct = (int) (layoutOut.top + lp.topMargin +
                    lp.shadowInfo.getShadowRadius() + lp.shadowInfo.getShadowY());
            int cr = cl + child.getMeasuredWidth();
            int cb = ct + child.getMeasuredHeight();
            child.layout(cl, ct, cr, cb);
        }
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        priChildShadow.drawChild(canvas, this, child);
        return super.drawChild(canvas, child, drawingTime);
    }

    public static class LayoutParams extends FrameLayout.LayoutParams {

        public final ShadowInfo shadowInfo = new ShadowInfo();
        private float[] roundRadius;

        public LayoutParams(@NonNull Context c, @Nullable AttributeSet attrs) {
            super(c, attrs);
            TypedArray ta = c.obtainStyledAttributes(attrs, R.styleable.ShadowLayout_Layout);
            shadowInfo.setShadowX(ta.getDimension(R.styleable.ShadowLayout_Layout_layout_shadowX, 0f));
            shadowInfo.setShadowY(ta.getDimension(R.styleable.ShadowLayout_Layout_layout_shadowY, 0f));
            shadowInfo.setShadowRadius(ta.getDimension(R.styleable.ShadowLayout_Layout_layout_shadowRadius, 0f));
            shadowInfo.setShadowColor(ta.getColor(R.styleable.ShadowLayout_Layout_layout_shadowColor, Color.BLACK));
            float round = ta.getDimension(R.styleable.ShadowLayout_Layout_layout_roundRadius, 0f);
            float roundLT = ta.getDimension(R.styleable.ShadowLayout_Layout_layout_roundRadiusLT, round);
            float roundRT = ta.getDimension(R.styleable.ShadowLayout_Layout_layout_roundRadiusRT, round);
            float roundRB = ta.getDimension(R.styleable.ShadowLayout_Layout_layout_roundRadiusRB, round);
            float roundLB = ta.getDimension(R.styleable.ShadowLayout_Layout_layout_roundRadiusLB, round);
            if (roundLT > 0 || roundRT > 0 || roundRB > 0 || roundLB > 0) {
                roundRadius = new float[]{
                        roundLT, roundLT,
                        roundRT, roundRT,
                        roundRB, roundRB,
                        roundLB, roundLB,
                };
            }
            ta.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(int width, int height, int gravity) {
            super(width, height, gravity);
        }

        public LayoutParams(@NonNull ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(@NonNull ViewGroup.MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(@NonNull FrameLayout.LayoutParams source) {
            super(source);
        }

        @Nullable
        public float[] getRoundRadius() {
            return roundRadius;
        }

        public void setRoundRadius(@Nullable float[] roundRadius) {
            if (null != roundRadius && roundRadius.length != 8) {
                throw new IllegalArgumentException("round radius length must be 8");
            }
            this.roundRadius = roundRadius;
        }
    }
}
