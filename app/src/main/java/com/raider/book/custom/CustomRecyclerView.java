package com.raider.book.custom;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.raider.book.utils.CustomViewUtils;

public class CustomRecyclerView extends RecyclerView {
    private static final int SCALE_CHANGE_DURATION = 500;

    private AppBarLayout appBarLayout;
    float downY;
    private int ablExtendedHeight;
    private float scaleFactor;
    private ViewSet mViewSet;
    private ObjectAnimator mScaleChange;
    private AnimatorSet animatorSet;
    private float anchorY;

    public CustomRecyclerView(Context context) {
        this(context, null);
    }

    public CustomRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        scaleFactor = 1;
        animatorSet = new AnimatorSet();
        animatorSet.setDuration(SCALE_CHANGE_DURATION);
    }

    public void setAppBarLayout(AppBarLayout appBarLayout, int ablExtendedHeightHeight) {
        this.appBarLayout = appBarLayout;
        this.ablExtendedHeight = ablExtendedHeightHeight;
        mViewSet = new ViewSet(appBarLayout, this, ablExtendedHeightHeight);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (animatorSet.isRunning())
                    return false;
                downY = e.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (getTop() == ablExtendedHeight && anchorY == 0) {
                    anchorY = e.getY();
                }
                if ((getTop() == ablExtendedHeight && e.getY() - anchorY > 0)) {
                    scaleFactor = 1 + (e.getY() - anchorY) / ablExtendedHeight;
                    appBarLayout.setScaleX(scaleFactor);
                    appBarLayout.setScaleY(scaleFactor);
                    setPadding(0, (int) (e.getY() - anchorY) / 2, 0, 0);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (getPaddingTop() == 0)
                    return super.onTouchEvent(e);
                mScaleChange = ObjectAnimator.ofFloat(mViewSet, CustomViewUtils.SCALE_HEADER, scaleFactor, 1);
                animatorSet.play(mScaleChange);
                animatorSet.start();
                // Initialize state.
                anchorY = 0;
                scaleFactor = 1;
                break;
        }
        return super.onTouchEvent(e);
    }

    public static class ViewSet {
        public AppBarLayout appBarLayout;
        public RecyclerView recyclerView;
        public float ablExtendedHeight;

        public ViewSet(AppBarLayout appBarLayout, RecyclerView recyclerView, float ablExtendedHeight) {
            this.appBarLayout = appBarLayout;
            this.recyclerView = recyclerView;
            this.ablExtendedHeight = ablExtendedHeight;
        }

        public float getPadding() {
            return recyclerView.getPaddingTop();
        }

        public float getScaleFactor() {
            return 1 + getPadding() * 2 / ablExtendedHeight;
        }
    }

}
