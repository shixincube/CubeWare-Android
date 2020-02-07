package cube.ware.service.message.file;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;

import cube.ware.service.message.file.entity.LocalMedia;
import cube.ware.service.message.file.entity.LocalMediaFolder;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 加载本地视频和图片
 *
 * @author Wangxx
 * @date 2017/2/10
 */

public class LocalMediaLoader {
    // load type
    public static final  int      TYPE_IMAGE       = 1;
    public static final  int      TYPE_VIDEO       = 2;
    private final static String[] IMAGE_PROJECTION = {
        MediaStore.Images.Media.DATA, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATE_ADDED, MediaStore.Images.Media._ID
    };

    private final static String[] VIDEO_PROJECTION = {
        MediaStore.Video.Media.DATA, MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.DATE_ADDED, MediaStore.Video.Media._ID, MediaStore.Video.Media.DURATION,
    };

    private int type = TYPE_IMAGE;
    private FragmentActivity activity;

    public LocalMediaLoader(FragmentActivity activity, int type) {
        this.activity = activity;
        this.type = type;
    }

    public void loadAllImage(final LocalMediaLoadListener imageLoadListener) {
        activity.getSupportLoaderManager().initLoader(type, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                CursorLoader cursorLoader = null;
                if (id == TYPE_IMAGE) {
                    cursorLoader = new CursorLoader(activity, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION, MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?" + " or " + MediaStore.Images.Media.MIME_TYPE + "=?", new String[] { "image/jpeg", "image/png", "image/gif" }, IMAGE_PROJECTION[2] + " DESC");
                }
                else if (id == TYPE_VIDEO) {
                    cursorLoader = new CursorLoader(activity, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, VIDEO_PROJECTION, null, null, VIDEO_PROJECTION[2] + " DESC");
                }
                return cursorLoader;
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                try {
                    ArrayList<LocalMediaFolder> imageFolders = new ArrayList<LocalMediaFolder>();
                    LocalMediaFolder allImageFolder = new LocalMediaFolder();
                    List<LocalMedia> allImages = new ArrayList<LocalMedia>();
                    if (data != null) {
                        int count = data.getCount();
                        if (count > 0) {
                            data.moveToFirst();
                            do {

                                String path = data.getString(data.getColumnIndexOrThrow(VIDEO_PROJECTION[0]));
                                String name = data.getString(data.getColumnIndexOrThrow(VIDEO_PROJECTION[1]));
                                // 如原图路径不存在或者路径存在但文件不存在,就结束当前循环
                                if (TextUtils.isEmpty(path) || !new File(path).exists()) {
                                    continue;
                                }
                                long dateTime = data.getLong(data.getColumnIndexOrThrow(VIDEO_PROJECTION[2]));
                                int duration = (type == TYPE_VIDEO ? data.getInt(data.getColumnIndexOrThrow(VIDEO_PROJECTION[4])) : 0);
                                File file = new File(path);
                                LocalMedia image = new LocalMedia(name,path, dateTime, duration, type, file.length());
                                LocalMediaFolder folder = getImageFolder(path, imageFolders);
                                folder.getImages().add(image);
                                folder.setType(type);
                                folder.setImageNum(folder.getImageNum() + 1);
                                allImages.add(image);
                                allImageFolder.setType(type);
                                allImageFolder.setImageNum(allImageFolder.getImageNum() + 1);
                            }
                            while (data.moveToNext());

                            if (allImages.size() > 0) {
                                imageFolders.add(0, allImageFolder);
                                String title = "";
                                switch (type) {
                                    case TYPE_VIDEO:
                                        title = "视频";
                                        break;
                                    case TYPE_IMAGE:
                                        title = "图片";
                                        break;
                                }
                                allImageFolder.setFirstImagePath(allImages.get(0).getPath());
                                allImageFolder.setName(title);
                                allImageFolder.setType(type);
                                allImageFolder.setImages(allImages);
                            }
                            imageLoadListener.loadComplete(imageFolders);
                            data.close();
                        }
                        else {
                            // 如果没有相册
                            imageLoadListener.loadComplete(imageFolders);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
            }
        });
    }

    private LocalMediaFolder getImageFolder(String path, List<LocalMediaFolder> imageFolders) {
        File imageFile = new File(path);
        File folderFile = imageFile.getParentFile();

        for (LocalMediaFolder folder : imageFolders) {
            if (folder.getName().equals(folderFile.getName())) {
                return folder;
            }
        }
        LocalMediaFolder newFolder = new LocalMediaFolder();
        newFolder.setName(folderFile.getName());
        newFolder.setPath(folderFile.getAbsolutePath());
        newFolder.setFirstImagePath(path);
        imageFolders.add(newFolder);
        return newFolder;
    }

    public interface LocalMediaLoadListener {
        void loadComplete(List<LocalMediaFolder> folders);
    }
}
