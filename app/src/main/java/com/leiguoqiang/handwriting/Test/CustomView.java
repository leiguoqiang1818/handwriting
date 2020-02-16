package com.leiguoqiang.handwriting.Test;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * @author leiguoqiang
 * contact: 274764936
 */
public class CustomView extends View {

    private int mViewWidth;
    private int mViewHeight;
    private Paint mDeafultPaint = new Paint();

    public CustomView(Context context) {
        super(context);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mViewHeight = getMeasuredHeight();
        mViewWidth = getMeasuredWidth();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.translate(mViewWidth / 2, mViewHeight / 2);          // 平移坐标系

        Path path = new Path();                                     // 创建Path并添加了一个矩形
        path.addRect(-200, -200, 200, 200, Path.Direction.CW);

        Path dst = new Path();                                      // 创建用于存储截取后内容的 Path

        PathMeasure measure = new PathMeasure(path, false);         // 将 Path 与 PathMeasure 关联

        // 截取一部分存入dst中，并使用 moveTo 保持截取得到的 Path 第一个点的位置不变
        measure.getSegment(200, 600, dst, false);

        mDeafultPaint.setStyle(Paint.Style.STROKE);
        mDeafultPaint.setStrokeWidth(4.0f);
        mDeafultPaint.setColor(Color.parseColor("#000000"));
        canvas.drawPath(path, mDeafultPaint);
        mDeafultPaint.setColor(Color.parseColor("#ff0000"));
        canvas.drawPath(dst, mDeafultPaint);
    }
}
