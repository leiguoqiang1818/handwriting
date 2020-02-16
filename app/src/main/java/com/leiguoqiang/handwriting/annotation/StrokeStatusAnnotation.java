package com.leiguoqiang.handwriting.annotation;

import androidx.annotation.IntDef;

import com.leiguoqiang.handwriting.constant.StrokeStatusConstant;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author leiguoqiang
 * contact: 274764936
 */
@IntDef({StrokeStatusConstant.STROKE_STATUS_PEN, StrokeStatusConstant.STROKE_STATUS_CUSTOM_ERASER, StrokeStatusConstant.STROKE_STATUS_NORMAL_ERASER,
        StrokeStatusConstant.STROKE_STATUS_BRUSH})
@Retention(RetentionPolicy.SOURCE)
public @interface StrokeStatusAnnotation {
}
