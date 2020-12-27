package com.leiguoqiang.handwritten.entity;

import androidx.annotation.Nullable;

import com.leiguoqiang.handwritten.model.CalculateModel;

/**
 * @author leiguoqiang
 * contact: 274764936
 */
public class PointBean {

    public float x;
    public float y;
    public long time;
    public float pressure;
    public float velacity;
    public float width;

    public PointBean() {

    }

    public PointBean(float x, float y, long time) {
        this.x = x;
        this.y = y;
        this.time = time;
    }

    public PointBean(float x, float y, long time, float pressure) {
        this.x = x;
        this.y = y;
        this.time = time;
        this.pressure = pressure;
    }

    /**
     * 计算当前点到下一点之间的滑动速度
     *
     * @param nextPoint
     * @return
     */
    public double calculateVelacity(PointBean nextPoint) {
        velacity = CalculateModel.velocity(this, nextPoint);
        return velacity;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        boolean result = false;
        if (obj instanceof PointBean) {
            if (this.x == ((PointBean) obj).x && this.y == ((PointBean) obj).y) {
                result = true;
            }
        }
        return result;
    }

    public PointBean createNew() {
        PointBean pointBean = null;
        pointBean = new PointBean(x, y, time, pressure);
        pointBean.velacity = velacity;
        pointBean.width = width;
        return pointBean;
    }
}
