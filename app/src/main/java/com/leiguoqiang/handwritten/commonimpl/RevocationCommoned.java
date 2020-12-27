package com.leiguoqiang.handwritten.commonimpl;

import com.leiguoqiang.handwritten.api.DrawCommonedApi;
import com.leiguoqiang.handwritten.manager.DrawingManager;

/**
 * @author leiguoqiang
 * contact: 274764936
 */
public class RevocationCommoned implements DrawCommonedApi {

    private DrawingManager manager;

    public RevocationCommoned(DrawingManager manager) {
        this.manager = manager;
    }

    @Override
    public void excutor(Runnable runnable) {
        manager.i(runnable);
    }
}
