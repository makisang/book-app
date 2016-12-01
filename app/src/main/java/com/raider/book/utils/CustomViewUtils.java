package com.raider.book.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.util.Property;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.raider.book.custom.CustomRecyclerView;

public class CustomViewUtils {

    public static void showProgress(Context context, final View view, ProgressBar progressBar) {
        int shortAnimTime = context.getResources().getInteger(android.R.integer.config_shortAnimTime);
        progressBar.setAlpha(0f);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.animate().alpha(1f).setDuration(shortAnimTime).setListener(null);

        view.animate().alpha(0f).setDuration(shortAnimTime).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setVisibility(View.GONE);
            }
        });
    }

    public static void hideProgress(Context context, final View view, final ProgressBar progressBar) {
        int shortAnimTime = context.getResources().getInteger(android.R.integer.config_shortAnimTime);
        view.setAlpha(0f);
        view.setVisibility(View.VISIBLE);
        view.animate().alpha(1f).setDuration(shortAnimTime).setListener(null);

        progressBar.animate().alpha(0f).setDuration(shortAnimTime).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public static void showWithRippleEffect(View view, AnimatorListenerAdapter listenerAdapter) {
        int cx = (view.getLeft() + view.getRight()) / 2;
        int cy = (view.getTop() + view.getBottom()) / 2;
        int endRadius = Math.max(view.getWidth(), view.getHeight());

        Animator animator = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, endRadius);
        animator.setInterpolator(new FastOutLinearInInterpolator());
        animator.addListener(listenerAdapter);
        animator.start();
    }

    public static void hideWithRippleEffect(View view, AnimatorListenerAdapter listenerAdapter) {
        int cx = (view.getLeft() + view.getRight()) / 2;
        int cy = (view.getTop() + view.getBottom()) / 2;
        int initialRadius = view.getWidth();

        Animator animator = ViewAnimationUtils.createCircularReveal(view, cx, cy, initialRadius, 0);
        animator.setInterpolator(new FastOutLinearInInterpolator());
        animator.addListener(listenerAdapter);
        animator.start();
    }

    public static Property<FrameLayout, Integer> FOREGROUND_COLOR
            = new Property<FrameLayout, Integer>(Integer.class, "foregroundColor") {

        @Override
        public void set(FrameLayout frameLayout, Integer value) {
//            super.set(frameLayout, value);
            if (frameLayout.getForeground() instanceof ColorDrawable) {
                ((ColorDrawable) frameLayout.getForeground().mutate()).setColor(value);
            } else {
                frameLayout.setForeground(new ColorDrawable(value));
            }
        }

        @Override
        public Integer get(FrameLayout frameLayout) {
            if (frameLayout.getForeground() instanceof ColorDrawable) {
                return ((ColorDrawable) frameLayout.getForeground()).getColor();
            } else {
                return Color.TRANSPARENT;
            }
        }
    };

    /**
     * Reset scale of header image in CollapsingToolbarLayout,
     * along with padding of RecyclerView.
     */
    public static Property<CustomRecyclerView.ViewSet, Float> SCALE_HEADER
            = new Property<CustomRecyclerView.ViewSet, Float>(Float.class, "scaleHeader") {

        @Override
        public void set(CustomRecyclerView.ViewSet viewSet, Float value) {
//            super.set(myLayout, value);
            viewSet.appBarLayout.setScaleX(value);
            viewSet.appBarLayout.setScaleY(value);
            viewSet.recyclerView.setPadding(0, (int) ((value - 1) * viewSet.ablExtendedHeight / 2), 0, 0);
        }

        @Override
        public Float get(CustomRecyclerView.ViewSet viewSet) {
            return viewSet.getScaleFactor();
        }
    };

}
