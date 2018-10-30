package com.common.mvp.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * 延迟加载的fragment（即：懒加载）
 *
 * @author LiuFeng
 * @date 2017-11-01
 */
public abstract class BaseLazyFragment extends Fragment {

    private boolean isFirstResume    = true;
    private boolean isFirstVisible   = true;
    private boolean isFirstInvisible = true;
    private boolean isPrepared;

    @Override

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initPrepare();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isFirstResume) {
            isFirstResume = false;
            return;
        }
        if (getUserVisibleHint()) {
            onUserVisible();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getUserVisibleHint()) {
            onUserInvisible();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (isFirstVisible) {
                isFirstVisible = false;
                initPrepare();
            }
            else {
                onUserVisible();
            }
        }
        else {
            if (isFirstInvisible) {
                isFirstInvisible = false;
                onFirstUserInvisible();
            }
            else {
                onUserInvisible();
            }
        }
    }

    private synchronized void initPrepare() {
        if (isPrepared) {
            onFirstUserVisible();
        }
        else {
            isPrepared = true;
        }
    }

    /**
     * 第一次fragment可见（进行初始化工作）
     */
    protected abstract void onFirstUserVisible();

    /**
     * fragment可见（切换回来或者onResume）
     */
    protected abstract void onUserVisible();

    /**
     * 第一次fragment不可见（不建议在此处理事件）
     */
    private void onFirstUserInvisible() {
        // here we do not recommend do something
    }

    /**
     * fragment不可见（切换掉或者onPause）
     */
    protected abstract void onUserInvisible();
}
