package com.raider.book.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Region;
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

public class BookView extends View {
    private static final String TAG = "BookView";

    private static final int AUTO_SCROLL_DURATION = 700;

    private Paint mPaint;
/*    private Bitmap redBitmap;
    private Bitmap blueBitmap;
    private Bitmap bgBitmap;*/

    private int turn_page_mode = 0;
    private static final int MODE_BOTTOM = 1;
    private static final int MODE_TOP = 2;
    private static final int REGION_CENTER = 3;

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

    PointF point_bezier_top1 = new PointF();
    PointF point_bezier_top2 = new PointF();

    private Rect mRect;
    //    private PointF point_touch;    //用户触摸点
    private GestureDetector mGestureDetector;
    private Path path_out;
    private Path path_back;
    private PointF o_point = new PointF();
    private PointF f_point = new PointF();
    private PointF center_point = new PointF();
    private PointF a_point = new PointF();
    private PointF d_point = new PointF();
    private PointF b_point = new PointF();
    private PointF h_point = new PointF();
    private double o_position;
    private float mOldX;
    private float mOldY;

    boolean mUpdateStrLines = false;
    //    private AtomicBoolean needAutoTurnPage = new AtomicBoolean(false);
//    private AtomicBoolean isHandTurningPage = new AtomicBoolean(false);
    private double mMaxRectangle;
    private double mCurrentRectangle;
    private float newDeltaX;
    private float newDeltaY;
    private float mStartX;
    private float mStartY;
    private Scroller mScroller;

    public BookView(Context context) {
        this(context, null);
    }

    public BookView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BookView(Context context, AttributeSet attrs, int defStyleAttr) {
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

        if (currentStrLines == null || nextStrLines == null) {
            currentStrLines = getStrLines();
            nextStrLines = getStrLines();
        }

        //绘制当前页的文字
        if (currentStrLines != null) drawCurrentPage(canvas, currentStrLines);

        // 绘制下一页的文字
        if (nextStrLines != null) drawNextPage(canvas, nextStrLines);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean doDraw;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                init_region_and_o_point();
                mStartX = event.getX();
                mStartY = event.getY();
                mOldX = event.getX();
                mOldY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                reset_region_and_o_point(event.getY() - mStartY);
                doDraw = calcCoordinates(event.getX() - mOldX, event.getY() - mOldY, false);
                if (mGestureDetector.onTouchEvent(event) && doDraw) {
                    mOldX = event.getX();
                    mOldY = event.getY();
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                computeAutoTurnPage();
                break;
        }
        return true;
    }

    /**
     * @param delta_x   O相对于自身的x轴偏移
     * @param delta_y   O相对于自身的y轴偏移
     * @param forceDraw 强制绘画
     * @return 是否在可以绘制的范围内
     */
    private boolean calcCoordinates(double delta_x, double delta_y, boolean forceDraw) {
        switch (turn_page_mode) {
            case MODE_TOP:
                f_point.x = mWidth;
                f_point.y = 0;
                break;
            case REGION_CENTER:
                break;
            case MODE_BOTTOM:
                f_point.x = mWidth;
                f_point.y = mHeight;
                break;
        }

        o_point.x = (float) (o_point.x + delta_x);
        o_point.y = (float) (o_point.y + delta_y);

        // center point of OF
        center_point.x = (f_point.x + o_point.x) / 2;
        center_point.y = (f_point.y + o_point.y) / 2;

        switch (turn_page_mode) {
            case MODE_TOP:
                // of中垂线和底线的交点a
                a_point.x = (float) (center_point.x - Math.pow(f_point.y - center_point.y, 2) / (mWidth - center_point.x));
                a_point.y = 0;
                // of中垂线和右边线的交点b
                b_point.x = mWidth;
                b_point.y = (float) (center_point.y + Math.pow(mWidth - center_point.x, 2) / center_point.y);
                break;
            case MODE_BOTTOM:
                // of中垂线和底线的交点a
                a_point.x = (float) (center_point.x - Math.pow(f_point.y - center_point.y, 2) / (mWidth - center_point.x));
                a_point.y = mHeight;
                // of中垂线和右边线的交点b
                b_point.x = mWidth;
                if ((mHeight - center_point.y) == 0) {
//                    b_point.y = -10000;
                } else {
                    b_point.y = (float) (center_point.y - Math.pow(mWidth - center_point.x, 2) / (mHeight - center_point.y));
                }

                Log.d("test", "a_point.x:" + a_point.x);
                Log.d("test", "b_point.y:" + b_point.y);
                break;
        }

        switch (turn_page_mode) {
            case MODE_TOP:
                //下方贝塞尔曲线的终点d
                d_point.x = (float) (a_point.x - Math.sqrt(Math.pow(o_point.x - a_point.x, 2) + Math.pow(o_point.y - a_point.y, 2) + 1));
                d_point.y = 0;
                //右边贝塞尔曲线的顶点h
                h_point.x = mWidth;
                h_point.y = (float) (b_point.y + Math.sqrt(Math.pow(o_point.x - b_point.x, 2) + Math.pow(o_point.y - b_point.y, 2) + 1));
                break;
            case MODE_BOTTOM:
                //下方贝塞尔曲线的终点d
                d_point.x = (float) (a_point.x - Math.sqrt(Math.pow(o_point.x - a_point.x, 2) + Math.pow(o_point.y - a_point.y, 2) + 1));
                d_point.y = mHeight;
                //右边贝塞尔曲线的顶点h
                h_point.x = mWidth;
                h_point.y = (float) (b_point.y - Math.sqrt(Math.pow(o_point.x - b_point.x, 2) + Math.pow(o_point.y - b_point.y, 2) + 1));
                break;
        }

        // 为了拟真
        if (d_point.x < 0 && !forceDraw) {
            d_point.x = 0;
            a_point.x = 2 / mWidth;
            // 计算出新的O点
            o_position = mWidth * Math.cos(Math.atan2(f_point.y - o_point.y, f_point.x - o_point.x)) /
                    Math.sqrt(Math.pow(o_point.x - f_point.x, 2) + Math.pow(o_point.y - f_point.y, 2));
            switch (turn_page_mode) {
                case MODE_TOP:
//                    o_point.x = o_point.x + (1 - o_position) * (f_point.x - o_point.x);
//                    o_point.y = o_point.y - (1 - o_position) * (o_point.y - f_point.y);
                    break;
                case MODE_BOTTOM:
//                    o_point.x = o_point.x + (1 - o_position) * (f_point.x - o_point.x);
//                    o_point.y = o_point.y + (1 - o_position) * (f_point.y - o_point.y);
                    newDeltaX = (float) ((1 - o_position) * (f_point.x - o_point.x));
                    newDeltaY = (float) ((1 - o_position) * (f_point.y - o_point.y));
                    break;
            }
            return calcCoordinates(newDeltaX, newDeltaY, true);
        }

        //贝塞尔曲线的中点
        float t = 0.5f;
        point_bezier_top1.x = (1 - t) * (1 - t) * o_point.x + 2 * (1 - t) * t * a_point.x + t * t * d_point.x;
        point_bezier_top1.y = (1 - t) * (1 - t) * o_point.y + 2 * (1 - t) * t * a_point.y + t * t * d_point.y;
        point_bezier_top2.x = (1 - t) * (1 - t) * o_point.x + 2 * (1 - t) * t * b_point.x + t * t * h_point.x;
        point_bezier_top2.y = (1 - t) * (1 - t) * o_point.y + 2 * (1 - t) * t * b_point.y + t * t * h_point.y;

        path_out = new Path();
        path_back = new Path();

        path_out.moveTo(h_point.x, h_point.y);
        path_out.quadTo(b_point.x, b_point.y, o_point.x, o_point.y);
        path_out.quadTo(a_point.x, a_point.y, d_point.x, d_point.y);
        path_out.lineTo(f_point.x, f_point.y);
        path_out.close();

        path_back.moveTo(h_point.x, h_point.y);
        path_back.quadTo(b_point.x, b_point.y, o_point.x, o_point.y);
        path_back.quadTo(a_point.x, a_point.y, d_point.x, d_point.y);
        path_back.lineTo(point_bezier_top1.x, point_bezier_top1.y);
        path_back.lineTo(point_bezier_top2.x, point_bezier_top2.y);
        path_back.close();

        return true;
    }

    private void computeAutoTurnPage() {
        if (MODE_BOTTOM == turn_page_mode) {
            // 滑动结束后更新strLines
            mUpdateStrLines = true;
            mScroller.startScroll((int) o_point.x, (int) o_point.y
                    , (int) -o_point.x - 300
                    , mHeight - (int) o_point.y, AUTO_SCROLL_DURATION);
            invalidate();
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            calcCoordinates(mScroller.getCurrX() - o_point.x, mScroller.getCurrY() - o_point.y, true);
            invalidate();
        } else if (mUpdateStrLines) {
            mUpdateStrLines = false;
            currentStrLines = nextStrLines;
            nextStrLines = getStrLines();
            invalidate();
        }
    }

    private void drawCurrentPage(Canvas canvas, ArrayList<String> currentStrLines) {
        int save_index = canvas.save();

        // 确定绘制区域：除了path_out的部分
        if (path_out == null) {
            canvas.clipRect(0, 0, mWidth, mHeight);
        } else {
            canvas.clipPath(path_out, Region.Op.XOR);
        }

        int lineIndex = 1;
        for (String lineStr : currentStrLines) {
            canvas.drawText(lineStr, 0, TEXT_SIZE_PX * lineIndex, mPaint);
            canvas.translate(0, LINE_SPACING_PX);
            lineIndex++;
        }
        canvas.restoreToCount(save_index);
    }

    private void drawNextPage(Canvas canvas, ArrayList<String> nextStrLines) {
        // 确定绘制区域：path_out中去除掉path_back的部分
        if (path_back == null) {
            canvas.clipRect(mWidth, 0, mWidth, mHeight);
        } else {
            canvas.clipPath(path_out);
            canvas.clipPath(path_back, Region.Op.DIFFERENCE);
        }

        int lineIndex = 1;
        for (String lineStr : nextStrLines) {
            canvas.drawText(lineStr, 0, TEXT_SIZE_PX * lineIndex, mPaint);
            canvas.translate(0, LINE_SPACING_PX);
            lineIndex++;
        }
    }

    private MappedByteBuffer openFile(String path) {
        Log.d(TAG, "open book in path:" + path);
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

    private ArrayList<String> getStrLines() {
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
//                    Log.d("test", "while2");
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

    private void init_region_and_o_point() {
        turn_page_mode = 0;
        o_point.x = mWidth;
    }

    private void reset_region_and_o_point(float deltaY) {
        if (deltaY <= 0 && turn_page_mode != MODE_BOTTOM) {
            // Note: 注意这里的x不要改变，不然从REGION_BOTTOM和REGION_TOP切换时会有bug
            turn_page_mode = MODE_BOTTOM;
            o_point.y = mHeight;
        }

        if (deltaY > 0 && turn_page_mode != MODE_TOP) {
            turn_page_mode = MODE_TOP;
            o_point.y = 0;
        }
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
