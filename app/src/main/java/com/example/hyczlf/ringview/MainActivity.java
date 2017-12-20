package com.example.hyczlf.ringview;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.hyczlf.ringview.view.CircleMenuLayout;
import com.example.hyczlf.ringview.view.Constants;
import com.example.hyczlf.ringview.view.RotateRingView;
import com.example.hyczlf.ringview.view.bean.Bean;

import java.util.Map;

public class MainActivity extends Activity {
    private CircleMenuLayout cl_group;
    private RotateRingView rl_view;
    private int[] ringColors = new int[4];
    private int[] mTextsColor = new int[4];
    private String[] mTexts = new String[]{"200至500", "500以内", "100\n以内", "100至200"};

    private int[] mImages = new int[]{
            R.drawable.shape_circle_pink_50, R.drawable.shape_circle_ring_pink,
            R.drawable.shape_circle_pink_50, R.drawable.shape_circle_pink_50};

    //旋转标志
    private Integer tag = 0;
    //默认选中的是1
    private int oldElect = 1;
    //map
    private Map<Integer, Bean> maps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //绑定控件
        initView();
        //初始化数据
        initData();
        //设置监听
        initEvent();
    }

    private void initEvent() {
        cl_group.setOnMenuItemClickListener(new CircleMenuLayout.OnMenuItemClickListener() {
            @Override
            public void itemClick(View iv, int pos, View tv) {
                //旋转动画
                tag = CircleMenuLayout.groupRotating(pos, oldElect, tag, cl_group, rl_view);
                //背景圆弧换颜色动画
                rl_view.setElect(oldElect, pos, 80, 50);
                //之前选中的
                Bean oldBean = maps.get(oldElect);
                //现在选中的
                Bean newBean = maps.get(pos);
                //得到ImageView的宽高
                int width = oldBean.getIv().getWidth();
                int height = oldBean.getIv().getHeight();
                //ImageView缩放、TextView换颜色
                CircleMenuLayout.zoomImageViewAnim(oldBean, newBean, width, height);
                oldElect = pos;
                Toast.makeText(MainActivity.this, "You clicked " + newBean.getTv().getText().toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initData() {
        //文本颜色
        mTextsColor[0] = Color.BLACK;
        mTextsColor[1] = Color.WHITE;
        mTextsColor[2] = Color.BLACK;
        mTextsColor[3] = Color.BLACK;
        //圆环颜色
        ringColors[0] = getResources().getColor(R.color.white_50);
        ringColors[1] = getResources().getColor(R.color.white_30);
        ringColors[2] = getResources().getColor(R.color.white_50);
        ringColors[3] = getResources().getColor(R.color.white_30);
        //设置Item
        cl_group.setMenuItemIconsAndTexts(mImages, mTexts, mTextsColor);
        maps = cl_group.getMaps();
        //设置圆环颜色
        rl_view.setDoughnutColors(ringColors);
        //设置动画完成的时间
        rl_view.setAnimTime(Constants.SUCCESSFUL_ANIM_TIME);
    }

    private void initView() {
        cl_group = findViewById(R.id.cl_group);
        rl_view = findViewById(R.id.id_circle_menu_item_center);
    }
}
