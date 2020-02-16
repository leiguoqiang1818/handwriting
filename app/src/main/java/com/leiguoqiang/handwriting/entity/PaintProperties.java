package com.leiguoqiang.handwriting.entity;

import com.leiguoqiang.handwriting.constant.ColorConstant;
import com.leiguoqiang.handwriting.constant.StrokeStatusConstant;
import com.leiguoqiang.handwriting.constant.StrokeWidthConstant;

/**
 * @author leiguoqiang
 * contact: 274764936
 * 画笔属性
 */
public class PaintProperties {

    public String color = ColorConstant.COLOR_BLACK;
    public int status = StrokeStatusConstant.STROKE_STATUS_PEN;
    public float width = StrokeWidthConstant.PEN_SIZE_NORMAL;

}
