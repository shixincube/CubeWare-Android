package cube.ware.service.listener;

import cube.service.call.CallAction;
import cube.service.call.model.CallSession;
import cube.service.common.model.CubeErrorCode;

/**
 * 通话状态监听器
 *
 * @author PengZhenjin
 * @date 2016-11-18
 */
public interface CallStateListener {

    void onInProgress(CallSession session);

    void onCallRinging(CallSession session);

    void onCallConnected(CallSession session);

    void onCallEnded(CallSession session, CallAction action);

    void onCallFailed(CallSession session, CubeErrorCode errorCode);
}
