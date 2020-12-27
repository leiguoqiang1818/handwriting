package com.leiguoqiang.handwritten.model;

import android.util.Log;

import com.leiguoqiang.handwritten.entity.PointBean;

import java.math.BigDecimal;

/**
 * @author leiguoqiang
 * contact: 274764936
 */
public class CalculateModel {

    public static float add(float a, float b) {
        float result = new BigDecimal(a).add(new BigDecimal(b)).floatValue();
        return Float.isNaN(result) ? 0.0f : result;
    }

    public static float subtraction(float a, float b) {
        float result = new BigDecimal(a).subtract(new BigDecimal(b)).floatValue();
        return Float.isNaN(result) ? 0.0f : result;
    }

    public static float subtraction(long a, long b) {
        float result = new BigDecimal(a).subtract(new BigDecimal(b)).longValue();
        return Float.isNaN(result) ? 0.0f : result;
    }

    public static float multiple(float a, float b) {
        float result = new BigDecimal(a).multiply(new BigDecimal(b)).floatValue();
        return Float.isNaN(result) ? 0.0f : result;
    }

    public static float divider(float a, float b) {
        float result = 0.0f;
        try {
            result = new BigDecimal(a).divide(new BigDecimal(b), 20, BigDecimal.ROUND_HALF_UP).floatValue();
        } catch (Exception e) {
        }
        return Float.isNaN(result) ? 0.0f : result;
    }

    public static float divider(long a, long b) {
        float result = 0;
        try {
            result = new BigDecimal(a).divide(new BigDecimal(b), 20, BigDecimal.ROUND_HALF_UP).longValue();
        } catch (Exception e) {
        }
        return Float.isNaN(result) ? 0.0f : result;
    }

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
        }
        return Float.isNaN(result) ? 0.0f : result;
    }

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
