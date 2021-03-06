package com.cqray.android.app;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.cqray.android.R;
import com.cqray.android.widget.OnTabSelectedListener;
import com.cqray.android.widget.SlidingTabLayout;

/**
 * 基础标签活动
 * @author LeiJue
 */
public class SlidingFragment extends SupportFragment {

    protected ViewPager mViewPager;
    protected SlidingTabLayout mTabLayout;
    protected SupportFragment[] mFragments;

    private int mCurrentTab;

    @Override
    public void onCreating(Bundle savedInstanceState) {
        super.onCreating(savedInstanceState);
        setNativeContentView(R.layout.android_layout_sliding);
        if (savedInstanceState != null) {
            mCurrentTab = savedInstanceState.getInt("currentTab", 0);
        }
        mViewPager = findViewById(R.id.__android_fragment);
        mTabLayout = findViewById(R.id.__android_tab);
        mToolbar = findViewById(R.id.__android_toolbar);
        // Tab选项
        mTabLayout = findViewById(R.id.__android_tab);
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

    /**
     * 加载多个Fragment页面
     * @param tabs Fragment项
     */
    public void loadMultipleFragment(FragmentTab... tabs) {
        loadMultipleFragment(tabs.length, tabs);
    }

    /**
     * 加载多个Fragment页面
     * @param pageLimit 可以缓存的页面数
     * @param tabs Fragment项
     */
    public void loadMultipleFragment(int pageLimit, final FragmentTab... tabs) {
        mFragments = new SupportFragment[tabs.length];
        // 初始化数组
        for (int i = 0, len = tabs.length; i < len; i++) {
            mFragments[i] = tabs[i].newFragment();
        }
        // 设置数据适配
        mViewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager(), 0) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return mFragments[position];
            }

            @Override
            public int getCount() {
                return mFragments.length;
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return tabs[position].getTitle();
            }
        });
        mViewPager.setOffscreenPageLimit(pageLimit);
        // 设置当前选项
        mTabLayout.setViewPager(mViewPager);
        // 设置当前展示的项
        mTabLayout.setCurrentTab(mCurrentTab > tabs.length - 1 ? tabs.length - 1 : mCurrentTab);
    }

    public SupportFragment [] getFragments() {
        return mFragments;
    }
}
