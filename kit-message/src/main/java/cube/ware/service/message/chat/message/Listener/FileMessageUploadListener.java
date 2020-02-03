package cube.ware.service.message.chat.message.Listener;


import cube.service.CubeError;
import cube.service.message.FileMessage;

/**
 * 文件消息上传监听器
 *
 * @author PengZhenjin
 * @date 2016-11-17
 */
public interface FileMessageUploadListener {
    void onUploading(FileMessage fileMessage, long processed, long total);

    void onUploadCompleted(FileMessage fileMessage);

    void onUploadFailed(FileMessage messageEntity, CubeError cubeError);
}
