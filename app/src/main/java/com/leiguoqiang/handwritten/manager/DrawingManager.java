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
        mPath.reset();
        mDrawingPaint = null;
        mBufferPaint.reset();
        mBufferBitmap.recycle();
        mBufferBitmap = null;
    }

    public DrawingManager() {
        mEraserGrading = (int) Math.ceil(EffectConstant.CUSTOM_ERASER_GRADING_VALUE / EffectConstant.BEISAIER_GRADING_VALUE);
    }

    public void p(Canvas canvas, Paint mPaint) {
        if (canvas == null) {
            Log.i("handwritten_module:", "DrawingManager init is failed");
            return;
        }
        mBufferBitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
        mBufferCanvas = new Canvas(mBufferBitmap);
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
        canvas.drawBitmap(mBufferBitmap, 0, 0, mDrawingPaint);
    }

    public PointBean m() {
        if (mCurrentStroke == null) {
            return null;
        }
        return mCurrentStroke.getPointBean(mCurrentStroke.size() - 1);
    }

    public void c(@NonNull PointBean point) {
        mTempStroke.clear();
        mCurrentControlPointBeans.clear();
        mCurrentStroke = new StrokeBean(ColorConstant.CURRENT_STROKE_COLOR);
        mCurrentStroke.setStrokeStatus(StrokeStatusConstant.CURRENT_STROKE_STATUS);
        mCurrentStroke.setColor(ColorConstant.CURRENT_STROKE_COLOR);
        if (StrokeStatusConstant.CURRENT_STROKE_STATUS != StrokeStatusConstant.STROKE_STATUS_CUSTOM_ERASER) {
            mStrokeManager.h(mCurrentStroke);
        }
        mCurrentStroke.addPointBean(point);
        mTempStroke.add(point);
        //添加一个辅助数据，适配曲线相关计算模型
        mTempStroke.add(point);
        mCurrentStroke.getGradingBeans().add(point);
        mBufferPaint.reset();
        mBufferPaint.setStyle(Paint.Style.STROKE);
        mBufferPaint.setAntiAlias(true);
        mBufferPaint.setFilterBitmap(true);
        mBufferPaint.setStrokeJoin(Paint.Join.ROUND);
        mBufferPaint.setColor(Color.parseColor(ColorConstant.CURRENT_STROKE_COLOR));
        if (StrokeStatusConstant.CURRENT_STROKE_STATUS == StrokeStatusConstant.STROKE_STATUS_NORMAL_ERASER) {
            mBufferPaint.setXfermode(mClearDuffXfermode);
        }
        mPath.reset();
        mPath.moveTo(mTempStroke.elementAt(0).x, mTempStroke.elementAt(0).y);

        mStartPosition = 0.0f;
        mCurrentDistance = 0.0f;
    }

    public void a(PointBean point) {
        mCurrentStroke.addPointBean(point);
        mTempStroke.add(point);
        mCurrentStroke.getGradingBeans().add(point);
        float width = StrokeWidthModel.width(mTempStroke.elementAt(0), mTempStroke.elementAt(1), mTempStroke.elementAt(2));
        mTempStroke.elementAt(1).width = width;
        if (mTempStroke.elementAt(0) == mTempStroke.elementAt(1)) {
            mCurrentControlPointBeans.add(mTempStroke.elementAt(0));

        } else {
            ArrayList<PointBean> mTempControlPointBeans = BeisaierModel.calculateControl(mTempStroke.elementAt(0), mTempStroke.elementAt(1), mTempStroke.elementAt(2), EffectConstant.BEISAIER_PINGHUA_VALUE);
            mCurrentControlPointBeans.add(mTempControlPointBeans.get(0));
            mCurrentControlPointBeans.add(mTempControlPointBeans.get(1));
            mPath.cubicTo(mCurrentControlPointBeans.get(0).x, mCurrentControlPointBeans.get(0).y,
                    mCurrentControlPointBeans.get(1).x, mCurrentControlPointBeans.get(1).y,
                    mTempStroke.elementAt(1).x, mTempStroke.elementAt(1).y);
            f(true);
            mCurrentControlPointBeans.remove(0);
            mCurrentControlPointBeans.remove(0);
        }
        mTempStroke.remove(0);
    }

    private void f(boolean isAddGradingBeans) {
        mPathMeasure.setPath(mPath, false);
        float length = mPathMeasure.getLength();
        float currentWidth = mTempStroke.elementAt(0).width;
        double gradingCount = Math.ceil(CalculateModel.divider(CalculateModel.subtraction(length, mStartPosition), EffectConstant.BEISAIER_GRADING_VALUE));
        float gradingWidth = CalculateModel.divider(Math.abs(CalculateModel.subtraction(mLastPenSize, mTempStroke.elementAt(0).width)), (float) gradingCount);
        int tempIndex = 0;
        for (int index = 0; index < gradingCount; index++) {
            mCurrentDistance += EffectConstant.BEISAIER_GRADING_VALUE;
            if (mCurrentDistance > mPathMeasure.getLength()) {
                mCurrentDistance = mPathMeasure.getLength();
            }
            if (isAddGradingBeans && index % mEraserGrading == 0) {
//            if (isAddGradingBeans) {
                tempIndex++;
                //获取辅助点,用于橡皮檫
                float[] positon = new float[2];
                float[] tanValue = new float[2];
                mPathMeasure.getPosTan(mCurrentDistance, positon, tanValue);
                mCurrentStroke.getGradingBeans().add(new PointBean(positon[0], positon[1], 0));
            }

            //截取曲线
            mGradingPath.reset();
            boolean result = mPathMeasure.getSegment(mStartPosition, mCurrentDistance, mGradingPath, true);
            if (result) {
                mBufferPaint.setStyle(Paint.Style.STROKE);
                if (currentWidth >= mLastPenSize) {
                    mLastPenSize += gradingWidth;
                    if (mLastPenSize > currentWidth) {
                        mLastPenSize = currentWidth;
                    }
                } else {
                    mLastPenSize -= gradingWidth;
                    if (mLastPenSize < currentWidth) {
                        mLastPenSize = currentWidth;
                    }
                }
                //精确最后一个粒度大小
                if (index == gradingCount - 1) {
                    mLastPenSize = currentWidth;
                }
                mBufferPaint.setStrokeWidth(mLastPenSize);
                if (mBufferCanvas != null) {
                    mBufferCanvas.drawPath(mGradingPath, mBufferPaint);
                }
            }
            //移动起点位置
            mStartPosition = mCurrentDistance;
        }
    }

    public void b(PointBean point) {
        mCurrentStroke.addPointBean(point);
        mTempStroke.add(point);

        mCurrentStroke.getGradingBeans().add(point);

        float width = StrokeWidthModel.width(mTempStroke.elementAt(0), mTempStroke.elementAt(1), mTempStroke.elementAt(2));
        mTempStroke.elementAt(1).width = width;

        ArrayList<PointBean> mTempControlPointBeans = BeisaierModel.calculateControl(mTempStroke.elementAt(0), mTempStroke.elementAt(1), mTempStroke.elementAt(2), EffectConstant.BEISAIER_PINGHUA_VALUE);
        mCurrentControlPointBeans.add(mTempControlPointBeans.get(0));
        mCurrentControlPointBeans.add(mTempControlPointBeans.get(1));

        mPath.cubicTo(mCurrentControlPointBeans.get(0).x, mCurrentControlPointBeans.get(0).y,
                mCurrentControlPointBeans.get(1).x, mCurrentControlPointBeans.get(1).y,
                mTempStroke.elementAt(1).x, mTempStroke.elementAt(1).y);
        f(true);

        mCurrentControlPointBeans.remove(0);
        mCurrentControlPointBeans.remove(0);
        mTempStroke.remove(0);

        mTempStroke.add(point);
        mCurrentControlPointBeans.add(point);
        mCurrentControlPointBeans.add(point);

        mPath.cubicTo(mCurrentControlPointBeans.get(0).x, mCurrentControlPointBeans.get(0).y,
                mCurrentControlPointBeans.get(1).x, mCurrentControlPointBeans.get(1).y,
                mTempStroke.elementAt(1).x, mTempStroke.elementAt(1).y);
        f(true);
        if (StrokeStatusConstant.CURRENT_STROKE_STATUS == StrokeStatusConstant.STROKE_STATUS_CUSTOM_ERASER) {
            d();
        }
    }

    private void d() {
        RectF rect = new RectF();
        mPath.close();
        mPath.computeBounds(rect, true);
        Region region = new Region();
        region.setPath(mPath, new Region((int) rect.left, (int) rect.top, (int) rect.right, (int) rect.bottom));
        Vector<StrokeBean> data = mStrokeManager.a();
        Vector<StrokeBean> mTempVector = new Vector<>();
        for (int index = 0; index < data.size(); index++) {
            StrokeBean strokeBean = data.get(index);
            if (strokeBean.getStrokeStatus() == StrokeStatusConstant.STROKE_STATUS_NORMAL_ERASER) {
                continue;
            }
            for (int tempIndex = 0; tempIndex < strokeBean.getGradingBeans().size(); tempIndex++) {
                PointBean pointBean = strokeBean.getGradingBeans().get(tempIndex);
                boolean result = region.contains((int) pointBean.x, (int) pointBean.y);
                if (result) {
                    mTempVector.add(strokeBean);
                    break;
                }
            }
        }
        if (mTempVector.size() != 0) {
            for (int index = 0; index < mTempVector.size(); index++) {
                StrokeBean strokeBean = mTempVector.get(index);
                data.remove(strokeBean);
                mStrokeManager.c().add(strokeBean);
            }
        }
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
        if (mBufferCanvas != null && mBufferPaint != null) {
            mBufferCanvas.drawColor(Color.parseColor(ColorConstant.COLOR_NORMAL_CLEAR), PorterDuff.Mode.CLEAR);
        }
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
        int tempStatus = StrokeStatusConstant.CURRENT_STROKE_STATUS;
        g();
        int size = mStrokeManager.f();
        for (int index = 0; index < size; index++) {
            StrokeBean stroke = mStrokeManager.e(index);
            for (int innerIndex = 0; innerIndex < stroke.size(); innerIndex++) {
                PointBean point = stroke.getPointBean(innerIndex);
                if (innerIndex == 0) {
                    mCurrentControlPointBeans.clear();
                    mTempStroke.clear();
                    mTempStroke.add(point);
                    //添加一个辅助数据，适配曲线相关计算模型
                    mTempStroke.add(point);
                    mBufferPaint.reset();
                    mBufferPaint.setStyle(Paint.Style.STROKE);
                    mBufferPaint.setAntiAlias(true);
                    mBufferPaint.setFilterBitmap(true);
                    mBufferPaint.setStrokeJoin(Paint.Join.ROUND);
                    mBufferPaint.setColor(Color.parseColor(stroke.getColor()));
                    StrokeStatusConstant.CURRENT_STROKE_STATUS = stroke.getStrokeStatus();
                    if (StrokeStatusConstant.CURRENT_STROKE_STATUS == StrokeStatusConstant.STROKE_STATUS_NORMAL_ERASER) {
                        mBufferPaint.setXfermode(mClearDuffXfermode);
                    }

                    mPath.reset();
                    mPath.moveTo(mTempStroke.elementAt(0).x, mTempStroke.elementAt(0).y);

                    mStartPosition = 0.0f;
                    mCurrentDistance = 0.0f;
                } else if (innerIndex == 1) {
                    mTempStroke.add(point);
                    mCurrentControlPointBeans.add(mTempStroke.elementAt(0));
                    mTempStroke.remove(0);
                } else {
                    mTempStroke.add(point);
                    ArrayList<PointBean> mTempControlPointBeans = BeisaierModel.calculateControl(mTempStroke.elementAt(0), mTempStroke.elementAt(1), mTempStroke.elementAt(2), EffectConstant.BEISAIER_PINGHUA_VALUE);
                    mCurrentControlPointBeans.add(mTempControlPointBeans.get(0));
                    mCurrentControlPointBeans.add(mTempControlPointBeans.get(1));
                    //绘制曲线，以mTempStroke中第一个点的属性进行绘制
                    mPath.cubicTo(mCurrentControlPointBeans.get(0).x, mCurrentControlPointBeans.get(0).y,
                            mCurrentControlPointBeans.get(1).x, mCurrentControlPointBeans.get(1).y,
                            mTempStroke.elementAt(1).x, mTempStroke.elementAt(1).y);
                    //绘制粒度曲线
                    f(false);
                    //移除已经使用过的控制点
                    mCurrentControlPointBeans.remove(0);
                    mCurrentControlPointBeans.remove(0);
                    mTempStroke.remove(0);

                    //绘制最后一段,添加辅助绘制点和控制点
                    if (innerIndex == stroke.size() - 1) {
                        mTempStroke.add(point);
                        mCurrentControlPointBeans.add(point);
                        mCurrentControlPointBeans.add(point);

                        mPath.cubicTo(mCurrentControlPointBeans.get(0).x, mCurrentControlPointBeans.get(0).y,
                                mCurrentControlPointBeans.get(1).x, mCurrentControlPointBeans.get(1).y,
                                mTempStroke.elementAt(1).x, mTempStroke.elementAt(1).y);
                        f(false);
                    }
                }
            }
        }
        if (mCallBack != null) {
            mCallBack.run();
        }
        //恢复当前画笔状态
        StrokeStatusConstant.CURRENT_STROKE_STATUS = tempStatus;
    }

    public StrokeManager l() {
        return mStrokeManager;
    }
}
