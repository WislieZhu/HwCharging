package com.wislie.charging.helper;

/**
 * author : Wislie
 * e-mail : 254457234@qq.comn
 * date   : 2020-03-05 10:52
 * desc   :
 * version: 1.0
 */
public abstract class Builder<T extends Builder> {
    /**
     * 颜色
     */
    public int color;

    /*** path系数 */
    public float lineSmoothness;

    public T setColor(int color) {
        this.color = color;
        return (T)this;
    }

    public T setLineSmoothness(float lineSmoothness) {
        this.lineSmoothness = lineSmoothness;
        return (T)this;
    }

    public abstract Object build();
}
