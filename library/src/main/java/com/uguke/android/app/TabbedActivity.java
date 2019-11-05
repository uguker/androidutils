package com.uguke.android.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.uguke.android.widget.OnTabSelectedListener;
import com.uguke.android.widget.CommonTabLayout;
import com.uguke.android.R;

import java.util.Arrays;

/**
 * 基础标签活动
 * @author LeiJue
 */
public class TabbedActivity extends SupportActivity {

    protected CommonTabLayout mTabLayout;
    protected SupportFragment [] mFragments;
    private int mCurrentTab = 0;

    @Override
    public void onCreating(Bundle savedInstanceState) {
        setNativeContentView(R.layout.android_layout_tabbed);
        mLayoutDelegate.initHeaderAndFooter();
        // 恢复选项位置
        if (savedInstanceState != null) {
            mCurrentTab = savedInstanceState.getInt("currentTab", 0);
        }
        // Tab选项
        mTabLayout = findViewById(R.id.android_tab);
        mTabLayout.addOnTabSelectedListener(new OnTabSelectedListener() {

            @Override
            public void onTabSelected(int position) {
                mDelegate.showHideFragment(mFragments[position]);
                mCurrentTab = position;
            }

            @Override
            public void onTabReselected(int position) {
                mCurrentTab = position;
            }

        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentTab", mCurrentTab);
    }

//    @Override
//    protected void onHandleCreators(View view) {
//        ViewGroup headerParent = view.findViewById(R.id.android_header);
//        ViewGroup footerParent = view.findViewById(R.id.android_footer);
//        // 创建Header和Footer内容
//        ViewCreator headerCreator = onCreateHeader(headerParent);
//        ViewCreator footerCreator = onCreateFooter(footerParent);
//        RelativeLayout.LayoutParams params;
//        // 初始化头部
//        if (headerCreator != null) {
//            if (headerParent.getChildCount() == 0) {
//                View header = LayoutInflater.from(this).inflate(headerCreator.getLayoutResId(), headerParent, true);
//                header.bringToFront();
//            }
//            boolean floating = headerCreator.isFloating();
//            params = new RelativeLayout.LayoutParams(-1, -1);
//            if (!floating) {
//                params.addRule(RelativeLayout.BELOW, R.id.android_header);
//            }
//            findViewById(R.id.android_fragment).setLayoutParams(params);
//        }
//        // 初始化底部
//        if (footerCreator != null) {
//            if (footerParent.getChildCount() == 0) {
//                View footer = LayoutInflater.from(this).inflate(footerCreator.getLayoutResId(), footerParent, true);
//                footer.bringToFront();
//            }
//            boolean floating = footerCreator.isFloating();
//            params = new RelativeLayout.LayoutParams(-1, -1);
//            if (!floating) {
//                params.addRule(RelativeLayout.ABOVE, R.id.android_header);
//            }
//            findViewById(R.id.android_fragment).setLayoutParams(params);
//        }
//    }

    public void showFragment(int position) {
        mDelegate.showHideFragment(mFragments[position]);
        mTabLayout.setCurrentTab(position);
    }

    public void showFragment(SupportFragment fragment) {
        int index = 0;
        for (int i = 0; i < mFragments.length; i++) {
            if (mFragments[i] == fragment) {
                index = i;
                break;
            }
        }
        mDelegate.showHideFragment(fragment);
        mTabLayout.setCurrentTab(index);
    }

    public void showFragment(Class<? extends SupportFragment> clazz) {
        int index = 0;
        for (int i = 0; i < mFragments.length; i++) {
            if (mFragments[i].getClass() == clazz) {
                index = i;
                break;
            }
        }
        mDelegate.showHideFragment(mFragments[index]);
        mTabLayout.setCurrentTab(index);
    }

    public void loadMultipleFragment(FragmentTab... tabs) {
        if (tabs == null || tabs.length == 0) {
            return;
        }
        mFragments = new SupportFragment[tabs.length];
        if (findFragmentByTag(tabs[0].getTag()) == null) {
            for (int i = 0, len = tabs.length; i < len; i++) {
                mFragments[i] = tabs[i].newFragment();
            }
            mDelegate.loadMultipleRootFragment(R.id.android_fragment, 0, mFragments);
        } else {
            for (int i = 0, len = tabs.length; i < len; i++) {
                mFragments[i] = findFragmentByTag(tabs[0].getTag());
            }
        }
        mTabLayout.setTabData(Arrays.asList(tabs));
        // 设置当前展示的项
        mTabLayout.setCurrentTab(mCurrentTab > tabs.length - 1 ? tabs.length - 1 : mCurrentTab);
    }

    public SupportFragment [] getFragments() {
        return mFragments;
    }
}
