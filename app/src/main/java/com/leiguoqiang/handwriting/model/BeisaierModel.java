package com.leiguoqiang.handwriting.model;

import android.util.Log;

import com.leiguoqiang.handwriting.constant.TagConstant;
import com.leiguoqiang.handwriting.entity.PointBean;

import java.util.ArrayList;

/**
 * @author leiguoqiang
 * contact: 274764936
 * 贝塞尔曲线算法模型
 */
public class BeisaierModel {


    /**
     * 获取临时点：用于计算贝塞尔曲线控制点
     * 实现方案：曲线拟合算法,正弦函数
     * 注意：参数的参数，是有一定的方向性的
     *
     * @param pointA
     * @param pointB
     * @param smoothValue： 平滑因子，值越大，曲线越平滑（取值范围0-1） 其实就是在AB两点连线之间有一个点(点C)将AB线段分成两段，
     *                     BC/AC=smoothVaule
     * @return
     */
    private static PointBean calculateTempPoint(PointBean pointA, PointBean pointB, float smoothValue) {
        float x = 0.0f;
        float y = 0.0f;
        try {
            float ab = CalculateModel.distance(pointA, pointB);
            float ac = CalculateModel.multiple(ab, (1 - smoothValue));
            float distanceY = Math.abs(CalculateModel.subtraction(pointA.y, pointB.y));
            float tempDistanceY = CalculateModel.divider(CalculateModel.multiple(distanceY, ac), ab);

            //A点在B点下方
            if (pointA.y >= pointB.y) {
                y = CalculateModel.subtraction(pointA.y, tempDistanceY);
            } else if (pointA.y < pointB.y) {
                y = CalculateModel.add(pointA.y, tempDistanceY);
            }

            float distanceX = Math.abs(CalculateModel.subtraction(pointA.x, pointB.x));
            float tempDistanceX = CalculateModel.divider(CalculateModel.multiple(distanceX, ac), ab);
            //A点在B点左方
            if (pointA.x <= pointB.x) {
                x = CalculateModel.add(pointA.x, tempDistanceX);
            } else {
                x = CalculateModel.subtraction(pointA.x, tempDistanceX);
            }
        } catch (Exception e) {
            Log.i("handwriting_module:", "构建贝塞尔曲线时，获取控制点所需的临时点，发生错误");
        }
        return new PointBean(x, y, 0l);
    }

    /**
     * 获取两个控制点
     * 计算控制点：根据有效的3个事件点，根据曲线拟合算法，分别计算出AB段、BC段的贝塞尔曲线控制点
     *
     * @param pointA：起始点
     * @param pointB:中间点
     * @param pointC：尾部点
     * @param smoothValue
     * @return
     */
    public static ArrayList<PointBean> calculateControl(PointBean pointA, PointBean pointB, PointBean pointC, float smoothValue) {
        ArrayList<PointBean> result = new ArrayList<>();
        try {
            PointBean tempAB = calculateTempPoint(pointA, pointB, smoothValue);
            PointBean tempCB = calculateTempPoint(pointC, pointB, smoothValue);
            float distance_AB = CalculateModel.distance(pointA, pointB);
            float distance_BC = CalculateModel.distance(pointC, pointB);

//            float tempSmoothValue = CalculateModel.divider(distance_BC, distance_AB);
//            PointBean tempAB_CB = calculateTempPoint(tempAB, tempCB, tempSmoothValue);
            PointBean tempAB_CB = calculateTempPoint(tempCB, tempAB, smoothValue);
            //点tempAB_CB平移到点pointB,计算X、Y方向的矢量差值
            float tempX = CalculateModel.subtraction(pointB.x, tempAB_CB.x);
            float tempY = CalculateModel.subtraction(pointB.y, tempAB_CB.y);
            result.add(new PointBean(CalculateModel.add(tempAB.x, tempX), CalculateModel.add(tempAB.y, tempY), 0l));
            result.add(new PointBean(CalculateModel.add(tempCB.x, tempX), CalculateModel.add(tempCB.y, tempY), 0l));

//            Log.i("=====", "" + result.get(0).x + "__" + result.get(0).y);
//            Log.i("=====", "" + pointB.x + "__" + pointB.y);
//            Log.i("=====", "" + result.get(1).x + "__" + result.get(1).y);

            Log.i("========", "距离11=" + CalculateModel.distance(result.get(0), pointB));
            Log.i("========", "距离22=" + CalculateModel.distance(result.get(1), pointB));
        } catch (Exception e) {
            Log.i(TagConstant.LOG_TAG, "计算控制点发生错误");
        }
        return result;
    }
}
