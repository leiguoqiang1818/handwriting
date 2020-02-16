package com.leiguoqiang.handwriting.commonimpl;

import com.leiguoqiang.handwriting.api.DrawCommonedApi;
import com.leiguoqiang.handwriting.manager.DrawingManager;

/**
 * @author leiguoqiang
 * contact: 274764936
 * 撤销命令（上一个操作撤销）
 */
public class RevocationCommoned implements DrawCommonedApi {

    private DrawingManager manager;

    public RevocationCommoned(DrawingManager manager) {
        this.manager = manager;
    }

    @Override
    public void excutor(Runnable runnable) {
        manager.revocaton(runnable);
    }
}
