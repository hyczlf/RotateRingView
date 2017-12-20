package com.example.hyczlf.ringview.view;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.hyczlf.ringview.R;

/**
 * 项目名：  RotateRingView
 * 包名：    com.example.hyczlf.ringview.view
 * 文件名:   RingLayout
 *
 * @author: hyczlf
 * 创建时间:  2017/12/19 10:41
 * 描述：    圆环
 */
public class RotateRingView extends View {
    //View默认最小宽度
    private static final int DEFAULT_MIN_WIDTH = 400;
    private int width;
    private int height;
    private float roundWidth;
    //每一份的角度
    private float angle;
    //颜色的数量
    private int colorsNum;
    //背景
    private boolean isFirst = false;
    //外圆
    private boolean isRoundFirst = false;
    private Canvas mCanvas;
    private Paint paint;
    private RectF rectF;
    //默认选中的是1
    private int oldElect = 1;
    private int newElect;
    private int oldAlpha = 0;
    private int newAlpha = 0;
    //动画时间
    private int animTime = 1000;
    //圆环颜色
    private int[] doughnutColors;

    public RotateRingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    //初始化
    private void init() {
        paint = new Paint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measure(widthMeasureSpec), measure(heightMeasureSpec));
    }

    private int measure(int origin) {
        int result = DEFAULT_MIN_WIDTH;
        int specMode = MeasureSpec.getMode(origin);
        int specSize = MeasureSpec.getSize(origin);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mCanvas = canvas;
        resetParams();
        if (!isFirst) {
            //画背景白色圆环
            drawBackRound(mCanvas);
            isFirst = !isFirst;
        }
        //画内圆
        drawInteriorRound(mCanvas);
    }

    //内圆
    private void drawInteriorRound(Canvas canvas) {
        //循环画圆环
        float nowPercent = 0;
        float nextPercent = 0;
        //旋转画布
        canvas.rotate(-45, width / 2, height / 2);
        paint.setStrokeWidth(roundWidth * 0.8f);
        paint.setStyle(Paint.Style.STROKE);
        //得到颜色的数量
        colorsNum = doughnutColors.length;
        //根据颜色的数量，得出角度
        angle = (1 / (float) colorsNum);
        if (!isRoundFirst) {
            for (int i = 0; i < colorsNum; i++) {
                nextPercent += 360 * angle;
                if (i == 1) {
                    paint.setColor(getResources().getColor(R.color.white_0));
                } else {
                    paint.setColor(doughnutColors[i]);
                }
                canvas.drawArc(rectF, nowPercent, 360 * angle, false, paint);
                nowPercent = nextPercent;
            }
            isRoundFirst = !isRoundFirst;
        } else {
            for (int i = 0; i < colorsNum; i++) {
                nextPercent += 360 * angle;
                if (i == newElect) {
                    //新的选中
                    paint.setColor(getResources().getColor(R.color.white));
                    paint.setAlpha(newAlpha);
                } else if (i == oldElect) {
                    //之前选中的
                    paint.setColor(getResources().getColor(R.color.white));
                    paint.setAlpha(oldAlpha);
                } else {
                    //其他的不变
                    paint.setColor(doughnutColors[i]);
                }
                canvas.drawArc(rectF, nowPercent, 360 * angle, false, paint);
                nowPercent = nextPercent;
            }
        }
    }

    //画背景圆
    private void drawBackRound(Canvas canvas) {
        initPaint();
        roundWidth = Math.min(width, height) / 2 * 0.15f;
        rectF = new RectF(
                (width > height ? Math.abs(width - height) / 2 : 0) + roundWidth / 2,
                (height > width ? Math.abs(height - width) / 2 : 0) + roundWidth / 2,
                width - (width > height ? Math.abs(width - height) / 2 : 0) - roundWidth / 2,
                height - (height > width ? Math.abs(height - width) / 2 : 0) - roundWidth / 2);
        paint.setStrokeWidth(roundWidth * 0.8f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(getResources().getColor(R.color.white_0));
        paint.setAntiAlias(true);
        canvas.drawArc(rectF, 0, 360, false, paint);
    }

    public void setRingRotating(int nowAngle, int futureAngle) {
        ObjectAnimator.ofFloat(this, "rotation", nowAngle, futureAngle).setDuration(animTime).start();
    }

    //设置颜色要显示还是隐藏的参数
    public void setElect(int oldElect, int newElect, int oldValue, int newValue) {
        this.oldElect = oldElect;
        this.newElect = newElect;
        //当前的
        int currentValue = 0;
        //将来的
        int futureValue = 0;
        currentValue = (oldElect % 2 == 0) ? oldValue : newValue;
        futureValue = (newElect % 2 == 0) ? oldValue : newValue;
        //动画
        ValueAnimator oldAnim = ValueAnimator.ofInt(0, currentValue);
        ValueAnimator newAnim = ValueAnimator.ofInt(futureValue, 0);
        oldAnim.setDuration(animTime).start();
        newAnim.setDuration(animTime).start();
        //显示
        oldAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                oldAlpha = (int) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        //隐藏
        newAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                newAlpha = (int) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
    }

    //判断范围
    private int judgmentRange(int elect, int oldValue, int newValue) {
        if (elect % 2 == 0) {
            return oldValue;
        } else {
            return newValue;
        }
    }

    //设置动画时间，默认1000
    public void setAnimTime(int animTime) {
        this.animTime = animTime;
    }

    //设置颜色
    public void setDoughnutColors(int[] doughnutColors) {
        this.doughnutColors = doughnutColors;
    }

    //初始化画笔
    private void initPaint() {
        paint.reset();
        paint.setAntiAlias(true);
    }

    //获取高宽
    private void resetParams() {
        width = getWidth();
        height = getHeight();
    }
}
