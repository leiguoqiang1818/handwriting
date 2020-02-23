package com.leiguoqiang.handwriting.model;

import android.util.Log;

import com.leiguoqiang.handwriting.entity.PointBean;

import java.math.BigDecimal;

/**
 * @author leiguoqiang
 * contact: 274764936
 * 算法模型：曲线拟合点算法，距离算法，笔锋辅助点算法
 */
public class CalculateModel {

    public static float add(float a, float b) {
        float result = new BigDecimal(a).add(new BigDecimal(b)).floatValue();
        return Float.isNaN(result) ? 0.0f : result;

//        float result = KeyImpl.add(a, b);
//        return Float.isNaN(result) ? 0.0f : result;
    }

    public static float subtraction(float a, float b) {
        float result = new BigDecimal(a).subtract(new BigDecimal(b)).floatValue();
        return Float.isNaN(result) ? 0.0f : result;

//        float result = KeyImpl.subtraction(a, b);
//        return Float.isNaN(result) ? 0.0f : result;
    }

    public static float subtraction(long a, long b) {
        float result = new BigDecimal(a).subtract(new BigDecimal(b)).longValue();
        return Float.isNaN(result) ? 0.0f : result;
//
//        float result = KeyImpl.subtraction(a, b);
//        return Float.isNaN(result) ? 0.0f : result;
    }

    public static float multiple(float a, float b) {
        float result = new BigDecimal(a).multiply(new BigDecimal(b)).floatValue();
        return Float.isNaN(result) ? 0.0f : result;
//
//        float result = KeyImpl.multiple(a, b);
//        return Float.isNaN(result) ? 0.0f : result;
    }

    public static float divider(float a, float b) {
        float result = 0.0f;
        try {
            result = new BigDecimal(a).divide(new BigDecimal(b), 20, BigDecimal.ROUND_HALF_UP).floatValue();
        } catch (Exception e) {
            Log.i("handwriting_module:", "除法错误");
        }
        return Float.isNaN(result) ? 0.0f : result;

//        float result = KeyImpl.divider(a, b);
//        return Float.isNaN(result) ? 0.0f : result;
    }

    public static float divider(long a, long b) {
        float result = 0;
        try {
            result = new BigDecimal(a).divide(new BigDecimal(b), 20, BigDecimal.ROUND_HALF_UP).longValue();
        } catch (Exception e) {
            Log.i("handwriting_module:", "除法错误");
        }
        return Float.isNaN(result) ? 0.0f : result;

//        float result = KeyImpl.divider(a, b);
//        return Float.isNaN(result) ? 0.0f : result;
    }

    /**
     * 两点间距离
     *
     * @param pointA
     * @param pointB
     * @return
     */
    public static float distance(PointBean pointA, PointBean pointB) {
        float result = 0.0f;
        if (pointA.equals(pointB)) {
            return result;
        }
        try {
            float distanceX = Math.abs(subtraction(pointA.x, pointB.x));
            float distanceY = Math.abs(subtraction(pointA.y, pointB.y));
            double temp = Math.sqrt(add(multiple(distanceX, distanceX),
                    multiple(distanceY, distanceY)));
            result = (float) temp;
        } catch (Exception e) {
            Log.i("handwriting_module:", "两点距离算法错误");
        }
        return Float.isNaN(result) ? 0.0f : result;
    }

    /**
     * 两点滑动速度
     *
     * @param pointA
     * @param pointB
     * @return
     */
    public static float velocity(PointBean pointA, PointBean pointB) {
        float result = 0.0f;
        try {
            float temp1 = (long) distance(pointA, pointB);
            float temp2 = subtraction(pointB.time, pointA.time);
            result = divider(temp1, temp2);
        } catch (Exception e) {
        }
        return result;
    }

}
