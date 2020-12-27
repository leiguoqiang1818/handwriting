package com.leiguoqiang.handwritten.entity;

import java.io.Serializable;
import java.util.Vector;

/**
 * @author leiguoqiang
 * contact: 274764936
 */
public class StrokeBean implements Serializable {

    private Vector<PointBean> pointBeans = new Vector<>();
    private Vector<PointBean> gradingBeans = new Vector<>();
    private String color = "#000000";
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
