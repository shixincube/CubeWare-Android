package cube.ware;

import com.umeng.analytics.MobclickAgent;
import cube.service.common.model.CubeError;
import cube.service.common.model.Version;
import cube.service.message.model.MessageEntity;

/**
 * @author: 2nd
 * @data: 2019/3/6
 */

public class ReportService {

    private static final StringBuilder BUFFER = new StringBuilder();

    /**
     * 上传错误到友盟
     *
     * @param messageEntity
     * @param cubeError
     */
    public static void reportError(MessageEntity messageEntity, CubeError cubeError) {
        String desc = (messageEntity != null ? ("sn:" + messageEntity.getSerialNumber() + " type:" + messageEntity.getType() + " sendTime:" + messageEntity.getSendTimestamp() + " time:" + messageEntity.getTimestamp()) : null);
        reportError(desc, cubeError);
    }

    /**
     * 上传错误到友盟
     *
     * @param desc
     */
    public static void reportError(String desc) {
        reportError(desc, null);
    }

    /**
     * 上传错误到友盟
     *
     * @param cubeError
     */
    public static void reportError(CubeError cubeError) {
        reportError("", cubeError);
    }

    /**
     * 上传错误到友盟
     *
     * @param desc
     * @param cubeError
     */
    public static void reportError(String desc, CubeError cubeError) {
        synchronized (BUFFER) {
            BUFFER.append("onFailed: Version:");
            BUFFER.append(Version.getDescription());
            BUFFER.append("WB:");
            BUFFER.append(Version.WB_V);
            BUFFER.append(" CC:");
            BUFFER.append(genie.api.Version.getNumbers());
            BUFFER.append("\n");
            BUFFER.append(cubeError != null ? cubeError.toString() : "");
            BUFFER.append("\n");
            BUFFER.append(desc);

            MobclickAgent.reportError(App.getContext(), BUFFER.toString());

            BUFFER.delete(0, BUFFER.length());
        }
    }
}
