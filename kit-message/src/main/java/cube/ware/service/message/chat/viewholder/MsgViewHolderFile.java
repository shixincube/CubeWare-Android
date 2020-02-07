package cube.ware.service.message.chat.viewholder;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseViewHolder;
import cube.service.CubeEngine;
import cube.ware.service.message.R;
import cube.ware.data.model.CubeMessageViewModel;
import cube.ware.data.model.dataModel.enmu.CubeFileMessageStatus;
import cube.ware.data.room.model.CubeMessage;
import cube.ware.service.message.MessageHandle;
import cube.ware.service.message.chat.adapter.ChatMessageAdapter;
import cube.ware.service.message.chat.listener.FileMessageReceiveListener;
import cube.ware.service.message.chat.listener.FileMessageSendListener;
import cube.ware.service.message.manager.FileDownLoadManager;
import cube.ware.utils.FileUtil;
import java.io.File;
import java.util.Map;

/**
 * 聊天消息文件模块
 *
 * @author Wangxx
 * @date 2017/1/10
 */

public class MsgViewHolderFile extends BaseMsgViewHolder {
    private static final String TAG = MsgViewHolderFile.class.getSimpleName();

    private ImageView   mFileIcon;          // 文件图标
    private TextView    mFileName;          // 文件名字
    private TextView    mFileStatus;        // 文件状态
    private TextView    mFileSize;             //文件大小
    private ProgressBar mFileProgressBar;   // 文件下载进度

    public MsgViewHolderFile(ChatMessageAdapter adapter, BaseViewHolder viewHolder, CubeMessageViewModel data, int position, Map<String, CubeMessage> selectedMap) {
        super(adapter, viewHolder, data, position, selectedMap);
    }

    /**
     * 当是发送出去的消息时，内容区域背景的drawable id
     *
     * @return
     */
    @Override
    protected int rightBackground() {
        return R.drawable.selector_chat_send_bg_white;
    }

    @Override
    protected int getContentResId() {
        return R.layout.item_chat_message_file;
    }

    @Override
    protected void initView() {
        this.mFileIcon = findViewById(R.id.item_message_file_icon_image);
        this.mFileName = findViewById(R.id.item_message_file_name_label);
        this.mFileStatus = findViewById(R.id.item_message_file_status_label);
        this.mFileProgressBar = findViewById(R.id.item_message_file_transfer_progress_bar);
        mFileSize = findViewById(R.id.item_message_file_size);
    }

    @Override
    protected void bindView() {
        String path = mData.mMessage.getFilePath();
        initDisplay();
        if (isReceivedMessage()) {
            // 文件长度
            StringBuilder sb = new StringBuilder();
            if (!TextUtils.isEmpty(path)) {
                File file = new File(path);
                Log.d("MsgViewHolderFile", file.getAbsolutePath());
                if (file.exists()) {
                    // 文件存在
                    sb.append(mContext.getString(R.string.message_received));
                }
                else {
                    sb.append(mContext.getString(R.string.not_download));
                }
            }
            else {
                sb.append(mContext.getString(R.string.not_download));
            }
            this.mFileStatus.setText(sb.toString());
        }
        else {
            if (mData.mMessage.getFileMessageStatus() == CubeFileMessageStatus.Uploading.getStatus()) {
                MessageHandle.getInstance().addUploadListener(mData.mMessage.getMessageSN(), CubeMessage.class.getSimpleName(), new FileMessageSendListener(mContext, mData.mMessage, mViewHolder, mInflate));
            }
            else {
                StringBuilder sb = new StringBuilder();
                sb.append(mContext.getString(R.string.send_success));
                this.mFileStatus.setText(sb.toString());
            }
        }
        mFileSize.setText(FileUtil.formatFileSize(mContext, mData.mMessage.getFileSize()));
    }

    private void initDisplay() {
        FileUtil.setFileIcon(mFileIcon, mData.mMessage.getFileName());
        this.mFileName.setText(mData.mMessage.getFileName());
        this.mFileStatus.setVisibility(View.VISIBLE);
        if (FileDownLoadManager.getInstance().isDownloading(mData.mMessage.getMessageSN())) {
            this.mFileStatus.setVisibility(View.GONE);
            this.mFileProgressBar.setVisibility(View.VISIBLE);
            int percent = (int) (Double.parseDouble(String.valueOf(mData.mMessage.getProcessedSize())) / Double.parseDouble(String.valueOf(mData.mMessage.getFileSize())) * 100);
            this.mFileProgressBar.setProgress(percent);
            MessageHandle.getInstance().addDownloadListener(mData.mMessage.getMessageSN(), CubeMessage.class.getSimpleName(), new FileMessageReceiveListener(mContext, mData.mMessage, mViewHolder, mInflate));
        }
        else {
            this.mFileProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onItemClick(View view) {
        if (!TextUtils.isEmpty(mData.mMessage.getFilePath())) {
            File file = new File(mData.mMessage.getFilePath());
            if (file.exists()) {
                // 文件存在，直接打开
                FileUtil.openFile(mContext, file);
            }
            else {
                accept(view);
            }
        }
        else {
            accept(view);
        }
    }

    @Override
    public void onDestroy() {
        if (mData.mMessage != null) {
            MessageHandle.getInstance().removeUploadListener(mData.mMessage.getMessageSN(), CubeMessage.class.getSimpleName());
            MessageHandle.getInstance().removeDownloadListener(mData.mMessage.getMessageSN(), CubeMessage.class.getSimpleName());
        }
    }

    /**
     * 下载文件
     *
     * @param view
     */
    private void accept(View view) {
        if (FileDownLoadManager.getInstance().isDownloading(mData.mMessage.getMessageSN()) || FileDownLoadManager.getInstance().isDownloadFailed(mData.mMessage.getMessageSN())) {
            return;
        }
        MessageHandle.getInstance().addDownloadListener(mData.mMessage.getMessageSN(), CubeMessage.class.getSimpleName(), new FileMessageReceiveListener(mContext, mData.mMessage, mViewHolder, mInflate));
        CubeEngine.getInstance().getMessageService().acceptMessage(mData.mMessage.getMessageSN(), null);
    }
}
