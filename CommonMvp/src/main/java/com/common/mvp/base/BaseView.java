package com.common.mvp.base;

/**
 * view基类
 *
 * @author LiuFeng
 * @date 2017-11-01
 */
public interface BaseView {
    /**
     * 显示Loading框
     */
    void showLoading();

    /**
     * 隐藏Loading框
     */
    void hideLoading();

    /**
     * 显示消息
     *
     * @param message
     */
    void showMessage(String message);

    /**
     * 错误处理
     *
     * @param code
     * @param message
     */
    void onError(int code, String message);
}
