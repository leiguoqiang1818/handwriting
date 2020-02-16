package com.leiguoqiang.handwriting.entity;

import android.graphics.Point;
import android.graphics.Rect;

import java.io.Serializable;
import java.util.Vector;

/**
 * @author leiguoqiang
 * contact: 274764936
 * 每条笔画数据（对应一套完整的事件）
 */
public class StrokeBean implements Serializable {

    /**
     * 点集合
     * 笔画颜色
     * private Path stroke;
     * private int location;//删除笔划原来所在笔划列表中的位置
     * private int priority;//所有笔划执行撤销动作的优先级
     * private Vector<TimePoint> points; //拟合后的几个点
     * private Vector<TimePoint> originPoints;//最原始的几个点
     * private Vector<Float> originWidth;
     */

    private Vector<PointBean> pointBeans = new Vector<>();
    private Vector<PointBean> gradingBeans = new Vector<>();
    private String color = "#000000";
    //画笔状态
    private int strokeStatus;

    public int getStrokeStatus() {
        return strokeStatus;
    }

    public void setStrokeStatus(int strokeStatus) {
        this.strokeStatus = strokeStatus;
    }

    public StrokeBean() {

    }

    public Vector<PointBean> getGradingBeans() {
        return gradingBeans;
    }

    public StrokeBean(String color) {
        this.color = color;
    }

    public void addPointBean(PointBean pointBean) {
        if (pointBean == null) {
            return;
        }
        pointBeans.add(pointBean);
    }

    public void addGradingPointBean(PointBean pointBean) {
        if (pointBean == null) {
            return;
        }
        gradingBeans.add(pointBean);
    }

    public PointBean getPointBean(int index) {
        if (pointBeans.size() == 0 || index > size() - 1) {
            return null;
        }
        return pointBeans.elementAt(index);
    }

    public int size() {
        return pointBeans.size();
    }

    public Vector<PointBean> getPointBeans() {
        return pointBeans;
    }

    public void setPointBeans(Vector<PointBean> pointBeans) {
        this.pointBeans = pointBeans;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setGradingBeans(Vector<PointBean> gradingBeans) {
        this.gradingBeans = gradingBeans;
    }

    public StrokeBean creatNew() {
        StrokeBean strokeBean = new StrokeBean(color);
        strokeBean.setStrokeStatus(strokeStatus);
        strokeBean.setColor(color);
        for (PointBean bean : pointBeans) {
            strokeBean.addPointBean(bean.createNew());
        }
        for (PointBean bean : gradingBeans) {
            strokeBean.addGradingPointBean(bean.createNew());
        }
        return strokeBean;
    }
}
