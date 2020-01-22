package cube.ware.service.message.chat.activity.file;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.common.mvp.base.BaseFragment;
import com.common.mvp.base.BasePresenter;
import com.common.utils.utils.SDCardUtil;
import com.common.utils.utils.log.LogUtil;
import cube.ware.R;
import cube.ware.service.message.chat.activity.file.adapter.FileAdapter;
import cube.ware.service.message.chat.activity.file.listener.OnFileItemSelected;
import cube.ware.widget.emptyview.EmptyViewUtil;
import cube.ware.widget.recyclerview.DividerItemDecoration;
import java.io.File;
import java.io.FileFilter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by dth
 * Des: 选着发送文件fragment
 * Date: 2018/9/10.
 */

public class LocalFileFragment extends BaseFragment {

    private static final int MAX_SELECT_FILE_NUM = 9;   // 最大选择文件的数量

    private TextView     mDirectorNameTv;   // 目录名称
    private RecyclerView mDirectorFileRv;   // 目录/目录文件
    private View         mBackDirectorLayout;    // 返回上级目录

    private boolean isClick = true;//是否可再选择

    private boolean isSend = true;//是否可发送

    public FileAdapter mAdapter;

    private int     mRequestCode;
    private boolean mIsMultiselect = false; // 是否支持多选

    private       File               mCurrentDirector;    // 当前目录
    public static boolean            isRoot = true;//是否处于根目录下
    private       long               mTotalSize;
    private       String             mText;
    private       long               mAttachmentSize;
    private       OnFileItemSelected onFileItemSelectedListener;

    @Override
    public void onAttach(Activity activity) {
        if (activity instanceof FileActivity) {
            this.onFileItemSelectedListener = (OnFileItemSelected) activity;
        }
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        this.onFileItemSelectedListener = null;
        super.onDetach();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_local_file;
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    public static LocalFileFragment newInstance(int requestCode, boolean isMultiselect, String text, long attachmentSize) {
        LocalFileFragment fragment = new LocalFileFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(FileActivity.REQUEST_CODE, requestCode);
        bundle.putBoolean("is_multiselect", isMultiselect);
        bundle.putString("text", text);
        bundle.putLong("size", attachmentSize);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initView() {
        this.mRequestCode = getArguments().getInt(FileActivity.REQUEST_CODE);
        this.mIsMultiselect = getArguments().getBoolean("is_multiselect");
        this.mText = getArguments().getString("text");
        this.mAttachmentSize = getArguments().getLong("size", 0);
        LogUtil.i("是否是多选：" + this.mIsMultiselect);
        this.mDirectorNameTv = (TextView) mRootView.findViewById(R.id.director_name_tv);
        this.mDirectorFileRv = (RecyclerView) mRootView.findViewById(R.id.director_file_rv);
        this.mBackDirectorLayout = mRootView.findViewById(R.id.back_director_layout);
        this.mDirectorFileRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST, getResources().getDrawable(R.drawable.shape_divider_horizontal));
        this.mDirectorFileRv.addItemDecoration(dividerItemDecoration);
        this.mAdapter = new FileAdapter(R.layout.item_local_file, new ArrayList<File>());
        this.mDirectorFileRv.setAdapter(this.mAdapter);
        EmptyViewUtil.EmptyViewBuilder.getInstance(this.getActivity()).setEmptyText("无文件").setIconSrc(R.drawable.ic_no_files).setShowText(true).setShowIcon(true).bindView(this.mDirectorFileRv);
    }

    @Override
    protected void initListener() {
        mBackDirectorLayout.setOnClickListener(this);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                File file = mAdapter.getItem(position);
                if (null != file && file.isDirectory()) {
                    getFileListByDirector(file.getAbsolutePath());
                }
                else {
                    mAdapter.toggleSelected(position, file, mIsMultiselect, MAX_SELECT_FILE_NUM, isClick);
                }
            }
        });
        mAdapter.setOnItemSelectedListener(new OnFileItemSelected() {
            @Override
            public void onFileSelected(Map<Integer, File> mSelectedFileMap) {
                if (onFileItemSelectedListener != null) {
                    onFileItemSelectedListener.onFileSelected(mSelectedFileMap);
                }
            }
        });
    }

    @Override
    protected void initData() {
        if (SDCardUtil.isSDCardEnable()) {
            String sdCardPath = SDCardUtil.getSDCardPath();
            getFileListByDirector(sdCardPath);    // 获取SDCard目录下的所有文件
            LogUtil.i("文件目录" + sdCardPath);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.back_director_layout) {
            back();
        }
    }

    /**
     * 返回
     */
    private void back() {
        if (null != mCurrentDirector) {
            File parentFile = mCurrentDirector.getParentFile();
            if (null != parentFile && parentFile.isDirectory()) {
                getFileListByDirector(parentFile.getAbsolutePath());
            }
        }
    }

    /**
     * 根据目录获取文件列表
     *
     * @param path 目录
     */
    private void getFileListByDirector(String path) {
        mAdapter.clearSelectedFile();//清除选择的文件
        String rootPathName = "手机";
        String pathName = path.replace("/storage/emulated/0", rootPathName);
        isRoot = pathName.equals("手机");
        this.mDirectorNameTv.setText(pathName);

        if (rootPathName.equals(pathName)) {
            this.mBackDirectorLayout.setVisibility(View.GONE);
        }
        else {
            this.mBackDirectorLayout.setVisibility(View.VISIBLE);
        }

        this.mCurrentDirector = new File(path);

        File[] files = this.mCurrentDirector.listFiles(new HiddenFilter());
        List<File> directorFileList = Arrays.asList(files);
        Collections.sort(directorFileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if (o1 == o2) {
                    return 0;
                }
                if (o1.isDirectory() && o2.isFile()) {
                    // Show directories above files
                    return -1;
                }
                if (o1.isFile() && o2.isDirectory()) {
                    // Show files below directories
                    return 1;
                }
                // Sort the directories alphabetically
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });   // 排序
        this.mAdapter.replaceData(directorFileList);
    }

    /**
     * 过滤隐藏文件
     */
    public class HiddenFilter implements FileFilter, Serializable {

        @Override
        public boolean accept(File f) {
            return !f.isHidden();
        }
    }
}
