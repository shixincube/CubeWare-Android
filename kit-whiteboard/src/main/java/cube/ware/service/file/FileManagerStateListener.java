package cube.ware.service.file;

import java.util.List;

import cube.service.common.model.CubeError;
import cube.service.file.model.FileInfo;

/**
 * author: kun .
 * date:   On 2018/11/15
 */
public interface FileManagerStateListener {

    public void onFileAdded(FileInfo fileInfo, FileInfo fileInfo1);

    public void onFileDeleted(List<FileInfo> list, FileInfo fileInfo);

    public void onFileRenamed(FileInfo fileInfo, FileInfo fileInfo1);

    public void onFileMoved(List<FileInfo> list, FileInfo fileInfo);

    public void onFileUploading(FileInfo fileInfo, long l, long l1);

    public void onFilePaused(FileInfo fileInfo, long l, long l1);

    public void onFileResumed(FileInfo fileInfo, long l, long l1) ;

    public void onFileUploadCompleted(FileInfo fileInfo);

    public void onFileDownloading(FileInfo fileInfo, long l, long l1);

    public void onFileDownloadCompleted(FileInfo fileInfo);

    public void onFileCanceled(FileInfo fileInfo);

    public void onFileManagerFailed(FileInfo fileInfo, CubeError cubeError);
}
