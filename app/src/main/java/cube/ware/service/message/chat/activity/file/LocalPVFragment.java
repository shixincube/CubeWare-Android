package cube.ware.service.message.chat.activity.file;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;

import com.common.mvp.base.BaseFragment;
import com.common.mvp.base.BasePresenter;
import com.common.utils.utils.ScreenUtil;
import com.common.utils.utils.log.LogUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cube.ware.R;
import cube.ware.service.message.chat.activity.file.adapter.PVFileAdapter;
import cube.ware.service.message.chat.activity.file.entity.LocalMedia;
import cube.ware.service.message.chat.activity.file.entity.LocalMediaFolder;
import cube.ware.service.message.chat.activity.file.listener.OnImgItemSelected;
import cube.ware.service.message.chat.activity.preview.PreviewVideoActivity;
import cube.ware.widget.recyclerview.GridSpacingItemDecoration;

/**
 * Created by dth
 * Des:
 * Date: 2018/9/10.
 */

public class LocalPVFragment extends BaseFragment implements PVFileAdapter.OnFileSelectChangedListener, OnImgItemSelected {
    private RecyclerView mRecyclerView;   // 目录/目录文件

    private List<LocalMedia> mSelectedImages = new ArrayList<>();//选中的图片，将放入预览中显示
    private boolean          mIsMultiSelect  = false; // 是否支持多选
    private int  mRequestCode;
    private static int TYPE_IMAGE = 1;//图片
    private static int TYPE_VIDEO = 2;//视频

    private boolean isSend = true;//是否可以点击

    public PVFileAdapter mAdapter;
    private List<LocalMedia> mLocalMedias = new ArrayList<>();
    private String            mText;
    private long              mAttachmentSize;
    private OnImgItemSelected onImgItemSelected;

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_local_pv;
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    public static LocalPVFragment newInstance(int requestCode, boolean isMultiSelect, String text, long attachmentSize) {
        LocalPVFragment fragment = new LocalPVFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(FileActivity.REQUEST_CODE, requestCode);
        bundle.putBoolean("is_multiSelect", isMultiSelect);
        bundle.putString("text", text);
        bundle.putLong("size", attachmentSize);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initView() {
        mRequestCode = getArguments().getInt(FileActivity.REQUEST_CODE);
        mIsMultiSelect = getArguments().getBoolean("is_multiSelect");
        mText = getArguments().getString("text");
        mAttachmentSize = getArguments().getLong("size", 0);
        LogUtil.i("是否是多选：" + this.mIsMultiSelect);

        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.director_file_rv);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(4, ScreenUtil.dip2px(4), false));
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        // 解决调用 notifyItemChanged 闪烁问题,取消默认动画
        ((SimpleItemAnimator) this.mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        if (mIsMultiSelect) {
            mAdapter = new PVFileAdapter(getContext(), 9, true, true);
        }
        else {
            mAdapter = new PVFileAdapter(getContext(), 1, true, true);
        }
        this.mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void initListener() {
        mAdapter.setOnImgItemSelected(this);
        mAdapter.setOnFileSelectChangedListener(this);
    }

    @Override
    protected void initData() {
        readLocalMedia();
    }

    /*
     * 根据type决定，查询本地图片或视频。
     */
    private void readLocalMedia() {
        new LocalMediaLoader(getActivity(), LocalMediaLoader.TYPE_IMAGE).loadAllImage(new LocalMediaLoader.LocalMediaLoadListener() {
            @Override
            public void loadComplete(List<LocalMediaFolder> folders) {
                if (folders.size() > 0) {
                    // 取相册或视频数据
                    LocalMediaFolder folder = folders.get(0);
                    mLocalMedias.addAll(folder.getImages());
                    new LocalMediaLoader(getActivity(), LocalMediaLoader.TYPE_VIDEO).loadAllImage(new LocalMediaLoader.LocalMediaLoadListener() {

                        @Override
                        public void loadComplete(List<LocalMediaFolder> folders) {
                            if (folders.size() > 0) {
                                // 取相册或视频数据
                                LocalMediaFolder folder = folders.get(0);
                                mLocalMedias.addAll(folder.getImages());
                            }
                            Collections.sort(mLocalMedias, new Comparator<LocalMedia>() {
                                @Override
                                public int compare(LocalMedia lhs, LocalMedia rhs) {
                                    long ltime = lhs.getLastUpdateAt();
                                    long rtime = rhs.getLastUpdateAt();
                                    return ltime == rtime ? 0 : (ltime < rtime ? 1 : -1);
                                }
                            });
                            mAdapter.bindData(mLocalMedias);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onChange(List<LocalMedia> selectImages) {
        this.mSelectedImages = selectImages;
        //this.isSendBtnEnabled(selectImages);
        List<File> files = new ArrayList<>();
        for (LocalMedia selectedImage : this.mSelectedImages) {
            files.add(new File(selectedImage.getPath()));
        }
        if (onImgItemSelected != null) {
            onImgItemSelected.onImgSelected(selectImages);
        }
    }

    @Override
    public void onPictureClick(LocalMedia media, int position) {
        if (media.getType() == TYPE_IMAGE) {//如果是图片
            boolean isExist = false;
            if (!mSelectedImages.contains(media)) {
                mSelectedImages.add(media);
                isExist = true;
            }
//            PvPagerActivity.start(getActivity(), position, mRequestCode, mLocalMedias, mSelectedImages);
            if (isExist) {
                mSelectedImages.remove(media);
            }
        }
        else if (media.getType() == TYPE_VIDEO) {//如果是视频
            PreviewVideoActivity.start(getActivity(), media.getPath());
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onImgSelected(List<LocalMedia> imgMap) {
        onImgItemSelected.onImgSelected(imgMap);
    }

    @Override
    public void onAttach(Activity activity) {
        if (activity instanceof OnImgItemSelected) {
            onImgItemSelected = (OnImgItemSelected) activity;
        }
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        onImgItemSelected = null;
        super.onDetach();
    }
}
