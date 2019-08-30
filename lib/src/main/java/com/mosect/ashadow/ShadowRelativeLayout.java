package com.mosect.ashadow;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * 阴影相对布局，其子视图可以设置阴影
 */
public class ShadowRelativeLayout extends RelativeLayout {

    private FastShadow priChildShadow;

    public ShadowRelativeLayout(Context context) {
        super(context);
    }

    public ShadowRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShadowRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
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
                if (null != lp && lp.shadowInfo.getShadowRadius() > 0) {
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

            @Override
            protected void getChildBounds(@NonNull ViewGroup parent, @NonNull View child, @NonNull RectF out) {
                out.left = child.getLeft();
                out.top = child.getTop();
                out.right = out.left + child.getMeasuredWidth();
                out.bottom = out.top + child.getMeasuredHeight();
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
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        priChildShadow.drawChild(canvas, this, child);
        return super.drawChild(canvas, child, drawingTime);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        return new LayoutParams(lp);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) continue;
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            int width = child.getMeasuredWidth();
            int height = child.getMeasuredHeight();
            if (!lp.shadowKey.equals(width, height, lp.shadowInfo, lp.roundRadius)) {
                // 阴影发生更改
                lp.shadowKey.set(width, height, lp.shadowInfo, lp.roundRadius);
            }
        }
    }

    public static class LayoutParams extends RelativeLayout.LayoutParams {

        /**
         * 阴影信息
         */
        public final ShadowInfo shadowInfo = new ShadowInfo();
        /**
         * 圆角
         */
        private float[] roundRadius;
        ShadowKey shadowKey;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray ta = c.obtainStyledAttributes(attrs, R.styleable.ShadowRelativeLayout_Layout);
            shadowInfo.setShadowX(ta.getDimension(R.styleable.ShadowRelativeLayout_Layout_layout_shadowX, 0f));
            shadowInfo.setShadowY(ta.getDimension(R.styleable.ShadowRelativeLayout_Layout_layout_shadowY, 0f));
            shadowInfo.setShadowRadius(ta.getDimension(R.styleable.ShadowRelativeLayout_Layout_layout_shadowRadius, 0f));
            shadowInfo.setShadowColor(ta.getColor(R.styleable.ShadowRelativeLayout_Layout_layout_shadowColor, Color.BLACK));
            float round = ta.getDimension(R.styleable.ShadowRelativeLayout_Layout_layout_roundRadius, 0f);
            float roundLT = ta.getDimension(R.styleable.ShadowRelativeLayout_Layout_layout_roundRadiusLT, round);
            float roundRT = ta.getDimension(R.styleable.ShadowRelativeLayout_Layout_layout_roundRadiusRT, round);
            float roundRB = ta.getDimension(R.styleable.ShadowRelativeLayout_Layout_layout_roundRadiusRB, round);
            float roundLB = ta.getDimension(R.styleable.ShadowRelativeLayout_Layout_layout_roundRadiusLB, round);
            if (roundLT > 0 || roundRT > 0 || roundRB > 0 || roundLB > 0) {
                roundRadius = new float[]{
                        roundLT, roundLT,
                        roundRT, roundRT,
                        roundRB, roundRB,
                        roundLB, roundLB,
                };
            }
            shadowInfo.setSolidColor(ta.getColor(R.styleable.ShadowRelativeLayout_Layout_layout_solidColor, Color.BLACK));
            ta.recycle();
            init();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
            init();
        }

        public LayoutParams(ViewGroup.LayoutParams p) {
            super(p);
            init();
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
            init();
        }

        public LayoutParams(RelativeLayout.LayoutParams source) {
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
