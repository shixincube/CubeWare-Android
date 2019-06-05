package cube.ware.service.call;

import android.content.Context;
import android.os.Bundle;
import com.alibaba.android.arouter.launcher.ARouter;
import com.common.utils.utils.RingtoneUtil;
import com.common.utils.utils.log.LogUtil;
import cube.service.CubeEngine;
import cube.service.call.CallAction;
import cube.service.call.CallDirection;
import cube.service.call.CallListener;
import cube.service.call.model.CallSession;
import cube.service.common.model.CubeError;
import cube.service.common.model.CubeErrorCode;
import cube.service.user.UserState;
import cube.service.user.model.User;
import cube.ware.AppConstants;
import cube.ware.CubeUI;
import cube.ware.R;
import cube.ware.data.model.dataModel.enmu.CallStatus;
import cube.ware.manager.MessageManager;
import cube.ware.service.call.manager.OneOnOneCallManager;
import cube.ware.service.listener.CallStateListener;
import java.util.ArrayList;
import java.util.List;

/**
 * 引擎通话服务处理
 *
 * @author LiuFeng
 * @date 2018-8-09
 */
public class CallHandle implements CallListener {

    private static CallHandle instance = new CallHandle();

    private long mCallTime = -1;//通话时长

    private Context mContext;

    private List<CallStateListener> mCallStateListenerList = new ArrayList<>();

    private CallHandle() {}

    public static CallHandle getInstance() {
        return instance;
    }

    /**
     * 启动监听
     */
    public void start() {
        mContext = CubeUI.getInstance().getContext();
        CubeEngine.getInstance().getCallService().addCallListener(this);
    }

    /**
     * 停止监听
     */
    public void stop() {
        CubeEngine.getInstance().getCallService().removeCallListener(this);
    }

    /**
     * 收到/发起呼叫（第一个呼叫）
     *
     * @param session
     * @param from
     */
    @Override
    public void onCall(CallSession session, User from) {

        LogUtil.d("===收到了邀请通知" + from.cubeId);
        LogUtil.i("CoreService ===> onCall callDirection=" + session + session.isVideoEnabled());
        if (OneOnOneCallManager.getInstance().isCalling() || CubeEngine.getInstance().getSession().userState != UserState.LoginSucceed) {
            return;
        }
        OneOnOneCallManager.getInstance().setCalling(true);

        String callId = session.getCaller().cubeId;    // 通话id
        CallStatus callState;    // 通话状态
        if (session.callDirection == CallDirection.Incoming) {    // 来电  呼叫失败
            if (session.isVideoEnabled()) {
                callState = CallStatus.VIDEO_INCOMING;
            }
            else {
                callState = CallStatus.AUDIO_INCOMING;
            }
            // 播放来电铃声
            RingtoneUtil.play(R.raw.ringing, mContext);

            // 跳转到拨打电话页面
            Bundle bundle = new Bundle();
            bundle.putString("call_id", callId);
            bundle.putSerializable("call_state", callState);
            bundle.putLong("call_time", 0l);
            ARouter.getInstance().build(AppConstants.Router.P2PCallActivity).withBundle("call_data", bundle).navigation();
        }
        else if (session.callDirection == CallDirection.Outgoing) {
            // 播放呼叫铃声
            RingtoneUtil.play(R.raw.outgoing, mContext);
        }
    }

    /**
     * 收到新的呼叫（在通话中收到新的呼叫）
     *
     * @param session
     * @param from
     */
    @Override
    public void onNewCall(CallSession session, User from) {

    }

    /**
     * 呼叫或者接听过程中
     *
     * @param session
     * @param from
     */
    @Override
    public void onCallProgress(CallSession session, User from) {

    }

    /**
     * 对方响铃（目前私有信令未实现）
     *
     * @param session
     * @param from
     */
    @Override
    public void onCallRinging(CallSession session, User from) {

    }

    /**
     * 呼叫建立
     *
     * @param session
     * @param from
     */
    @Override
    public void onCallConnected(CallSession session, User from) {
        // 释放铃声
        RingtoneUtil.release();
        mCallTime = System.currentTimeMillis();
        if (this.mCallStateListenerList != null && !this.mCallStateListenerList.isEmpty()) {
            for (CallStateListener listener : this.mCallStateListenerList) {
                if (listener != null) {
                    listener.onCallConnected(session);
                }
            }
        }
    }

    /**
     * 呼叫挂断
     *
     * @param session
     * @param from
     */
    @Override
    public void onCallEnded(CallSession session, User from) {
        // TODO: 2017/7/15
        OneOnOneCallManager.getInstance().restCalling();

        LogUtil.i("CoreService ===> onCallEnded---session action ==" + session.getAction());
        LogUtil.i("CoreService ===> onCallEnded---session 描述 ==" + session.getCallDirection());
        // 释放铃声
        RingtoneUtil.release();
        RingtoneUtil.play1(R.raw.hungup, mContext);

        if (this.mCallStateListenerList != null && !this.mCallStateListenerList.isEmpty()) {
            for (CallStateListener listener : this.mCallStateListenerList) {
                if (listener != null) {
                    listener.onCallEnded(session, session.getAction());
                }
            }
        }
        LogUtil.d("====走到了这里吗？====onCallEnded---");
        MessageManager.getInstance().onCallEnd(mContext, session, session.getAction());
        mCallTime = 0;
    }

    /**
     * 呼叫在其他设备挂断
     *
     * @param session
     * @param from
     */
    @Override
    public void onCallEndedOther(CallSession session, User from) {

    }

    /**
     * 呼叫失败
     *
     * @param session
     * @param error
     * @param from
     */
    @Override
    public void onCallFailed(CallSession session, CubeError error, User from) {
        LogUtil.d("===走到了这里了==呼叫失败");
        OneOnOneCallManager.getInstance().restCalling();
        // 释放铃声
        RingtoneUtil.release();
        if (error.code == CubeErrorCode.RequestTerminated.code || error.code == CubeErrorCode.DoNotDisturb.code) {
            this.mCallTime = System.currentTimeMillis() - mCallTime;
            MessageManager.getInstance().onCallFailed(mContext, session, error);
        }
        else if (error.code == CubeErrorCode.RequestTimeout.code) {
            //对端不正常挂断错误码 例如对方杀死进程或闪退
            this.mCallTime = System.currentTimeMillis() - mCallTime;
            MessageManager.getInstance().onCallEnd(mContext, session, CallAction.UNKNOWN, error);
        }
        else {
            MessageManager.getInstance().onCallFailed(mContext, session, error);
        }
        if (CubeErrorCode.convert(error.code) == CubeErrorCode.NetworkNotReachable) {
            RingtoneUtil.play1(R.raw.network_not_reachable, mContext);
        }
        else {
            RingtoneUtil.play1(R.raw.hungup, mContext);
        }
        if (this.mCallStateListenerList != null && !this.mCallStateListenerList.isEmpty()) {
            for (CallStateListener listener : this.mCallStateListenerList) {
                LogUtil.d("===监听器的大小===" + mCallStateListenerList.size());
                if (listener != null) {
                    listener.onCallFailed(session, CubeErrorCode.convert(error.code));
                }
            }
        }
    }

    /**
     * 添加一个通话状态监听器
     *
     * @param listener
     */
    public void addCallStateListener(CallStateListener listener) {
        this.mCallStateListenerList.add(listener);
    }

    /**
     * 移除一个通话状态监听器
     *
     * @param listener
     */
    public void removeCallStateListener(CallStateListener listener) {
        this.mCallStateListenerList.remove(listener);
    }
}
