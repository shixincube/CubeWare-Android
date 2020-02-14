package cube.ware.service.call;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import com.alibaba.android.arouter.launcher.ARouter;
import com.common.mvp.eventbus.EventBusUtil;
import com.common.utils.utils.RingtoneUtil;
import com.common.utils.utils.log.LogUtil;
import cube.service.CubeEngine;
import cube.service.CubeError;
import cube.service.CubeErrorCode;
import cube.service.Session;
import cube.service.account.AccountState;
import cube.service.call.CallAction;
import cube.service.call.CallDirection;
import cube.service.call.CallListener;
import cube.service.call.ResponseState;
import cube.service.message.CustomMessage;
import cube.service.message.MessageStatus;
import cube.service.message.Receiver;
import cube.service.message.Sender;
import cube.ware.api.CubeUI;
import cube.ware.core.CubeConstants;
import cube.ware.core.CubeCore;
import cube.ware.data.model.dataModel.enmu.CallStatus;
import cube.ware.data.model.dataModel.enmu.CubeCustomMessageType;
import cube.ware.data.model.dataModel.enmu.CubeSessionType;
import cube.ware.service.call.listener.CallStateListener;
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
        mContext = CubeCore.getContext();
        CubeEngine.getInstance().getCallService().addCallListener(this);
    }

    /**
     * 停止监听
     */
    public void stop() {
        CubeEngine.getInstance().getCallService().removeCallListener(this);
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

    /**
     * 收到/发起呼叫（第一个呼叫）
     *
     * @param session
     */
    @Override
    public void onNewCall(CallDirection direction, Session session) {
        if (CubeCore.getInstance().isCalled() || CubeEngine.getInstance().getSession().getAccountState() != AccountState.LoginSucceed) {
            return;
        }

        String callId = session.getCallPeer().getCubeId();    // 通话id
        CallStatus callState;    // 通话状态
        if (session.getCallDirection() == CallDirection.Incoming) {    // 来电  呼叫失败
            if (session.getVideoEnabled()) {
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
            ARouter.getInstance().build(CubeConstants.Router.P2PCallActivity).withBundle("call_data", bundle).navigation();
        }
        else if (session.getCallDirection() == CallDirection.Outgoing) {
            // 播放呼叫铃声
            RingtoneUtil.play(R.raw.outgoing, mContext);
        }
    }

    @Override
    public void onAnotherCall(String s, String s1, boolean b) {

    }

    @Override
    public void onInProgress(Session session) {

    }

    @Override
    public void onCallRinging(Session session) {

    }

    /**
     * 呼叫建立
     *
     * @param session
     */
    @Override
    public void onCallConnected(Session session) {
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
     * @param action
     */
    @Override
    public void onCallEnded(Session session, CallAction action) {
        LogUtil.i("CoreService ===> onCallEnded---session 描述 ==" + session.getCallDirection());
        // 释放铃声
        RingtoneUtil.release();
        RingtoneUtil.play1(R.raw.hungup, mContext);

        if (this.mCallStateListenerList != null && !this.mCallStateListenerList.isEmpty()) {
            for (CallStateListener listener : this.mCallStateListenerList) {
                if (listener != null) {
                    listener.onCallEnded(session, action);
                }
            }
        }

        handleCallEnd(session, action);
        mCallTime = 0;
    }

    @Override
    public void onCallEnded(Session session, CallAction callAction, ResponseState responseState) {

    }

    /**
     * P2P音视频结束消息封装，更新界面显示
     *
     * @param session
     * @param callAction
     */
    public void handleCallEnd(Session session, CallAction callAction) {
        handleCallEnd(session, callAction, null);
    }

    public void handleCallEnd(Session session, CallAction callAction, CubeError error) {
        boolean isCall = false;
        Context context = CubeUI.getInstance().getContext();
        if (session.getCallPeer() != null) {
            String content;
            Sender sender;
            Receiver receiver;
            LogUtil.d("=====结束:" + callAction + " " + session.getCallDirection());
            if (session.getCallDirection() == CallDirection.Outgoing && CallAction.CANCEL.equals(callAction)) {
                content = context.getString(R.string.peer_has_refused);
                isCall = true;
            }
            else if (session.getCallDirection() == CallDirection.Incoming && CallAction.CANCEL.equals(callAction)) {
                content = context.getString(R.string.call_not_accept);
            }
            else if (session.getCallDirection() == CallDirection.Incoming && CallAction.ANSWER_BY_OTHER.equals(callAction) || session.getCallDirection() == CallDirection.Outgoing && CallAction.ANSWER_BY_OTHER.equals(callAction)) {
                content = context.getString(R.string.other_terminal_has_answered);
                isCall = true;
            }
            else if (session.getCallDirection() == CallDirection.Outgoing && CallAction.CANCEL_ACK.equals(callAction)) {
                content = context.getString(R.string.cancelled);
            }
            else if (session.getCallDirection() == CallDirection.Outgoing && CallAction.CANCEL_BY_OTHER.equals(callAction)) {
                content = context.getString(R.string.call_voice_not_answer);
            }
            else if (session.getCallDirection() == CallDirection.Incoming && CallAction.CANCEL_BY_OTHER.equals(callAction)) {
                content = context.getString(R.string.other_terminal_has_cancle);
            }
            else if (session.getCallDirection() == CallDirection.Incoming && CallAction.CANCEL_ACK.equals(callAction)) {
                content = context.getString(R.string.refused);
                isCall = true;
            }
            else if (session.getCallDirection() == CallDirection.Outgoing && CallAction.BYE.equals(callAction) || session.getCallDirection() == CallDirection.Outgoing && CallAction.BYE_ACK.equals(callAction)) {
                if (session.getCallTime() != 0l) {
                    content = context.getString(R.string.call_completed);
                    isCall = true;
                }
                else {
                    content = context.getString(R.string.call_user_busy);
                }
            }
            else if (session.getCallDirection() == CallDirection.Incoming && CallAction.BYE.equals(callAction) || session.getCallDirection() == CallDirection.Incoming && CallAction.BYE_ACK.equals(callAction)) {
                if (session.getCallTime() != 0l) {
                    content = context.getString(R.string.call_completed);
                    isCall = true;
                }
                else {
                    content = context.getString(R.string.call_user_busy);
                }
            }
            else {
                content = context.getString(R.string.call_completed);
            }

            // FIXME: 2017/9/5 暂时避免引擎同时回调callFailed和callEnd的错误
            if (TextUtils.isEmpty(session.getCallPeer().getCubeId())) {
                return;
            }
            if (session.getCallDirection() == CallDirection.Outgoing) {
                sender = new Sender(session.getCubeId(), session.getDisplayName());
                receiver = new Receiver(session.getCallPeer().getCubeId(), session.getCallPeer().getDisplayName());
            }
            else {
                sender = new Sender(session.getCallPeer().getCubeId(), session.getCallPeer().getDisplayName());
                receiver = new Receiver(session.getCubeId(), session.getDisplayName());
            }

            if (error != null) {
                if (error.code == CubeErrorCode.RequestTerminated.code || error.code == CubeErrorCode.DoNotDisturb.code) {
                    content = context.getString(R.string.call_voice_not_answer);
                    String tips = "通话未接听，点击回拨";
                    tips = context.getString(R.string.call_not_accept);
                    //告诉对方，有未接来电
                    CustomMessage message = buildCustomMessage(CubeSessionType.P2P, sender, receiver, tips);
                    if (session.getVideoEnabled()) {
                        message.setHeader("operate", CubeCustomMessageType.VideoCall.type);
                    }
                    else {
                        message.setHeader("operate", CubeCustomMessageType.AudioCall.type);
                    }
                    message.setStatus(MessageStatus.None);
                    CubeEngine.getInstance().getMessageService().sendMessage(message);
                }
            }

            CustomMessage message = buildCustomMessage(CubeSessionType.P2P, sender, receiver, content);
            if (session.getVideoEnabled()) {
                message.setHeader("operate", CubeCustomMessageType.VideoCall.type);
            }
            else {
                message.setHeader("operate", CubeCustomMessageType.AudioCall.type);
            }
            message.setReceived(isCall);
            //存储消息
            EventBusUtil.post(CubeConstants.Event.addMessageInLocal, message);
        }
    }

    /**
     * P2P处理音视频错误回调消息，如对方正在通话中
     *
     * @param session
     * @param cubeError
     */
    public void handleCallFailed(Session session, CubeError cubeError) {
        boolean isCall = false;
        Context context = CubeUI.getInstance().getContext();
        if (session != null && session.getCallPeer() != null) {
            String content = null;
            Sender sender;
            Receiver receiver;
            if (cubeError.code == CubeErrorCode.DoNotDisturb.code || cubeError.code == CubeErrorCode.BusyHere.code || cubeError.code == CubeErrorCode.RequestTerminated.code) {
                content = context.getString(R.string.call_user_busy);
            }
            // FIXME: 2017/9/5 暂时避免引擎同时回调callFailed和callEnd的错误
            if (TextUtils.isEmpty(session.getCallPeer().getCubeId())) {
                return;
            }
            if (session.getCallDirection() == CallDirection.Outgoing) {
                sender = new Sender(session.getCubeId(), session.getDisplayName());
                receiver = new Receiver(session.getCallPeer().getCubeId(), session.getCallPeer().getDisplayName());
            }
            else {
                sender = new Sender(session.getCallPeer().getCubeId(), session.getCallPeer().getDisplayName());
                receiver = new Receiver(session.getCubeId(), session.getDisplayName());
            }

            if (!TextUtils.isEmpty(content)) {
                CustomMessage message = buildCustomMessage(CubeSessionType.P2P, sender, receiver, content);
                if (session.getVideoEnabled()) {
                    message.setHeader("operate", CubeCustomMessageType.VideoCall.type);
                }
                else {
                    message.setHeader("operate", CubeCustomMessageType.AudioCall.type);
                }
                message.setReceived(isCall);
                //存储消息
                EventBusUtil.post(CubeConstants.Event.addMessageInLocal, message);
            }
        }
    }

    /**
     * 呼叫失败
     *
     * @param session
     * @param error
     */
    @Override
    public void onCallFailed(Session session, CubeError error) {
        // 释放铃声
        RingtoneUtil.release();
        if (error.code == CubeErrorCode.RequestTerminated.code || error.code == CubeErrorCode.DoNotDisturb.code) {
            this.mCallTime = System.currentTimeMillis() - mCallTime;
            handleCallFailed(session, error);
        }
        else if (error.code == CubeErrorCode.RequestTimeout.code) {
            //对端不正常挂断错误码 例如对方杀死进程或闪退
            this.mCallTime = System.currentTimeMillis() - mCallTime;
            handleCallEnd(session, CallAction.NUKNOW, error);
        }
        else {
            handleCallFailed(session, error);
        }
        if (CubeErrorCode.convert(error.code) == CubeErrorCode.NetworkNotReachable) {
            RingtoneUtil.play1(R.raw.network_not_reachable, mContext);
        }
        else {
            RingtoneUtil.play1(R.raw.hungup, mContext);
        }
        if (this.mCallStateListenerList != null && !this.mCallStateListenerList.isEmpty()) {
            for (CallStateListener listener : this.mCallStateListenerList) {
                if (listener != null) {
                    listener.onCallFailed(session, CubeErrorCode.convert(error.code));
                }
            }
        }
    }

    /**
     * 构建自定义消息
     *
     * @param sessionType
     * @param sender
     * @param receiver
     * @param content
     *
     * @return
     */
    private static CustomMessage buildCustomMessage(CubeSessionType sessionType, Sender sender, Receiver receiver, String content) {
        CustomMessage message = new CustomMessage(content);
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setStatus(MessageStatus.Succeed);
        message.setTimestamp(System.currentTimeMillis());
        // 设置群消息ID
        if (sessionType == CubeSessionType.Group) {
            message.setGroupId(receiver.getCubeId());
        }
        return message;
    }
}
