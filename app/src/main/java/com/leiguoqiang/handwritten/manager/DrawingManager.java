package com.leiguoqiang.handwritten.manager;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Looper;
import android.util.Log;
import androidx.annotation.NonNull;
import com.leiguoqiang.handwritten.constant.ColorConstant;
import com.leiguoqiang.handwritten.constant.EffectConstant;
import com.leiguoqiang.handwritten.constant.StrokeStatusConstant;
import com.leiguoqiang.handwritten.constant.StrokeWidthConstant;
import com.leiguoqiang.handwritten.entity.PointBean;
import com.leiguoqiang.handwritten.entity.StrokeBean;
import com.leiguoqiang.handwritten.model.BeisaierModel;
import com.leiguoqiang.handwritten.model.CalculateModel;
import com.leiguoqiang.handwritten.model.StrokeWidthModel;
import java.util.ArrayList;
import java.util.Vector;

/**
 * @author leiguoqiang
 * contact: 274764936
 */
public class DrawingManager {

    private volatile Bitmap mBufferBitmap;
    private Paint mDrawingPaint;
    private volatile Canvas mBufferCanvas;
    private StrokeBean mCurrentStroke;
    private StrokeManager mStrokeManager = new StrokeManager();
    private Vector<PointBean> mTempStroke = new Vector<>();
    private ArrayList<PointBean> mCurrentControlPointBeans = new ArrayList<>();
    private Paint mBufferPaint = new Paint();
    private Path mPath = new Path();
    private PathMeasure mPathMeasure = new PathMeasure();
    private float mCurrentDistance;
    private float mStartPosition;
    private Path mGradingPath = new Path();
    private float mLastPenSize = StrokeWidthConstant.PEN_SIZE_MIN;
    private Runnable mCallBack;
    private PorterDuffXfermode mClearDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
    private int mEraserGrading;

    public void r(Runnable mCallBack) {
        this.mCallBack = mCallBack;
    }

    public void q() {
        mCallBack = null;
    }

    public DrawingManager() {
    }

    public void p(Canvas canvas, Paint mPaint) {
        this.mDrawingPaint = mPaint;
    }

    public Bitmap o() {
        return mBufferBitmap;
    }

    public void n(Canvas canvas) {
        if (Looper.getMainLooper().getThread() != Thread.currentThread()) {
        }
        if (mBufferBitmap == null) {
            return;
        }
    }

    public PointBean m() {
        if (mCurrentStroke == null) {
            return null;
        }
        return mCurrentStroke.getPointBean(mCurrentStroke.size() - 1);
    }

    public void c(@NonNull PointBean point) {
    }

    public void a(PointBean point) {
    }

    private void f(boolean Beans) {
    }

    public void b(PointBean point) {
        d();
    }

    private void d() {
        k();
    }

    public void e() {
        if (mStrokeManager != null) {
            mStrokeManager.k();
        }
        g();
    }

    public void f() {
        mStrokeManager = new StrokeManager();
        g();
        if (mCallBack != null) {
            mCallBack.run();
        }
    }

    private void g() {
    }

    public void h(Runnable callBack) {
        if (callBack == null || mStrokeManager == null || mStrokeManager.g() == 0) {
            return;
        }
        mStrokeManager.i();
        k();
    }

    public void i(Runnable callBack) {
        if (callBack == null || mStrokeManager == null || mStrokeManager.f() == 0) {
            return;
        }
        mStrokeManager.h();
        k();
    }

    public void j(StrokeManager strokeManager) {
        if (strokeManager == null || mCallBack == null) {
            return;
        }
        mStrokeManager = strokeManager;
        k();
    }

    public void k() {
    }

    public StrokeManager l() {
        return mStrokeManager;
    }
}
