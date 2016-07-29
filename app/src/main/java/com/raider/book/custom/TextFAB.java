package com.raider.book.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;

public class TextFAB extends FloatingActionButton {

    private Paint mPaint;
    private int TEXT_SIZE = 60;
    private int mNumber;

    public TextFAB(Context context) {
        this(context, null);
    }

    public TextFAB(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextFAB(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setTextSize(TEXT_SIZE);
        mPaint.setColor(Color.WHITE);
        mPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawText(String.valueOf(mNumber), getWidth() / 2, getHeight() / 2 + TEXT_SIZE / 2, mPaint);
    }

    public void setNumber(int number) {
        mNumber = number;
        invalidate();
    }
}
