package com.raider.book.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;

public class SlideBookView extends View {
    private static final String TAG = "BookView";

    private static final int AUTO_SCROLL_DURATION = 500;

    private Paint mPaint;

    private int leftPadding = 20;
    private int rightPadding = 20;
    private int topPadding = 20;
    private int bottomPadding = 20;

    private MappedByteBuffer mappedByteBuffer;
    private ArrayList<String> currentStrLines;
    private ArrayList<String> nextStrLines;

    private static final int TEXT_SIZE_PX = 48;
    private static final int LINE_SPACING_PX = 10;
    private int mWidth;
    private int mHeight;
    private long maxLen;
    private int maxLineSize;
    // 段落的开始在整体文件中的index
    private int mStart = 0;
    HashMap<String, Integer> indexMap = new HashMap<>();

    private Rect mRect;
    private GestureDetector mGestureDetector;

    private float mDeltaX;
    float oldX;
    float dx;

    //    private float mStartX;
    private Scroller mScroller;
    private boolean mPageChanged;
    private boolean inScroll;


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
            mappedByteBuffer = openFile(path);
        }
        invalidate();
    }

    public void setBook(File file) {

    }

    private void init() {
        /** 向后读取数据流时的起始位置 **/
        indexMap.put("last", 0);
        /** 当前页起始点的的数据流索引 **/
        indexMap.put("current", 0);
        /** 上一页起始点的的数据流索引 **/
        indexMap.put("previous", 0);
        /** 下一页起始点的的数据流索引 **/
        indexMap.put("next", 0);

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
        Log.d("test", "onDraw");
        // book path not set yet
        if (mappedByteBuffer == null) return;

        canvas.getClipBounds(mRect);
        canvas.translate(leftPadding, topPadding);
        canvas.getClipBounds(mRect);

        if (currentStrLines == null || nextStrLines == null) {
            currentStrLines = getNextPageText();
            nextStrLines = getNextPageText();
        }

        if (mDeltaX <= 0) {
            //绘制当前页的文字
            if (currentStrLines != null) drawCurrentPage(canvas, currentStrLines);
            // 绘制下一页的文字
            if (nextStrLines != null) drawNextPage(canvas, nextStrLines);
        } else {
            // 让前一页的文字覆盖上来

            //绘制当前页的文字

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                oldX = event.getX();
                // 翻页了，更新数据
                if (mPageChanged) {
                    mPageChanged = false;
                    currentStrLines = nextStrLines;
                    nextStrLines = getNextPageText();
                }

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
                slideByScroll();
                break;
        }
        return true;
    }

    private void slideByScroll() {
        inScroll = true;
        mScroller.startScroll((int) mDeltaX, 0, (int) -(mWidth + mDeltaX), 0, AUTO_SCROLL_DURATION);
        invalidate();
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            Log.d("test", "scroll");
            mDeltaX = mScroller.getCurrX();
            invalidate();
        } else if (inScroll) {
            Log.d("test", "end");
            inScroll = false;
            mPageChanged = true;
        }
    }

    private void drawCurrentPage(Canvas canvas, ArrayList<String> currentStrLines) {
        canvas.save();
        canvas.clipRect(0, 0, mWidth + mDeltaX, mHeight);
        canvas.translate(mDeltaX, 0);

        int lineIndex = 1;
        for (String lineStr : currentStrLines) {
            canvas.drawText(lineStr, 0, TEXT_SIZE_PX * lineIndex, mPaint);
            canvas.translate(0, LINE_SPACING_PX);
            lineIndex++;
        }
        canvas.restore();
    }

    private void drawNextPage(Canvas canvas, ArrayList<String> nextStrLines) {
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

    private MappedByteBuffer openFile(String path) {
        File file = new File(path);
        maxLen = file.length();
        MappedByteBuffer mbb = null;
        try {
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            // TODO expensive
            mbb = raf.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mbb;
    }

    private ArrayList<String> getNextPageText() {
        ArrayList<String> strLines = new ArrayList<>();
        String str_paragraph;
        int length;
        int i;
        while (strLines.size() < maxLineSize) {
            byte[] bytes = readNextParagraph(mStart);
            try {
                str_paragraph = new String(bytes, "GBK");

                length = str_paragraph.length();
                i = mPaint.breakText(str_paragraph, true, mWidth - 20, null);

                // 当集合包含的行数没有超出限制时，将当前段落中的内容分为一行行的字符串加到集合中
                while (i <= length) {
                    if (strLines.size() >= maxLineSize)
                        break;
                    // 更新start位置
                    mStart += str_paragraph.substring(0, i).getBytes("GBK").length;
                    strLines.add(str_paragraph.substring(0, i));
                    if (i == length)
                        break;
                    str_paragraph = str_paragraph.substring(i);
                    i = mPaint.breakText(str_paragraph, true, mWidth - leftPadding - rightPadding, null);
                    length = str_paragraph.length();
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

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
