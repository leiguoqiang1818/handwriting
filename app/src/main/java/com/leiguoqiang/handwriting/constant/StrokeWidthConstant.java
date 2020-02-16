package com.leiguoqiang.handwriting.constant;

/**
 * @author leiguoqiang
 * contact: 274764936
 * 线条宽度常量，全局控制
 */
public class StrokeWidthConstant {

    /**
     * 笔画最小尺寸,调节笔锋效果
     */
    public static float PEN_SIZE_MIN = 0.01f;
    /**
     * 钢笔3中常规尺寸
     */
    public static float PEN_SIZE_SMALL = 6.0f;
    public static float PEN_SIZE_NORMAL = 10.0f;
    public static float PEN_SIZE_BIG = 14.0f;

    /**
     * 毛笔3中常规尺寸
     */
    public static float BRUSH_SIZE_SMALL = 20.0f;
    public static float BRUSH_SIZE_NORMAL = 30.0f;
    public static float BRUSH_SIZE_BIG = 40.0f;

    /**
     * 通用橡皮檫常规尺寸
     */
    public static float NORMAL_ERASER_SIZE_SMALL = 20.0f;
    public static float NORMAL_ERASER_SIZE_NORMAL = 30.0f;
    public static float NORMAL_ERASER_SIZE_BIG = 40.0f;

    /**
     * 自定义橡皮檫宽度
     */
    public static float CUSTOM_ERASER_SIZE = 6.0f;

    /**
     * 画笔宽度,默认钢笔效果
     */
    public volatile static float CURRENT_STROKE_SIZE = PEN_SIZE_NORMAL;

    /**
     * 历史钢笔宽度
     */
    public volatile static float LAST_PEN_STROKE_SIZE = CURRENT_STROKE_SIZE;
    /**
     * 历史毛笔宽度
     */
    public volatile static float LAST_BRUSH_STROKE_SIZE = BRUSH_SIZE_SMALL;
    /**
     * 历史通用橡皮檫宽度
     */
    public volatile static float LAST_NORMAL_ERASER_STROKE_SIZE = NORMAL_ERASER_SIZE_SMALL;


}
