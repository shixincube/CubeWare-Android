package cube.ware.service.file;

import com.common.utils.log.LogUtil;
import cube.service.CubeEngine;
import cube.service.common.model.CubeError;
import cube.service.file.FileManagerListener;
import cube.service.file.model.FileInfo;
import java.util.ArrayList;
import java.util.List;

/**
 * author: kun .
 * date:   On 2018/11/15
 */
public class FileHandle implements FileManagerListener {

    private static FileHandle instance = new FileHandle();

    private List<FileManagerStateListener> mFileManagerStateListeners = new ArrayList<>();

    private FileHandle() {}

    public static FileHandle getInstance() {
        return instance;
    }

    /**
     * 启动监听
     */
    public void start() {
        CubeEngine.getInstance().getFileManagerService().addFileManagerListener(this);
    }

    /**
     * 停止监听
     */
    public void stop() {
        CubeEngine.getInstance().getFileManagerService().removeFileManagerListener(this);
    }

    public void addFileManagerStateListener(FileManagerStateListener fileManagerStateListener) {
        if (fileManagerStateListener != null) {
            LogUtil.i("=============", "addFileManagerStateListener");
            mFileManagerStateListeners.add(fileManagerStateListener);
        }
    }

    public void removeFileManagerStateListener(FileManagerStateListener fileManagerStateListener) {
        if (fileManagerStateListener != null && mFileManagerStateListeners != null) {
            if (mFileManagerStateListeners.contains(fileManagerStateListener)) {
                LogUtil.i("=============", "removeFileManagerStateListener");
                mFileManagerStateListeners.remove(fileManagerStateListener);
            }
        }
    }

    @Override
    public void onFileAdded(FileInfo fileInfo, FileInfo fileInfo1) {
        for (int i = 0; i < mFileManagerStateListeners.size(); i++) {
            mFileManagerStateListeners.get(i).onFileAdded(fileInfo, fileInfo1);
        }
    }

    @Override
    public void onFileDeleted(List<FileInfo> list, FileInfo fileInfo) {
        for (int i = 0; i < mFileManagerStateListeners.size(); i++) {
            mFileManagerStateListeners.get(i).onFileDeleted(list, fileInfo);
        }
    }

    @Override
    public void onFileRenamed(FileInfo fileInfo, FileInfo fileInfo1) {
        for (int i = 0; i < mFileManagerStateListeners.size(); i++) {
            mFileManagerStateListeners.get(i).onFileRenamed(fileInfo, fileInfo1);
        }
    }

    @Override
    public void onFileMoved(List<FileInfo> list, FileInfo fileInfo) {
        for (int i = 0; i < mFileManagerStateListeners.size(); i++) {
            mFileManagerStateListeners.get(i).onFileMoved(list, fileInfo);
        }
    }

    @Override
    public void onFileUploading(FileInfo fileInfo, long l, long l1) {
        for (int i = 0; i < mFileManagerStateListeners.size(); i++) {
            mFileManagerStateListeners.get(i).onFileUploading(fileInfo, l, l1);
        }
    }

    @Override
    public void onFilePaused(FileInfo fileInfo, long l, long l1) {
        for (int i = 0; i < mFileManagerStateListeners.size(); i++) {
            mFileManagerStateListeners.get(i).onFilePaused(fileInfo, l, l1);
        }
    }

    @Override
    public void onFileResumed(FileInfo fileInfo, long l, long l1) {
        for (int i = 0; i < mFileManagerStateListeners.size(); i++) {
            mFileManagerStateListeners.get(i).onFileResumed(fileInfo, l, l1);
        }
    }

    @Override
    public void onFileUploadCompleted(FileInfo fileInfo) {
        for (int i = 0; i < mFileManagerStateListeners.size(); i++) {
            mFileManagerStateListeners.get(i).onFileUploadCompleted(fileInfo);
        }
    }

    @Override
    public void onFileDownloading(FileInfo fileInfo, long l, long l1) {
        for (int i = 0; i < mFileManagerStateListeners.size(); i++) {
            mFileManagerStateListeners.get(i).onFileDownloading(fileInfo, l, l1);
        }
    }

    @Override
    public void onFileDownloadCompleted(FileInfo fileInfo) {
        for (int i = 0; i < mFileManagerStateListeners.size(); i++) {
            mFileManagerStateListeners.get(i).onFileDownloadCompleted(fileInfo);
        }
    }

    @Override
    public void onFileCanceled(FileInfo fileInfo) {
        for (int i = 0; i < mFileManagerStateListeners.size(); i++) {
            mFileManagerStateListeners.get(i).onFileCanceled(fileInfo);
        }
    }

    @Override
    public void onFileManagerFailed(FileInfo fileInfo, CubeError cubeError) {
        for (int i = 0; i < mFileManagerStateListeners.size(); i++) {
            mFileManagerStateListeners.get(i).onFileManagerFailed(fileInfo, cubeError);
        }
    }
}
