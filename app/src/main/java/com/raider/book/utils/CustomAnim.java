package com.raider.book.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.ProgressBar;

public class CustomAnim {

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
        int finalRadius = Math.max(view.getWidth(), view.getHeight());

        Animator animator = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);
        animator.addListener(listenerAdapter);
        animator.start();
    }

    public static void hideWithRippleEffect(View view, AnimatorListenerAdapter listenerAdapter) {
        int cx = (view.getLeft() + view.getRight()) / 2;
        int cy = (view.getTop() + view.getBottom()) / 2;
        int initialRadius = view.getWidth();

        Animator animator = ViewAnimationUtils.createCircularReveal(view, cx, cy, initialRadius, 0);
        animator.addListener(listenerAdapter);
        animator.start();
    }

}
