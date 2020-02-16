package com.leiguoqiang.handwriting.model;

import android.util.Log;

import com.leiguoqiang.handwriting.constant.EffectConstant;
import com.leiguoqiang.handwriting.constant.StrokeWidthConstant;
import com.leiguoqiang.handwriting.entity.PointBean;

/**
 * @author leiguoqiang
 * contact: 274764936
 * 笔画宽度算法模型
 * 主要功能：头部处理、尾部处理、宽度处理
 */
public class StrokeWidthModel {
    /**
     * 注意：宽度差=当前点和上一个点的宽度差值
     * 如果当前点为第一个点，则直接做线性操作，反之，做非线性操作
     * 调用时机：至少接受到2个事件点(加上速度因素，至少需要存在两个事件点)，此方法才有效
     *
     * @param pointPrevious：如果没有上一个点，则传当前点
     * @param pointCurrent
     * @return
     */
    public static float width(PointBean pointPrevious, PointBean pointCurrent, PointBean pointNext) {
        /**
         * W_previous + min( abs(k*s - W_previous), distance * K_width_unit_change) ---------->(k * s-W_previous) >= 0
         * 　　W_previous - min( abs(k*s - W_previous), distance * K_width_unit_change) ---------->(k * s-W_previous) < 0
         */
        float result = 0.0f;
        try {
            //非法调用检测
            if (pointNext.equals(pointCurrent)) {
                throw new RuntimeException("非法调用函数：com.leiguoqiang.handwriting.model.StrokeWidthModel.width(...)");
            }
            float tempResult = baseWidth(pointCurrent, pointNext);
            //同一个点，即为起点
            if (pointPrevious.equals(pointCurrent)) {
                result = tempResult;
            } else {
                float distance = CalculateModel.distance(pointPrevious, pointCurrent);
                if (tempResult >= pointPrevious.width) {
                    result = CalculateModel.add(pointPrevious.width, Math.min(Math.abs(tempResult - pointPrevious.width), CalculateModel.multiple(distance, EffectConstant.WIDTH_CHANGED_GRADIENT)));
                    if (result > StrokeWidthConstant.CURRENT_STROKE_SIZE) {
                        result = StrokeWidthConstant.CURRENT_STROKE_SIZE;
                    }
                } else {
                    result = CalculateModel.subtraction(pointPrevious.width, Math.min(Math.abs(tempResult - pointPrevious.width), CalculateModel.multiple(distance, EffectConstant.WIDTH_CHANGED_GRADIENT)));
                    if (result < StrokeWidthConstant.PEN_SIZE_MIN) {
                        result = StrokeWidthConstant.PEN_SIZE_MIN;
                    }
                }
            }
        } catch (Exception e) {
            Log.i("handwriting_module:", "计算线条宽度错误");
        }
        Log.i("handwriting_module:", "线条宽度=" + result);
        return result;
//        return 6;

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
        try {
            float pressureEffct = CalculateModel.multiple(StrokeWidthConstant.CURRENT_STROKE_SIZE, pointCurrent.pressure);
            // TODO: 2019/11/17 需要调试优化处理
            //初步处理原理：分别算出两个因素所造成的影响值，然后再综合作用
            pointCurrent.calculateVelacity(pointNext);
            //此处暂且0-1.0正常范围处理
            // TODO: 2019/11/21  速度超过1.0额外处理，此处做弱化处理
            float tempVelacity = 0.0f;
            if (pointCurrent.velacity > EffectConstant.VELACITY_MAX) {
                tempVelacity = EffectConstant.VELACITY_MAX;
            } else if (pointCurrent.velacity < EffectConstant.VELACITY_MIN) {
                tempVelacity = EffectConstant.VELACITY_MIN;
            } else {
                tempVelacity = pointCurrent.velacity;
            }
            //添加强化因子
            float velacityEffect = CalculateModel.multiple(CalculateModel.multiple(StrokeWidthConstant.CURRENT_STROKE_SIZE, tempVelacity), EffectConstant.VELOCITY_INTENSIFY_FACTOR);
            Log.i("handwriting_module:===", "当前点滑动速度大小=" + pointCurrent.velacity);
            Log.i("handwriting_module:===", "速度影响值=" + velacityEffect);
            result = CalculateModel.divider(CalculateModel.subtraction(CalculateModel.add(StrokeWidthConstant.CURRENT_STROKE_SIZE, pressureEffct), velacityEffect), 2.0f);
        } catch (Exception e) {
            Log.i("handwriting_module:", "线条宽度(线性算法)发生错误");
        }
        return result;
    }

}
