package cube.ware.service.message.chat.listener;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseViewHolder;
import com.common.utils.utils.glide.GlideUtil;
import com.common.utils.utils.log.LogUtil;
import cube.service.message.FileMessage;
import cube.service.message.ImageMessage;
import cube.service.message.VideoClipMessage;
import cube.service.message.VoiceClipMessage;
import cube.ware.service.message.R;
import cube.ware.data.model.dataModel.enmu.CubeMessageType;
import cube.ware.data.room.model.CubeMessage;
import cube.ware.service.message.MessageHandle;
import cube.ware.service.message.chat.message.Listener.FileMessageDownloadListener;
import cube.ware.utils.ImageUtil;
import cube.ware.widget.CubeProgressBar;
import java.io.File;

/**
 * 文件消息发送监听器
 *
 * @author PengZhenjin
 * @date 2016/6/13
 */
public class FileMessageReceiveListener implements FileMessageDownloadListener {
    private static final String          TAG = FileMessageReceiveListener.class.getSimpleName();
    private              FrameLayout     mContainerView;
    private              Context         mContext;
    private              CubeMessage     mCubeMessage;
    private              BaseViewHolder  mViewHolder;
    private              View            mInflate;
    private              TextView        mFileStatus;
    private              ProgressBar     mFileProgressBar;
    private              ProgressBar     mProgressBar;//消息接收进度条
    private              CubeProgressBar mPVProgressBar;//图片/视频接收进度条
    private              LinearLayout    mProgressLayout;//图片传输进度布局
    private              ImageView       mVideoPlay;//视频播放按钮

    private boolean isDownLoading;

    private int percent;

    /**
     * 获取下载的百分比
     *
     * @return
     */
    public int getReceivePercent() {
        return percent;
    }

    public boolean isDownLoading() {
        return isDownLoading;
    }

    public FileMessageReceiveListener(Context context, CubeMessage cubeMessage, BaseViewHolder holder, View inflate) {
        this.mContext = context;
        this.mCubeMessage = cubeMessage;
        this.mViewHolder = holder;
        this.mInflate = inflate;
        mInflate.setTag(R.string.app_name, cubeMessage.getMessageSN() + "");
        CubeMessageType messageType = cubeMessage.getMessageType();
        if (messageType == CubeMessageType.Video || messageType == CubeMessageType.Image) {
            this.mPVProgressBar = (CubeProgressBar) this.mInflate.findViewById(R.id.chat_progress_bar);
            this.mProgressLayout = (LinearLayout) this.mInflate.findViewById(R.id.chat_progress_cover);
            this.mVideoPlay = (ImageView) this.mInflate.findViewById(R.id.chat_video_iv);
        }
        else if (messageType == CubeMessageType.File) {
            this.mFileProgressBar = (ProgressBar) this.mInflate.findViewById(R.id.item_message_file_transfer_progress_bar);
            this.mFileStatus = (TextView) this.mInflate.findViewById(R.id.item_message_file_status_label);
            this.mContainerView = this.mViewHolder.getView(R.id.chat_item_content);
        }
        else if (messageType == CubeMessageType.Voice) {
            this.mProgressBar = this.mViewHolder.getView(R.id.chat_item_progress);
            this.mContainerView = (FrameLayout) this.mInflate.findViewById(R.id.item_message_audio_container);
        }
    }

    @Override
    public void onDownloading(FileMessage fileMessage, long processed, long total) {
        LogUtil.i("文件消息正在接收：" + processed);
        //因为收到下载的回调的时候 View可能已经被复用了 直接显示会出现图片错乱 所以要判断现在view是属于哪个SN的
        String sn = (String) mInflate.getTag(R.string.app_name);
        if (fileMessage == null || sn == null || !sn.equals(String.valueOf(fileMessage.getSerialNumber()))) {
            return;
        }
        isDownLoading = true;
        if (fileMessage instanceof ImageMessage) {
            this.mProgressLayout.setVisibility(View.GONE);
            //showProgressNum(processed, total);
        }
        else if (fileMessage instanceof VoiceClipMessage) {
            this.mProgressBar.setVisibility(View.VISIBLE);
        }
        else if (fileMessage instanceof VideoClipMessage) {
            showProgressNum(processed, total);
        }
        else {
            //            this.mFileProgressBar.setVisibility(View.GONE);
            this.mFileProgressBar.setVisibility(View.VISIBLE);
            int percent = (int) (Double.parseDouble(String.valueOf(processed)) / Double.parseDouble(String.valueOf(total)) * 100);
            this.mFileProgressBar.setProgress(percent);
        }
    }

    private void showProgressNum(long processed, long total) {
        this.mVideoPlay.setVisibility(View.GONE);
        this.mProgressLayout.setVisibility(View.VISIBLE);
        this.mPVProgressBar.setVisibility(View.VISIBLE);
        percent = (int) ((Double.longBitsToDouble(processed) / Double.longBitsToDouble(total)) * 100);
        this.mPVProgressBar.setProgress(percent);
    }

    @Override
    public void onDownloadCompleted(FileMessage fileMessage) {
        // TODO: 2017/9/11  2没有及时释放监听器 导致下载完成后 crash
        //因为收到下载的回调的时候 View可能已经被复用了 直接显示会出现图片错乱 所以要判断现在view是属于哪个SN的
        String sn = (String) mInflate.getTag(R.string.app_name);
        LogUtil.i(TAG + "文件消息接收完成 下载的时候存储的sn=" + sn);
        if (fileMessage == null || sn == null || !sn.equals(String.valueOf(fileMessage.getSerialNumber()))) {
            return;
        }
        isDownLoading = false;
        if (fileMessage instanceof ImageMessage) {
            this.mProgressLayout.setVisibility(View.GONE);
            this.mPVProgressBar.setVisibility(View.GONE);
            ImageMessage imageMessage = (ImageMessage) fileMessage;
            ImageView contentImageView = mViewHolder.getView(R.id.chat_content_iv);
            File imageFile = imageMessage.getFile();
            File thumbFile = imageMessage.getThumbFile();
            if (null != thumbFile && thumbFile.exists()) {
                contentImageView.setImageURI(Uri.fromFile(thumbFile));
                // TODO: 2018/1/24 使用Glide加载出现了加载出空白的bug 没有找到原因 因此替换为 setImageURI原生方式
                // ImageUtil.displayImage(mContext, R.drawable.default_image, contentImageView, imageSize.width, imageSize.height, thumbPath);
            }
            else if (null != imageFile && imageFile.exists()) {
                contentImageView.setImageURI(Uri.fromFile(imageFile));
                //ImageUtil.displayImage(mContext, R.drawable.default_image, contentImageView, imageSize.width, imageSize.height, imagePath);
                //FileDiskRepository.getInstance().add(fileMessage);//保存到文件管理
            }
        }
        else if (fileMessage instanceof VoiceClipMessage) {
            this.mProgressBar.setVisibility(View.GONE);
            VoiceClipMessage voiceMessage = (VoiceClipMessage) fileMessage;
            File voiceFile = voiceMessage.getFile();
            if (null != voiceFile && voiceFile.exists()) {
                this.mContainerView.setEnabled(true);
            }
        }
        else if (fileMessage instanceof VideoClipMessage) {
            this.mVideoPlay.setVisibility(View.VISIBLE);
            this.mProgressLayout.setVisibility(View.GONE);
            this.mPVProgressBar.setVisibility(View.GONE);
            VideoClipMessage videoMessage = (VideoClipMessage) fileMessage;
            File videoFile = videoMessage.getFile();
            File thumbFile = videoMessage.getThumbFile();
            ImageView contentImageView = mViewHolder.getView(R.id.chat_content_iv);
            ImageUtil.ImageSize imageSize = ImageUtil.getThumbnailDisplaySize(mContext, videoMessage.getWidth(), videoMessage.getHeight());
            if (null != thumbFile && thumbFile.exists()) {
                contentImageView.setImageURI(Uri.fromFile(thumbFile));
                //GlideUtil.loadVideo(thumbFile.getAbsolutePath(), mContext, contentImageView, imageSize.width, imageSize.height, R.drawable.default_image);
            }
            else if (null != videoFile && videoFile.exists()) {
                GlideUtil.loadVideo(Uri.fromFile(videoFile), mContext, contentImageView, imageSize.width, imageSize.height, R.drawable.default_image);
            }
        }
        else {
            this.mFileProgressBar.setVisibility(View.GONE);
            this.mContainerView.setEnabled(true);
            this.mFileStatus.setText(mContext.getString(R.string.message_received));
            this.mFileStatus.setVisibility(View.VISIBLE);
            //FileDiskRepository.getInstance().add(fileMessage);//保存到文件管理
        }

        // 移除监听器
        LogUtil.i("移除监听器" + this.mCubeMessage.toString());
        MessageHandle.getInstance().removeDownloadListener(this.mCubeMessage.getMessageSN(), CubeMessage.class.getSimpleName());
    }
}
