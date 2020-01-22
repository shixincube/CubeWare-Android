package cube.ware.service.message.chat.adapter;

import android.text.TextUtils;
import android.view.View;
import com.chad.library.adapter.base.BaseViewHolder;
import com.common.utils.utils.glide.GlideUtil;
import com.common.utils.utils.log.LogUtil;
import cube.ware.data.model.CubeMessageViewModel;
import cube.ware.data.model.dataModel.enmu.CubeSessionType;
import cube.ware.data.room.model.CubeMessage;
import cube.ware.R;
import cube.ware.service.message.chat.activity.preview.PreviewImageActivity;
import java.io.File;
import java.util.Map;

/**
 * 聊天消息图片模块
 *
 * @author Wangxx
 * @date 2017/1/10
 */

public class MsgViewHolderImg extends MsgViewHolderPVBase {
    private static final String TAG = MsgViewHolderImg.class.getSimpleName();

    public MsgViewHolderImg(ChatMessageAdapter adapter, BaseViewHolder viewHolder, CubeMessageViewModel data, int position, Map<String, CubeMessage> selectedMap) {
        super(adapter, viewHolder, data, position, selectedMap);
    }

    @Override
    protected void loadThumbnailImage(String path) {
        LogUtil.d("加载缩略图url-----> " + mThumbUrl + " " + mPath);
        if (!TextUtils.isEmpty(mFileUrl) && mFileUrl.contains(".gif")) {
            if (!TextUtils.isEmpty(mPath) && new File(mPath).exists()) {
                GlideUtil.loadImage(mPath, mContext, mChatContentIv, R.drawable.default_image, true);
            }
            else {
                GlideUtil.loadImage(mFileUrl, mContext, mChatContentIv, R.drawable.default_image, true);
            }
        }
        else {
            if (TextUtils.isEmpty(mThumbUrl)) {
                mThumbUrl = mFileUrl + "?imageView2/0/w/200/h/100";//缩略图规则，w，h可以自定义
            }
            if (!TextUtils.isEmpty(mPath) && new File(mPath).exists()) {
                GlideUtil.loadImage(mPath, mContext, mChatContentIv, R.drawable.default_image, false);
            }
            else {
                GlideUtil.loadImage(mThumbUrl, mContext, mChatContentIv, R.drawable.default_image, false);
            }
        }
    }

    @Override
    protected int getContentResId() {
        return R.layout.item_chat_message_picture;
    }

    @Override
    protected void refreshStatus() {
        super.refreshStatus();
    }

    @Override
    protected void onItemClick(View view) {
        PreviewImageActivity.start(mContext, this.mAdapter.mChatId, CubeSessionType.Secret.getType(), this.mData.mMessage.getMessageSN());
    }

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
