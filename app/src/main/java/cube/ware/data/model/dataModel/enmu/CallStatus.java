package cube.ware.data.model.dataModel.enmu;

import java.io.Serializable;

/**
 * 通话状态
 *
 * @author PengZhenjin
 * @date 2017-2-8
 */
public enum CallStatus implements Serializable {

    NO_CALL(70001),                    // 没呼叫

    AUDIO_OUTGOING(70002),            // 语音呼叫
    AUDIO_OUTGOING_RING(70003),      // 语音呼叫对方振铃
    AUDIO_CALLING(70004),             // 语音通话中
    AUDIO_INCOMING(70005),           // 语音来电

    VIDEO_OUTGOING(70006),           // 视频呼叫
    VIDEO_OUTGOING_RING(70007),     // 视频呼叫中对方振铃
    VIDEO_CALLING(70008),            // 视频通话中
    VIDEO_INCOMING(70009),          // 视频来电

    OUTGOING_ERROR(70010),          // 呼叫错误
    INCOMING_ERROR(70011),          // 来电错误

    GROUP_AUDIO_CALLING(70012),       // 多人语音通话中
    GROUP_VIDEO_CALLING(70013),       // 多人语音通话中
    GROUP_CALL_INCOMING(70014),       // 多人语音来电
    GROUP_CALL_JOIN(70015),           // 加入多人语音

    REMOTE_DESKTOP_CALLING(70016),    // 远程桌面通话中
    REMOTE_DESKTOP_INCOMING(70017),   // 远程桌面来电
    REMOTE_DESKTOP_JOIN(70018);       // 加入远程桌面

    public int status;

    CallStatus(int status) {
        this.status = status;
    }

    public static CallStatus parse(int status) {
        for (CallStatus cs : CallStatus.values()) {
            if (cs.status == status) {
                return cs;
            }
        }
        return NO_CALL;
    }

    public int getStatus() {
        return this.status;
    }
}
