package cube.ware.service.message.chat.activity.file.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.common.utils.utils.ToastUtil;
import com.common.utils.utils.glide.GlideUtil;
import com.common.utils.utils.log.LogUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cube.ware.R;
import cube.ware.service.message.chat.activity.file.FileActivity;
import cube.ware.service.message.chat.activity.file.LocalMediaLoader;
import cube.ware.service.message.chat.activity.file.entity.LocalMedia;
import cube.ware.service.message.chat.activity.file.listener.OnImgItemSelected;
import cube.ware.utils.FileUtil;
import cube.ware.utils.ImageUtil;

/**
 * Created by dth
 * Des:
 * Date: 2018/9/10.
 */

public class PVFileAdapter extends RecyclerView.Adapter<PVFileAdapter.ViewHolder> {

    private Context                     mContext;
    private OnFileSelectChangedListener mSelectChangedListener;
    private int                         maxSelectNum;
    private List<LocalMedia> mLocalMedias = new ArrayList<LocalMedia>();
    private List<LocalMedia> mSelect      = new ArrayList<LocalMedia>();
    private boolean enablePreview;
    private boolean enablePreviewVideo = false;
    private Boolean isCheck            = true;
    private OnImgItemSelected onImgItemSelectedListener;

    public PVFileAdapter(Context mContext, int maxSelectNum, boolean enablePreview, boolean enablePreviewVideo) {
        this.mContext = mContext;
        this.maxSelectNum = maxSelectNum;
        this.enablePreview = enablePreview;
        this.enablePreviewVideo = enablePreviewVideo;
    }

    public void bindData(List<LocalMedia> localMedias) {
        if (localMedias != null && localMedias.size() > 0) {
            this.mLocalMedias = localMedias;
        }
        notifyDataSetChanged();
    }

    public void bindSelectImages(List<LocalMedia> localMedias) {
        this.mSelect = localMedias;
        notifyDataSetChanged();
        if (mSelectChangedListener != null) {
            mSelectChangedListener.onChange(mSelect);
        }
    }

    public List<LocalMedia> getSelectedImages() {
        return mSelect;
    }

    public List<LocalMedia> getImages() {
        return mLocalMedias;
    }

    @Override
    public PVFileAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pv_file, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PVFileAdapter.ViewHolder holder, final int position) {
        final LocalMedia image = mLocalMedias.get(position);
        image.position = holder.getAdapterPosition();
        String path = image.getPath();
        final int type = image.getType();

        selectImage(holder, isSelected(image), false);

        if (type == LocalMediaLoader.TYPE_VIDEO) {
            Uri pathUri = Uri.fromFile(new File(path));
            GlideUtil.loadVideo(pathUri, mContext, holder.picture, R.drawable.default_image);
            long duration = image.getDuration();
            holder.rl_duration.setVisibility(View.VISIBLE);
            holder.tv_duration.setText("时长：" + timeParse(duration));
        }
        else {
            ImageUtil.displayImage(mContext, R.drawable.default_image, holder.picture, path);
            holder.rl_duration.setVisibility(View.GONE);
        }
        if (enablePreview || enablePreviewVideo) {
            holder.ll_check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //if (isCheck) {
                    changeCheckboxState(holder, image);
                    //RxBus.getInstance().post(CubeEvent.EVENT_FILE_COUNT, image.getPath());
                    //}
                    //else {
                    //    ToastUtil.showToast(mContext, "你最多可以选择9个文件");
                    //}
                }
            });
        }
        holder.contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type == LocalMediaLoader.TYPE_VIDEO && enablePreviewVideo && mSelectChangedListener != null && mSelect.size() == 0) {
                    LogUtil.e("点击了视频");
                    mSelectChangedListener.onPictureClick(image, position);
                }
                else if (type == LocalMediaLoader.TYPE_IMAGE && enablePreview && mSelectChangedListener != null && mSelect.size() == 0) {
                    LogUtil.e("点击了图片");
                    mSelectChangedListener.onPictureClick(image, position);
                }
                else {
                    changeCheckboxState(holder, image);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mLocalMedias.size();
    }

    public void setIsClick(Boolean isCheck) {
        this.isCheck = isCheck;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView      picture;
        TextView       check;
        TextView       tv_duration;
        View           contentView;
        LinearLayout   ll_check;
        RelativeLayout rl_duration;

        public ViewHolder(View itemView) {
            super(itemView);
            contentView = itemView;
            picture = (ImageView) itemView.findViewById(R.id.picture);
            check = (TextView) itemView.findViewById(R.id.check);
            ll_check = (LinearLayout) itemView.findViewById(R.id.ll_check);
            tv_duration = (TextView) itemView.findViewById(R.id.tv_duration);
            rl_duration = (RelativeLayout) itemView.findViewById(R.id.rl_duration);
        }
    }

    public boolean isSelected(LocalMedia image) {
        for (LocalMedia media : mSelect) {
            if (media.getPath().equals(image.getPath())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 重置选择器
     */
    public void setState() {
        mSelect.clear();
        if (mSelectChangedListener != null) {
            mSelectChangedListener.onChange(mSelect);
        }
        notifyDataSetChanged();
    }

    /**
     * 改变图片选中状态
     *
     * @param contentHolder
     * @param image
     */

    @SuppressLint("StringFormatMatches")
    private void changeCheckboxState(ViewHolder contentHolder, LocalMedia image) {
        boolean isChecked = contentHolder.check.isSelected();
        LogUtil.e("是否选中" + isChecked);

        File file = new File(image.getPath());
        if (FileActivity.flag == FileActivity.FLAG_SELECT_ATTACHMENT) {
            if (file.exists() && file.length() > FileActivity.remainSize) {
                ToastUtil.showToast(mContext, "附件总大小不能超过20M");
                return;
            }
        }
        else {
            if (FileActivity.remainPicNum < 1 && !isChecked) {
                ToastUtil.showToast(mContext, "你最多可以选择9张图片");
                return;
            }
            if (file.exists() && FileUtil.isImage(file.getName()) && file.length() > 10 * 1024 * 1024) {
                ToastUtil.showToast(mContext, "单张图片不能超过10M，请重新选择");
                return;
            }

            if (null != file && file.exists() && file.length() > FileActivity.remainSize && !isChecked) {
                ToastUtil.showToast(mContext, "发送文件总大小不能超过100M");
                return;
            }
        }
        if (isChecked) {
            for (LocalMedia media : mSelect) {
                if (media.getPath().equals(image.getPath())) {
                    mSelect.remove(media);
                    break;
                }
            }
        }
        else {
            mSelect.add(image);
            image.setNum(mSelect.size());
        }
        //通知点击项发生了改变
        notifyItemChanged(contentHolder.getAdapterPosition());
        selectImage(contentHolder, !isChecked, true);
        if (mSelectChangedListener != null) {
            mSelectChangedListener.onChange(mSelect);
        }
    }

    public void selectImage(ViewHolder holder, boolean isChecked, boolean isAnim) {
        holder.check.setSelected(isChecked);
        if (isChecked) {
            if (isAnim) {
                Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.modal_in);
                holder.check.startAnimation(animation);
            }
            holder.picture.setAlpha(0.4f);
        }
        else {
            holder.picture.setAlpha(1.0f);
        }
    }

    public interface OnFileSelectChangedListener {

        void onChange(List<LocalMedia> selectImages);

        void onPictureClick(LocalMedia media, int position);
    }

    public void setOnFileSelectChangedListener(OnFileSelectChangedListener mSelectChangedListener) {
        this.mSelectChangedListener = mSelectChangedListener;
    }

    public void setOnImgItemSelected(OnImgItemSelected onImgItemSelectedListener) {
        this.onImgItemSelectedListener = onImgItemSelectedListener;
    }

    /**
     * 毫秒转时分秒
     *
     * @param duration
     *
     * @return
     */
    public String timeParse(long duration) {
        String time = "";
        long minute = duration / 60000;
        long seconds = duration % 60000;
        long second = Math.round((float) seconds / 1000);
        if (minute < 10) {
            time += "0";
        }
        time += minute + ":";
        if (second < 10) {
            time += "0";
        }
        time += second;
        return time;
    }
}
