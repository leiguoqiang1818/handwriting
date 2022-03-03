package com.leiguoqiang.handwritten.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;

import com.leiguoqiang.handwritten.annotation.StrokeStatusAnnotation;
import com.leiguoqiang.handwritten.constant.ColorConstant;
import com.leiguoqiang.handwritten.constant.StrokeStatusConstant;
import com.leiguoqiang.handwritten.constant.StrokeWidthConstant;
import com.leiguoqiang.handwritten.entity.PaintProperties;
import com.leiguoqiang.handwritten.entity.PointBean;
import com.leiguoqiang.handwritten.manager.DrawOptionManager;
import com.leiguoqiang.handwritten.manager.DrawingManager;
import com.leiguoqiang.handwritten.manager.StrokeManager;

import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author leiguoqiang
 * contact: 274764936
 */
public class DrawView extends View {

    private Paint mPaint = new Paint();
    private DrawingManager mDrawingManager;
    private DrawOptionManager mOptionManager;
    private volatile boolean mHighPrecision;
    private InnerRunnable innerRunnable;
    private AtomicBoolean mIsInited = new AtomicBoolean(false);
    private boolean mTouchEnable = true;
    private AtomicInteger delayTime = new AtomicInteger(0);
    private String mHandlerThreadName = "handwritten_name";
    private HandlerThread mHandlerThread;
    private Handler mHandler;
    private Runnable mAutoConfirmCallBack;
    private long mDelayTime = 1000 * 1;
    private Runnable mAutoRunnable;
    private PaintProperties mPaintProperties = new PaintProperties();
    private boolean filterFilger;

    public void setFilterFilger(boolean filterFilger) {
        this.filterFilger = filterFilger;
    }

    public void setAutoConfirmIntervelTime(long mDelayTime) {
        this.mDelayTime = mDelayTime;
    }

    public void setAutoConfirmCallBack(Runnable mAutoConfirmCallBack) {
        this.mAutoConfirmCallBack = mAutoConfirmCallBack;
    }

    public void setTouchEnable(boolean mTouchEnable) {
        this.mTouchEnable = mTouchEnable;
    }

    public DrawView(Context context) {
        super(context);
        init();
    }

    public DrawView(Context context, @androidx.annotation.Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawView(Context context, @androidx.annotation.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public DrawView(Context context, @androidx.annotation.Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setKeepScreenOn(true);
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mDrawingManager = new DrawingManager();
        mOptionManager = new DrawOptionManager(mDrawingManager);
        innerRunnable = new InnerRunnable(this);
        mDrawingManager.r(innerRunnable);
        mHandlerThread = new HandlerThread(mHandlerThreadName);
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mIsInited.compareAndSet(false, true)) {
            mDrawingManager.p(canvas, mPaint);
            mDrawingManager.k();
        }
        mDrawingManager.n(canvas);
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mTouchEnable) {
            return false;
        }
        final int action = event.getAction();
        int type = event.getToolType(0);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (mHandler != null) {
                    mHandler.removeCallbacksAndMessages(null);
                }
                PointBean pointBean = new PointBean(event.getX(), event.getY(), event.getEventTime(), event.getPressure());
                mDrawingManager.c(pointBean);
                break;
            case MotionEvent.ACTION_MOVE:
                int size = event.getHistorySize();
                PointBean tempBean;
                PointBean lastPoint = mDrawingManager.m();
                tempBean = new PointBean(event.getX(), event.getY(), event.getEventTime(), event.getPressure());
                if (!tempBean.equals(lastPoint)) {
                    mDrawingManager.a(tempBean);
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                tempBean = new PointBean(event.getX(), event.getY(), event.getEventTime(), event.getPressure());
                mDrawingManager.b(tempBean);
                invalidate();
                if (mAutoConfirmCallBack != null) {
                    if (mAutoRunnable == null) {
                        mAutoRunnable = new InnerAutoRunnable(mAutoConfirmCallBack);
                    }
                    mHandler.postDelayed(mAutoRunnable, mDelayTime);
                }
                break;
        }
        return true;
    }

    public void s(boolean highPrecision) {
        mHighPrecision = highPrecision;
    }

    public void r() {
        if (mOptionManager != null) {
            mOptionManager.clear();
            invalidate();
        }
    }

    public void q() {
        if (mOptionManager != null) {
            mOptionManager.save();
        }
    }

    public void p() {
        if (mOptionManager != null) {
            if (innerRunnable == null) {
                innerRunnable = new InnerRunnable(this);
            }
            mOptionManager.recover(innerRunnable);
        }
    }

    public void o() {
        if (mOptionManager != null) {
            if (innerRunnable == null) {
                innerRunnable = new InnerRunnable(this);
            }
            mOptionManager.revocation(innerRunnable);
        }
    }

    private static class InnerRunnable implements Runnable {
        private WeakReference<HandwritingView> mView;

        public InnerRunnable(HandwritingView mView) {
            this.mView = new WeakReference<>(mView);
        }

        @Override
        public void run() {
            if (mView.get() != null) {
                mView.get().postInvalidate();
            }
        }
    }

    public StrokeManager l() {
        return mDrawingManager.l();
    }

    public void k() {
        mDrawingManager.f();
    }

    public void j(StrokeManager strokeManager) {
        if (mDrawingManager != null) {
            mDrawingManager.j(strokeManager);
        }
    }

    public void i(@StrokeStatusAnnotation int strokeStatus) {
        StrokeStatusConstant.CURRENT_STROKE_STATUS = strokeStatus;
        if (StrokeStatusConstant.CURRENT_STROKE_STATUS == StrokeStatusConstant.STROKE_STATUS_PEN) {
            h(ColorConstant.LAST_COLOR_PEN, StrokeStatusConstant.CURRENT_STROKE_STATUS);
            o(StrokeWidthConstant.LAST_PEN_STROKE_SIZE, StrokeStatusConstant.CURRENT_STROKE_STATUS);

        } else if (StrokeStatusConstant.CURRENT_STROKE_STATUS == StrokeStatusConstant.STROKE_STATUS_BRUSH) {
            h(ColorConstant.LAST_COLOR_BRUSH, StrokeStatusConstant.CURRENT_STROKE_STATUS);
            o(StrokeWidthConstant.LAST_BRUSH_STROKE_SIZE, StrokeStatusConstant.CURRENT_STROKE_STATUS);

        } else if (StrokeStatusConstant.CURRENT_STROKE_STATUS == StrokeStatusConstant.STROKE_STATUS_NORMAL_ERASER) {
            h(ColorConstant.COLOR_NORMAL_CLEAR, StrokeStatusConstant.CURRENT_STROKE_STATUS);
            o(StrokeWidthConstant.LAST_NORMAL_ERASER_STROKE_SIZE, StrokeStatusConstant.CURRENT_STROKE_STATUS);

        } else if (StrokeStatusConstant.CURRENT_STROKE_STATUS == StrokeStatusConstant.STROKE_STATUS_CUSTOM_ERASER) {
            h(ColorConstant.COLOR_CUSTOM_ERASER, StrokeStatusConstant.CURRENT_STROKE_STATUS);
            o(StrokeWidthConstant.CUSTOM_ERASER_SIZE, StrokeStatusConstant.CURRENT_STROKE_STATUS);
        }
    }

    public void h(String color, @StrokeStatusAnnotation int strokeStatus) {
        if (TextUtils.isEmpty(color)) {
            return;
        }
        if (strokeStatus == StrokeStatusConstant.STROKE_STATUS_PEN) {
            ColorConstant.LAST_COLOR_PEN = color;
            if (StrokeStatusConstant.CURRENT_STROKE_STATUS == strokeStatus) {
                ColorConstant.CURRENT_STROKE_COLOR = color;
            }
        } else if (strokeStatus == StrokeStatusConstant.STROKE_STATUS_BRUSH) {
            ColorConstant.LAST_COLOR_BRUSH = color;
            if (StrokeStatusConstant.CURRENT_STROKE_STATUS == strokeStatus) {
                ColorConstant.CURRENT_STROKE_COLOR = color;
            }
        } else if (strokeStatus == StrokeStatusConstant.STROKE_STATUS_NORMAL_ERASER) {
            if (StrokeStatusConstant.CURRENT_STROKE_STATUS == strokeStatus) {
                ColorConstant.CURRENT_STROKE_COLOR = ColorConstant.COLOR_NORMAL_CLEAR;
            }
        } else if (strokeStatus == StrokeStatusConstant.STROKE_STATUS_CUSTOM_ERASER) {
            if (StrokeStatusConstant.CURRENT_STROKE_STATUS == strokeStatus) {
                ColorConstant.CURRENT_STROKE_COLOR = ColorConstant.COLOR_CUSTOM_ERASER;
            }
        }
    }

    public void o(float strokeWidth, @StrokeStatusAnnotation int strokeStatus) {
        if (strokeWidth <= 0) {
            return;
        }
        if (strokeStatus == StrokeStatusConstant.STROKE_STATUS_PEN) {
            StrokeWidthConstant.LAST_PEN_STROKE_SIZE = strokeWidth;
            if (StrokeStatusConstant.CURRENT_STROKE_STATUS == strokeStatus) {
                StrokeWidthConstant.CURRENT_STROKE_SIZE = strokeWidth;
            }
        } else if (strokeStatus == StrokeStatusConstant.STROKE_STATUS_BRUSH) {
            StrokeWidthConstant.LAST_BRUSH_STROKE_SIZE = strokeWidth;
            if (StrokeStatusConstant.CURRENT_STROKE_STATUS == strokeStatus) {
                StrokeWidthConstant.CURRENT_STROKE_SIZE = strokeWidth;
            }
        } else if (strokeStatus == StrokeStatusConstant.STROKE_STATUS_NORMAL_ERASER) {
            StrokeWidthConstant.LAST_NORMAL_ERASER_STROKE_SIZE = strokeWidth;
            if (StrokeStatusConstant.CURRENT_STROKE_STATUS == strokeStatus) {
                StrokeWidthConstant.CURRENT_STROKE_SIZE = strokeWidth;
            }
        } else if (strokeStatus == StrokeStatusConstant.STROKE_STATUS_CUSTOM_ERASER) {
            if (StrokeStatusConstant.CURRENT_STROKE_STATUS == strokeStatus) {
                StrokeWidthConstant.CURRENT_STROKE_SIZE = strokeWidth;
            }
        }
    }

    @MainThread
    public Bitmap n() {
        if (getMeasuredWidth() == 0 || getMeasuredHeight() == 0) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        draw(c);
        return bitmap;
    }

    @MainThread
    public Bitmap m() {
        if (getMeasuredWidth() == 0 || getMeasuredHeight() == 0) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        c.drawColor(Color.parseColor("#ffffffff"));
        draw(c);
        return bitmap;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        if (mHandlerThread != null) {
            mHandlerThread.quitSafely();
        }
        mHandlerThread = null;
        mHandler = null;
    }

    private static class InnerAutoRunnable implements Runnable {

        private WeakReference<Runnable> runnable;

        public InnerAutoRunnable(Runnable runnable) {
            this.runnable = new WeakReference<>(runnable);
        }

        @Override
        public void run() {
            if (runnable.get() != null) {
                runnable.get().run();
            }
        }
    }

    public void c() {
        mPaintProperties.color = ColorConstant.CURRENT_STROKE_COLOR;
        mPaintProperties.width = StrokeWidthConstant.CURRENT_STROKE_SIZE;
        mPaintProperties.status = StrokeStatusConstant.CURRENT_STROKE_STATUS;
    }

    public void b() {
        ColorConstant.CURRENT_STROKE_COLOR = mPaintProperties.color;
        StrokeWidthConstant.CURRENT_STROKE_SIZE = mPaintProperties.width;
        StrokeStatusConstant.CURRENT_STROKE_STATUS = mPaintProperties.status;
    }

    public void a() {
        ColorConstant.CURRENT_STROKE_COLOR = ColorConstant.COLOR_BLACK;
        StrokeWidthConstant.CURRENT_STROKE_SIZE = StrokeWidthConstant.PEN_SIZE_SMALL;
        StrokeStatusConstant.CURRENT_STROKE_STATUS = StrokeStatusConstant.STROKE_STATUS_PEN;
    }

}
