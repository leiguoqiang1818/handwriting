package com.leiguoqiang.handwriting.commonimpl;

import com.leiguoqiang.handwriting.api.DrawCommonedApi;
import com.leiguoqiang.handwriting.manager.DrawingManager;

/**
 * @author leiguoqiang
 * contact: 274764936
 * 清除命令
 */
public class ClearCommoned implements DrawCommonedApi {

    private DrawingManager manager;

    public ClearCommoned(DrawingManager manager) {
        this.manager = manager;
    }

    @Override
    public void excutor(Runnable runnable) {
        manager.clear();
    }
}
