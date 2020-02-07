package cube.ware.service.message.chat.listener;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import com.chad.library.adapter.base.BaseViewHolder;
import com.common.utils.utils.log.LogUtil;
import cube.service.CubeError;
import cube.service.message.FileMessage;
import cube.service.message.ImageMessage;
import cube.service.message.VideoClipMessage;
import cube.ware.data.model.dataModel.enmu.CubeMessageType;
import cube.ware.data.room.model.CubeMessage;
import cube.ware.service.message.MessageHandle;
import cube.ware.service.message.R;
import cube.ware.service.message.chat.fragment.Listener.FileMessageUploadListener;
import cube.ware.widget.CubeProgressBar;

/**
 * 文件消息接收监听器
 *
 * @author PengZhenjin
 * @date 2016/6/13
 */
public class FileMessageSendListener implements FileMessageUploadListener {

    private CubeMessage mCubeMessage;

    private ProgressBar     mProgressBar;//传输进度条
    private CubeProgressBar mPVProgressBar;//图片/视频传输进度条
    private LinearLayout    mProgressLayout;//图片传输进度布局
    private ImageView       mVideoPlay;//视频播放按钮

    public FileMessageSendListener(Context context, CubeMessage cubeMessage, BaseViewHolder holder, View inflate) {
        this.mCubeMessage = cubeMessage;
        CubeMessageType messageType = cubeMessage.getMessageType();
        if (messageType == CubeMessageType.Video || messageType == CubeMessageType.Image) {
            this.mPVProgressBar = (CubeProgressBar) inflate.findViewById(R.id.chat_progress_bar);
            this.mProgressLayout = (LinearLayout) inflate.findViewById(R.id.chat_progress_cover);
            this.mVideoPlay = (ImageView) inflate.findViewById(R.id.chat_video_iv);
            this.mPVProgressBar.setMax(100);
        }
        else {
            this.mProgressBar = holder.getView(R.id.chat_item_progress);
        }
    }

    @Override
    public void onUploading(FileMessage fileMessage, long processed, long total) {
        LogUtil.i("文件消息正在发送：" + processed);
        if (fileMessage instanceof VideoClipMessage) {
            showPV(processed, total);
        }
        else if (fileMessage instanceof ImageMessage) {
            //            this.mProgressLayout.setVisibility(View.GONE);
            showPV(processed, total);
        }
        else {
            this.mProgressBar.setVisibility(View.VISIBLE);
            int percent = (int) ((Double.longBitsToDouble(processed) / Double.longBitsToDouble(total)) * 100);
            this.mProgressBar.setProgress(percent);
        }
    }

    private void showPV(long processed, long total) {
        this.mVideoPlay.setVisibility(View.GONE);
        this.mProgressLayout.setVisibility(View.VISIBLE);
        this.mPVProgressBar.setVisibility(View.VISIBLE);
        int percent = (int) ((Double.longBitsToDouble(processed) / Double.longBitsToDouble(total)) * 100);
        this.mPVProgressBar.setProgress(percent);
    }

    @Override
    public void onUploadCompleted(FileMessage fileMessage) {
        if (mCubeMessage.getMessageSN() != fileMessage.getSerialNumber()) {
            return;
        }
        LogUtil.i("文件消息发送完成");
        if (fileMessage instanceof ImageMessage) {
            this.mProgressLayout.setVisibility(View.GONE);
            this.mPVProgressBar.setVisibility(View.GONE);
            this.mVideoPlay.setVisibility(View.GONE);
        }
        else if (fileMessage instanceof VideoClipMessage) {
            this.mProgressLayout.setVisibility(View.GONE);
            this.mPVProgressBar.setVisibility(View.GONE);
            this.mVideoPlay.setVisibility(View.VISIBLE);
        }
        else {
            this.mProgressBar.setVisibility(View.GONE);
        }

        // 移除监听器
        MessageHandle.getInstance().removeUploadListener(this.mCubeMessage.getMessageSN(), CubeMessage.class.getSimpleName());
    }

    @Override
    public void onUploadFailed(FileMessage fileMessage, CubeError cubeError) {
        if (mCubeMessage.getMessageSN() != fileMessage.getSerialNumber()) {
            return;
        }
        if (fileMessage instanceof ImageMessage) {
            this.mProgressLayout.setVisibility(View.GONE);
            this.mPVProgressBar.setVisibility(View.GONE);
            this.mVideoPlay.setVisibility(View.GONE);
        }
        else if (fileMessage instanceof VideoClipMessage) {
            this.mProgressLayout.setVisibility(View.GONE);
            this.mPVProgressBar.setVisibility(View.GONE);
            this.mVideoPlay.setVisibility(View.VISIBLE);
        }
        else {
            this.mProgressBar.setVisibility(View.GONE);
        }
    }
}
