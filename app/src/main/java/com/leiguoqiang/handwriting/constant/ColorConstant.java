package com.leiguoqiang.handwriting.constant;

/**
 * @author leiguoqiang
 * contact: 274764936
 */
public class ColorConstant {

    /**
     * 黑色
     */
    public static String COLOR_BLACK = "#000000";

    /**
     * 当前画笔颜色
     */
    public static String CURRENT_STROKE_COLOR = COLOR_BLACK;
    /**
     * 清除色(通用橡皮檫)
     */
    public static String COLOR_NORMAL_CLEAR = "#00000000";
    /**
     * 自定义橡皮檫颜色
     */
    public static String COLOR_CUSTOM_ERASER = "#FF0000";

    /**
     * 钢笔历史颜色
     */
    public static String LAST_COLOR_PEN = CURRENT_STROKE_COLOR;
    /**
     * 毛笔历史颜色
     */
    public static String LAST_COLOR_BRUSH = CURRENT_STROKE_COLOR;

}
