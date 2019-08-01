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
    private FastShadow priChildShadow;

    public ShadowLayout(@NonNull Context context) {
        super(context);
    }

    public ShadowLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ShadowLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (null != priChildShadow) {
            priChildShadow.close();
        }
        priChildShadow = new FastShadow() {
            @Nullable
            @Override
            protected ShadowInfo getShadowInfo(@NonNull ViewGroup parent, @NonNull View child) {
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if (null != lp) {
                    return lp.shadowInfo;
                }
                return null;
            }

            @Nullable
            @Override
            protected Object getChildShadowKey(@NonNull ViewGroup parent, @NonNull View child) {
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if (null != lp) {
                    return lp.shadowKey;
                }
                return null;
            }

            @Override
            public float[] getChildRounds(@NonNull ViewGroup parent, @NonNull View child) {
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if (null != lp) {
                    return lp.roundRadius;
                }
                return null;
            }

            @NonNull
            @Override
            protected Object copyKey(@NonNull Object key) {
                return ((ShadowKey) key).clone();
            }
        };
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (null != priChildShadow) {
            priChildShadow.close();
            priChildShadow = null;
        }
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
            float shadowRadius = lp.spaceShadow ? lp.shadowInfo.getShadowRadius() : 0f;
            int hmar = (int) (lp.leftMargin + lp.rightMargin + shadowRadius * 2);
            int vmar = (int) (lp.topMargin + lp.bottomMargin + shadowRadius * 2);
            int ws = MeasureUtils.makeChildMeasureSpec(cws, lp.width, hmar);
            int hs = MeasureUtils.makeChildMeasureSpec(chs, lp.height, vmar);
            child.measure(ws, hs);
            int width = child.getMeasuredWidth() + hmar;
            int height = child.getMeasuredHeight() + vmar;
            if (width > contentWidth) contentWidth = width;
            if (height > contentHeight) contentHeight = height;
            if (!lp.shadowKey.equals(width, height, lp.shadowInfo, lp.roundRadius)) {
                // 阴影发生更改
                lp.shadowKey.set(width, height, lp.shadowInfo, lp.roundRadius);
            }
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
            float shadowRadius = 0f, shadowX = 0f, shadowY = 0f;
            if (lp.spaceShadow) {
                shadowRadius = lp.shadowInfo.getShadowRadius();
                shadowX = lp.shadowInfo.getShadowX();
                shadowY = lp.shadowInfo.getShadowY();
            }
            int gravity = lp.gravity == Gravity.NO_GRAVITY ? Gravity.LEFT | Gravity.TOP : lp.gravity;
            int width = (int) (child.getMeasuredWidth() + lp.leftMargin +
                    lp.rightMargin + shadowRadius * 2);
            int height = (int) (child.getMeasuredHeight() + lp.topMargin +
                    lp.bottomMargin + shadowRadius * 2);
            Gravity.apply(gravity, width, height, layoutContainer, layoutOut);
            int cl = (int) (layoutOut.left + lp.leftMargin + shadowRadius + shadowX);
            int ct = (int) (layoutOut.top + lp.topMargin + shadowRadius + shadowY);
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

        /**
         * 阴影信息
         */
        public final ShadowInfo shadowInfo = new ShadowInfo();
        /**
         * 是否使用裁剪方式显示阴影，
         * 如果为true，solidColor属性不生效，视图内容部分留空，但在部分机型会出现问题
         * 如果为false，solidColor属性生效，会在视图内容部分填充此颜色
         * 默认为true
         *
         * @deprecated 此属性不再使用
         */
        public boolean clipShadow = true;
        /**
         * 设置阴影是否影响视图位置，默认开启
         */
        public boolean spaceShadow = true;
        /**
         * 圆角
         */
        private float[] roundRadius;
        ShadowKey shadowKey;

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
            shadowInfo.setSolidColor(ta.getColor(R.styleable.ShadowLayout_Layout_layout_solidColor, Color.BLACK));
            clipShadow = ta.getBoolean(R.styleable.ShadowLayout_Layout_layout_clipShadow, true);
            spaceShadow = ta.getBoolean(R.styleable.ShadowLayout_Layout_layout_spaceShadow, true);
            ta.recycle();
            init();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
            init();
        }

        public LayoutParams(int width, int height, int gravity) {
            super(width, height, gravity);
            init();
        }

        public LayoutParams(@NonNull ViewGroup.LayoutParams source) {
            super(source);
            init();
        }

        public LayoutParams(@NonNull ViewGroup.MarginLayoutParams source) {
            super(source);
            init();
        }

        public LayoutParams(@NonNull FrameLayout.LayoutParams source) {
            super(source);
            init();
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

        private void init() {
            shadowKey = new ShadowKey();
            shadowKey.set(0, 0, shadowInfo, roundRadius);
        }
    }

}
