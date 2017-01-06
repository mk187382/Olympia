package com.olympicwinners.olympia;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;


public class DrawingView extends View {
    private Path mDrawPath;
    private Paint mDrawPaint, mCanvasPaint;
    private int mPaintColor = 0xFF660000;
    private Canvas mDrawCanvas;
    private Bitmap mCanvasBitmap;
    private Bitmap naturalmutableBmp;
    private Bitmap mutableBmp;
    private float mBrushSize, mLastBrushSize;
    private boolean isFilling = false;
    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;
    int resourceIDs[] = {
            R.raw.quick1,
            R.raw.quick2,
            R.raw.quick3,
            R.raw.quick4,
            R.raw.quick5,
            R.raw.quick6
    };
    Bitmap bmp;

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDrawing();
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    private void setupDrawing() {
        mBrushSize = getResources().getInteger(R.integer.medium_size);
        mLastBrushSize = mBrushSize;
        mDrawPath = new Path();
        mDrawPaint = new Paint();
        mDrawPaint.setColor(mPaintColor);
        mDrawPaint.setAntiAlias(true);
        mDrawPaint.setStrokeWidth(mBrushSize);
        mDrawPaint.setStyle(Paint.Style.STROKE);
        mDrawPaint.setStrokeJoin(Paint.Join.ROUND);
        mDrawPaint.setStrokeCap(Paint.Cap.ROUND);
        mCanvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        int widthOfNewBitmap, heightOfNewBitmap;

        widthOfNewBitmap = this.getWidth();
        heightOfNewBitmap = this.getHeight();
        super.onSizeChanged(w, h, oldw, oldh);
        newBitmapOnCanvas(widthOfNewBitmap, heightOfNewBitmap);
    }

    private void newBitmapOnCanvas(int widthOfNewBitmap, int heightOfNewBitmap) {
        Random rB = new Random();
        naturalmutableBmp = BitmapFactory.decodeResource(getResources(), resourceIDs[rB.nextInt(5)]);
        Bitmap mutableBmp = Bitmap.createScaledBitmap(naturalmutableBmp, widthOfNewBitmap, heightOfNewBitmap, true);
        bmp = convertToMutable(mutableBmp);
        mDrawCanvas = new Canvas(bmp);
    }

    public void openBitmapOnCanvas(int widthOfNewBitmap, int heightOfNewBitmap,Uri uri,Context context) throws FileNotFoundException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        naturalmutableBmp = BitmapFactory.decodeStream(inputStream, null, options);
        Bitmap mutableBmp = Bitmap.createScaledBitmap(naturalmutableBmp, widthOfNewBitmap, heightOfNewBitmap, true);
        bmp = convertToMutable(mutableBmp);
        mDrawCanvas = new Canvas(bmp);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        canvas.scale(mScaleFactor, mScaleFactor);
        canvas.drawBitmap(bmp, 0, 0, mCanvasPaint);
        canvas.drawPath(mDrawPath, mDrawPaint);
        canvas.restore();
    }

    public static Bitmap convertToMutable(Bitmap imgIn) {
        try {
            File file = new File(Environment.getExternalStorageDirectory() + File.separator + "temp.tmp");

            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");

            int width = imgIn.getWidth();
            int height = imgIn.getHeight();
            Bitmap.Config type = imgIn.getConfig();

            FileChannel channel = randomAccessFile.getChannel();
            MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0, imgIn.getRowBytes() * height);
            imgIn.copyPixelsToBuffer(map);
            imgIn.recycle();
            System.gc();


            imgIn = Bitmap.createBitmap(width, height, type);
            map.position(0);

            imgIn.copyPixelsFromBuffer(map);

            channel.close();
            randomAccessFile.close();

            file.delete();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imgIn;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));

            invalidate();
            return true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        if (isFilling) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    FloodFill(new Point((int) touchX, (int) touchY));
                    break;
                default:
                    return true;
            }
        } else {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mDrawPath.moveTo(touchX, touchY);
                    break;
                case MotionEvent.ACTION_MOVE:
                    mDrawPath.lineTo(touchX, touchY);
                    break;
                case MotionEvent.ACTION_UP:
                    mDrawCanvas.drawPath(mDrawPath, mDrawPaint);
                    mDrawPath.reset();
                    break;
                default:
                    return false;
            }
        }
        invalidate();
        mScaleDetector.onTouchEvent(event);
        return true;
    }

    public void setColor(String newColor) {
        invalidate();
        mPaintColor = Color.parseColor(newColor);
        mDrawPaint.setColor(mPaintColor);
        mDrawPaint.setShader(null);
    }

    public void setBrushSize(float newSize) {
        mBrushSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                newSize, getResources().getDisplayMetrics());
        mDrawPaint.setStrokeWidth(mBrushSize);
    }

    public void setLastBrushSize(float lastSize) {
        mLastBrushSize = lastSize;
    }

    public float getLastBrushSize() {
        return mLastBrushSize;
    }

    public void setErase(boolean isErase) {
        if (isErase) {
            mDrawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        } else {
            mDrawPaint.setXfermode(null);
        }
    }

    public void startNew(int widthOfNewBitmap, int heightOfNewBitmap) {
        newBitmapOnCanvas(widthOfNewBitmap, heightOfNewBitmap);
        invalidate();
    }


    public void fillColor() {
        isFilling = true;
    }

    private synchronized void FloodFill(Point startPoint) {
        Queue<Point> queue = new LinkedList<>();
        queue.add(startPoint);
        int targetColor = bmp.getPixel(startPoint.x, startPoint.y);
        while (queue.size() > 0) {
            Point nextPoint = queue.poll();
            if (bmp.getPixel(nextPoint.x, nextPoint.y) != targetColor)
                continue;
            Point point = new Point(nextPoint.x + 1, nextPoint.y);
            while ((nextPoint.x > 0) && (bmp.getPixel(nextPoint.x, nextPoint.y) == targetColor)) {
                bmp.setPixel(nextPoint.x, nextPoint.y, mPaintColor);
                if ((nextPoint.y > 0) && (bmp.getPixel(nextPoint.x, nextPoint.y - 1) == targetColor))
                    queue.add(new Point(nextPoint.x, nextPoint.y - 1));
                if ((nextPoint.y < bmp.getHeight() - 1) && (bmp.getPixel(nextPoint.x, nextPoint.y + 1) == targetColor))
                    queue.add(new Point(nextPoint.x, nextPoint.y + 1));
                nextPoint.x--;
            }
            while ((point.x < bmp.getWidth() - 1) && (bmp.getPixel(point.x, point.y) == targetColor)) {
                bmp.setPixel(point.x, point.y, mPaintColor);
                if ((point.y > 0) && (bmp.getPixel(point.x, point.y - 1) == targetColor))
                    queue.add(new Point(point.x, point.y - 1));
                if ((point.y < bmp.getHeight() - 1)
                        && (bmp.getPixel(point.x, point.y + 1) == targetColor))
                    queue.add(new Point(point.x, point.y + 1));
                point.x++;
            }
        }
        isFilling = false;
    }


}