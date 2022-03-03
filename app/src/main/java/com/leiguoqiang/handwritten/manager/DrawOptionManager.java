package com.leiguoqiang.handwritten.manager;

import com.leiguoqiang.handwritten.api.DrawCommonedApi;
import com.leiguoqiang.handwritten.commonimpl.ClearCommoned;
import com.leiguoqiang.handwritten.commonimpl.RecoverCommoned;
import com.leiguoqiang.handwritten.commonimpl.RevocationCommoned;
import com.leiguoqiang.handwritten.commonimpl.SaveCommoned;

/**
 * @author leiguoqiang
 * contact: 274764936
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
