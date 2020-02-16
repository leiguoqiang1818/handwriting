package com.leiguoqiang.handwriting.manager;

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

import com.leiguoqiang.handwriting.constant.ColorConstant;
import com.leiguoqiang.handwriting.constant.EffectConstant;
import com.leiguoqiang.handwriting.constant.StrokeStatusConstant;
import com.leiguoqiang.handwriting.constant.StrokeWidthConstant;
import com.leiguoqiang.handwriting.entity.PointBean;
import com.leiguoqiang.handwriting.entity.StrokeBean;
import com.leiguoqiang.handwriting.model.BeisaierModel;
import com.leiguoqiang.handwriting.model.CalculateModel;
import com.leiguoqiang.handwriting.model.StrokeWidthModel;

import java.util.ArrayList;
import java.util.Vector;


/**
 * @author leiguoqiang
 * contact: 274764936
 * 绘制策略管理
 * 功能：负责绘制相关操作
 */
public class DrawingManager {

    //ui线程绘制使用的bitmap对象
    private volatile Bitmap mBufferBitmap;
    //ui线程绘制使用画笔
    private Paint mDrawingPaint;
    //缓冲画布对象，缓冲即时的绘制数据
    private volatile Canvas mBufferCanvas;
    //当前笔画
    private StrokeBean mCurrentStroke;
    //笔画管理总对象
    private StrokeManager mStrokeManager = new StrokeManager();
    //临时数据集合，辅助处理线条宽度、曲线使用
    private Vector<PointBean> mTempStroke = new Vector<>();
    //当前控制点集合，辅助绘制曲线使用
    private ArrayList<PointBean> mCurrentControlPointBeans = new ArrayList<>();
    //二级缓存使用的画笔
    private Paint mBufferPaint = new Paint();
    //临时路径对象
    private Path mPath = new Path();
    private PathMeasure mPathMeasure = new PathMeasure();
    //当前距离起点的长度，辅助绘制曲线使用
    private float mCurrentDistance;
    //截取曲线的起点位置
    private float mStartPosition;
    //粒度曲线
    private Path mGradingPath = new Path();
    //上一个粒度大小，上一次笔画宽度，辅助计算粒度梯度使用
    private float mLastPenSize = StrokeWidthConstant.PEN_SIZE_MIN;
    //绘制回调对象
    private Runnable mCallBack;
    private PorterDuffXfermode mClearDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
    //自定义橡皮擦精确因素
    private int mEraserGrading;

    public void setDrawCallBack(Runnable mCallBack) {
        this.mCallBack = mCallBack;
    }

    public void onDestory() {
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

    public void init(Canvas canvas, Paint mPaint) {
        if (canvas == null) {
            Log.i("handwriting_module:", "DrawingManager init is failed");
            return;
        }
        /**
         * 画布
         * 画笔
         * bitmap对象，保存数据
         */
        mBufferBitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
//        mBufferBitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_4444);
//        mBufferBitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.RGB_565);
        mBufferCanvas = new Canvas(mBufferBitmap);
        this.mDrawingPaint = mPaint;
    }

//    public void init(int width, int height, Paint mPaint) {
//        /**
//         * 画布
//         * 画笔
//         * bitmap对象，保存数据
//         */
//        mBufferBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
////        mBufferBitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_4444);
////        mBufferBitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.RGB_565);
//        mBufferCanvas = new Canvas(mBufferBitmap);
//        this.mDrawingPaint = mPaint;
//    }

    public Bitmap getBitmap() {
        return mBufferBitmap;
    }

    public void onDraw(Canvas canvas) {
        if (Looper.getMainLooper().getThread() != Thread.currentThread()) {
            throw new RuntimeException("绘制发生错误，不能在非UI线程进行绘制");
        }
        if (mBufferBitmap == null) {
            return;
        }
        canvas.drawBitmap(mBufferBitmap, 0, 0, mDrawingPaint);
    }

    public PointBean getCurrentStrokeLastPoint() {
        if (mCurrentStroke == null) {
            return null;
        }
        return mCurrentStroke.getPointBean(mCurrentStroke.size() - 1);
    }

    /**
     * 接受数据点，封装数据
     * 进行缓存绘制操作
     */
    public void actionDown(@NonNull PointBean point) {
        /**
         * 初始化数据集合
         * 当前笔画添加到笔画集合
         */
        mTempStroke.clear();
        mCurrentControlPointBeans.clear();
        mCurrentStroke = new StrokeBean(ColorConstant.CURRENT_STROKE_COLOR);
        mCurrentStroke.setStrokeStatus(StrokeStatusConstant.CURRENT_STROKE_STATUS);
        mCurrentStroke.setColor(ColorConstant.CURRENT_STROKE_COLOR);
        if (StrokeStatusConstant.CURRENT_STROKE_STATUS != StrokeStatusConstant.STROKE_STATUS_CUSTOM_ERASER) {
            mStrokeManager.addStroke(mCurrentStroke);
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

    public void actionMove(PointBean point) {
        /**
         * 注意：
         * 1）该处计算的是中间点相关属性
         * 2）绘制的是上一段的的曲线
         *
         * 计算宽度、贝塞尔曲线控制点
         * 绘制当前子笔画
         */
        mCurrentStroke.addPointBean(point);
        mTempStroke.add(point);
        //添加橡皮数据
        mCurrentStroke.getGradingBeans().add(point);

        //说明是一个move点，此时无法绘制曲线（至少3个点才能绘制曲线）
        float width = StrokeWidthModel.width(mTempStroke.elementAt(0), mTempStroke.elementAt(1), mTempStroke.elementAt(2));
        mTempStroke.elementAt(1).width = width;
        if (mTempStroke.elementAt(0) == mTempStroke.elementAt(1)) {
            //只有两个点，辅助添加一个控制点
            mCurrentControlPointBeans.add(mTempStroke.elementAt(0));

            //第二个move点以及以上场景，可以绘制曲线
        } else {
            ArrayList<PointBean> mTempControlPointBeans = BeisaierModel.calculateControl(mTempStroke.elementAt(0), mTempStroke.elementAt(1), mTempStroke.elementAt(2), EffectConstant.BEISAIER_PINGHUA_VALUE);
            mCurrentControlPointBeans.add(mTempControlPointBeans.get(0));
            mCurrentControlPointBeans.add(mTempControlPointBeans.get(1));
            //绘制曲线，以mTempStroke中第一个点的属性进行绘制
            mPath.cubicTo(mCurrentControlPointBeans.get(0).x, mCurrentControlPointBeans.get(0).y,
                    mCurrentControlPointBeans.get(1).x, mCurrentControlPointBeans.get(1).y,
                    mTempStroke.elementAt(1).x, mTempStroke.elementAt(1).y);
            //绘制粒度曲线
            drawGraingPath(true);
            //移除已经使用过的控制点
            mCurrentControlPointBeans.remove(0);
            mCurrentControlPointBeans.remove(0);
        }

        //删除第一个数据
        mTempStroke.remove(0);
    }

    private void drawGraingPath(boolean isAddGradingBeans) {
        //细分曲线
        mPathMeasure.setPath(mPath, false);
        float length = mPathMeasure.getLength();
        //当前目标宽度
        float currentWidth = mTempStroke.elementAt(0).width;
        //计算粒子数量,向上取整
        double gradingCount = Math.ceil(CalculateModel.divider(CalculateModel.subtraction(length, mStartPosition), EffectConstant.BEISAIER_GRADING_VALUE));
        //计算粒子宽度,其实粒子宽度设置一个默认大小,其实笔画也带笔锋
        float gradingWidth = CalculateModel.divider(Math.abs(CalculateModel.subtraction(mLastPenSize, mTempStroke.elementAt(0).width)), (float) gradingCount);
        int tempIndex = 0;
        for (int index = 0; index < gradingCount; index++) {
            mCurrentDistance += EffectConstant.BEISAIER_GRADING_VALUE;
            if (mCurrentDistance > mPathMeasure.getLength()) {
                mCurrentDistance = mPathMeasure.getLength();
            }
            /**
             * 正常速度手写，概况如下
             * 粒度细分为0.6f，正常辅助粒度30个左右，折算就是正常有效点间隔18px
             * 15*0.6=9px，粗略以9px密度进行辅助点添加，效果ok
             */
            if (isAddGradingBeans && index % mEraserGrading == 0) {
//            if (isAddGradingBeans) {
                tempIndex++;
                Log.i("handwriting_module:", "进入增加辅助" + tempIndex);
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

    public void actionUp(PointBean point) {
        /**
         * 最后一个点，此时有有两端曲线需要绘制0
         * 前一段正常绘制
         * 后一段通过辅助点进行绘制
         * 结尾需要扩展笔锋逻辑
         */
        mCurrentStroke.addPointBean(point);
        mTempStroke.add(point);

        //添加橡皮数据
        mCurrentStroke.getGradingBeans().add(point);

        float width = StrokeWidthModel.width(mTempStroke.elementAt(0), mTempStroke.elementAt(1), mTempStroke.elementAt(2));
        mTempStroke.elementAt(1).width = width;

        ArrayList<PointBean> mTempControlPointBeans = BeisaierModel.calculateControl(mTempStroke.elementAt(0), mTempStroke.elementAt(1), mTempStroke.elementAt(2), EffectConstant.BEISAIER_PINGHUA_VALUE);
        mCurrentControlPointBeans.add(mTempControlPointBeans.get(0));
        mCurrentControlPointBeans.add(mTempControlPointBeans.get(1));

        //绘制倒数第二段曲线
        mPath.cubicTo(mCurrentControlPointBeans.get(0).x, mCurrentControlPointBeans.get(0).y,
                mCurrentControlPointBeans.get(1).x, mCurrentControlPointBeans.get(1).y,
                mTempStroke.elementAt(1).x, mTempStroke.elementAt(1).y);
        drawGraingPath(true);

        mCurrentControlPointBeans.remove(0);
        mCurrentControlPointBeans.remove(0);
        mTempStroke.remove(0);

        //绘制最后一段,添加辅助绘制点和控制点
        mTempStroke.add(point);
        mCurrentControlPointBeans.add(point);
        mCurrentControlPointBeans.add(point);

        mPath.cubicTo(mCurrentControlPointBeans.get(0).x, mCurrentControlPointBeans.get(0).y,
                mCurrentControlPointBeans.get(1).x, mCurrentControlPointBeans.get(1).y,
                mTempStroke.elementAt(1).x, mTempStroke.elementAt(1).y);
        drawGraingPath(true);

        //处理自定义橡皮檫功能
        if (StrokeStatusConstant.CURRENT_STROKE_STATUS == StrokeStatusConstant.STROKE_STATUS_CUSTOM_ERASER) {
            handCustomEraser();
        }
    }

    private void handCustomEraser() {
        RectF rect = new RectF();
        mPath.close();
        mPath.computeBounds(rect, true);
        Region region = new Region();
        region.setPath(mPath, new Region((int) rect.left, (int) rect.top, (int) rect.right, (int) rect.bottom));
        Vector<StrokeBean> data = mStrokeManager.getDefaultData();
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
                mStrokeManager.getRecycleData().add(strokeBean);
            }
        }
        drawStroke();
    }

    public void clear() {
        if (mStrokeManager != null) {
            mStrokeManager.clear();
        }
        clearCanvas();
    }

    public void reset() {
        mStrokeManager = new StrokeManager();
        clearCanvas();
        if (mCallBack != null) {
            mCallBack.run();
        }
    }

    private void clearCanvas() {
        if (mBufferCanvas != null && mBufferPaint != null) {
            mBufferCanvas.drawColor(Color.parseColor(ColorConstant.COLOR_NORMAL_CLEAR), PorterDuff.Mode.CLEAR);
        }
    }

    /**
     * 恢复数据
     */
    public void recover(Runnable callBack) {
        if (callBack == null || mStrokeManager == null || mStrokeManager.recycleSize() == 0) {
            return;
        }
        mStrokeManager.recover();
        //重绘数据
        drawStroke();
    }

    /**
     * 撤销数据
     */
    public void revocaton(Runnable callBack) {
        if (callBack == null || mStrokeManager == null || mStrokeManager.size() == 0) {
            return;
        }
        mStrokeManager.revocation();
        //重绘数据
        drawStroke();
    }

    /**
     * 重置数据
     */
    public void setData(StrokeManager strokeManager) {
        if (strokeManager == null || mCallBack == null) {
            return;
        }
        mStrokeManager = strokeManager;
        drawStroke();
    }

    /**
     * 绘制数据
     */
    public void drawStroke() {
        // TODO: 2019/11/26 此处可以用线程池进行优化
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                /**
//                 * 绘制数据
//                 * 回调处理
//                 */
//                clearCanvas();
//                int size = mStrokeManager.size();
//                for (int index = 0; index < size; index++) {
//                    StrokeBean stroke = mStrokeManager.getStroke(index);
//                    for (int innerIndex = 0; innerIndex < stroke.size(); innerIndex++) {
//                        PointBean point = stroke.getPointBean(innerIndex);
//                        if (innerIndex == 0) {
//                            mCurrentControlPointBeans.clear();
//                            mTempStroke.clear();
//                            mTempStroke.add(point);
//                            //添加一个辅助数据，适配曲线相关计算模型
//                            mTempStroke.add(point);
//                            mBufferPaint.reset();
//                            mBufferPaint.setStyle(Paint.Style.STROKE);
//                            mBufferPaint.setAntiAlias(true);
//                            mBufferPaint.setFilterBitmap(true);
//                            mBufferPaint.setStrokeJoin(Paint.Join.ROUND);
//                            mBufferPaint.setColor(Color.parseColor(stroke.getColor()));
//
//                            mPath.reset();
//                            mPath.moveTo(mTempStroke.elementAt(0).x, mTempStroke.elementAt(0).y);
//
//                            mStartPosition = 0.0f;
//                            mCurrentDistance = 0.0f;
//                        } else if (innerIndex == 1) {
//                            mTempStroke.add(point);
//                            mCurrentControlPointBeans.add(mTempStroke.elementAt(0));
//                            mTempStroke.remove(0);
//                        } else {
//                            mTempStroke.add(point);
//                            ArrayList<PointBean> mTempControlPointBeans = BeisaierModel.calculateControl(mTempStroke.elementAt(0), mTempStroke.elementAt(1), mTempStroke.elementAt(2), EffectConstant.BEISAIER_PINGHUA_VALUE);
//                            mCurrentControlPointBeans.add(mTempControlPointBeans.get(0));
//                            mCurrentControlPointBeans.add(mTempControlPointBeans.get(1));
//                            //绘制曲线，以mTempStroke中第一个点的属性进行绘制
//                            mPath.cubicTo(mCurrentControlPointBeans.get(0).x, mCurrentControlPointBeans.get(0).y,
//                                    mCurrentControlPointBeans.get(1).x, mCurrentControlPointBeans.get(1).y,
//                                    mTempStroke.elementAt(1).x, mTempStroke.elementAt(1).y);
//                            //绘制粒度曲线
//                            drawGraingPath();
//                            //移除已经使用过的控制点
//                            mCurrentControlPointBeans.remove(0);
//                            mCurrentControlPointBeans.remove(0);
//                            mTempStroke.remove(0);
//
//                            //绘制最后一段,添加辅助绘制点和控制点
//                            if (innerIndex == stroke.size() - 1) {
//                                mTempStroke.add(point);
//                                mCurrentControlPointBeans.add(point);
//                                mCurrentControlPointBeans.add(point);
//
//                                mPath.cubicTo(mCurrentControlPointBeans.get(0).x, mCurrentControlPointBeans.get(0).y,
//                                        mCurrentControlPointBeans.get(1).x, mCurrentControlPointBeans.get(1).y,
//                                        mTempStroke.elementAt(1).x, mTempStroke.elementAt(1).y);
//                                drawGraingPath();
//                            }
//                        }
//                    }
//                }
//                if (callBack != null) {
//                    callBack.run();
//                }
//            }
//        }).start();

        //重新绘制数据，需要保存当前画笔状态
        int tempStatus = StrokeStatusConstant.CURRENT_STROKE_STATUS;
        clearCanvas();
        int size = mStrokeManager.size();
        for (int index = 0; index < size; index++) {
            StrokeBean stroke = mStrokeManager.getStroke(index);
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
                    drawGraingPath(false);
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
                        drawGraingPath(false);
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

    public StrokeManager getData() {
        return mStrokeManager;
    }
}
