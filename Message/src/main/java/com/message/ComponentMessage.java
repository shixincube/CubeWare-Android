package com.message;

import com.billy.cc.core.component.CC;
import com.billy.cc.core.component.CCResult;
import com.billy.cc.core.component.CCUtil;
import com.billy.cc.core.component.IComponent;
import com.billy.cc.core.component.IMainThread;
import com.common.sdk.RouterHub;

/**
 * 消息组件
 *
 * @author LiuFeng
 * @data 2019/1/22 10:13
 */
public class ComponentMessage implements IComponent, IMainThread {

    @Override
    public String getName() {
        return RouterHub.ComponentName.Message;
    }

    @Override
    public boolean onCall(CC cc) {
        String actionName = cc.getActionName();
        CCUtil.navigateTo(cc, MainActivity.class);

        //发送组件调用的结果（返回信息）
        CC.sendCCResult(cc.getCallId(), CCResult.success());
        //返回值说明
        // false: 组件同步实现（onCall方法执行完之前会将执行结果CCResult发送给CC）
        // true: 组件异步实现（onCall方法执行完之后再将CCResult发送给CC，CC会持续等待组件调用CC.sendCCResult发送的结果，直至超时）
        return false;
    }

    @Override
    public Boolean shouldActionRunOnMainThread(String actionName, CC cc) {
        //onCall(CC) 方法是否在主线程运行
        return null;
    }
}
