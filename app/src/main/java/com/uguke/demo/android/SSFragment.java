package com.uguke.demo.android;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.uguke.android.app.FragmentTab;
import com.uguke.android.app.SlidingFragment;
import com.uguke.android.app.ViewCreator;
import com.uguke.android.bus.RxBus;
import com.uguke.android.widget.CommonToolbar;

/**
 * @author LeiJue
 */
public class SSFragment extends SlidingFragment {

    TextView tv;

    @Override
    public void onCreating(@Nullable Bundle savedInstanceState) {
        super.onCreating(savedInstanceState);
        //setContentView(R.layout.fragment_info);
        //setBackgroundColor(Color.RED);
        //setSwipeBackEnable(true);

//        SwipeBackHelper.getCurrentPage(this)
//                .setSwipeEdgePercent(0.1f);
        //applyToolbar();



        mToolbar.setRippleEnable(true)
                .setBackIcon(R.drawable.def_back_material_dark)
                .setActionIconVisible(0, true)
                .setActionIcon(1, R.drawable.ic_empty)
                .setActionText(0, "你好")
                .setActionTextBold(true)
                .setActionTextColor(Color.WHITE)
                .setActionTextSize(18)
                .setBackText("返回")
                .setBackIconVisible(true)
                .setBackTextVisible(false)
                .setTitle("你好啊")
                .setTitleMargin(16)
                .setBackMargin(0)
                .setTitleTextGravity(CommonToolbar.CENTER)
                .setActionText(1, "6666")
                .setPadding(16, 16)
                .setActionTextVisible(true);

        //showLoading("我在加载中");


        loadMultipleFragment(
                new FragmentTab("我的", TestFragment.class),
                new FragmentTab("她的", TestFragment.class)
        );

        RxBus.with(this, int.class)
                .setCode(1)
                .subscribe(event -> {
                    Log.e("数据", "我接到:" + event);
                }, throwable -> {
                    Log.e("数据", "失败:" + throwable.getMessage());
                });
        RxBus.with(this, CharSequence.class)
                .setCode(1)
                .subscribe(event -> {
                    Log.e("数据", "我接到2:" + event);
                }, throwable -> {
                    Log.e("数据", "失败2:" + throwable.getMessage());
                });
//
//        tv = findViewById(R.id.text2);
//        tv.setText("测试");

    }

    @Override
    public ViewCreator onCreateHeader(ViewGroup container) {
        ViewCreator vc = ViewCreator.create(R.layout.bottom2, container);
        TextView tv = vc.findViewById(R.id.text2);
        tv.setText("我城东了");
        return vc;
    }

    //    @OnClick(R.id.tv)
//    public void onClick() {
//        Intent intent = new Intent(mActivity, MainActivity.class);
//        mActivity.startActivity(intent);
//
//    }
}
