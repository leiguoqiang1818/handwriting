package com.leiguoqiang.handwriting.constant;

/**
 * @author leiguoqiang
 * contact: 274764936
 * 手写笔迹控制因子（效果参数）
 * 改变参数值可以不断优化出最佳手写效果
 */
public interface EffectConstant {

    /**
     * 绘制性能影响因素
     * 1）高精度开关（事件点密集度），打开高精度，处理的事件点越密集，消耗性能越大
     * 2）bitmap像素模式（默认Bitmap.Config.RGB_565），像素模式越高，消耗的性能和内存越大
     * 3）单位曲线细分粒度，粒度越小，消耗性能越大
     * 4）画笔对象增强开关，会牺牲内存和绘制性能
     */

    /**
     * 手写原笔迹平滑影响因素
     * 1）贝塞尔曲线平滑因子，值越大，越平滑
     * 2）曲线细分粒度，粒度越小，过渡越好，越平滑
     */

    /**
     * 手写笔锋影响因素
     * 1）笔画大小梯度，梯度越小，过渡越细腻自然；梯度越大笔锋越大，牺牲笔画细腻度
     * 2）速度强化因子，调节速度的影响值，因子越大，速度对笔画的影响值越大
     */


    /**
     * 贝塞尔曲线平滑因子，按照现有算法，有如下结论
     * value和平滑度关系：类似正比关系
     * 取值范围：0—1
     * 暂且普通采样下0.38最佳
     */
    float BEISAIER_PINGHUA_VALUE = 0.38f;
    /**
     * 细分曲线粒度，粒度越小，过渡越平滑，但会牺牲绘制性能，慎重调试
     * 暂且0.6f最佳
     */
    float BEISAIER_GRADING_VALUE = 0.6f;
    /**
     * 宽度改变的梯度（粒度）：value/1px,每像素允许改变的值
     * 这个需要慎重调试
     * 初始良好效果0.005f
     */
    float WIDTH_CHANGED_GRADIENT = 0.5f;
    /**
     * 点与点之间的距离限制
     * 避免计算错误、过滤无效事件点
     */
    float POINT_DISTANCE = 0.2f;
    /**
     * 速度强化因子，决定速度的影响程度，值越大，影响程度越高
     * 暂且0.4f最佳
     */
    float VELOCITY_INTENSIFY_FACTOR = 0.8f;
    /**
     * 正常手写速度范围控制在0.0-1，超过这个范围就是快速滑动
     */
    float VELACITY_MIN = 0.01f;
    float VELACITY_MAX = 1.0f;
    /**
     * 自定义橡皮擦精度，值越小，橡皮擦捕捉越精确，体现：对橡皮擦曲线的笔直度容忍度越大
     * 注意：与贝塞尔细分曲线粒度配合使用：BEISAIER_GRADING_VALUE
     */
    float CUSTOM_ERASER_GRADING_VALUE = 9.0f;
}
