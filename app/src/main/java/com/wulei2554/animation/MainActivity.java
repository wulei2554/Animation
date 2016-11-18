package com.wulei2554.animation;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.util.Random;

import static com.wulei2554.animation.R.id.iv_head;
import static com.wulei2554.animation.R.id.main_listview;
import static com.wulei2554.animation.R.id.menu_listview;
import static com.wulei2554.animation.R.id.my_layout;

public class MainActivity extends AppCompatActivity {

    private ListView menu_listview, main_listview;

    private SlidingMenu slideMenu;

    private ImageView iv_head;
    private MyLinearLayout my_layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        initData();

        slideMenu.setIOnDragStateChangeListen(new SlidingMenu.IOnDragStateChangeListen() {
            @Override
            public void Open() {
              menu_listview.smoothScrollToPosition(new Random().nextInt(menu_listview.getCount()));
            }

            @Override
            public void Close() {
                Log.e("wulei","Close...");
                ViewPropertyAnimator.animate(iv_head).
                        translationXBy(15).
                        setInterpolator(new CycleInterpolator(3))
                        .setDuration(1000).start();
            }

            @Override
            public void Draging(float fraction) {
                Log.e("wulei","Draging :" +fraction);
            }
        });

    }

    private void initView() {
        menu_listview = (ListView) findViewById(R.id.menu_listview);
        main_listview = (ListView) findViewById(R.id.main_listview);
        slideMenu = (SlidingMenu) findViewById(R.id.slideMenu);
        iv_head = (ImageView) findViewById(R.id.iv_head);
        my_layout = (MyLinearLayout) findViewById(R.id.my_layout);
    }

    private void initData() {
        menu_listview.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Constant.sCheeseStrings) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(Color.WHITE);
                return textView;
            }
        });

        main_listview.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Constant.NAMES) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = convertView == null ? super.getView(position, convertView, parent) : convertView;
                //先缩小view
                ViewHelper.setScaleX(view, 0.5f);
                ViewHelper.setScaleY(view, 0.5f);
                //以属性动画放大
                ViewPropertyAnimator.animate(view).scaleX(1).setDuration(350).start();
                ViewPropertyAnimator.animate(view).scaleY(1).setDuration(350).start();
                return view;
            }
        });

        my_layout.setSlidingMenu(slideMenu);
    }
}
