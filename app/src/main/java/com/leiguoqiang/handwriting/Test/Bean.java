package com.leiguoqiang.handwriting.Test;

/**
 * @author leiguoqiang
 * contact: 274764936
 */
public class Bean {

//    private float[][] result = new float[Integer.MAX_VALUE][];
    private float[][] result = {{1,2},{2,3}};

    public float[][] getResult() {
        return result;
    }

    public void setResult(float[][] result) {
        this.result = result;
//        this.result[2] = new float[]{1, 2};
    }

    public void addBean(float x, float y) {
        int length = this.result.length;
        result[length] = new float[]{x, y};
    }

}
