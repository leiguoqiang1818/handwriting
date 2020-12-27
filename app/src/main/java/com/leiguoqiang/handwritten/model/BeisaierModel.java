package com.leiguoqiang.handwritten.model;

import android.util.Log;

import com.leiguoqiang.handwritten.constant.TagConstant;
import com.leiguoqiang.handwritten.entity.PointBean;

import java.util.ArrayList;

/**
 * @author leiguoqiang
 * contact: 274764936
 */
public class BeisaierModel {

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
        }
        return new PointBean(x, y, 0l);
    }

    public static ArrayList<PointBean> calculateControl(PointBean pointA, PointBean pointB, PointBean pointC, float smoothValue) {
        ArrayList<PointBean> result = new ArrayList<>();
        try {
            PointBean tempAB = calculateTempPoint(pointA, pointB, smoothValue);
            PointBean tempCB = calculateTempPoint(pointC, pointB, smoothValue);

            PointBean tempAB_CB = calculateTempPoint(tempCB, tempAB, smoothValue);
            float tempX = CalculateModel.subtraction(pointB.x, tempAB_CB.x);
            float tempY = CalculateModel.subtraction(pointB.y, tempAB_CB.y);
            result.add(new PointBean(CalculateModel.add(tempAB.x, tempX), CalculateModel.add(tempAB.y, tempY), 0l));
            result.add(new PointBean(CalculateModel.add(tempCB.x, tempX), CalculateModel.add(tempCB.y, tempY), 0l));

        } catch (Exception e) {
        }
        return result;
    }
}
