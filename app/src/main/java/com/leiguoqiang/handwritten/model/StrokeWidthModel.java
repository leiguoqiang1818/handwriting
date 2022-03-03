package com.leiguoqiang.handwritten.model;

import android.util.Log;

import com.leiguoqiang.handwritten.constant.EffectConstant;
import com.leiguoqiang.handwritten.constant.StrokeWidthConstant;
import com.leiguoqiang.handwritten.entity.PointBean;

/**
 * @author leiguoqiang
 * contact: 274764936
 */
public class StrokeWidthModel {
    public static float width(PointBean pointPrevious, PointBean pointCurrent, PointBean pointNext) {
        return 6;
    }

    /**
     * 线性方法计算宽度
     * 综合速度和压感
     *
     * @param pointCurrent
     * @return
     */
    private static float baseWidth(PointBean pointCurrent, PointBean pointNext) {
        float result = 0.0f;
        return result;
    }

}
