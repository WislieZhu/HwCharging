package com.wislie.charging.view;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.wislie.charging.helper.Builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * author : Wislie
 * e-mail : 254457234@qq.comn
 * date   : 2020-02-29 10:19
 * desc   : 圆环分段集
 * version: 1.0
 */
public class CircularSegments {

    private String TAG = "CircularSegments";

    /*** 圆环片段集合 */
    private List<CircularSeg> mSegments = new ArrayList<>();

    private Random rd;

    /*** 偏移的最大值 */
    private float maxOffset;
    /*** 偏移量最小值 */
    private float minOffset;
    /*** 总的旋转角度差 最大值 */
    private float maxDValueAngle;
    /*** 正常的旋转速率 */
    private float rotateRadio;
    /*** path系数 */
    private float lineSmoothness;
    /*** 最小的角度 */
    private int minAngle;
    /*** 偏移增大率 */
    private float offsetRadio;
    /*** 旋转速率的差值 */
    private float radio_dVal;
    /*** 颜色 */
    private int color;
    /*** 圆环片段数量 */
    private int segmentCount;
    /*** 圆心 */
    private int circularCenterX, circularCenterY;
    /*** 圆环半径 */
    private float circularRadius;

    public CircularSegments(CircularSegmentsBuilder builder) {
        if (builder.rd == null) throw new NullPointerException("builder.rd == null");
        if (builder.lineSmoothness <= 0) throw new IllegalArgumentException("lineSmoothness <= 0");
        if (builder.minOffset > builder.maxOffset)
            throw new IllegalArgumentException("builder.minOffset > builder.maxOffset");
        if (builder.segmentCount <= 0)
            throw new IllegalArgumentException("builder.segmentCount <= 0");
        this.rd = builder.rd;
        this.lineSmoothness = builder.lineSmoothness;
        this.color = builder.color;
        this.minAngle = builder.minAngle;
        this.offsetRadio = builder.offsetRadio;
        this.radio_dVal = builder.radio_dVal;
        this.rotateRadio = builder.rotateRadio;
        this.maxDValueAngle = builder.maxDValueAngle;
        this.minOffset = builder.minOffset;
        this.maxOffset = builder.maxOffset;
        this.segmentCount = builder.segmentCount;
        this.circularCenterX = builder.circularCenterX;
        this.circularCenterY = builder.circularCenterY;
        this.circularRadius = builder.circularRadius;
    }

    static class CircularSegmentsBuilder extends Builder<CircularSegmentsBuilder> {
        /*** 随机Random */
        private Random rd;

        /*** 圆环片段最小角度 */
        private int minAngle;
        /*** 偏移增大率 */
        private float offsetRadio;
        /*** 旋转速率的差值 */
        private float radio_dVal;
        /*** 正常的旋转速率 */
        private float rotateRadio;
        /*** 总的旋转角度差 最大值 */
        private float maxDValueAngle;
        /*** 偏移的最大值 */
        private float maxOffset;
        /*** 偏移量最小值 */
        private float minOffset;
        /*** 圆环片段数量 */
        private int segmentCount;
        /*** 圆心 */
        private int circularCenterX, circularCenterY;
        /*** 圆环半径 */
        private float circularRadius;

        public CircularSegmentsBuilder setRd(Random rd) {
            this.rd = rd;
            return this;
        }

        public CircularSegmentsBuilder setMinAngle(int minAngle) {
            this.minAngle = minAngle;
            return this;
        }

        public CircularSegmentsBuilder setOffsetRadio(float offsetRadio) {
            this.offsetRadio = offsetRadio;
            return this;
        }

        public CircularSegmentsBuilder setRadio_dVal(float radio_dVal) {
            this.radio_dVal = radio_dVal;
            return this;
        }

        public CircularSegmentsBuilder setRotateRadio(float rotateRadio) {
            this.rotateRadio = rotateRadio;
            return this;
        }

        public CircularSegmentsBuilder setMaxDValueAngle(float maxDValueAngle) {
            this.maxDValueAngle = maxDValueAngle;
            return this;
        }

        public CircularSegmentsBuilder setMaxOffset(float maxOffset) {
            this.maxOffset = maxOffset;
            return this;
        }

        public CircularSegmentsBuilder setMinOffset(float minOffset) {
            this.minOffset = minOffset;
            return this;
        }

        public CircularSegmentsBuilder setSegmentCount(int segmentCount) {
            this.segmentCount = segmentCount;
            return this;
        }

        public CircularSegmentsBuilder setCircularCenterX(int circularCenterX) {
            this.circularCenterX = circularCenterX;
            return this;
        }

        public CircularSegmentsBuilder setCircularCenterY(int circularCenterY) {
            this.circularCenterY = circularCenterY;
            return this;
        }

        public CircularSegmentsBuilder setCircularRadius(float circularRadius) {
            this.circularRadius = circularRadius;
            return this;
        }

        @Override
        public CircularSegments build() {
            return new CircularSegments(this);
        }
    }

    /**
     * 生成一个圆环
     */
    public void generateCircular() {

        //角度数组
        float[] sweepAngleArr = new float[segmentCount];

        float startAngle = 0;
        int averageAngle = 360 / segmentCount;
        //扫描角度的偏移量
        int offsetAngle = 0;
        for (int i = 0; i < sweepAngleArr.length; i++) {
            offsetAngle = rd.nextInt(segmentCount);
            if (i != sweepAngleArr.length - 1) {
                sweepAngleArr[i] = minAngle + rd.nextInt(Math.abs(averageAngle - minAngle)) + offsetAngle;
            } else {
                //剩余的扫描角度赋值给最后一个圆环片段
                sweepAngleArr[i] = 360 - startAngle + offsetAngle;
            }
            //透明度
            int alphaRadio = rd.nextInt(segmentCount);
            int maxAlpha = 255 / 2 + rd.nextInt(255 / (segmentCount / 2));
            int minAlpha = maxAlpha - rd.nextInt(255 / segmentCount );

            int quickRotateStartAngle = rd.nextInt(360);
            float offsetMax = minOffset + rd.nextInt((int) (maxOffset - minOffset));
            CircularSeg seg = new CircularSeg.CircularSegBuilder()
                    .setColor(color)
                    .setAlphaRadio(alphaRadio)
                    .setMinAlpha(minAlpha)
                    .setMaxAlpha(maxAlpha)
                    .setQuickRotateStartAngle(quickRotateStartAngle)
                    .setOffsetMax(offsetMax)
                    .setRadio_dVal(radio_dVal)
                    .setMaxDValueAngle(maxDValueAngle)
                    .setRotateRadio(rotateRadio)
                    .setOffsetRadio(offsetRadio)
                    .setLineSmoothness(lineSmoothness)
                    .build();
            seg.generatePoints(circularCenterX, circularCenterY, circularRadius, startAngle, sweepAngleArr[i]);
            mSegments.add(seg);

            startAngle += sweepAngleArr[i] - offsetAngle;
        }
    }

    /**
     * 改变圆环的路径
     */
    public void changeCircular() {
        for (int i = 0; i < mSegments.size(); i++) {
            CircularSeg seg = mSegments.get(i);
            seg.changePath();
            seg.changeAlpha();
        }
    }

    /**
     * 改变颜色
     *
     * @param color
     */
    public void setColor(int color) {
        for (int i = 0; i < mSegments.size(); i++) {
            CircularSeg seg = mSegments.get(i);
            seg.setColor(color);
        }
    }

    /**
     * 绘制圆环
     */
    public void drawSegments(Canvas canvas, Paint paint) {
        for (int i = 0; i < mSegments.size(); i++) {
            CircularSeg seg = mSegments.get(i);
            seg.drawSegment(canvas, paint);
        }
    }
}
