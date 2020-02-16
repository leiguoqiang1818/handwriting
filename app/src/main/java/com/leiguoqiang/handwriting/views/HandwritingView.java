package com.leiguoqiang.handwriting.views;

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

import com.leiguoqiang.handwriting.annotation.StrokeStatusAnnotation;
import com.leiguoqiang.handwriting.constant.ColorConstant;
import com.leiguoqiang.handwriting.constant.StrokeStatusConstant;
import com.leiguoqiang.handwriting.constant.StrokeWidthConstant;
import com.leiguoqiang.handwriting.entity.PaintProperties;
import com.leiguoqiang.handwriting.entity.PointBean;
import com.leiguoqiang.handwriting.manager.DrawOptionManager;
import com.leiguoqiang.handwriting.manager.DrawingManager;
import com.leiguoqiang.handwriting.manager.StrokeManager;

import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author leiguoqiang
 * contact: 274764936
 * 手写笔迹控件：钢笔，毛笔，铅笔
 * 绘制优化思路：利用类似的双缓冲机制，将绘制过程中的计算操作颗粒化分离出来
 */
public class HandwritingView extends View {

    private Paint mPaint = new Paint();
    private DrawingManager mDrawingManager;
    private DrawOptionManager mOptionManager;
    /**
     * 高精度开关
     */
    private volatile boolean mHighPrecision;
    private InnerRunnable innerRunnable;
    private AtomicBoolean mIsInited = new AtomicBoolean(false);
    private boolean mTouchEnable = true;
    private AtomicInteger delayTime = new AtomicInteger(0);
    private String mHandlerThreadName = "handwriting_name";
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

    public HandwritingView(Context context) {
        super(context);
        init();
    }

    public HandwritingView(Context context, @androidx.annotation.Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HandwritingView(Context context, @androidx.annotation.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public HandwritingView(Context context, @androidx.annotation.Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
        mDrawingManager.setDrawCallBack(innerRunnable);
        mHandlerThread = new HandlerThread(mHandlerThreadName);
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mIsInited.compareAndSet(false, true)) {
            mDrawingManager.init(canvas, mPaint);
            mDrawingManager.drawStroke();
        }
        mDrawingManager.onDraw(canvas);
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
        if (filterFilger && type != MotionEvent.TOOL_TYPE_STYLUS) {
            return false;
        }
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (mHandler != null) {
                    mHandler.removeCallbacksAndMessages(null);
                }
                PointBean pointBean = new PointBean(event.getX(), event.getY(), event.getEventTime(), event.getPressure());
                mDrawingManager.actionDown(pointBean);
                break;
            case MotionEvent.ACTION_MOVE:
                int size = event.getHistorySize();
                PointBean tempBean;
                //高精度模式，影响性能，慎重
                if (mHighPrecision) {
                    for (int index = 0; index < size; index++) {
                        tempBean = new PointBean(event.getHistoricalX(index), event.getHistoricalY(index), event.getHistoricalEventTime(index), event.getHistoricalPressure(index));
                        PointBean lastPoint = mDrawingManager.getCurrentStrokeLastPoint();
                        if (!tempBean.equals(lastPoint)) {
                            mDrawingManager.actionMove(tempBean);
                        }
                    }
                }
                PointBean lastPoint = mDrawingManager.getCurrentStrokeLastPoint();
                tempBean = new PointBean(event.getX(), event.getY(), event.getEventTime(), event.getPressure());
                if (!tempBean.equals(lastPoint)) {
                    mDrawingManager.actionMove(tempBean);
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                tempBean = new PointBean(event.getX(), event.getY(), event.getEventTime(), event.getPressure());
                mDrawingManager.actionUp(tempBean);
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

    public void enableHighPrecision(boolean highPrecision) {
        mHighPrecision = highPrecision;
    }

    public void clear() {
        if (mOptionManager != null) {
            mOptionManager.clear();
            invalidate();
        }
    }

    public void save() {
        if (mOptionManager != null) {
            mOptionManager.save();
        }
    }

    public void recover() {
        if (mOptionManager != null) {
            if (innerRunnable == null) {
                innerRunnable = new InnerRunnable(this);
            }
            mOptionManager.recover(innerRunnable);
        }
    }

    public void revocation() {
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

    public StrokeManager getData() {
        return mDrawingManager.getData();
    }

    /**
     * 恢复初始状态
     */
    public void reset() {
        mDrawingManager.reset();
    }

    /**
     * 重置数据
     */
    public void resetData(StrokeManager strokeManager) {
        if (mDrawingManager != null) {
            mDrawingManager.setData(strokeManager);
        }
    }

    /**
     * 画笔状态切换：钢笔、毛笔、自定义橡皮檫、通用橡皮檫
     * 设置状态，并设置默认属性
     */
    public void setStrokeStatus(@StrokeStatusAnnotation int strokeStatus) {
        StrokeStatusConstant.CURRENT_STROKE_STATUS = strokeStatus;
        if (StrokeStatusConstant.CURRENT_STROKE_STATUS == StrokeStatusConstant.STROKE_STATUS_PEN) {
            setStrokeColor(ColorConstant.LAST_COLOR_PEN, StrokeStatusConstant.CURRENT_STROKE_STATUS);
            setStrokeWidth(StrokeWidthConstant.LAST_PEN_STROKE_SIZE, StrokeStatusConstant.CURRENT_STROKE_STATUS);

        } else if (StrokeStatusConstant.CURRENT_STROKE_STATUS == StrokeStatusConstant.STROKE_STATUS_BRUSH) {
            setStrokeColor(ColorConstant.LAST_COLOR_BRUSH, StrokeStatusConstant.CURRENT_STROKE_STATUS);
            setStrokeWidth(StrokeWidthConstant.LAST_BRUSH_STROKE_SIZE, StrokeStatusConstant.CURRENT_STROKE_STATUS);

        } else if (StrokeStatusConstant.CURRENT_STROKE_STATUS == StrokeStatusConstant.STROKE_STATUS_NORMAL_ERASER) {
            setStrokeColor(ColorConstant.COLOR_NORMAL_CLEAR, StrokeStatusConstant.CURRENT_STROKE_STATUS);
            setStrokeWidth(StrokeWidthConstant.LAST_NORMAL_ERASER_STROKE_SIZE, StrokeStatusConstant.CURRENT_STROKE_STATUS);

        } else if (StrokeStatusConstant.CURRENT_STROKE_STATUS == StrokeStatusConstant.STROKE_STATUS_CUSTOM_ERASER) {
            setStrokeColor(ColorConstant.COLOR_CUSTOM_ERASER, StrokeStatusConstant.CURRENT_STROKE_STATUS);
            setStrokeWidth(StrokeWidthConstant.CUSTOM_ERASER_SIZE, StrokeStatusConstant.CURRENT_STROKE_STATUS);
        }
    }

    /**
     * 设置笔画颜色
     */
    public void setStrokeColor(String color, @StrokeStatusAnnotation int strokeStatus) {
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
            //通用橡皮檫，直接透明色
        } else if (strokeStatus == StrokeStatusConstant.STROKE_STATUS_NORMAL_ERASER) {
            if (StrokeStatusConstant.CURRENT_STROKE_STATUS == strokeStatus) {
                ColorConstant.CURRENT_STROKE_COLOR = ColorConstant.COLOR_NORMAL_CLEAR;
            }
            //自定义橡皮檫，直接红色
        } else if (strokeStatus == StrokeStatusConstant.STROKE_STATUS_CUSTOM_ERASER) {
            if (StrokeStatusConstant.CURRENT_STROKE_STATUS == strokeStatus) {
                ColorConstant.CURRENT_STROKE_COLOR = ColorConstant.COLOR_CUSTOM_ERASER;
            }
        }
    }

    /**
     * 设置指定状态下的画笔宽度
     */
    public void setStrokeWidth(float strokeWidth, @StrokeStatusAnnotation int strokeStatus) {
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
    public Bitmap getBitmap() {
        if (getMeasuredWidth() == 0 || getMeasuredHeight() == 0) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        draw(c);
        return bitmap;
    }

    @MainThread
    public Bitmap getBitmapWithWhiteBg() {
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

    /**
     * 保存当前画笔属性
     */
    public void saveProperties() {
        mPaintProperties.color = ColorConstant.CURRENT_STROKE_COLOR;
        mPaintProperties.width = StrokeWidthConstant.CURRENT_STROKE_SIZE;
        mPaintProperties.status = StrokeStatusConstant.CURRENT_STROKE_STATUS;
    }

    /**
     * 恢复当前画笔属性
     */
    public void restore() {
        ColorConstant.CURRENT_STROKE_COLOR = mPaintProperties.color;
        StrokeWidthConstant.CURRENT_STROKE_SIZE = mPaintProperties.width;
        StrokeStatusConstant.CURRENT_STROKE_STATUS = mPaintProperties.status;
    }

    /**
     * 设置通用属性
     */
    public void setCommonProperties() {
        ColorConstant.CURRENT_STROKE_COLOR = ColorConstant.COLOR_BLACK;
        StrokeWidthConstant.CURRENT_STROKE_SIZE = StrokeWidthConstant.PEN_SIZE_SMALL;
        StrokeStatusConstant.CURRENT_STROKE_STATUS = StrokeStatusConstant.STROKE_STATUS_PEN;
    }

}
