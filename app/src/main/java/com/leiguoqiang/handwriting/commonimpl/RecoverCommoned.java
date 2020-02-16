package com.leiguoqiang.handwriting.commonimpl;

import com.leiguoqiang.handwriting.api.DrawCommonedApi;
import com.leiguoqiang.handwriting.manager.DrawingManager;

/**
 * @author leiguoqiang
 * contact: 274764936
 * 重做（恢复）命令
 */
public class RecoverCommoned implements DrawCommonedApi {

    private DrawingManager manager;

    public RecoverCommoned(DrawingManager manager) {
        this.manager = manager;
    }

    @Override
    public void excutor(Runnable runnable) {
        manager.recover(runnable);
    }
}
