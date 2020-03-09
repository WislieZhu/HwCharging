package com.wislie.charging.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;

import com.wislie.charging.helper.Builder;
import com.wislie.charging.helper.ChargingHelper;

/**
 * author : Wislie
 * e-mail : 254457234@qq.comn
 * date   : 2020-03-04 08:00
 * desc   : 发射器
 * version: 1.0
 */
public class BubbleEmitter {

    /*** path系数 */
    private float lineSmoothness;

    /*** 发射器颜色 */
    private int color;

    /*** 发射器的3个点,用来连接成弧线 */
    private PointF[] mPoints = new PointF[3];

    /*** 发射器路径 */
    private Path mPath = new Path();

    public BubbleEmitter(EmitterBuilder builder) {
        if (builder.lineSmoothness <= 0) throw new IllegalArgumentException("lineSmoothness <= 0");
        this.lineSmoothness = builder.lineSmoothness;
        this.color = builder.color;
    }

    static class EmitterBuilder extends Builder<EmitterBuilder> {

        @Override
        public BubbleEmitter build() {
            return new BubbleEmitter(this);
        }
    }

    /**
     * 生成点集合,并连接在一起
     */
    public void generatePoints(float emitterCenterX, float emitterCenterY, float emitterRadius,
                               float startAngle, float sweepAngle) {
        mPoints = ChargingHelper.generatePoints(emitterCenterX, emitterCenterY,
                emitterRadius, startAngle, sweepAngle);
        //连接路径
        ChargingHelper.connectToPath(mPath, mPoints, lineSmoothness);
    }

    /**
     * 设置颜色
     *
     * @param color
     */
    public void setColor(int color) {
        this.color = color;
    }

    /**
     * 绘制发射器
     */
    public void drawEmitter(Canvas canvas, Paint paint) {
        paint.setColor(color);
        canvas.drawPath(mPath, paint);
    }

}
