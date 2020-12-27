package com.leiguoqiang.handwritten.entity;

import com.leiguoqiang.handwritten.constant.ColorConstant;
import com.leiguoqiang.handwritten.constant.StrokeStatusConstant;
import com.leiguoqiang.handwritten.constant.StrokeWidthConstant;

/**
 * @author leiguoqiang
 * contact: 274764936
 */
public class PaintProperties {

    public String color = ColorConstant.COLOR_BLACK;
    public int status = StrokeStatusConstant.STROKE_STATUS_PEN;
    public float width = StrokeWidthConstant.PEN_SIZE_NORMAL;

}
