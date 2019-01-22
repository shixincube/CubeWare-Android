package com.common.sdk;

/**
 * 路由地址中心（用于模块间交互）
 *
 * @author LiuFeng
 * @date 2018-8-15
 */
public interface RouterHub {

    /**
     * 组件名
     */
    interface ComponentName {

        // 设置
        String Settings = "ComponentSettings";

        // 消息
        String Message = "ComponentMessage";
    }

    /**
     * 组名-app
     */
    interface App {

    }
}
