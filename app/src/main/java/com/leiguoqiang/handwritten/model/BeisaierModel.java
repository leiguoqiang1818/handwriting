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
        return new PointBean(x, y, 0l);
    }

    public static ArrayList<PointBean> calculateControl(PointBean pointA, PointBean pointB, PointBean pointC, float smoothValue) {
        ArrayList<PointBean> result = new ArrayList<>();
        return result;
    }
}
