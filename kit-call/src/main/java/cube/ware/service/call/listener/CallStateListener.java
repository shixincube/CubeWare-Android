package cube.ware.service.call.listener;

import cube.service.Session;
import cube.service.call.CallAction;
import cube.service.CubeErrorCode;

/**
 * 通话状态监听器
 *
 * @author PengZhenjin
 * @date 2016-11-18
 */
public interface CallStateListener {

    void onCallConnected(Session session);

    void onCallEnded(Session session, CallAction action);

    void onCallFailed(Session session, CubeErrorCode errorCode);
}
