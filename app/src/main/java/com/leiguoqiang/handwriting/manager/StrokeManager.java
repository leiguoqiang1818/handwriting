package com.leiguoqiang.handwriting.manager;


import com.leiguoqiang.handwriting.entity.StrokeBean;

import java.io.Serializable;
import java.util.Vector;

/**
 * @author leiguoqiang
 * contact: 274764936
 * 笔画策略管理
 */
public class StrokeManager implements Serializable {

    /**
     * 所有笔画集合
     * 撤销、删除的笔画集合（最多10笔）
     * 注意：撤销、恢复是两个反向的操作
     */

    //默认所有正常数据
    private Vector<StrokeBean> defaultData = new Vector<>();
    //删除、撤销之后，回收的数据
    private Vector<StrokeBean> recycleData = new Vector<>();

    public StrokeManager creatNew() {
        StrokeManager strokeManager = new StrokeManager();
        for (StrokeBean bean : defaultData) {
            strokeManager.addStroke(bean.creatNew());
        }
        for (StrokeBean bean : recycleData) {
            strokeManager.getRecycleData().add(bean.creatNew());
        }
        return strokeManager;
    }

    public Vector<StrokeBean> getDefaultData() {
        return defaultData;
    }

    public void setDefaultData(Vector<StrokeBean> defaultData) {
        this.defaultData = defaultData;
    }

    public Vector<StrokeBean> getRecycleData() {
        return recycleData;
    }

    public void setRecycleData(Vector<StrokeBean> recycleData) {
        this.recycleData = recycleData;
    }

    public StrokeBean getStroke(int index) {
        StrokeBean result = null;
        if (size() > 0) {
            result = defaultData.get(index);
        }
        return result;
    }

    public int size() {
        return defaultData.size();
    }

    public int recycleSize() {
        return recycleData.size();
    }

    /**
     * 添加新数据时候，清空所有回收的数据
     *
     * @param strokeBean
     */
    public void addStroke(StrokeBean strokeBean) {
        defaultData.add(strokeBean);
        recycleData.clear();
    }

    /**
     * 恢复数据
     */
    public void recover() {
        if (recycleData.size() != 0) {
            StrokeBean remove = recycleData.remove(recycleData.size() - 1);
            defaultData.add(remove);
        }
    }

    /**
     * 撤销数据
     */
    public void revocation() {
        if (defaultData.size() != 0) {
            StrokeBean remove = defaultData.remove(defaultData.size() - 1);
            recycleData.add(remove);
        }
    }

    public void save() {
        // TODO: 2019/11/26 暂且不实现，需要配合数据库
    }

    public void clear() {
        defaultData.clear();
        recycleData.clear();
    }
}
