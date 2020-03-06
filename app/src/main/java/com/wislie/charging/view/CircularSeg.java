package com.wislie.charging.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;

import com.wislie.charging.helper.Builder;
import com.wislie.charging.helper.ChargingHelper;
import com.wislie.charging.util.DegreeUtil;

/**
 * author : Wislie
 * e-mail : 254457234@qq.comn
 * date   : 2020-02-29 10:19
 * desc   : 圆环片段
 * version: 1.0
 */
public class CircularSeg {

    private String TAG = "CircularSeg";

    /*** 偏移量 */
    private float offset;

    /*** 偏移增大率 */
    private float offsetRadio;

    /*** 表示偏移量变小 */
    private boolean isDecreaseOffset;

    /*** path系数 */
    private float lineSmoothness;

    /*** 不变的路径 */
    private Path mStickPath;

    /*** 变化的路径 */
    private Path mDynamicPath;

    /*** 圆环中心坐标 */
    private int circularCenterX, circularCenterY;

    /*** 圆环半径 */
    private float circularRadius;

    /*** 中间的弧度值 */
    private double midRad;

    /*** 旋转速度 */
    private float rotateAngle;

    /*** 正常的旋转速率 */
    private float rotateRadio;

    /*** 快速的旋转速率 */
    private float quickRotateRadio;

    /*** 较慢的旋转速率 */
    private float slowRotateRadio;

    /*** 总的旋转角度差 */
    private float dValueAngle;

    /*** 总的旋转角度差 最大值 */
    private float maxDValueAngle;

    /*** 是否是快速旋转 */
    private boolean isQuickRotate;

    /**
     * 圆环 某段圆弧上的3个点, 起始点,中间点,结束点
     * mStickPoints[0], mStickPoints[2]为圆弧上交点,mStickPoints[1]为这段圆弧的中心点
     */
    private PointF[] mStickPoints = new PointF[3];
    /**
     * 圆环凸出部分 某段圆弧上的3个点, 起始点,中间点,结束点
     * mDynamicPoints[2], mDynamicPoints[0]为圆弧上交点,mDynamicPoints[1]为这段圆弧的中心点
     */
    private PointF[] mDynamicPoints = new PointF[3];

    /*** 透明度 */
    private int alpha;

    /*** 透明度增加或减小的速率 */
    private int alphaRadio;

    /*** 最大透明度 */
    private int maxAlpha;

    /*** 最小透明度 */
    private int minAlpha;

    /*** 透明度是否减小*/
    private boolean isDecreaseAlpha;

    /*** 画笔颜色 */
    private int color;

    /*** 快速旋转的起始角度*/
    private int quickRotateStartAngle;

    /*** 设置最大偏移量 */
    private float offsetMax;


    public CircularSeg(CircularSegBuilder builder) {
        if (builder.radio_dVal >= 1) throw new IllegalArgumentException("radio_dVal >= 1");
        this.alphaRadio = builder.alphaRadio;
        this.alpha = this.minAlpha = builder.minAlpha;
        this.maxAlpha = builder.maxAlpha;
        this.color = ChargingHelper.getComposeColor(alpha, builder.color);
        this.quickRotateStartAngle = builder.quickRotateStartAngle;
        this.offsetMax = builder.offsetMax;
        this.maxDValueAngle = builder.maxDValueAngle;
        this.rotateRadio = builder.rotateRadio;
        this.offsetRadio = builder.offsetRadio;
        this.lineSmoothness = builder.lineSmoothness;
        this.slowRotateRadio = rotateRadio * (1 - builder.radio_dVal);
        this.quickRotateRadio = rotateRadio * (1 + builder.radio_dVal);
        mStickPath = new Path();
        mDynamicPath = new Path();
    }

    static class CircularSegBuilder extends Builder<CircularSegBuilder> {
        /*** 透明度增加或减小的速率 */
        private int alphaRadio;
        /*** 最大透明度 */
        private int maxAlpha;
        /*** 最小透明度 */
        private int minAlpha;
        /*** 快速旋转的起始角度 */
        private int quickRotateStartAngle;
        /*** 设置最大偏移量 */
        private float offsetMax;
        /*** 旋转速率的差值 */
        private float radio_dVal;
        /*** 总的旋转角度差 最大值 */
        private float maxDValueAngle;
        /*** 正常的旋转速率 */
        private float rotateRadio;
        /*** 偏移增大率 */
        private float offsetRadio;
        /*** path系数 */
        private float lineSmoothness;

        public CircularSegBuilder setAlphaRadio(int alphaRadio) {
            this.alphaRadio = alphaRadio;
            return this;
        }

        public CircularSegBuilder setMaxAlpha(int maxAlpha) {
            this.maxAlpha = maxAlpha;
            return this;
        }

        public CircularSegBuilder setMinAlpha(int minAlpha) {
            this.minAlpha = minAlpha;
            return this;
        }

        public CircularSegBuilder setQuickRotateStartAngle(int quickRotateStartAngle) {
            this.quickRotateStartAngle = quickRotateStartAngle;
            return this;
        }

        public CircularSegBuilder setOffsetMax(float offsetMax) {
            this.offsetMax = offsetMax;
            return this;
        }

        public CircularSegBuilder setRadio_dVal(float radio_dVal) {
            this.radio_dVal = radio_dVal;
            return this;
        }

        public CircularSegBuilder setMaxDValueAngle(float maxDValueAngle) {
            this.maxDValueAngle = maxDValueAngle;
            return this;
        }

        public CircularSegBuilder setRotateRadio(float rotateRadio) {
            this.rotateRadio = rotateRadio;
            return this;
        }

        public CircularSegBuilder setOffsetRadio(float offsetRadio) {
            this.offsetRadio = offsetRadio;
            return this;
        }

        public CircularSegBuilder setLineSmoothness(float lineSmoothness) {
            this.lineSmoothness = lineSmoothness;
            return this;
        }

        @Override
        public CircularSeg build() {
            return new CircularSeg(this);
        }
    }


    /**
     * 生成不变的点坐标,连接成path
     *
     * @param circularCenterX 圆环的中心x坐标
     * @param circularCenterY 圆环的中心y坐标
     * @param circularRadius  圆环半径
     * @param startAngle      圆环起始角度
     * @param sweepAngle      圆环结束角度
     */
    public void generatePoints(int circularCenterX, int circularCenterY, float circularRadius,
                               float startAngle, float sweepAngle) {

        this.circularCenterX = circularCenterX;
        this.circularCenterY = circularCenterY;
        this.circularRadius = circularRadius;

        midRad = DegreeUtil.toRadians(startAngle + sweepAngle / 2);

        mStickPoints = ChargingHelper.generatePoints(circularCenterX, circularCenterY,
                circularRadius, startAngle, sweepAngle);

        mDynamicPoints[0] = mStickPoints[2];
        mDynamicPoints[2] = mStickPoints[0];
        calculateDynamicPoint();
        //连接 不变的路径
        ChargingHelper.connectToPath(mStickPath, mStickPoints, lineSmoothness);
    }

    /**
     * 改变颜色
     *
     * @param color
     */
    public void setColor(int color) {
        this.color = ChargingHelper.getComposeColor(alpha, color);
    }

    /**
     * 改变路径
     */
    public void changePath() {
        mDynamicPath.reset();
        mDynamicPath.addPath(mStickPath);

        calculateDynamicPoint();
        //连接 变化的路径,形成闭环
        ChargingHelper.connectToPath(mDynamicPath, mDynamicPoints, lineSmoothness);
    }

    /**
     * 改变透明度
     */
    public void changeAlpha() {

        if (!isDecreaseAlpha) { //透明度需要增大
            if (alpha >= maxAlpha) {
                isDecreaseAlpha = true; //透明度减小
            } else {
                alpha = alpha + alphaRadio;
            }
        } else {
            if (alpha < (minAlpha + maxAlpha) / 2) {
                isDecreaseAlpha = false;
            } else {
                alpha = alpha - alphaRadio;
            }
        }
    }

    /**
     * 计算变化的点
     */
    private void calculateDynamicPoint() {
        if (mDynamicPoints[1] == null) {
            mDynamicPoints[1] = new PointF();
        }

        double offsetX = circularCenterX + DegreeUtil.getCosSideLength(circularRadius + offset, midRad);
        double offsetY = circularCenterY + DegreeUtil.getSinSideLength(circularRadius + offset, midRad);
        mDynamicPoints[1].set((float) offsetX, (float) offsetY);

        if (!isDecreaseOffset) {
            if (offset < offsetMax) {
                offset = offset + offsetRadio;
            } else {
                //大于最大偏移量
                isDecreaseOffset = true;
            }

        } else {
            //偏移量变小
            float offsetDecreaseLimit = offsetMax / 2;
            if (offset > offsetDecreaseLimit) {
                offset = offset - offsetRadio / 2;
                //偏移量小于一定范围后,偏移量继续增大
                if (offset < offsetDecreaseLimit) {
                    isDecreaseOffset = false;
                }
            }
        }
    }

    /**
     * 绘制圆环片段
     *
     * @param canvas
     */
    public void drawSegment(Canvas canvas, Paint paint) {

        if (dValueAngle == 0) { //以正常的速度运行
            if (Math.abs(rotateAngle - quickRotateStartAngle) < rotateRadio) {
                dValueAngle = quickRotateRadio - rotateRadio;
                rotateAngle = rotateAngle % 360 + quickRotateRadio;
                isQuickRotate = true;
            } else {
                rotateAngle = rotateAngle % 360 + rotateRadio;
            }
        } else {
            if (isQuickRotate) {
                if (dValueAngle + (quickRotateRadio - rotateRadio) > maxDValueAngle) {
                    //需要减慢速度了
                    dValueAngle = dValueAngle - (rotateRadio - slowRotateRadio);
                    rotateAngle = rotateAngle % 360 + slowRotateRadio;
                    isQuickRotate = false;
                } else {
                    dValueAngle = dValueAngle + quickRotateRadio - rotateRadio;
                    rotateAngle = rotateAngle % 360 + quickRotateRadio;
                }

            } else {
                if (dValueAngle - (rotateRadio - slowRotateRadio) < 0) {
                    //正常速度
                    dValueAngle = 0;
                    rotateAngle = rotateAngle % 360 + rotateRadio;
                } else {
                    //保持低速
                    dValueAngle = dValueAngle - (rotateRadio - slowRotateRadio);
                    rotateAngle = rotateAngle % 360 + slowRotateRadio;
                }
            }

        }
        paint.setColor(color);
        canvas.save();
        canvas.rotate(rotateAngle, circularCenterX, circularCenterY);
        canvas.drawPath(mDynamicPath, paint);
        canvas.restore();
    }
}
