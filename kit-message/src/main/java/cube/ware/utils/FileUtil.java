package cube.ware.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cube.ware.service.message.R;


public class FileUtil {
    private static final String TAG = "FileUtil";

    public static final int WORD  = 0;
    public static final int EXCEL = 1;
    public static final int PPT   = 2;
    public static final int PDF   = 3;
    public static final int TXT   = 4;

    public static File createImageFile(String name) {
        // Created an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        name = TextUtils.isEmpty(name) ? timeStamp : "";
        File file = new File(SpUtil.getImagePath());
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 获取视频文件的缩略图
     *
     * @param filePath
     *
     * @return
     */
    public static Bitmap getVideoThumbnail(String filePath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    /**
     * 递归获取office文件列表
     *
     * @param fileDirectory  文件目录
     * @param officeFileList office文件列表
     *
     * @return
     */
    public static List<List<File>> getOfficeFileList(String fileDirectory, List<List<File>> officeFileList) {
        File file = new File(fileDirectory);
        if (!file.exists() || !file.isDirectory()) {
            return officeFileList;
        }
        File[] files = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return !f.isHidden();   // 过滤隐藏文件
            }
        });
        for (File f : files) {
            if (f.isDirectory()) {
                getOfficeFileList(f.getAbsolutePath(), officeFileList);
            }
            else {
                String fileName = f.getName().toLowerCase();    // 文件名转为小写
                if (fileName.endsWith(".doc") || fileName.endsWith(".docx")) {
                    officeFileList.get(WORD).add(f);
                }
                else if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) {
                    officeFileList.get(EXCEL).add(f);
                }
                else if (fileName.endsWith(".ppt") || fileName.endsWith(".pptx")) {
                    officeFileList.get(PPT).add(f);
                }
                else if (fileName.endsWith(".pdf")) {
                    officeFileList.get(PDF).add(f);
                }
                else if (fileName.endsWith(".txt")) {
                    officeFileList.get(TXT).add(f);
                }
            }
        }
        return officeFileList;
    }

    public static String getMimeType(String fileUrl) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        return fileNameMap.getContentTypeFor(fileUrl);
    }

    /**
     * 将Uri转换为Url
     *
     * @param context
     * @param uri
     *
     * @return
     */
    public String uriToUrl(Context context, Uri uri) {
        if (null == uri) {
            return null;
        }
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null) {
            data = uri.getPath();
        }
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        }
        else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    /**
     * 将Uri转换为File
     *
     * @param context
     * @param uri
     *
     * @return
     */
    public static File uriToFile(Activity context, Uri uri) {
        File file;
        String[] project = { MediaStore.Images.Media.DATA };
        Cursor actualImageCursor = context.getContentResolver().query(uri, project, null, null, null);
        if (actualImageCursor != null) {
            int actual_image_column_index = actualImageCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            actualImageCursor.moveToFirst();
            String img_path = actualImageCursor.getString(actual_image_column_index);
            file = new File(img_path);
        }
        else {
            file = new File(uri.getPath());
        }
        if (actualImageCursor != null) {
            actualImageCursor.close();
        }
        return file;
    }

    /**
     * 格式化文件大小，如：B、KB、MB等
     *
     * @param context
     * @param sizeBytes 文件总大小
     *
     * @return B、KB、MB等
     */
    public static String formatFileSize(Context context, long sizeBytes) {
        // TODO: 2018/5/24   有BUG
        return Formatter.formatFileSize(context, sizeBytes);
    }

    /**
     * 稳定好用的工具
     * <p>
     * 格式化文件大小，如：B、KB、MB等
     *
     * @param size 文件总大小
     *
     * @return B、KB、MB等
     */
    public static String convertStorage(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;

        if (size >= gb) {
            return String.format("%.2f GB", (float) size / gb);
        }
        else if (size >= mb) {
            float f = (float) size / mb;
            return String.format("%.2f MB", f);
        }
        else if (size >= kb) {
            float f = (float) size / kb;
            return String.format("%.2f KB", f);
        }
        else {
            return String.format("%d B", size);
        }
    }

    /**
     * 格式化文件大小,  如：B、KB、MB等
     *
     * @param sizeBytes 文件总大小
     *
     * @return B、KB、MB等
     * <p/>
     *
     * @deprecated Use {@link #formatFileSize}
     */
    @Deprecated
    public static String formatFileSize(long sizeBytes) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (sizeBytes < 1024) {
            fileSizeString = df.format((double) sizeBytes) + "B";
        }
        else if (sizeBytes < 1048576) {
            fileSizeString = df.format((double) sizeBytes / 1024) + "K";
        }
        else if (sizeBytes < 1073741824) {
            fileSizeString = df.format((double) sizeBytes / 1048576) + "M";
        }
        else {
            fileSizeString = df.format((double) sizeBytes / 1073741824) + "G";
        }
        return fileSizeString;
    }

    /**
     * 调用系统应用打开文件
     *
     * @param context
     * @param file
     */
    public static void openFile(Context context, File file) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(getFileUri(context, file), getMIMEType(file));
            ComponentName componentName = intent.resolveActivity(context.getPackageManager());
            if(null == componentName){
                Toast.makeText(context, "找不到打开此文件的应用！", Toast.LENGTH_SHORT).show();
            }
            else{
                context.startActivity(intent);
            }
        } catch (Exception e) {
            Toast.makeText(context, "找不到打开此文件的应用！", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    /**
     * 获取文件Uri，兼容Android 7.0 及以上版本
     *
     * @param context
     * @param file
     *
     * @return
     */
    public static Uri getFileUri(Context context, File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {    // Android 7.0 以上版本
            return FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
        }
        else {
            return Uri.fromFile(file);
        }
    }

    /**
     * 根据文件后缀名获得对应的MIME类型。
     *
     * @param file
     */
    public static String getMIMEType(File file) {
        String type = "*/*";
        String fName = file.getName();
        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex < 0) {
            return type;
        }
        String end = fName.substring(dotIndex, fName.length()).toLowerCase(Locale.CHINA);
        if ("".equals(end)) {
            return type;
        }
        for (String[] aMIME_MapTable : MIME_MapTable) {
            if (end.equals(aMIME_MapTable[0])) {
                type = aMIME_MapTable[1];
            }
        }
        return type;
    }

    /**
     * 根据uri得到路径
     *
     * @param context
     * @param uri
     *
     * @return
     */
    public static String getPathFromUri(Context context, Uri uri) {
        if (uri == null) {
            return null;
        }
        String filePath = null;
        Uri filePathUri = uri;
        String scheme = uri.getScheme();
        if (scheme == null) {
            filePath = uri.getPath();
        }
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) { // file:///开头的uri
            filePath = uri.getPath();
        }
        else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {   // content:///开头的uri
            Cursor cursor = context.getContentResolver().query(uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null);
            if (null != cursor && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                if (columnIndex > -1) {
                    filePath = cursor.getString(columnIndex);
                }
                else {
                    filePath = uri.getPath();
                }
                cursor.close();
            }
            else {
                filePath = uri.getPath();
            }
        }
        return filePath;
    }

    /**
     * 使用文件内存映射实现
     *
     * @throws IOException
     */
    public static void fileCopy(File source, File target) throws IOException {
        FileInputStream fis = new FileInputStream(source);
        RandomAccessFile faf = new RandomAccessFile(target, "rw");
        FileChannel fcin = fis.getChannel();
        FileChannel fcout = faf.getChannel();
        MappedByteBuffer mbbi = fcin.map(FileChannel.MapMode.READ_ONLY, 0, fcin.size());
        MappedByteBuffer mbbo = fcout.map(FileChannel.MapMode.READ_WRITE, 0, fcin.size());
        mbbo.put(mbbi);
        mbbi.clear();
        mbbo.clear();
    }

    /**
     * 使用文件内存映射实现
     *
     * @throws IOException
     */
    public static void fileCopy(String source, String target) throws IOException {
        FileInputStream fis = new FileInputStream(source);
        RandomAccessFile faf = new RandomAccessFile(target, "rw");
        FileChannel fcin = fis.getChannel();
        FileChannel fcout = faf.getChannel();
        MappedByteBuffer mbbi = fcin.map(FileChannel.MapMode.READ_ONLY, 0, fcin.size());
        MappedByteBuffer mbbo = fcout.map(FileChannel.MapMode.READ_WRITE, 0, fcin.size());
        mbbo.put(mbbi);
        mbbi.clear();
        mbbo.clear();
    }

    /**
     * 建立一个MIME类型与文件后缀名的匹配表
     */
    private final static String[][] MIME_MapTable = {
        // {后缀名，MIME类型}
        { ".3gp", "video/3gpp" },   //
        { ".apk", "application/vnd.android.package-archive" },  //
        { ".asf", "video/x-ms-asf" },   //
        { ".avi", "video/x-msvideo" },  //
        { ".bin", "application/octet-stream" }, //
        { ".bmp", "image/bmp" }, //
        { ".c", "text/plain" }, //
        { ".class", "application/octet-stream" },   //
        { ".conf", "text/plain" }, //
        { ".cpp", "text/plain" }, //
        { ".doc", "application/msword" },   //
        { ".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document" }, //
        { ".exe", "application/octet-stream" }, //
        { ".gif", "image/gif" },    //
        { ".gtar", "application/x-gtar" },  //
        { ".gz", "application/x-gzip" },    //
        { ".h", "text/plain" }, //
        { ".htm", "text/html" },    //
        { ".html", "text/html" },   //
        { ".jar", "application/java-archive" }, //
        { ".java", "text/plain" },  //
        { ".jpeg", "image/jpeg" },  //
        { ".jpg", "image/jpeg" },   //
        { ".js", "application/x-javascript" },  //
        { ".log", "text/plain" },   //
        { ".m3u", "audio/x-mpegurl" },  //
        { ".m4a", "audio/mp4a-latm" },  //
        { ".m4b", "audio/mp4a-latm" },  //
        { ".m4p", "audio/mp4a-latm" }, //
        { ".m4u", "video/vnd.mpegurl" }, //
        { ".m4v", "video/x-m4v" }, //
        { ".mov", "video/quicktime" },  //
        { ".mp2", "audio/x-mpeg" }, //
        { ".mp3", "audio/x-mpeg" }, //
        { ".mp4", "video/mp4" }, //
        { ".mpc", "application/vnd.mpohun.certificate" }, //
        { ".mpe", "video/mpeg" },   //
        { ".mpeg", "video/mpeg" },  //
        { ".mpg", "video/mpeg" },   //
        { ".mpg4", "video/mp4" },   //
        { ".mpga", "audio/mpeg" },  //
        { ".msg", "application/vnd.ms-outlook" },   //
        { ".ogg", "audio/ogg" },    //
        { ".pdf", "application/pdf" },  //
        { ".png", "image/png" },    //
        { ".pps", "application/vnd.ms-powerpoint" },    //
        { ".ppt", "application/vnd.ms-powerpoint" },    //
        { ".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation" },   //
        { ".prop", "text/plain" },  //
        { ".rar", "application/x-rar-compressed" },    //
        { ".rc", "text/plain" },    //
        { ".rmvb", "audio/x-pn-realaudio" },    //
        { ".rtf", "application/rtf" },  //
        { ".sh", "text/plain" },    //
        { ".tar", "application/x-tar" },    //
        { ".tgz", "application/x-compressed" }, //
        { ".txt", "text/plain" },   //
        { ".wav", "audio/x-wav" },  //
        { ".wma", "audio/x-ms-wma" },   //
        { ".wmv", "audio/x-ms-wmv" },   //
        { ".wps", "application/vnd.ms-works" }, //
        { ".xls", "application/vnd.ms-excel" }, //
        { ".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" },   //
        { ".xml", "text/plain" },   //
        { ".z", "application/x-compress" }, //
        { ".zip", "application/zip" },  //
        { "", "*/*" }   //
    };

    /**
     * 设置文件图标
     *
     * @param fileImageView
     * @param fileName
     */
    public static void setFileIcon(ImageView fileImageView, String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return;
        }
        String fileExtensionName = getFileExtensionName(fileName);
        switch (fileExtensionName) {
            case ".txt":
                fileImageView.setImageResource(R.drawable.ic_file_txt);
                break;
            case ".doc":
            case ".docx":
                fileImageView.setImageResource(R.drawable.ic_file_word);
                break;
            case ".xls":
            case ".xlsx":
                fileImageView.setImageResource(R.drawable.ic_file_excel);
                break;
            case ".ppt":
            case ".pptx":
                fileImageView.setImageResource(R.drawable.ic_file_ppt);
                break;
            case ".pdf":
                fileImageView.setImageResource(R.drawable.ic_file_pdf);
                break;
            case ".png":
            case ".jpg":
            case ".jpeg":
            case ".bmp":
            case ".gif":
                fileImageView.setImageResource(R.drawable.ic_file_png);
                break;
            case ".mp3":
                fileImageView.setImageResource(R.drawable.ic_file_voice);
                break;
            case ".mp4":
                fileImageView.setImageResource(R.drawable.ic_file_video);
                break;
            case ".zip":
            case ".7z":
            case ".rar":
                fileImageView.setImageResource(R.drawable.ic_file_rar);
                break;
            default:
                fileImageView.setImageResource(R.drawable.ic_unknown_file);
                break;
        }
    }

    /**
     * 设置文件图标
     *
     * @param fileImageView
     * @param fileName
     */
    public static void setMessageFileIcon(ImageView fileImageView, String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return;
        }
        String fileExtensionName = getFileExtensionName(fileName);
        switch (fileExtensionName.toLowerCase()) {
            case ".txt":
                fileImageView.setImageResource(R.drawable.ic_file_txt);
                break;
            case ".doc":
            case ".docx":
                fileImageView.setImageResource(R.drawable.ic_file_word);
                break;
            case ".xls":
            case ".xlsx":
                fileImageView.setImageResource(R.drawable.ic_file_excel);
                break;
            case ".ppt":
            case ".pptx":
                fileImageView.setImageResource(R.drawable.ic_file_ppt);
                break;
            case ".pdf":
                fileImageView.setImageResource(R.drawable.ic_file_pdf);
                break;
            case ".zip":
            case ".rar":
            case ".7z":
                fileImageView.setImageResource(R.drawable.ic_file_rar);
                break;
            case ".mp3":
                fileImageView.setImageResource(R.drawable.ic_file_voice);
                break;
            case ".jpg":
            case ".png":
            case ".gif":
            case ".webp":
                fileImageView.setImageResource(R.drawable.ic_file_png);
                break;
            case ".mp4":
            case ".avi":
            case ".wmv":
            case ".3gp":
                fileImageView.setImageResource(R.drawable.ic_file_video);
                break;
            default:
                fileImageView.setImageResource(R.drawable.ic_file);
                break;
        }
    }

    /**
     * 获取不同文件类型默认显示的资源图标ID
     *
     * @param fileName
     *
     * @return
     */
    public static int getFileIconId(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return R.drawable.ic_unknown_file;
        }
        String fileExtensionName = getFileExtensionName(fileName);
        int IconId;
        switch (fileExtensionName.toLowerCase()) {
            case ".txt":
                IconId = R.drawable.ic_file_txt;
                break;
            case ".doc":
            case ".docx":
                IconId = R.drawable.ic_file_word;
                break;
            case ".xls":
            case ".xlsx":
                IconId = R.drawable.ic_file_excel;
                break;
            case ".ppt":
            case ".pptx":
                IconId = R.drawable.ic_file_ppt;
                break;
            case ".pdf":
                IconId = R.drawable.ic_file_pdf;
                break;
            case ".zip":
            case ".rar":
            case ".7z":
                IconId = R.drawable.ic_file_rar;
                break;
            case ".mp3":
                IconId = R.drawable.ic_file_voice;
                break;
            case ".jpg":
            case ".png":
            case ".gif":
            case ".webp":
                IconId = R.drawable.ic_file_png;
                break;
            case ".mp4":
            case ".avi":
            case ".wmv":
            case ".3gp":
                IconId = R.drawable.ic_file_video;
                break;
            default:
                IconId = R.drawable.ic_unknown_file;
                break;
        }
        return IconId;
    }

    /**
     * 判断是否为文档
     *
     * @param fileName
     *
     * @return
     */
    public static boolean isDocument(String fileName) {
        switch (fileName) {
            case ".txt":
            case ".doc":
            case ".docx":
            case ".xls":
            case ".xlsx":
            case ".ppt":
            case ".pptx":
            case ".pdf":
                return true;
        }
        return false;
    }

    /**
     * 是否是图片和视频
     *
     * @param fileName
     *
     * @return
     */
    public static boolean isMedia(String fileName) {
        switch (fileName) {
            case ".jpg":
            case ".jpeg":
            case ".png":
            case ".bmp":
            case ".gif":
            case ".mp4":
            case ".3gp":
            case ".avi":
            case ".rmvb":
            case ".mkv":
                return true;
        }
        return false;
    }

    /**
     * 判断该文件是图片还是视频
     *
     * @param fileName
     */
    public static int isImageOrVideo(String fileName) {
        int fileType = -1;
        switch (getFileExtensionName(fileName)) {
            case ".jpg":
            case ".jpeg":
            case ".png":
            case ".bmp":
            case ".gif":
                fileType = 0;
                break;
            case ".mp4":
            case ".3gp":
            case ".avi":
            case ".rmvb":
            case ".mkv":
                fileType = 1;
                break;
        }
        return fileType;
    }

    /**
     * 是否是图片
     *
     * @param fileName
     *
     * @return
     */
    public static boolean isImage(String fileName) {
        String fileExtensionName = getFileExtensionName(fileName);
        switch (fileExtensionName) {
            case ".png":
            case ".jpg":
            case ".jpeg":
            case ".bmp":
            case ".gif":
                return true;
            default:
                return false;
        }
    }

    /**
     * 判断是否是动图
      * @param filePath
     * @return
     */
    public static boolean isGif(String filePath){
        File file = new File(filePath);
        String fileExtensionName = getFileExtensionName(file.getName());
        switch (fileExtensionName) {
            case ".gif":
                return true;
            default:
                return false;
        }
    }

    /**
     * 获取文件扩展名
     *
     * @param fileName
     *
     * @return
     */
    public static String getFileExtensionName(String fileName) {
        String fileExtensionName = "";
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex != -1) {
            fileExtensionName = fileName.substring(lastDotIndex);
            fileExtensionName = TextUtils.isEmpty(fileExtensionName) ? fileExtensionName : fileExtensionName.toLowerCase();
        }
        return fileExtensionName;
    }

    /**
     * 获取去掉".扩展名"之后的文件名
     *
     * @param fileName 原文件名
     *
     * @return 去掉".扩展名"之后的文件名
     */
    public static String getFileName(String fileName) {
        String fileName1 = "";
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex != -1) {
            fileName1 = fileName.substring(0, lastDotIndex);
        }
        else {//无后缀名的文件名
            fileName1 = fileName;
        }
        return fileName1;
    }

    public static boolean hasExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        return (dot > -1) && (dot < (filename.length() - 1));
    }

    // 获取文件名
    public static String getFileNameFromPath(String filepath) {
        if ((filepath != null) && (filepath.length() > 0)) {
            int sep = filepath.lastIndexOf('/');
            if ((sep > -1) && (sep < filepath.length() - 1)) {
                return filepath.substring(sep + 1);
            }
        }
        return filepath;
    }

    // 获取不带扩展名的文件名
    public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

    //判断文件是否存在
    public static boolean fileIsExists(String path) {
        try {
            File f = new File(path);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }
        return true;
    }

    public static int getVideoDuration(String path) {
        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(path);
            String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION); // 播放时长单位为毫秒
            int i = Integer.parseInt(duration);
            return i / 1000;
        } catch (Exception e) {
            return 0;
        }
    }
}
