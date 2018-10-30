package cube.ware.ui.chat.adapter;

import android.text.TextUtils;
import android.view.View;

import com.common.utils.utils.glide.GlideUtil;
import com.common.utils.utils.log.LogUtil;

import java.util.Map;

import cube.ware.R;
import cube.ware.data.model.dataModel.CubeMessageViewModel;
import cube.ware.data.model.dataModel.enmu.CubeMessageType;
import cube.ware.data.model.dataModel.enmu.CubeSessionType;
import cube.ware.data.room.model.CubeMessage;
import cube.ware.ui.chat.activity.preview.PreviewVideoActivity;
import cube.ware.ui.chat.listener.FileMessageReceiveListener;
import cube.ware.widget.recyclerview.BaseRecyclerViewHolder;

/**
 * 聊天消息视频模块
 *
 * @author Wangxx
 * @date 2017/1/10
 */

public class MsgViewHolderVideo extends MsgViewHolderPVBase {
    private static final String TAG = MsgViewHolderVideo.class.getSimpleName();
    private FileMessageReceiveListener mDownLoadVideoListener;

    public MsgViewHolderVideo(ChatMessageAdapter adapter, BaseRecyclerViewHolder viewHolder, CubeMessageViewModel data, int position, Map<String, CubeMessage> selectedMap) {
        super(adapter, viewHolder, data, position, selectedMap);
    }

    @Override
    protected void loadThumbnailImage(String path) {
        LogUtil.d("加载缩略图url-----> " + mThumbUrl);
        GlideUtil.loadImage(mThumbUrl,mContext,mChatContentIv, R.drawable.default_image,false);
    }

    /**
     * 每次重新bind到界面上时需要手动设置进度
     */
    @Override
    protected void refreshStatus() {
        super.refreshStatus();

        if (mDownLoadVideoListener != null && mDownLoadVideoListener.isDownLoading()) {
            mVideoPlay.setVisibility(View.GONE);
            mProgressLayout.setVisibility(View.VISIBLE);
            mPVProgressBar.setVisibility(View.VISIBLE);
            int receivePercent = mDownLoadVideoListener.getReceivePercent();
            mPVProgressBar.setProgress(receivePercent);
        }
        if (!TextUtils.isEmpty(mThumbUrl) && mDownLoadVideoListener == null) {
            mDownLoadVideoListener = new FileMessageReceiveListener(mContext, mData.mMessage, mViewHolder, mInflate);
            mData.mMessage.addFileMessageDownloadListener(mData.mMessage.getMessageSN(), mDownLoadVideoListener);
        }
    }

    @Override
    protected int getContentResId() {
        return R.layout.item_chat_message_video;
    }

    @Override
    protected void onItemClick(View view) {
        PreviewVideoActivity.start(mContext, this.mAdapter.mChatId, CubeSessionType.None.getType(), CubeMessageType.Video, this.mData.mMessage.getMessageSN());
    }

    @Override
    protected int leftBackground() {
        return R.color.transparent;
    }

    /**
     * 当是发送出去的消息时，内容区域背景的drawable id
     *
     * @return
     */
    protected int rightBackground() {
        return R.color.transparent;
    }
}
