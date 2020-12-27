package com.leiguoqiang.handwritten.manager;


import com.leiguoqiang.handwritten.entity.StrokeBean;

import java.io.Serializable;
import java.util.Vector;

/**
 * @author leiguoqiang
 * contact: 274764936
 */
public class StrokeManager implements Serializable {

    private Vector<StrokeBean> defaultData = new Vector<>();
    private Vector<StrokeBean> recycleData = new Vector<>();

    public StrokeManager creatNew() {
        StrokeManager strokeManager = new StrokeManager();
        for (StrokeBean bean : defaultData) {
            strokeManager.h(bean.creatNew());
        }
        for (StrokeBean bean : recycleData) {
            strokeManager.c().add(bean.creatNew());
        }
        return strokeManager;
    }

    public Vector<StrokeBean> a() {
        return defaultData;
    }

    public void b(Vector<StrokeBean> defaultData) {
        this.defaultData = defaultData;
    }

    public Vector<StrokeBean> c() {
        return recycleData;
    }

    public void d(Vector<StrokeBean> recycleData) {
        this.recycleData = recycleData;
    }

    public StrokeBean e(int index) {
        StrokeBean result = null;
        if (f() > 0) {
            result = defaultData.get(index);
        }
        return result;
    }

    public int f() {
        return defaultData.size();
    }

    public int g() {
        return recycleData.size();
    }

    public void h(StrokeBean strokeBean) {
        defaultData.add(strokeBean);
        recycleData.clear();
    }

    public void i() {
        if (recycleData.size() != 0) {
            StrokeBean remove = recycleData.remove(recycleData.size() - 1);
            defaultData.add(remove);
        }
    }

    public void h() {
        if (defaultData.size() != 0) {
            StrokeBean remove = defaultData.remove(defaultData.size() - 1);
            recycleData.add(remove);
        }
    }

    public void j() {
    }

    public void k() {
        defaultData.clear();
        recycleData.clear();
    }
}
