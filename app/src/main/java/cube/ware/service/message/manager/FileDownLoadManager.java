package cube.ware.service.message.manager;

import android.content.Context;
import android.util.LongSparseArray;
import com.common.utils.receiver.NetworkStateReceiver;
import cube.service.CubeEngine;
import cube.service.common.model.CubeError;
import cube.service.message.MessageOperate;
import cube.service.message.model.FileMessage;
import cube.service.message.model.ImageMessage;
import cube.service.message.model.MessageEntity;
import cube.service.message.model.VideoClipMessage;
import cube.ware.service.message.chat.message.Listener.MessageListenerAdapter;
import java.util.HashMap;

/**
 * @author CloudZhang
 * @date 2017/9/23 16:10
 */

public class FileDownLoadManager implements NetworkStateReceiver.NetworkStateChangedListener {
    private static final String TAG = FileDownLoadManager.class.getSimpleName();

    private static final FileDownLoadManager    sInstance = new FileDownLoadManager();
    private              Context                mContext;
    private              MessageListenerAdapter messageListenerAdapter;

    public enum FileDownloadStatus {
        NO_THUMB_NO_FILE(1), //消息既没有下载原文件也没有缩略图

        THUMB_DOWNLOADING(2), //缩略图下载中

        THUMB_DOWNLOAD(3),    //缩略图下载成功

        FILE_DOWNLOADING(4),  //原文件下载中

        FILE_DOWNLOADED(5);   //原文件下载成功

        private int mStatus;

        FileDownloadStatus(int status) {
            mStatus = status;
        }
    }

    private LongSparseArray<Integer> mImageThumbMap = new LongSparseArray<>();//图片消息缩略图下载进度Map
    private LongSparseArray<Integer> mImageMap      = new LongSparseArray<>();//图片消息原图下载进度Map
    private LongSparseArray<Integer> mVideoThumbMap = new LongSparseArray<>(); //视频缩略图下载进度Map
    private LongSparseArray<Integer> mVideoMap      = new LongSparseArray<>(); //视频下载进度
    private LongSparseArray<Integer> mFileMap       = new LongSparseArray<>(); //文件下载进度

    private HashMap<Long, Integer> mFailedMap = new HashMap<>();// 下载失败的消息

    public static FileDownLoadManager getInstance() {
        return sInstance;
    }

    public void init(Context context) {
        mContext = context;
        initAdapter();
        CubeEngine.getInstance().getMessageService().addMessageListener(messageListenerAdapter);
        NetworkStateReceiver.getInstance().addNetworkStateChangedListener(this);
    }

    private void initAdapter() {
        messageListenerAdapter = new MessageListenerAdapter() {
            @Override
            public void onMessageDownloading(MessageEntity messageEntity, long processed, long total) {
                if (messageEntity == null || !(messageEntity instanceof FileMessage)) {
                    return;
                }
                //将进度记录下来
                saveProcessToMap(messageEntity, processed, total);
            }

            @Override
            public void onMessageDownloadCompleted(MessageEntity messageEntity) {
                if (messageEntity == null || !(messageEntity instanceof FileMessage)) {
                    return;
                }
                removeFromMap(messageEntity);
            }

            @Override
            public void onMessageFailed(MessageEntity messageEntity, CubeError cubeError) {
                if (messageEntity == null || !(messageEntity instanceof FileMessage)) {
                    return;
                }
                removeFromMap(messageEntity);
                mFailedMap.put(messageEntity.getSerialNumber(), 0);
            }

            @Override
            public void onMessageCanceled(MessageEntity messageEntity) {
                if (messageEntity == null || !(messageEntity instanceof FileMessage)) {
                    return;
                }
                removeFromMap(messageEntity);
                mFailedMap.put(messageEntity.getSerialNumber(), 0);
            }
        };
    }

    /**
     * 清空下载失败的列表
     */
    public void clearFailedMap() {
        mFailedMap.clear();
    }

    public void acceptMessageVideo(long messageSN) {
        //CubeEngine.getInstance().getMessageService().acceptMessage(messageSN);
        mVideoMap.put(messageSN, 0);
    }

    /**
     * 下载消息缩略图
     *
     * @param messageSN
     * @param imageThumbnail
     */
    public void acceptMessageThumb(long messageSN, MessageOperate imageThumbnail) {
        //如果正在下载中 或者 已经下载失败了 则不去下载
        //每次网络状态变为true 或者 消息界面关闭 清空失败列表
        if (!FileDownLoadManager.getInstance().isDownloading(messageSN) || !FileDownLoadManager.getInstance().isDownloadFailed(messageSN)) {
            if (CubeEngine.getInstance().getMessageService().acceptMessage(messageSN, imageThumbnail)) {
                if (imageThumbnail == MessageOperate.VideoThumbnail) {
                    mVideoThumbMap.put(messageSN, 0);
                }
                else if (imageThumbnail == MessageOperate.ImageThumbnail) {
                    mImageThumbMap.put(messageSN, 0);
                }
            }
        }
    }

    private void removeFromMap(MessageEntity messageEntity) {
        Long sn = messageEntity.getSerialNumber();
        if (messageEntity instanceof VideoClipMessage) {
            VideoClipMessage videoClipMessage = (VideoClipMessage) messageEntity;
            boolean thumb = videoClipMessage.isThumb();
            if (thumb) {
                mVideoThumbMap.remove(sn);
            }
            else {
                mVideoMap.remove(sn);
            }
        }
        else if (messageEntity instanceof ImageMessage) {
            ImageMessage imageMessage = (ImageMessage) messageEntity;
            boolean thumb = imageMessage.isThumb();
            if (thumb) {
                mImageThumbMap.remove(sn);
            }
            else {
                mImageMap.remove(sn);
            }
        }
        else {
            mFileMap.remove(sn);
        }
    }

    //存储进度到Map
    private void saveProcessToMap(MessageEntity messageEntity, long processed, long total) {
        Long sn = messageEntity.getSerialNumber();
        int percent = (int) (processed / total);
        if (messageEntity instanceof VideoClipMessage) {
            VideoClipMessage videoClipMessage = (VideoClipMessage) messageEntity;
            boolean thumb = videoClipMessage.isThumb();
            if (thumb) {
                mVideoThumbMap.put(sn, percent);
            }
            else {
                mVideoMap.put(sn, percent);
            }
        }
        else if (messageEntity instanceof ImageMessage) {
            ImageMessage imageMessage = (ImageMessage) messageEntity;
            boolean thumb = imageMessage.isThumb();
            if (thumb) {
                mImageThumbMap.put(sn, percent);
            }
            else {
                mImageMap.put(sn, percent);
            }
        }
        else {
            mFileMap.put(sn, percent);
        }
    }

    public boolean isDownloading(long sn) {
        return mImageThumbMap.get(sn) != null || mVideoMap.get(sn) != null || mVideoThumbMap.get(sn) != null || mImageMap.get(sn) != null || mFileMap.get(sn) != null;
    }

    public boolean isDownloadFailed(long sn) {
        return mFailedMap.containsKey(sn);
    }

    @Override
    public void onNetworkStateChanged(boolean isNetAvailable) {
        if (isNetAvailable) {
            //连上网络后清空下载失败列表
            mFailedMap.clear();
        }
    }

    public void release() {
        mImageThumbMap.clear();
        mImageMap.clear();
        mVideoThumbMap.clear();
        mVideoMap.clear();
        mFailedMap.clear();
        mFileMap.clear();
    }
}
