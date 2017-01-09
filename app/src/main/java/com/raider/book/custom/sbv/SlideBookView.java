package com.raider.book.custom.sbv;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Scroller;

import com.raider.book.dao.Book;
import com.raider.book.dao.LocalBook;
import com.raider.book.dao.NetBook;
import com.raider.book.interf.BookLoadListener;
import com.raider.book.utils.BookDBOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.raider.book.contract.RaiderDBContract.ShelfReader.COLUMN_END;
import static com.raider.book.contract.RaiderDBContract.ShelfReader.COLUMN_PATH;
import static com.raider.book.contract.RaiderDBContract.ShelfReader.COLUMN_START;
import static com.raider.book.contract.RaiderDBContract.ShelfReader.COLUMN_TITLE;
import static com.raider.book.contract.RaiderDBContract.ShelfReader.TABLE_NAME;
import static com.raider.book.contract.URLCollection.QING_STOR_FILE_DIC;

public class SlideBookView extends android.view.View {
    private static final String TAG = "BookView";
    private static final String CHARSET_GBK = "GBK";
    private static final String CURRENT_START = "start";
    private static final String CURRENT_END = "end";

    private static final int AUTO_SCROLL_DURATION = 500;
    private static int TEXT_SIZE_PX;
    private static final int LINE_SPACING_PX = 10;

    private Book mBook;
    private Paint mPaint;
    private int mWidth;
    private int mHeight;
    private long maxLen;
    private int maxLineSize;
    private int leftPadding = 20;
    private int rightPadding = 20;
    private int topPadding = 20;
    private int bottomPadding = 20;

    private String mStoragePath;
    private MappedByteBuffer mappedByteBuffer;
    private int prevPageSize;
    private int nextPageSize;
    private ArrayList<String> prevStrLines;
    private ArrayList<String> currentStrLines;
    private ArrayList<String> nextStrLines;
    HashMap<String, Integer> indexMap = new HashMap<>();

    private Rect mRect;
    private float mDeltaX;
    private Scroller mScroller;
    private boolean aInScroll;
    private boolean aPageChanged;
    private boolean aForward;
    private boolean aSlideRecovery;
    private GestureDetector mGestureDetector;
    /**
     * Book Load Listener.
     */
    private BookLoadListener mBookLoadListener;


    public SlideBookView(Context context, Book book, AttributeSet attrs) {
        this(context, attrs);
        mBook = book;
        init();
    }

    public SlideBookView(Context context) {
        this(context, null);
    }

    public SlideBookView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideBookView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        mScroller = new Scroller(getContext().getApplicationContext());

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextAlign(Paint.Align.LEFT);
        mPaint.setColor(Color.BLACK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mPaint.setLetterSpacing(0.05f);
        }

        mGestureDetector = new GestureDetector(getContext().getApplicationContext(), new MyGestureListener());
        mRect = new Rect();

        mWidth = getResources().getDisplayMetrics().widthPixels - leftPadding - rightPadding - 40;
        mHeight = getResources().getDisplayMetrics().heightPixels;
        TEXT_SIZE_PX = (mWidth - leftPadding - rightPadding) / 20;
        mPaint.setTextSize(TEXT_SIZE_PX);
        maxLineSize = (mHeight - topPadding - bottomPadding - TEXT_SIZE_PX - LINE_SPACING_PX) / (TEXT_SIZE_PX + LINE_SPACING_PX);

        // Load book data requires maxLineSize.
        if (mappedByteBuffer == null) {
            loadBook();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        mWidth = MeasureSpec.getSize(widthMeasureSpec);
//        mHeight = MeasureSpec.getSize(heightMeasureSpec);
//        Log.d("test", "onMeasure: " + mWidth + ", " + mHeight);

    }

    public void addLoadListener(BookLoadListener bookLoadListener) {
        mBookLoadListener = bookLoadListener;
    }

    private void loadBook() {
        // Get book file path.
        Func1<Book, String> func1 = new Func1<Book, String>() {
            @Override
            public String call(Book book) {
                if (book instanceof LocalBook) {
                    return book.path;
                } else if (book instanceof NetBook) {
                    // Check whether already downloaded.
                    String storagePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + mBook.title + ".txt";
                    File file = new File(storagePath);
                    if (file.exists()) {
                        return storagePath;
                    }
                    return downloadBook(book.path, storagePath);
                }
                return null;
            }
        };

        // Query db and init mappedByteBuffer.
        Func1<String, Boolean> func2 = new Func1<String, Boolean>() {
            @Override
            public Boolean call(String path) {
                if (path == null) return false;
                // Query last read history.
                BookDBOpenHelper dbOpenHelper = new BookDBOpenHelper(getContext());
                SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
                Cursor cursor = database.query(TABLE_NAME, new String[]{COLUMN_START, COLUMN_END}, COLUMN_PATH + "=?", new String[]{path}, null, null, null);
                if (cursor.getCount() == 0) {
                    // No book data in db, insert one.
                    ContentValues values = new ContentValues();
                    values.put(COLUMN_TITLE, mBook.title);
                    values.put(COLUMN_PATH, path);
                    database.insert(TABLE_NAME, null, values);
                    cursor.close();
                    return initMBB(path, 0, 0);
                }
                int startPoint = 0;
                int endPoint = 0;
                if (cursor.moveToFirst()) {
                    startPoint = cursor.getInt(cursor.getColumnIndex(COLUMN_START));
                    endPoint = cursor.getInt(cursor.getColumnIndex(COLUMN_END));
                }
                cursor.close();
                return initMBB(path, startPoint, endPoint);
            }
        };

        // Interface callback.
        Action1<Boolean> action1 = new Action1<Boolean>() {
            @Override
            public void call(Boolean initSuccess) {
                mBookLoadListener.onLoadCompleted(initSuccess);
            }
        };

        Observable.just(mBook)
                .map(func1)
                .map(func2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action1);
    }

    /**
     * Download book from server.
     */
    private String downloadBook(String url, final String storagePath) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        Request request = new Request.Builder()
                .get()
                .url(QING_STOR_FILE_DIC + mBook.title + ".txt")
                .build();
        Call call = okHttpClient.newCall(request);
        try {
            Response response = call.execute();
            InputStream inputStream = response.body().byteStream();

            boolean fileExists = true;
            File file = new File(storagePath);
            if (!file.exists()) {
                fileExists = file.createNewFile();
            }
            if (fileExists) {
                FileOutputStream fos = new FileOutputStream(file);
                byte[] bytes = new byte[1024];
                int len;
                while ((len = inputStream.read(bytes)) != -1) {
                    fos.write(bytes, 0, len);
                }
                inputStream.close();
                fos.close();
            }
            // Notify MediaStore.
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file));
            getContext().sendBroadcast(intent);

            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean initMBB(String path, int startPoint, int endPoint) {
        File file = new File(path);
        try {
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            // Get file size.
            maxLen = raf.length();
            // TODO expensive
            mappedByteBuffer = raf.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            indexMap.put(CURRENT_START, startPoint);
            indexMap.put(CURRENT_END, endPoint);
            prevStrLines = getPrevPage();
            currentStrLines = getFirstPage(startPoint);
            nextStrLines = getNextPage();
            raf.close();
            postInvalidate();
            mStoragePath = path;
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mScroller.forceFinished(true);
        mScroller = null;
        mappedByteBuffer = null;
        prevStrLines = null;
        currentStrLines = null;
        nextStrLines = null;

        // Save read history in db.
        new Thread(new Runnable() {
            @Override
            public void run() {
                BookDBOpenHelper dbOpenHelper = new BookDBOpenHelper(getContext());
                SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(COLUMN_START, indexMap.get(CURRENT_START));
                values.put(COLUMN_END, indexMap.get(CURRENT_END));
                int update = database.update(TABLE_NAME, values, COLUMN_PATH + "=?", new String[]{mStoragePath});
                Log.d("test", "update number: " + update);
            }
        }).start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Book path not set.
        if (mappedByteBuffer == null) return;

        // After slideScroll(), update 3 strLines.
        if (aPageChanged) {
            updateStrLines(aForward);
            aPageChanged = false;
        }

        canvas.getClipBounds(mRect);
        canvas.translate(leftPadding, topPadding);
        canvas.getClipBounds(mRect);
        if (mDeltaX <= 0) {
            if (currentStrLines != null) drawCurrentPage(canvas);
            if (nextStrLines != null) drawNextPage(canvas);
        } else {
            if (prevStrLines != null) {
                drawPrevPage(canvas);
            }
            if (currentStrLines != null) drawCurrentPage(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                return mGestureDetector.onTouchEvent(event);
            case MotionEvent.ACTION_UP:
                // OnSingleTapUp and OnFling.
                if (mGestureDetector.onTouchEvent(event))
                    return true;
                // Situation: scroll first, then pause for a while and then lift finger,
                // OnSingleTapUp and OnFling will not catch this ACTION_UP.
                // In this situation, mDeltaX = 0 means user tries to slide back in first page,
                // so aForward is not mDeltaX <= 0.
                aForward = mDeltaX < 0;
                // Can't slide back in first page.
                if (!aForward && prevStrLines == null)
                    return true;
                slideScroll(aForward);
        }
        return true;
    }

    class MyGestureListener implements GestureDetector.OnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            if (aInScroll) {
                return false;
            }
            mDeltaX = 0;
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            // Can't slide back in first page.
            if (e.getX() < mWidth / 2 && prevStrLines == null)
                return true;
            // Use Scroller, slide left or right according to the tap position.
            aForward = e.getX() > mWidth / 2;
            slideScroll(aForward);
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            // Can't slide back in first page.
            if (e2.getX() - e1.getX() > 0 && prevStrLines == null)
                return true;

            mDeltaX = e2.getX() - e1.getX();
            invalidate();
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if ((e2.getX() - e1.getX() > 0 ^ velocityX > 0)) {
                slideRecovery();
                return true;
            }
            // Can't slide back in first page.
            if (e2.getX() - e1.getX() > 0 && prevStrLines == null)
                return true;
            aForward = e2.getX() - e1.getX() < 0;
            slideScroll(aForward);
            return true;
        }
    }

    private void slideScroll(boolean slideForward) {
        aInScroll = true;
        if (slideForward) {
            mScroller.startScroll((int) mDeltaX, 0, (int) -(mWidth + mDeltaX), 0, AUTO_SCROLL_DURATION);
        } else {
            mScroller.startScroll((int) mDeltaX, 0, (int) (mWidth - mDeltaX), 0, AUTO_SCROLL_DURATION);
        }
        invalidate();
    }

    private void slideRecovery() {
        aSlideRecovery = true;
        aInScroll = true;
        mScroller.startScroll((int) mDeltaX, 0, (int) (-mDeltaX), 0, AUTO_SCROLL_DURATION);
        invalidate();
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            mDeltaX = mScroller.getCurrX();
            invalidate();
        } else if (aInScroll) {
            aInScroll = false;
            mDeltaX = 0;
            // If the scroll is for recover to current page,
            // then don't update strLines.
            aPageChanged = !aSlideRecovery;
            aSlideRecovery = false;
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

    private void updateStrLines(boolean updateForward) {
        int currentStart = indexMap.get(CURRENT_START);
        int currentEnd = indexMap.get(CURRENT_END);
        int currentPageSize = currentEnd - currentStart + 1;

        if (updateForward) {
            prevPageSize = currentPageSize;

            indexMap.put(CURRENT_START, currentEnd + 1);
            indexMap.put(CURRENT_END, currentEnd + nextPageSize);

            prevStrLines = currentStrLines;
            currentStrLines = nextStrLines;
            nextStrLines = getNextPage();
        } else {
            nextPageSize = currentPageSize;

            indexMap.put(CURRENT_START, currentStart - prevPageSize);
            indexMap.put(CURRENT_END, currentStart - 1);

            nextStrLines = currentStrLines;
            currentStrLines = prevStrLines;
            prevStrLines = getPrevPage();
        }
    }

    private ArrayList<String> getPrevPage() {
        int end = indexMap.get(CURRENT_START) - 1;
        int pageSize = 0;

        if (end <= 0) return null;

        ArrayList<String> strLines = new ArrayList<>();
        ArrayList<String> tempLines = new ArrayList<>();
        ArrayList<String> currentLines = new ArrayList<>();
        String str_paragraph;
        int length;
        int measuredSize;
        try {
            while (tempLines.size() < maxLineSize) {
                // No more data to read forward.
                if (end <= 0) break;

                currentLines.clear();
                byte[] bytes = readPrevParagraph(end);
                str_paragraph = new String(bytes, CHARSET_GBK);
                length = str_paragraph.length();

                measuredSize = mPaint.breakText(str_paragraph, true, mWidth, null);
                while (measuredSize <= length) {
                    String measuredStr = str_paragraph.substring(0, measuredSize);
                    byte[] measuredBytes = measuredStr.getBytes(CHARSET_GBK);

                    // In break process, '\r\n' may be broken into '\r' in the end of one line,
                    // and '\n' in the next line, so deal with this situation.
                    // Ignore single '\n'.
                    if (!(measuredBytes.length == 1 && 0x0a == measuredBytes[0])) {
                        // If ends with '\r', add '\n' in the end.
                        if (0x0d == measuredBytes[measuredBytes.length - 1]) {
                            measuredStr = measuredStr + new String(new byte[]{0x0a}, CHARSET_GBK);
                        }
                        end -= measuredStr.getBytes(CHARSET_GBK).length;
                        currentLines.add(measuredStr);
                    }

                    if (measuredSize == length) break;
                    str_paragraph = str_paragraph.substring(measuredSize);
                    length = str_paragraph.length();
                    measuredSize = mPaint.breakText(str_paragraph, true, mWidth, null);
                }
                tempLines.addAll(0, currentLines);
            }

            if (tempLines.size() > maxLineSize) {
                for (int j = tempLines.size() - maxLineSize; j < tempLines.size(); j++) {
                    strLines.add(tempLines.get(j));
                }
            } else {
                strLines.addAll(tempLines);
            }
            // Calculate pageSize.
            for (String line : strLines) {
                pageSize += line.getBytes(CHARSET_GBK).length;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        prevPageSize = pageSize;
        return strLines;
    }

    private ArrayList<String> getNextPage() {
        int start = indexMap.get(CURRENT_END) + 1;
        int pageSize = 0;

        ArrayList<String> strLines = new ArrayList<>();
        String str_paragraph;
        int length;
        int measuredSize;
        try {
            while (strLines.size() < maxLineSize) {
                byte[] bytes = readNextParagraph(start);
                // Reach end.
                if (bytes.length == 0) break;

                str_paragraph = new String(bytes, CHARSET_GBK);
                length = str_paragraph.length();
                measuredSize = mPaint.breakText(str_paragraph, true, mWidth, null);
                while (measuredSize <= length) {
                    if (strLines.size() >= maxLineSize) break;
                    String measuredStr = str_paragraph.substring(0, measuredSize);
                    byte[] measuredBytes = measuredStr.getBytes(CHARSET_GBK);
                    // In break process, '\r\n' may be broken into '\r' in the end of one line,
                    // and '\n' in the next line, so deal with this situation.
                    // TODO: deal with line break in form of '\r' or '\n'.
                    if (!(measuredBytes.length == 1 && 0x0a == measuredBytes[0])) {
                        // If ends with '\r', add '\n' in the end.
                        if (0x0d == measuredBytes[measuredBytes.length - 1]) {
                            measuredStr = measuredStr + new String(new byte[]{0x0a}, CHARSET_GBK);
                        }
                        pageSize += measuredStr.getBytes(CHARSET_GBK).length;
                        // Update start index.
                        start += measuredStr.getBytes(CHARSET_GBK).length;
                        strLines.add(measuredStr);
                    }
                    // All the text remaining in this paragraph are shown in one line,
                    // so read next paragraph.
                    if (measuredSize == length) break;
                    // Drop the text already in strLines, measure the remaining text.
                    str_paragraph = str_paragraph.substring(measuredSize);
                    measuredSize = mPaint.breakText(str_paragraph, true, mWidth, null);
                    length = str_paragraph.length();
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        nextPageSize = pageSize;
        return strLines;
    }

    private ArrayList<String> getFirstPage(int startPoint) {
        int index = startPoint, pageSize = 0;

        ArrayList<String> strLines = new ArrayList<>();
        String str_paragraph;
        int length;
        int measuredSize;

        try {
            while (strLines.size() < maxLineSize) {
                byte[] bytes = readNextParagraph(index);
                // Reach end.
                if (bytes.length == 0) break;

                str_paragraph = new String(bytes, CHARSET_GBK);
                length = str_paragraph.length();
                measuredSize = mPaint.breakText(str_paragraph, true, mWidth, null);
                while (measuredSize <= length) {
                    if (strLines.size() >= maxLineSize) break;
                    String measuredStr = str_paragraph.substring(0, measuredSize);
                    byte[] measuredBytes = measuredStr.getBytes(CHARSET_GBK);
                    // In break process, '\r\n' may be broken into '\r' in the end of one line,
                    // and '\n' in the next line, so deal with this situation.
                    // TODO: deal with line break in form of '\r' or '\n'.
                    if (!(measuredBytes.length == 1 && 0x0a == measuredBytes[0])) {
                        // If ends with '\r', add '\n' in the end.
                        if (0x0d == measuredBytes[measuredBytes.length - 1]) {
                            measuredStr = measuredStr + new String(new byte[]{0x0a}, CHARSET_GBK);
                        }
                        pageSize += measuredStr.getBytes(CHARSET_GBK).length;
                        // Update start index.
                        index += measuredStr.getBytes(CHARSET_GBK).length;
                        strLines.add(measuredStr);
                    }
                    if (measuredSize == length) break;
                    str_paragraph = str_paragraph.substring(measuredSize);
                    measuredSize = mPaint.breakText(str_paragraph, true, mWidth, null);
                    length = str_paragraph.length();
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

//        indexMap.put(CURRENT_START, startPoint);
        indexMap.put(CURRENT_END, startPoint + pageSize - 1);
        Log.d("test", "first page line size: " + strLines.size());
        return strLines;
    }

    /**
     * 获取下一段文字，以0x0a为结束标记
     *
     * @param start 段落起始位置在mappedByteBuffer中的索引
     * @return 段落字符串的byte数组
     */
    private byte[] readNextParagraph(int start) throws UnsupportedEncodingException {
        int length = 0;

        for (int i = 0; i < maxLen - start; i++) {
            // Line break: \r\n
            if ((0x0d == mappedByteBuffer.get(start + i) && 0x0a == mappedByteBuffer.get(start + i + 1))) {
                length = i + 2;
                break;
            }
        }

        mappedByteBuffer.mark();
        mappedByteBuffer.position(start);
        byte[] bytes = new byte[length];
        mappedByteBuffer.get(bytes, 0, length);
        mappedByteBuffer.reset();   // 将mappedByteBuffer还原到mark的位置，即position重新变为0

        return bytes;
    }

    private byte[] readPrevParagraph(int end) throws UnsupportedEncodingException {
        int length = 0;

        for (int i = 1; i < end; i++) {
            // Line break: \r\n
            if (0x0a == mappedByteBuffer.get(end - i) && 0x0d == mappedByteBuffer.get(end - i - 1)) {
                length = i;
                break;
            }
            // There is no Line break ahead, so its the very beginning of file.
            length = end + 1;
        }

        mappedByteBuffer.mark();
        mappedByteBuffer.position(end - length + 1);
        byte[] bytes = new byte[length];
        mappedByteBuffer.get(bytes, 0, length);
        mappedByteBuffer.reset();

        return bytes;
    }

}
