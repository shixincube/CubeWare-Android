package cube.ware.service.message.chat.adapter;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseViewHolder;
import com.common.utils.utils.log.LogUtil;
import cube.ware.data.model.CubeMessageViewModel;
import cube.ware.data.model.dataModel.enmu.CubeFileMessageStatus;
import cube.ware.data.model.dataModel.enmu.CubeMessageStatus;
import cube.ware.data.model.dataModel.enmu.CubeMessageType;
import cube.ware.data.room.model.CubeMessage;
import cube.ware.service.message.MessageHandle;
import cube.ware.service.message.R;
import cube.ware.service.message.chat.listener.FileMessageSendListener;
import cube.ware.utils.ImageUtil;
import cube.ware.widget.CubeProgressBar;
import java.util.Map;

/**
 * 聊天消息图片和视频模块
 *
 * @author Wangxx
 * @date 2017/1/10
 */

public abstract class MsgViewHolderPVBase extends BaseMsgViewHolder {
    private static final String          TAG = MsgViewHolderPVBase.class.getSimpleName();
    protected            FrameLayout     mChatRoot;        // 根布局
    protected            ImageView       mChatContentIv;   // 消息图片
    protected            TextView        mChatSecret;      // 密聊消息提示
    protected            ImageView       mVideoPlay;       // 视频播放按钮
    protected            CubeProgressBar mPVProgressBar;
    protected            LinearLayout    mProgressLayout;

    protected String              mPath;
    protected String              mThumbPath;
    protected String              mThumbUrl;
    protected String              mFileUrl;
    protected ImageUtil.ImageSize mImageSize;

    public MsgViewHolderPVBase(ChatMessageAdapter adapter, BaseViewHolder viewHolder, CubeMessageViewModel data, int position, Map<String, CubeMessage> selectedMap) {
        super(adapter, viewHolder, data, position, selectedMap);
    }

    @Override
    protected void initView() {
        this.mPath = mData.mMessage.getFilePath();
        this.mThumbPath = mData.mMessage.getThumbPath();
        this.mThumbUrl = mData.mMessage.getThumbUrl();
        this.mFileUrl = mData.mMessage.getFileUrl();
        this.mImageSize = ImageUtil.getThumbnailDisplaySize(mContext, mData.mMessage.getImgWidth(), mData.mMessage.getImgHeight());
        this.mChatRoot = findViewById(R.id.chat_root);
        this.mChatContentIv = findViewById(R.id.chat_content_iv);
        this.mChatSecret = findViewById(R.id.chat_secret_tv);
        this.mProgressLayout = findViewById(R.id.chat_progress_cover);
        this.mPVProgressBar = findViewById(R.id.chat_progress_bar);
        this.mVideoPlay = findViewById(R.id.chat_video_iv);
    }

    @Override
    protected void bindView() {
        LogUtil.i(TAG, "bindView mData=" + mData.mMessage.getMessageSN());
        if (!isReceivedMessage()) {
            if (!TextUtils.isEmpty(mData.mMessage.getFilePath()) && mData.mMessage.getFileMessageStatus() == CubeFileMessageStatus.Uploading.getStatus()) {
                // TODO: 2017/9/22 这一段没有问题 每次new一个新的监听器是个瑕疵
                MessageHandle.getInstance().addUploadListener(mData.mMessage.getMessageSN(), CubeMessage.class.getSimpleName(), new FileMessageSendListener(mContext, mData.mMessage, mViewHolder, mInflate));
            }
        }

        ViewGroup.LayoutParams layoutParams = mChatRoot.getLayoutParams();
        layoutParams.width = mImageSize.width;
        layoutParams.height = mImageSize.height;
        FrameLayout.LayoutParams contentLayoutParams = (FrameLayout.LayoutParams) mChatContentIv.getLayoutParams();
        contentLayoutParams.width = mImageSize.width;
        contentLayoutParams.height = mImageSize.height;

        if (isShowSecretMessage()) {
            if (mData.mMessage.getMessageType() == CubeMessageType.Video) {
                this.mChatContentIv.setImageResource(R.drawable.ic_chat_secret_video_message);
            }
            else {
                this.mChatContentIv.setImageResource(R.drawable.ic_chat_secret_image_message);
            }
            this.mChatSecret.setVisibility(View.VISIBLE);
        }
        else {
            this.showData();
        }

        this.refreshStatus();
    }

    /**
     * 展示数据
     */
    protected void showData() {
        this.mChatSecret.setVisibility(View.GONE);
        loadThumbnailImage(mThumbPath);
        //        if (!TextUtils.isEmpty(mThumbPath)) {
        //            loadThumbnailImage(mThumbPath);
        //        }
        //        else if (!TextUtils.isEmpty(mPath)) {
        //            loadThumbnailImage(mPath);
        //        }
        //        else {
        //            if (mData.mMessage.isAnonymous() && mData.mMessage.isReceivedMessage()) {
        //                return;
        //            }
        //            loadThumbnailImage(null);
        //        }
    }

    protected abstract void loadThumbnailImage(String path);

    protected void refreshStatus() {
        if (!isReceivedMessage()) {
            // 检测文件是否为失败状态
            if (TextUtils.isEmpty(this.mData.mMessage.getFilePath()) && TextUtils.isEmpty(this.mData.mMessage.getThumbPath())) {
                if (this.mData.mMessage.getMessageStatus() == CubeMessageStatus.Failed.getStatus()) {
                    this.mRepeatButton.setVisibility(View.VISIBLE);
                }
                else {
                    this.mRepeatButton.setVisibility(View.GONE);
                }
            }
        }

        // 检查文件是否为上传状态
        if (this.mData.mMessage.getFileMessageStatus() == CubeFileMessageStatus.Uploading.getStatus()) {
            this.mProgressLayout.setVisibility(View.VISIBLE);
            this.mVideoPlay.setVisibility(View.GONE);
            //            int percent = (int) ((Double.longBitsToDouble(mData.mMessage.getProcessedSize()) / Double.longBitsToDouble(mData.mMessage.getFileSize())) * 100);
            //            this.mPVProgressBar.setProgress(percent);
            //            FileSendManager.getInstance().resendFileMessageIfNeeded(mData.mMessage);
        }
        else {
            if (this.mData.mMessage.getMessageType() == CubeMessageType.Video) {
                mVideoPlay.setVisibility(View.VISIBLE);
            }
            //            if (this.mData.mMessage.getMessageType().equals(CubeMessageType.Video.getType()) && this.mData.mMessage.getFileMessageStatus() == CubeFileMessageStatus.Succeed.getStatus()) {
            //                this.mVideoPlay.setVisibility(View.VISIBLE);
            //            }
            //            else {
            //                this.mVideoPlay.setVisibility(View.GONE);
            //            }
            //            this.mProgressLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        if (mData.mMessage != null) {
            MessageHandle.getInstance().removeUploadListener(mData.mMessage.getMessageSN(), CubeMessage.class.getSimpleName());
            MessageHandle.getInstance().removeDownloadListener(mData.mMessage.getMessageSN(), CubeMessage.class.getSimpleName());
        }
    }
}
