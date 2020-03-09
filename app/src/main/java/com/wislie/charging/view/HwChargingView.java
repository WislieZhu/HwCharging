package com.wislie.charging.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.RequiresApi;

import com.wislie.charging.util.DegreeUtil;
import com.wislie.charging.util.TypedValueUtil;

import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * author : Wislie
 * e-mail : 254457234@qq.comn
 * date   : 2020-02-28 20:37
 * desc   : 华为充电动画
 * version: 1.0
 */
public class HwChargingView extends View {

    private String TAG = "HwChargingView";
    /**
     *
     * 1.发射器
     *
     *
     * 2.气泡
     * 从发射器的底部随机生成,并向上运动
     * 以一定的速率变小,甚至消失
     * 气泡与发射器之间的关系:离开时黏性
     * 气泡与气泡之间的关系:有黏性
     * 气泡与圆环之间的关系:有黏性
     *
     * 3.圆环
     * 围绕自身旋转
     * 圆环四周有多个小月亮,也在旋转,并且速度不一样
     * 气泡与圆环间的关系:进入时有粘性,等完全进入时消失
     *
     *
     */

    /*** 线程池 */
    private Executor mExecutor;
    /*** 标记是否运行 */
    private boolean isStop = false;

    /*** 视图背景色 */
    private int backGroundColor = Color.parseColor("#130d15");

    /*** 进度值 */
    private int progress = 0;

    private Rect mTextBounds = new Rect();

    private Paint mPaint = new Paint();
    /*** 文字画笔 */
    private TextPaint mTextPaint = new TextPaint();

    private int[] mColorArr = {
            Color.parseColor("#cb1c0e"),
            Color.parseColor("#f58a13"),
            Color.parseColor("#1dcc8b")
    };

    private Random rd = new Random();

    /*** 圆环 */
    private CircularSegments mCircularSegment;
    /*** 最小的角度 */
    private int minAngle = 50;
    /*** 偏移增大率 */
    private float offsetRadio = 0.045f;
    /*** 旋转速率的差值 */
    private float radio_dVal = 0.2f;
    /*** 正常的旋转速率 */
    private float rotateRadio = 1.5f;
    /*** 总的旋转角度差 最大值 */
    private float maxDValueAngle = 15;
    /*** 偏移的最大值 */
    private float maxOffset = 12;
    /*** 偏移量最小值 */
    private float minOffset = 8;
    /*** 圆环片段数量 */
    private int segmentCount = 6;
    /*** 圆环半径 */
    private float mCircularRadius = 100;

    /*** 发射器 */
    private BubbleEmitter mBubbleEmitter;
    /*** 颜色 */
    private int color;
    /*** path系数 */
    private float lineSmoothness = 0.3f;
    /*** 发射器半径 */
    private float emitterRadius = 40;
    /*** 发射器的圆弧占周长的1/4 */
    private float emitterPercent = 1.0f / 4;
    /*** 发射器的中心x坐标 */
    private float emitterCenterX;
    /*** 发射器的中心y坐标 */
    private float emitterCenterY;

    /*** 气泡 */
    private BubbleController mBubbleController;
    /*** 气泡的最小半径 */
    private float minBubbleRadius = 5;
    /*** 气泡的最大半径 */
    private float maxBubbleRadius = 15;
    /*** 速率百分比,当气泡与发射器之间有粘合贝塞尔曲线时,运动速度减慢 */
    private float speedRadio = 0.5f;
    /*** 半径变化率 */
    private float radiusRadio = 0.1f;
    /*** 气泡的最小速度 */
    private float minBubbleSpeedY = 2;
    /*** 气泡的最大速度 */
    private float maxBubbleSpeedY = 5;
    /*** 气泡最多有几个 */
    private int maxBubbleCount = 10;

    private Handler mHandler = new Handler();

    public HwChargingView(Context context) {
        this(context, null);
    }

    public HwChargingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(final Context context) {
        //设置高亮
        setKeepScreenOn(true);
        //设置半径
        emitterRadius = TypedValueUtil.dp2px(context, emitterRadius);

        //圆环半径
        mCircularRadius = TypedValueUtil.dp2px(context, mCircularRadius);
        //圆环片段的偏移量
        minOffset = TypedValueUtil.dp2px(context, minOffset);
        maxOffset = TypedValueUtil.dp2px(context, maxOffset);

        //设置气泡的最小半径和最大半径
        minBubbleRadius = TypedValueUtil.dp2px(context, minBubbleRadius);
        maxBubbleRadius = TypedValueUtil.dp2px(context, maxBubbleRadius);

        final float sweepAngle = 360 * emitterPercent;
        final float startAngle = -sweepAngle / 2 - 90;

        final double midRad = DegreeUtil.toRadians(sweepAngle / 2);

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                //发射器
                emitterCenterX = getWidth() / 2;
                emitterCenterY = (float) (getHeight() + DegreeUtil.getCosSideLength(emitterRadius, midRad)); // todo有问题
                mBubbleEmitter = new BubbleEmitter.EmitterBuilder()
                        .setColor(color)
                        .setLineSmoothness(lineSmoothness)
                        .build();
                mBubbleEmitter.generatePoints(emitterCenterX, emitterCenterY, emitterRadius, startAngle, sweepAngle);

                //分片的圆环
                mCircularSegment = new CircularSegments.CircularSegmentsBuilder()
                        .setColor(color)
                        .setLineSmoothness(lineSmoothness)
                        .setRd(rd)
                        .setMinAngle(minAngle)
                        .setOffsetRadio(offsetRadio)
                        .setRadio_dVal(radio_dVal)
                        .setRotateRadio(rotateRadio)
                        .setMaxDValueAngle(maxDValueAngle)
                        .setMaxOffset(maxOffset)
                        .setMinOffset(minOffset)
                        .setSegmentCount(segmentCount)
                        .setCircularCenterX(getWidth() / 2)
                        .setCircularCenterY(getHeight() / 2)
                        .setCircularRadius(mCircularRadius)
                        .build();
                mCircularSegment.generateCircular();

                //气泡
                mBubbleController = new BubbleController.BubbleControllerBuilder()
                        .setColor(color)
                        .setRd(rd)
                        .setLineSmoothness(lineSmoothness)
                        .setMinBubbleRadius(minBubbleRadius)
                        .setMaxBubbleRadius(maxBubbleRadius)
                        .setSpeedRadio(speedRadio)
                        .setRadiusRadio(radiusRadio)
                        .setMinBubbleSpeedY(minBubbleSpeedY)
                        .setMaxBubbleSpeedY(maxBubbleSpeedY)
                        .setMaxBubbleCount(maxBubbleCount)
                        .setCircularCenterX(getWidth() / 2)
                        .setCircularCenterY(getHeight() / 2)
                        .setCircularRadius(mCircularRadius)
                        .setEmitterCenterX(emitterCenterX)
                        .setEmitterCenterY(emitterCenterY)
                        .setEmitterRadius(emitterRadius)
                        .build();

                getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.FILL);

        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setDither(true);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(TypedValueUtil.sp2px(context, 32));
        mTextPaint.setStrokeWidth(3);
        mTextPaint.getTextBounds(progress + "%", 0, (progress + "%").length(), mTextBounds);

        mExecutor = new ThreadPoolExecutor(3, 5, 15,
                TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(8));
        mExecutor.execute(mBubbleRunnable);
    }

    @Override
    public void onDrawForeground(Canvas canvas) {
        super.onDrawForeground(canvas);
        //设置背景色
        setBackgroundColor(backGroundColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制进度
        drawProgress(canvas);
        //绘制圆环
        if (mCircularSegment != null) {
            mCircularSegment.drawSegments(canvas, mPaint);
        }
        //绘制气泡
        if (mBubbleController != null) {
            mBubbleController.drawBubble(canvas, mPaint);
        }
        //绘制发射器
        if (mBubbleEmitter != null) {
            mBubbleEmitter.drawEmitter(canvas, mPaint);
        }
    }

    /**
     * 绘制进度
     *
     * @param canvas
     */
    private void drawProgress(Canvas canvas) {

        float x = getWidth() / 2 - mTextBounds.width() / 2;
        float y = getHeight() / 2 + mTextBounds.height() / 2;

        canvas.drawText(progress + "%", x, y, mTextPaint);
    }

    private Runnable mBubbleRunnable = new Runnable() {
        @Override
        public void run() {

            if (!isStop) {

                if (mBubbleController != null) {
                    //生成气泡
                    mBubbleController.generateBubble();
                    //遍历气泡
                    mBubbleController.performTraversals();
                }

                if (mCircularSegment != null) {
                    mCircularSegment.changeCircular();
                }

                postInvalidate();
                mHandler.postDelayed(this, 16);
            }
        }
    };

    /**
     * 设置进度
     *
     * @param percent
     */
    public void setProgress(int percent) {
        this.progress = percent;
        mTextPaint.getTextBounds(progress + "%", 0, (progress + "%").length(), mTextBounds);
        if (percent <= 10) {
            changeColor(mColorArr[0]);
        } else if (percent <= 20) {
            changeColor(mColorArr[1]);
        } else {
            changeColor(mColorArr[2]);
        }
    }

    /**
     * 改变颜色
     *
     * @param color
     */
    private void changeColor(int color) {
        //颜色如果一样,则不用改变颜色
        if (color == this.color) return;
        this.color = color;
        if (mCircularSegment != null) {
            mCircularSegment.setColor(color);
        }
        if (mBubbleController != null) {
            mBubbleController.setColor(color);
        }
        if (mBubbleEmitter != null) {
            mBubbleEmitter.setColor(color);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        isStop = false;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isStop = true;
    }
}
