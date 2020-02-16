package com.leiguoqiang.handwriting.manager;

import com.leiguoqiang.handwriting.api.DrawCommonedApi;
import com.leiguoqiang.handwriting.commonimpl.ClearCommoned;
import com.leiguoqiang.handwriting.commonimpl.RecoverCommoned;
import com.leiguoqiang.handwriting.commonimpl.RevocationCommoned;
import com.leiguoqiang.handwriting.commonimpl.SaveCommoned;

/**
 * @author leiguoqiang
 * contact: 274764936
 * 绘制请求对象（请求者）
 * 功能：处理全部清除、撤销、恢复、保存等操作
 */
public class DrawOptionManager {

    private DrawCommonedApi mClearCommoned;
    private DrawCommonedApi mSaveCommoned;
    private DrawCommonedApi mRevocationCommoned;
    private DrawCommonedApi mRecoverCommoned;


    public DrawOptionManager(DrawingManager drawingManager) {
        mClearCommoned = new ClearCommoned(drawingManager);
        mSaveCommoned = new SaveCommoned(drawingManager);
        mRevocationCommoned = new RevocationCommoned(drawingManager);
        mRecoverCommoned = new RecoverCommoned(drawingManager);
    }

    public void clear() {
        if (mClearCommoned != null) {
            mClearCommoned.excutor(null);
        }
    }

    public void revocation(Runnable runnable) {
        if (mRevocationCommoned != null) {
            mRevocationCommoned.excutor(runnable);
        }
    }

    public void save() {
        if (mSaveCommoned != null) {
            mSaveCommoned.excutor(null);
        }
    }

    public void recover(Runnable runnable) {
        if (mRecoverCommoned != null) {
            mRecoverCommoned.excutor(runnable);
        }
    }

}
