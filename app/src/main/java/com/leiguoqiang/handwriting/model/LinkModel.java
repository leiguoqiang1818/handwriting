package com.leiguoqiang.handwriting.model;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import com.leiguoqiang.handwriting.entity.PointBean;

/**
 * @author leiguoqiang
 * contact: 274764936
 * 两条线段衔接模型，主要原理：根据切线原理，绘制扇形，两条切线确定（value=线宽差值/2,两条线的外边分别+、-value）
 * 主要功能：处理两条线段交界处，断连场景
 */
public class LinkModel {
    // TODO: 2019/11/17

    /**
     * 连接处直接画圆
     *
     * @param pointPresure
     * @param pointCurrent
     */
    public void drawCircle(PointBean pointPresure, PointBean pointCurrent, Canvas canvas, Paint paint) {
        try {
            float radius = CalculateModel.divider(CalculateModel.add(pointPresure.width, pointCurrent.width), 2.0f);
            canvas.drawCircle(pointCurrent.x, pointCurrent.y, radius, paint);
        } catch (Exception e) {
            Log.i("handwriting_module:", "线条连接处画圆发生错误:" + e.toString());
        }
    }
}
