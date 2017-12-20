package com.example.hyczlf.ringview.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hyczlf.ringview.R;
import com.example.hyczlf.ringview.view.bean.Bean;

import java.util.HashMap;
import java.util.Map;

/**
 * 项目名：  RotateRingView
 * 包名：    com.example.hyczlf.ringview.view
 * 文件名:   CircleMenuLayout
 *
 * @author: hyczlf
 * 创建时间:  2017/12/19 10:44
 * 描述：    圆形布局
 */
public class CircleMenuLayout extends ViewGroup {
    //半径
    private int mRadius;
    // 布局时的开始角度
    private double mStartAngle = 0;
    //该容器内child item的默认尺寸
    private static final float RADIO_DEFAULT_CHILD_DIMENSION = 1 / 4f;
    // 菜单项的文本
    private String[] mItemTexts;
    //菜单项文本的颜色
    private int[] mItemTextColors;
    // 菜单项的图标
    private int[] mItemImgs;
    // 菜单的个数
    private int mMenuItemCount;
    // MenuItem的点击事件接口
    private OnMenuItemClickListener mOnMenuItemClickListener;
    private int mMenuItemLayoutId = R.layout.item_circle_menu;
    //ImageView和TextView绑定
    private Map<Integer, Bean> maps = new HashMap<>();

    public CircleMenuLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        //无视padding
        setPadding(0, 0, 0, 0);
    }

    /**
     * 设置布局的宽高，并测量menu item宽高
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int resWidth = 0;
        int resHeight = 0;
        /**
         * 根据传入的参数，分别获取测量模式和测量值
         */
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        /**
         * 如果宽或者高的测量模式非精确值
         */
        if (widthMode != MeasureSpec.EXACTLY || heightMode != MeasureSpec.EXACTLY) {
            // 主要设置为背景图的高度
            resWidth = getSuggestedMinimumWidth();
            // 如果未设置背景图片，则设置为屏幕宽高的默认值
            resWidth = resWidth == 0 ? getDefaultWidth() : resWidth;
            resHeight = getSuggestedMinimumHeight();
            // 如果未设置背景图片，则设置为屏幕宽高的默认值
            resHeight = resHeight == 0 ? getDefaultWidth() : resHeight;
        } else {
            // 如果都设置为精确值，则直接取小值；
            resWidth = resHeight = Math.min(width, height);
        }
        setMeasuredDimension(resWidth, resHeight);
        // 获得半径
        mRadius = Math.max(getMeasuredWidth(), getMeasuredHeight());
        // menu item数量
        final int count = getChildCount();
        // menu item尺寸
        int childSize = (int) (mRadius * RADIO_DEFAULT_CHILD_DIMENSION);
        // menu item测量模式
        int childMode = MeasureSpec.EXACTLY;
        final float tmp = mRadius / 2f - childSize / 2f;
        // 迭代测量
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            // 计算menu item的尺寸；以及和设置好的模式，去对item进行测量
            int makeMeasureSpec = -1;
            if (child.getId() == R.id.id_circle_menu_item_center) {
                makeMeasureSpec = MeasureSpec.makeMeasureSpec(
                        (int) (tmp * 2.2f),
                        childMode);
            } else {
                makeMeasureSpec = MeasureSpec.makeMeasureSpec(childSize, childMode);
            }
            child.measure(makeMeasureSpec, makeMeasureSpec);
        }
    }

    /**
     * 设置menu item的位置
     */
    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
        int layoutRadius = mRadius;
        float angleDelay;
        final int childCount = getChildCount();
        int left, top;
        // menu item 的尺寸
        int cWidth = (int) (layoutRadius * RADIO_DEFAULT_CHILD_DIMENSION);
        // 找到中心的view
        View cView = findViewById(R.id.id_circle_menu_item_center);
        // 根据menu item的个数，计算角度
        if (cView != null) {
            //如果中心View存在item个数减1
            angleDelay = 360 / (getChildCount() - 1);
            // 设置center item位置
            //居中
            int cl = layoutRadius / 2 - cView.getMeasuredWidth() / 2;
            int cr = cl + cView.getMeasuredWidth();
            cView.layout(cl, cl, cr, cr);
        } else {
            angleDelay = 360 / (getChildCount());
        }
        // 遍历去设置menuitem的位置
        for (int j = 0; j < childCount; j++) {
            final View child = getChildAt(j);
            if (child.getId() == R.id.id_circle_menu_item_center)
                continue;
            if (child.getVisibility() == GONE) {
                continue;
            }
            mStartAngle %= 360;
            // 计算，中心点到menu item中心的距离
            float tmp = layoutRadius / 2f - cWidth / 2;
            // tmp cosa 即menu item中心点的横坐标
            left = layoutRadius
                    / 2
                    + (int) Math.round(tmp
                    * Math.cos(Math.toRadians(mStartAngle)) - 1 / 2f
                    * cWidth);
            // tmp sina 即menu item的纵坐标
            top = layoutRadius
                    / 2
                    + (int) Math.round(tmp
                    * Math.sin(Math.toRadians(mStartAngle)) - 1 / 2f
                    * cWidth);

            child.layout(left, top, left + cWidth, top + cWidth);
            // 叠加尺寸
            mStartAngle += angleDelay;
        }
    }

    // 获得默认该layout的尺寸
    private int getDefaultWidth() {
        WindowManager wm = (WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return Math.min(outMetrics.widthPixels, outMetrics.heightPixels);
    }

    /**
     * 设置菜单条目的图标和文本
     *
     * @param resIds
     */
    public void setMenuItemIconsAndTexts(int[] resIds, String[] texts) {
        mItemImgs = resIds;
        mItemTexts = texts;
        // 参数检查
        if (resIds == null && texts == null) {
            throw new IllegalArgumentException("菜单项文本和图片至少设置其一");
        }
        // 初始化mMenuCount
        mMenuItemCount = resIds == null ? texts.length : resIds.length;
        if (resIds != null && texts != null) {
            mMenuItemCount = Math.min(resIds.length, texts.length);
        }
        addMenuItems();
    }

    /**
     * 设置菜单条目的图标和文本+颜色
     *
     * @param resIds
     */
    public void setMenuItemIconsAndTexts(int[] resIds, String[] texts, int[] textColors) {
        mItemImgs = resIds;
        mItemTexts = texts;
        mItemTextColors = textColors;
        // 参数检查
        if (resIds == null && texts == null) {
            throw new IllegalArgumentException("菜单项文本和图片至少设置其一");
        }
        // 初始化mMenuCount
        mMenuItemCount = resIds == null ? texts.length : resIds.length;
        if (resIds != null && texts != null) {
            mMenuItemCount = Math.min(resIds.length, texts.length);
        }
        addMenuItems();
    }

    // 添加菜单项
    private void addMenuItems() {
        LayoutInflater mInflater = LayoutInflater.from(getContext());
        // 根据用户设置的参数，初始化view
        for (int i = 0; i < mMenuItemCount; i++) {
            Bean bean = new Bean();
            final int j = i;
            View view = mInflater.inflate(mMenuItemLayoutId, this, false);
            ImageView iv = view.findViewById(R.id.id_circle_menu_item_image);
            TextView tv = view.findViewById(R.id.id_circle_menu_item_text);
            final TextView t = tv;
            if (tv != null) {
                tv.setVisibility(View.VISIBLE);
                tv.setText(mItemTexts[i]);
                tv.setTextColor(mItemTextColors[i]);
                bean.setTv(tv);
            }
            if (iv != null) {
                iv.setVisibility(View.VISIBLE);
                iv.setBackgroundResource(mItemImgs[i]);
                bean.setIv(iv);
                iv.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnMenuItemClickListener != null) {
                            mOnMenuItemClickListener.itemClick(v, j, t);
                        }
                    }
                });
            }
            //添加到Map中
            maps.put(i, bean);
            // 添加view到容器中
            addView(view);
        }
    }

    //设置监听
    public void setOnMenuItemClickListener(OnMenuItemClickListener mOnMenuItemClickListener) {
        this.mOnMenuItemClickListener = mOnMenuItemClickListener;
    }

    public void setmStartAngle(double angle) {
        mStartAngle = angle;
        requestLayout();
    }

    public Map<Integer, Bean> getMaps() {
        return maps;
    }

    // 接口
    public interface OnMenuItemClickListener {

        void itemClick(View iv, int pos, View tv);
    }

    //旋转
    public static int groupRotating(int pos, int oldElect, int tag, final CircleMenuLayout cl_group, RotateRingView rl_view) {
        ValueAnimator anim;
        int newElect = pos;
        int angle;
        if (oldElect == 0 && newElect == 3) {
            //当前选中的是0，未来选中的是3，则顺时针旋转180
            angle = Constants.ROTATION_ANGLE;
        } else if (oldElect == 3 && newElect == 0) {
            //当前选中的是3，未来选中的是0，则逆时针旋转180
            angle = -Constants.ROTATION_ANGLE;
        } else {
            //若不属于以上两种情况，按普通的处理
            angle = (oldElect - newElect) * Constants.ROTATION_ANGLE;
        }
        //范围，基准角度----基准角度+要旋转的角度
        anim = ValueAnimator.ofInt(tag, tag + angle);
        anim.setDuration(Constants.SUCCESSFUL_ANIM_TIME).start();
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int angle = (int) valueAnimator.getAnimatedValue();
                cl_group.setmStartAngle(angle);
            }
        });
        //背景圆环同步旋转
        rl_view.setRingRotating(tag, tag + angle);
        return tag + angle;
    }

    public static void zoomImageViewAnim(Bean oldBean, Bean newBean, final int width, final int height) {
        //当前选中的缩小，换背景，字体换颜色
        //将来选中的缩小，换背景，字体换颜色
        final TextView oldTv = oldBean.getTv();
        final ImageView oldIv = oldBean.getIv();

        final TextView newTv = newBean.getTv();
        final ImageView newIv = newBean.getIv();

        ValueAnimator shrinkImageAnimWidth;
        ValueAnimator shrinkImageAnimHeight;
        shrinkImageAnimWidth = ValueAnimator.ofInt(width, 0);
        shrinkImageAnimHeight = ValueAnimator.ofInt(height, 0);
        //缩小
        AnimatorSet set = new AnimatorSet();
        set.playTogether(shrinkImageAnimWidth, shrinkImageAnimHeight);
        set.setDuration(500).start();
        shrinkImageAnimWidth.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int w = (int) animation.getAnimatedValue();
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) oldIv.getLayoutParams();
                params.width = w;
                //改变宽度
                oldIv.setLayoutParams(params);
                newIv.setLayoutParams(params);
            }
        });
        shrinkImageAnimHeight.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int h = (int) animation.getAnimatedValue();
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) oldIv.getLayoutParams();
                params.height = h;
                //改变高度
                oldIv.setLayoutParams(params);
                newIv.setLayoutParams(params);
            }
        });
        shrinkImageAnimWidth.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //缩小动画，完成，开启放大动画
                //文字换颜色
                oldTv.setTextColor(Color.BLACK);
                newTv.setTextColor(Color.WHITE);
                //ImageView换背景
                oldIv.setBackgroundResource(R.drawable.shape_circle_pink_50);
                newIv.setBackgroundResource(R.drawable.shape_circle_ring_pink);

                AnimatorSet set1 = new AnimatorSet();
                ValueAnimator bigImageAnimWidth;
                ValueAnimator bigImageAnimHeight;
                bigImageAnimWidth = ValueAnimator.ofInt(0, width);
                bigImageAnimHeight = ValueAnimator.ofInt(0, height);

                set1.playTogether(bigImageAnimWidth, bigImageAnimHeight);
                set1.setDuration(500).start();

                bigImageAnimWidth.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int w = (int) animation.getAnimatedValue();
                        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) oldIv.getLayoutParams();
                        params.width = w;
                        //改变宽度
                        oldIv.setLayoutParams(params);
                        newIv.setLayoutParams(params);
                    }
                });

                bigImageAnimHeight.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int h = (int) animation.getAnimatedValue();
                        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) oldIv.getLayoutParams();
                        params.height = h;
                        //改变高度
                        oldIv.setLayoutParams(params);
                        newIv.setLayoutParams(params);
                    }
                });
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }
}
