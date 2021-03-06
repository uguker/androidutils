package com.cqray.android.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.cqray.android.R;
import com.cqray.android.swipe.SwipeBackHelper;
import com.cqray.android.widget.CommonToolbar;
import com.cqray.android.widget.LoadingLayout;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import me.yokeyword.fragmentation.ExtraTransaction;
import me.yokeyword.fragmentation.ISupportActivity;
import me.yokeyword.fragmentation.SupportActivityDelegate;
import me.yokeyword.fragmentation.SupportHelper;
import me.yokeyword.fragmentation.anim.FragmentAnimator;

/**
 * 基础Activity
 * @author LeiJue
 */
public class SupportActivity extends AppCompatActivity implements ISupportActivity, ViewProvider {

    public View mContentView;
    /** 标题 **/
    public CommonToolbar mToolbar;
    /** 多状态控件（加载） **/
    public LoadingLayout mLoadingLayout;
    /** 刷新控件 **/
    public SmartRefreshLayout mRefreshLayout;

    final CompositeDisposable mDisposable = new CompositeDisposable();
    /** 界面布局委托 **/
    final ViewDelegate mViewDelegate = new ViewDelegate(this);
    /** Fragment管理委托 **/
    final SupportActivityDelegate mDelegate = new SupportActivityDelegate(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDelegate.onCreate(savedInstanceState);
        mViewDelegate.onCreate(savedInstanceState);
        if (onSwipeBackSupport()) {
            SwipeBackHelper.onCreate(this);
        }
        onCreating(savedInstanceState);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDelegate.onPostCreate(savedInstanceState);
        if (onSwipeBackSupport()) {
            SwipeBackHelper.onPostCreate(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mViewDelegate.onViewVisible();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mViewDelegate.onViewInvisible();
    }

    @Override
    protected void onDestroy() {
        mDisposable.dispose();
        mViewDelegate.onDestroy();
        mDelegate.onDestroy();
        if (onSwipeBackSupport()) {
            SwipeBackHelper.onDestroy(this);
        }
        super.onDestroy();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return mDelegate.dispatchTouchEvent(ev) || super.dispatchTouchEvent(ev);
    }

    /**
     * 不建议复写该方法,请使用 {@link #onBackPressedSupport} 代替
     */
    @Override
    final public void onBackPressed() {
        mDelegate.onBackPressed();
    }

    @Override
    public SupportActivityDelegate getSupportDelegate() {
        return mDelegate;
    }

    /**
     * Perform some extra transactions.
     * 额外的事务：自定义Tag，添加SharedElement动画，操作非回退栈Fragment
     */
    @Override
    public ExtraTransaction extraTransaction() {
        return mDelegate.extraTransaction();
    }

    /**
     * 该方法回调时机为,Activity回退栈内Fragment的数量 小于等于1 时,默认finish Activity
     * 请尽量复写该方法,避免复写onBackPress(),以保证SupportFragment内的onBackPressedSupport()回退事件正常执行
     */
    @Override
    public void onBackPressedSupport() {
        mDelegate.onBackPressedSupport();
    }

    /**
     * 获取设置的全局动画 copy
     */
    @Override
    public FragmentAnimator getFragmentAnimator() {
        return mDelegate.getFragmentAnimator();
    }

    /**
     * 设置Fragment内的全局动画
     */
    @Override
    public void setFragmentAnimator(FragmentAnimator fragmentAnimator) {
        mDelegate.setFragmentAnimator(fragmentAnimator);
    }

    /**
     * 构建Fragment转场动画
     * 如果是在Activity内实现,则构建的是Activity内所有Fragment的转场动画,
     * 如果是在Fragment内实现,则构建的是该Fragment的转场动画,此时优先级 > Activity的onCreateFragmentAnimator()
     * @return FragmentAnimator对象
     */
    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        return mDelegate.onCreateFragmentAnimator();
    }

    @Override
    public void post(Runnable runnable) {
        mDelegate.post(runnable);
    }

    public void onCreating(@Nullable Bundle savedInstanceState) {}

    @Override
    public void setContentView(@LayoutRes int id) {
        mViewDelegate.setContentView(id);
    }

    @Override
    public void setContentView(View view) {
        mViewDelegate.setContentView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        view.setLayoutParams(params);
        mViewDelegate.setContentView(view);
    }

    @Override
    public void onViewCreated(View view) {
        // 设置布局
        getDelegate().setContentView(view);
        // 设置内容布局
        mContentView = view;
        // 初始化控件
        mToolbar = mViewDelegate.getToolbar();
        mLoadingLayout = mViewDelegate.getLoadingLayout();
        mRefreshLayout = mViewDelegate.getRefreshLayout();
    }

    public void setNativeContentView(@LayoutRes int id) {
        mViewDelegate.setNativeContentView(id);
    }

    public void setNativeContentView(View view) {
        mViewDelegate.setNativeContentView(view);
    }

    public ViewCreator onCreateHeader(@NonNull ViewGroup container) {
        return null;
    }

    public ViewCreator onCreateFooter(@NonNull ViewGroup container) {
        return null;
    }

    public void showTips(String tips) {
        mViewDelegate.showTips(tips);
    }

    public void hideToolbar() {
        View view = findViewById(R.id.__android_bar);
        if (view != null) {
            view.setVisibility(View.GONE);
        } else if (mToolbar != null) {
            mToolbar.setVisibility(View.GONE);
        }
    }

    public void addDisposable(Disposable disposable) {
        mDisposable.add(disposable);
    }

    /**
     * 是否支持侧滑返回 true 支持 false 不支持
     * 不支持的情况下{@link SwipeBackHelper}所有方法无效
     */
    public boolean onSwipeBackSupport() {
        return AndroidDelegate.getInstance().isSwipeBackSupport();
    }

    /**
     * 限制SwipeBack的条件,默认栈内Fragment数 <= 1时 , 优先滑动退出Activity , 而不是Fragment
     * @return true: Activity优先滑动退出;  false: Fragment优先滑动退出
     */
    public boolean onSwipeBackPriority() {
        return getSupportFragmentManager().getBackStackEntryCount() <= 1;
    }

    /**
     * 当Fragment根布局 没有 设定background属性时,
     * Fragmentation默认使用Theme的android:windowBackground作为Fragment的背景,
     * 可以通过该方法改变其内所有Fragment的默认背景。
     */
    public void setDefaultFragmentBackground(@DrawableRes int backgroundRes) {
        mDelegate.setDefaultFragmentBackground(backgroundRes);
    }

    //============ Fragment操作方法 ============//

    /**
     * 加载根Fragment, Fragment内的第一个子Fragment
     * @param containerId 容器id
     * @param toFragment  目标Fragment
     */
    public void loadRootFragment(int containerId, SupportFragment toFragment) {
        mDelegate.loadRootFragment(containerId, toFragment);
    }

    public void loadRootFragment(int containerId, SupportFragment toFragment, boolean addToBackStack, boolean allowAnim) {
        mDelegate.loadRootFragment(containerId, toFragment, addToBackStack, allowAnim);
    }

    public void start(SupportFragment toFragment) {
        mDelegate.start(toFragment);
    }

    public void start(final SupportFragment toFragment, @SupportFragment.LaunchMode int launchMode) {
        mDelegate.start(toFragment, launchMode);
    }

    /**
     * 类型{@link Activity#startActivityForResult(Intent, int)}
     * 成功回调{@link SupportFragment#onFragmentResult(int, int, Bundle)}
     * @param toFragment 目标Fragment
     * @param requestCode 请求码
     */
    public void startForResult(SupportFragment toFragment, int requestCode) {
        mDelegate.startForResult(toFragment, requestCode);
    }

    /**
     * 启动一个Fragment同时销毁自己
     */
    public void startWithPop(SupportFragment toFragment) {
        mDelegate.startWithPop(toFragment);
    }

    /**
     * @see #popTo(Class, boolean)
     * +
     * @see #start(SupportFragment)
     */
    public void startWithPopTo(SupportFragment toFragment, Class<?> targetFragmentClass, boolean includeTargetFragment) {
        mDelegate.startWithPopTo(toFragment, targetFragmentClass, includeTargetFragment);
    }

    public void replaceFragment(SupportFragment toFragment, boolean addToBackStack) {
        mDelegate.replaceFragment(toFragment, addToBackStack);
    }

    public void pop() {
        mDelegate.pop();
    }

    /**
     * 出栈到目标fragment
     * @param targetFragmentClass   目标fragment
     * @param includeTargetFragment 是否包含该fragment
     */
    public void popTo(Class<?> targetFragmentClass, boolean includeTargetFragment) {
        mDelegate.popTo(targetFragmentClass, includeTargetFragment);
    }

    /**
     * 如果你想在出栈后, 立刻进行FragmentTransaction操作，请使用该方法
     */
    public void popTo(Class<?> targetFragmentClass, boolean includeTargetFragment, Runnable afterPopTransactionRunnable) {
        mDelegate.popTo(targetFragmentClass, includeTargetFragment, afterPopTransactionRunnable);
    }

    public void popTo(Class<?> targetFragmentClass, boolean includeTargetFragment, Runnable afterPopTransactionRunnable, int popAnim) {
        mDelegate.popTo(targetFragmentClass, includeTargetFragment, afterPopTransactionRunnable, popAnim);
    }

    /**
     * 得到位于栈顶Fragment
     */
    public SupportFragment getTopFragment() {
        return (SupportFragment) SupportHelper.getTopFragment(getSupportFragmentManager());
    }

    /**
     * 获取栈内的fragment对象
     */
    public <T extends SupportFragment> T findFragment(Class<T> fragmentClass) {
        return SupportHelper.findFragment(getSupportFragmentManager(), fragmentClass);
    }

    /**
     * 获取栈内的fragment对象
     */
    public SupportFragment findFragmentByTag(String tag) {
        return SupportHelper.findFragment(getSupportFragmentManager(), tag);
    }
}
