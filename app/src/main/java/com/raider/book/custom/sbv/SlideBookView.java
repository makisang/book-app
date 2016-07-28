package com.raider.book.custom.sbv;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Scroller;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

public class SlideBookView extends android.view.View implements SlideContract.View {
    private static final String TAG = "BookView";

    private static final int AUTO_SCROLL_DURATION = 500;
    private static final int TEXT_SIZE_PX = 48;
    private static final int LINE_SPACING_PX = 10;

    private Paint mPaint;
    private int mWidth;
    private int mHeight;
    private long maxLen;
    private int maxLineSize;
    private int leftPadding = 20;
    private int rightPadding = 20;
    private int topPadding = 20;
    private int bottomPadding = 20;

    private SlidePresenter mPresenter;
    private MappedByteBuffer mappedByteBuffer;
    private int prevPageSize;
    private int nextPageSize;
    private ArrayList<String> prevStrLines;
    private ArrayList<String> currentStrLines;
    private ArrayList<String> nextStrLines;
    HashMap<String, Integer> indexMap = new HashMap<>();

    private Rect mRect;
    float dx;
    float oldX;
    private float mDeltaX;
    private boolean inScroll;
    private Scroller mScroller;
    private boolean mPageChanged;
    private GestureDetector mGestureDetector;

    public SlideBookView(Context context) {
        this(context, null);
    }

    public SlideBookView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideBookView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setBook(String path) {
        if (mappedByteBuffer == null) {
            File file = new File(path);
            maxLen = file.length();
            mPresenter.loadBook(path);
        }
    }

    public void setBook(File file) {

    }

    private void init() {
        indexMap.put("current_start", 0);

        mScroller = new Scroller(getContext().getApplicationContext());

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextAlign(Paint.Align.LEFT);
        mPaint.setTextSize(TEXT_SIZE_PX);
        mPaint.setColor(Color.BLACK);
        mPaint.setLetterSpacing(0.05f);

        mGestureDetector = new GestureDetector(getContext().getApplicationContext(), new MyGestureListener());
        mRect = new Rect();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        maxLineSize = (mHeight - TEXT_SIZE_PX - topPadding - bottomPadding) / (TEXT_SIZE_PX + LINE_SPACING_PX) + 1;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // book path not set yet
        if (mappedByteBuffer == null) return;

        canvas.getClipBounds(mRect);
        canvas.translate(leftPadding, topPadding);
        canvas.getClipBounds(mRect);

        if (mDeltaX <= 0) {
            //绘制当前页的文字
            if (currentStrLines != null) drawCurrentPage(canvas);
            // 绘制下一页的文字
            if (nextStrLines != null) drawNextPage(canvas);
        } else {
            // 让前一页的文字覆盖上来
            if (prevStrLines != null) {
                drawPrevPage(canvas);
            }
            //绘制当前页的文字
            if (currentStrLines != null) drawCurrentPage(canvas);
        }

        // 翻页了，更新数据
        if (mPageChanged) {
            updateStrLines(mDeltaX <= 0);
            mPageChanged = false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                oldX = event.getX();

                if (inScroll) {
                    mScroller.forceFinished(true);
                } else {
                    mDeltaX = 0;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                dx = event.getX() - oldX;
                oldX = event.getX();
                if (dx != 0) {
                    mDeltaX += dx;
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                slideScroll(mDeltaX <= 0);
                break;
        }
        return true;
    }

    private void slideScroll(boolean slideForward) {
        inScroll = true;
        if (slideForward) {
            mScroller.startScroll((int) mDeltaX, 0, (int) -(mWidth + mDeltaX), 0, AUTO_SCROLL_DURATION);
        } else {
            mScroller.startScroll((int) mDeltaX, 0, (int) (mWidth - mDeltaX), 0, AUTO_SCROLL_DURATION);
        }
        invalidate();
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            mDeltaX = mScroller.getCurrX();
            invalidate();
        } else if (inScroll) {
            inScroll = false;
            mPageChanged = true;
        }
    }

    private void drawPrevPage(Canvas canvas) {
        canvas.save();
        canvas.clipRect(0, 0, mDeltaX, mHeight);
        canvas.translate(mDeltaX - mWidth, 0);

        int lineIndex = 1;
        for (String lineStr : prevStrLines) {
            canvas.drawText(lineStr, 0, TEXT_SIZE_PX * lineIndex, mPaint);
            canvas.translate(0, LINE_SPACING_PX);
            lineIndex++;
        }
        canvas.restore();
    }

    private void drawCurrentPage(Canvas canvas) {
        canvas.save();
        if (mDeltaX < 0) {
            canvas.clipRect(0, 0, mWidth + mDeltaX, mHeight);
            canvas.translate(mDeltaX, 0);
        } else {
            canvas.clipRect(mDeltaX, 0, mWidth, mHeight);
        }

        int lineIndex = 1;
        for (String lineStr : currentStrLines) {
            canvas.drawText(lineStr, 0, TEXT_SIZE_PX * lineIndex, mPaint);
            canvas.translate(0, LINE_SPACING_PX);
            lineIndex++;
        }
        canvas.restore();
    }

    private void drawNextPage(Canvas canvas) {
        canvas.save();
        canvas.clipRect(mWidth + mDeltaX, 0, mWidth, mHeight);

        int lineIndex = 1;
        for (String lineStr : nextStrLines) {
            canvas.drawText(lineStr, 0, TEXT_SIZE_PX * lineIndex, mPaint);
            canvas.translate(0, LINE_SPACING_PX);
            lineIndex++;
        }
        canvas.restore();
    }

    @Override
    public void setMBB(MappedByteBuffer mbb) {
        this.mappedByteBuffer = mbb;
        prevStrLines = getPrevPage();
        currentStrLines = getCurrentPage();
        nextStrLines = getNextPage();
        invalidate();
    }

    private void updateStrLines(boolean updateForward) {
        Log.d("test", "update");
        if (updateForward) {
            indexMap.put("current_start", indexMap.get("current_end") + 1);
            indexMap.put("current_end", indexMap.get("current_end") + nextPageSize);

            prevStrLines = currentStrLines;
            currentStrLines = nextStrLines;
            nextStrLines = getNextPage();
        } else {
            indexMap.put("current_end", indexMap.get("current_start") - 1);
            indexMap.put("current_start", indexMap.get("current_start") - prevPageSize);

            nextStrLines = currentStrLines;
            currentStrLines = prevStrLines;
            prevStrLines = getPrevPage();
        }
    }

    private ArrayList<String> getPrevPage() {
        ArrayList<String> strLines = new ArrayList<>();
        int end = indexMap.get("current_start");
        String str_paragraph;
        int length;
        int measuredSize;

        while (strLines.size() < maxLineSize) {
            byte[] bytes = readPrevParagraph(end);
            try {
                str_paragraph = new String(bytes, "GBK");

                length = str_paragraph.length();
                measuredSize = mPaint.breakText(str_paragraph, false, mWidth - 20, null);
                while (measuredSize <= length) {
                    if (strLines.size() == maxLineSize) break;
                    String newLine = str_paragraph.substring(str_paragraph.length() - measuredSize);
                    end -= newLine.getBytes("GBK").length;
                    strLines.add(0, newLine);

                    if (measuredSize == length) break;
                    str_paragraph = str_paragraph.substring(0, str_paragraph.length() - measuredSize);
                    length = str_paragraph.length();
                    measuredSize = mPaint.breakText(str_paragraph, false, mWidth - 20, null);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        prevPageSize = indexMap.get("current_start") - end;
        return strLines;
    }

    private ArrayList<String> getCurrentPage() {
        ArrayList<String> strLines = new ArrayList<>();
        int end = indexMap.get("current_start");
        String str_paragraph;
        int length;
        int measuredSize;
        while (strLines.size() < maxLineSize) {
            byte[] bytes = readNextParagraph(end);
            try {
                str_paragraph = new String(bytes, "GBK");

                length = str_paragraph.length();
                measuredSize = mPaint.breakText(str_paragraph, true, mWidth - 20, null);
                // 当集合包含的行数没有超出限制时，将当前段落中的内容分为一行行的字符串加到集合中
                while (measuredSize <= length) {
                    if (strLines.size() >= maxLineSize) break;
                    end += str_paragraph.substring(0, measuredSize).getBytes("GBK").length;
                    strLines.add(str_paragraph.substring(0, measuredSize));

                    if (measuredSize == length) break;
                    str_paragraph = str_paragraph.substring(measuredSize);
                    measuredSize = mPaint.breakText(str_paragraph, true, mWidth - leftPadding - rightPadding, null);
                    length = str_paragraph.length();
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        indexMap.put("current_end", end);
        return strLines;
    }

    private ArrayList<String> getNextPage() {
        Log.d("test", "getNext");
        Log.d("test", "changed:" + mPageChanged);
        ArrayList<String> strLines = new ArrayList<>();
        int start = indexMap.get("current_end");
        String str_paragraph;
        int length;
        int measuredSize;
        while (strLines.size() < maxLineSize) {
            byte[] bytes = readNextParagraph(start);
            try {
                str_paragraph = new String(bytes, "GBK");

                length = str_paragraph.length();
                measuredSize = mPaint.breakText(str_paragraph, true, mWidth - 20, null);
                // 当集合包含的行数没有超出限制时，将当前段落中的内容分为一行行的字符串加到集合中
                while (measuredSize <= length) {
                    if (strLines.size() >= maxLineSize) break;
                    start += str_paragraph.substring(0, measuredSize).getBytes("GBK").length;
                    strLines.add(str_paragraph.substring(0, measuredSize));

                    if (measuredSize == length) break;
                    str_paragraph = str_paragraph.substring(measuredSize);
                    measuredSize = mPaint.breakText(str_paragraph, true, mWidth - leftPadding - rightPadding, null);
                    length = str_paragraph.length();
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        nextPageSize = start - indexMap.get("current_end");
        return strLines;
    }

    /**
     * 获取下一段文字，以0x0a为结束标记
     *
     * @param start 段落起始位置在mappedByteBuffer中的索引
     * @return 段落字符串的byte数组
     */
    private byte[] readNextParagraph(int start) {
        int end = start;

        // 判断段落结束标记
        for (int i = 0; i < maxLen - start; i++) {
            if (0x0a == mappedByteBuffer.get(start + i)) {
                // Note: 这里需要跳过这个结束标记，不然跳出之后再读下一段还是读到的这个标记
                end += i + 1;
                break;
            }
        }

        mappedByteBuffer.mark();
        mappedByteBuffer.position(start);
        byte[] bytes = new byte[end - start];
        mappedByteBuffer.get(bytes, 0, end - start);
        mappedByteBuffer.reset();   // 将mappedByteBuffer还原到mark的位置，即position重新变为0

        return bytes;
    }

    private byte[] readPrevParagraph(int end) {
        int start = end;

        for (int i = 0; i < end; i++) {
            if (0x0a == mappedByteBuffer.get(end - i)) {
                start -= i + 1;
                break;
            }
        }

        mappedByteBuffer.mark();
        mappedByteBuffer.position(start);
        byte[] bytes = new byte[end - start];
        mappedByteBuffer.get(bytes, 0, end - start);
        mappedByteBuffer.reset();

        return bytes;
    }

    @Override
    public void _setPresenter(SlidePresenter presenter) {
        mPresenter = presenter;
    }

    class MyGestureListener implements GestureDetector.OnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    }

}
