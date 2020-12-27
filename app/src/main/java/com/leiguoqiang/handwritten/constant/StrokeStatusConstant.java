package com.leiguoqiang.handwritten.constant;

/**
 * @author leiguoqiang
 * contact: 274764936
 */
public class StrokeStatusConstant {
    public static final int STROKE_STATUS_PEN = 1;
    public static final int STROKE_STATUS_CUSTOM_ERASER = 2;
    public static final int STROKE_STATUS_NORMAL_ERASER = 3;
    public static final int STROKE_STATUS_BRUSH = 4;
    public volatile static int CURRENT_STROKE_STATUS = STROKE_STATUS_PEN;
}
