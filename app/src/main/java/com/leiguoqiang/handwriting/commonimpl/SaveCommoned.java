package com.leiguoqiang.handwriting.commonimpl;

import android.widget.Toast;

import com.leiguoqiang.handwriting.api.DrawCommonedApi;
import com.leiguoqiang.handwriting.manager.DrawingManager;

/**
 * @author leiguoqiang
 * contact: 274764936
 * 保存命令
 * 后续开发，需要数据库支持
 */
public class SaveCommoned implements DrawCommonedApi {

    private DrawingManager manager;

    public SaveCommoned(DrawingManager manager) {
        this.manager = manager;
    }

    @Override
    public void excutor(Runnable runnable) {
        // TODO: 2019/11/26 暂不实现，需要数据库支持
    }
}
