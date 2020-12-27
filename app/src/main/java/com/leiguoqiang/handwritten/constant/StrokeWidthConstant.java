package com.leiguoqiang.handwritten.constant;

/**
 * @author leiguoqiang
 * contact: 274764936
 */
public class StrokeWidthConstant {
    public static float PEN_SIZE_MIN = 0.01f;
    public static float PEN_SIZE_SMALL = 6.0f;
    public static float PEN_SIZE_NORMAL = 10.0f;
    public static float PEN_SIZE_BIG = 14.0f;
    public static float BRUSH_SIZE_SMALL = 20.0f;
    public static float BRUSH_SIZE_NORMAL = 30.0f;
    public static float BRUSH_SIZE_BIG = 40.0f;
    public static float NORMAL_ERASER_SIZE_SMALL = 20.0f;
    public static float NORMAL_ERASER_SIZE_NORMAL = 30.0f;
    public static float NORMAL_ERASER_SIZE_BIG = 40.0f;
    public static float CUSTOM_ERASER_SIZE = 6.0f;
    public volatile static float CURRENT_STROKE_SIZE = PEN_SIZE_NORMAL;
    public volatile static float LAST_PEN_STROKE_SIZE = CURRENT_STROKE_SIZE;
    public volatile static float LAST_BRUSH_STROKE_SIZE = BRUSH_SIZE_SMALL;
    public volatile static float LAST_NORMAL_ERASER_STROKE_SIZE = NORMAL_ERASER_SIZE_SMALL;
}
