package cube.ware.service.message.file.adapter;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.common.utils.utils.DateUtil;
import com.common.utils.utils.ToastUtil;
import cube.ware.service.message.R;
import cube.ware.service.message.file.FileActivity;
import cube.ware.service.message.file.listener.OnFileItemSelected;
import cube.ware.utils.FileUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dth
 * Des:
 * Date: 2018/9/10.
 */

public class FileAdapter extends BaseQuickAdapter<File, BaseViewHolder> {
    private Map<Integer, File> mSelectedFileMap = new HashMap<>(); // 存储已选中文件

    private int                mLastSelectedPosition = -1;  // 上次选中的位置
    private OnFileItemSelected onItemSelectedListener;

    public FileAdapter(int layoutResId, List<File> dataList) {
        super(layoutResId, dataList);
    }

    @Override
    protected void convert(BaseViewHolder viewHolder, File file) {
        ImageView fileIconIv = viewHolder.getView(R.id.file_icon_iv);
        TextView fileNameTv = viewHolder.getView(R.id.file_name_tv);
        TextView fileSizeTv = viewHolder.getView(R.id.file_size_tv);
        TextView fileDescriptionTv = viewHolder.getView(R.id.file_description_tv);
        final CheckBox fileCb = viewHolder.getView(R.id.file_cb);

        if (null != file) {
            if (file.isDirectory()) {  // 是目录
                fileIconIv.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_file_folder));
                fileNameTv.setText(file.getName());
                fileDescriptionTv.setText("目录");
                fileCb.setVisibility(View.GONE);
                fileSizeTv.setVisibility(View.GONE);
            }
            else { // 是文件
                String fileName = file.getName();
                String exName = FileUtil.getFileExtensionName(fileName);
                //if (exName.equals(".png") || exName.equals(".jpg") || exName.equals(".jpeg") || exName.equals(".bmp") || exName.equals(".gif")) {
                //    GlideUtil.loadCircleImage(file, mContext, fileIconIv, R.drawable.ic_file_png);
                //}
                //else {
                FileUtil.setFileIcon(fileIconIv, fileName);
                //}
                fileNameTv.setText(fileName);
                fileSizeTv.setText(FileUtil.formatFileSize(mContext, file.length()));
                fileSizeTv.setVisibility(View.VISIBLE);
                fileDescriptionTv.setText(DateUtil.dateToString(file.lastModified(), "yyyy-MM-dd HH:mm"));
                fileCb.setVisibility(View.VISIBLE);
            }

            if (this.mSelectedFileMap.containsKey(viewHolder.getAdapterPosition())) {
                fileCb.setChecked(true);
            }
            else {
                fileCb.setChecked(false);
            }
        }
    }

    public void setOnItemSelectedListener(OnFileItemSelected listener) {
        onItemSelectedListener = listener;
    }

    /**
     * 重置选择器
     */
    public void setState() {
        mSelectedFileMap.clear();
        if (onItemSelectedListener != null) {
            onItemSelectedListener.onFileSelected(mSelectedFileMap);
        }
        notifyDataSetChanged();
    }

    /**
     * 选中或取消选中
     *
     * @param position
     * @param file
     * @param isMultiselect
     * @param maxSelectNum
     */
    public void toggleSelected(int position, File file, boolean isMultiselect, int maxSelectNum, boolean isCheck) {
        if (isMultiselect) {    // 多选
            if (this.mSelectedFileMap.containsKey(position)) {
                this.mSelectedFileMap.remove(position);
            }
            else {
                if (FileActivity.flag == FileActivity.FLAG_SELECT_ATTACHMENT) {
                    //如果是选择附件，只判断附件的总大小，不判断数量
                    if (null != file && file.exists() && file.length() > FileActivity.remainSize) {
                        ToastUtil.showToast(mContext, "附件总大小不能超过20M");
                        return;
                    }
                }
                else {
                    if (FileActivity.remainFileNum < 1) {
                        ToastUtil.showToast(mContext, "你最多可以选择9个文件");
                        return;
                    }
                    if (null != file && file.exists() && file.length() > FileActivity.remainSize) {
                        ToastUtil.showToast(mContext, "发送文件总大小不能超过100M");
                        return;
                    }
                }
                this.mSelectedFileMap.put(position, file);
            }
        }
        else {
            if (this.mLastSelectedPosition == position) {
                this.mSelectedFileMap.remove(position);
                this.mLastSelectedPosition = -1;
            }
            else {
                this.mSelectedFileMap.clear();
                this.mSelectedFileMap.put(position, file);
                this.mLastSelectedPosition = position;
            }
        }

        onItemSelectedListener.onFileSelected(mSelectedFileMap);
        this.notifyDataSetChanged();
    }

    /**
     * 获取已选中的文件
     *
     * @return
     */
    public List<File> getSelectedFile() {
        if (this.mSelectedFileMap.isEmpty()) {
            return null;
        }
        else {
            return new ArrayList<>(this.mSelectedFileMap.values());
        }
    }

    /**
     * 清空已选中的文件
     */
    public void clearSelectedFile() {
        this.mSelectedFileMap.clear();
        onItemSelectedListener.onFileSelected(new HashMap<Integer, File>());
    }
}
