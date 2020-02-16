package com.leiguoqiang.handwriting.constant;

/**
 * @author leiguoqiang
 * contact: 274764936
 * 画笔状态：钢笔，自定义橡皮檫，通用橡皮檫
 */
public class StrokeStatusConstant {

    /**
     * 钢笔
     */
    public static final int STROKE_STATUS_PEN = 1;
    /**
     * 自定义橡皮擦
     */
    public static final int STROKE_STATUS_CUSTOM_ERASER = 2;
    /**
     * 通用橡皮擦
     */
    public static final int STROKE_STATUS_NORMAL_ERASER = 3;
    /**
     * 毛笔
     */
    public static final int STROKE_STATUS_BRUSH = 4;
    /**
     * 当前画笔状态，默认为钢笔
     */
    public volatile static int CURRENT_STROKE_STATUS = STROKE_STATUS_PEN;


}
