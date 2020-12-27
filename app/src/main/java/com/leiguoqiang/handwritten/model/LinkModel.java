package com.leiguoqiang.handwritten.model;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import com.leiguoqiang.handwritten.entity.PointBean;

/**
 * @author leiguoqiang
 * contact: 274764936
 */
public class LinkModel {
    public void drawCircle(PointBean pointPresure, PointBean pointCurrent, Canvas canvas, Paint paint) {
        try {
            float radius = CalculateModel.divider(CalculateModel.add(pointPresure.width, pointCurrent.width), 2.0f);
            canvas.drawCircle(pointCurrent.x, pointCurrent.y, radius, paint);
        } catch (Exception e) {
        }
    }
}
