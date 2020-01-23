package cube.ware.service.message.chat.activity.file;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.common.mvp.base.BasePresenter;
import com.common.utils.utils.ToastUtil;
import com.common.utils.utils.log.LogUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cube.ware.service.message.R;
import cube.ware.service.message.chat.BaseToolBarActivity;
import cube.ware.service.message.chat.activity.file.entity.LocalMedia;
import cube.ware.service.message.chat.activity.file.listener.OnFileItemSelected;
import cube.ware.service.message.chat.activity.file.listener.OnImgItemSelected;
import cube.ware.service.message.chat.activity.preview.PreviewVideoActivity;
import cube.ware.utils.FileUtil;
import cube.ware.widget.PagerSlidingTabStrip;
import cube.ware.widget.SlideViewPager;
import cube.ware.widget.toolbar.ICubeToolbar;
import cube.ware.widget.toolbar.ToolBarOptions;

/**
 * Created by dth
 * Des:
 * Date: 2018/9/10.
 */

public class FileActivity extends BaseToolBarActivity implements ICubeToolbar.OnTitleItemClickListener, OnFileItemSelected, OnImgItemSelected {

    public static final String REQUEST_CODE   = "request_code";
    public static final String TAKE_FILE_LIST = "FILE_LIST";
    public static final int    TAKE_FILE_CODE = 1002;
    public static       long   FILE_MAX_SIZE  = 100 * 1024 * 1024;  // 文件最大大小
    private int mRequestCode;

    public static long remainSize    = 100 * 1024 * 1024;
    public static long remainFileNum = 9;
    public static long remainPicNum  = 9;

    private List<TabInfo> tabList;
    private SlideViewPager pager = null;
    public  MyPagerAdapter       adapter;
    private PagerSlidingTabStrip tabs;
    private String               mText;
    private long                 mAttachmentSize;

    private List<String> mCount = new ArrayList<>();
    private TextView   mSendNumTv;
    private TextView   mPreviewTv;
    private TextView   mTotalSizeTv;
    private TextView   mSendBtn;
    private View       dividerView;
    private int        mTotalSize;
    private List<File> files;

    private static int TYPE_IMAGE = 1;//图片
    private static int TYPE_VIDEO = 2;//视频
    public static  int flag       = -1;

    public static final int FLAG_SELECT_ATTACHMENT = 1;

    private LocalPVFragment   pvFragment;
    private LocalFileFragment fileFragment;

    private Map<Integer, File> fileMap;
    private List<LocalMedia>   imgList;


    @Override
    protected int getContentViewId() {
        return R.layout.activity_file;
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected void initToolBar() {
        ToolBarOptions toolBarOptions = new ToolBarOptions();
        toolBarOptions.setTitle(getResources().getString(R.string.file_local));
        toolBarOptions.setRightVisible(true);
        toolBarOptions.setRightText(getResources().getString(R.string.cancel));
        toolBarOptions.setRightTextColor(R.color.selector_back_text);
        toolBarOptions.setOnTitleClickListener(this);
        super.setToolBar(toolBarOptions);
    }

    @Override
    public void onTitleItemClick(View v) {
        if (v.getId() == R.id.right) {
            onBackPressed();
            overridePendingTransition(0, R.anim.activity_close);
        }
    }

    @Override
    protected void initView() {
        getArguments();

        pager = (SlideViewPager) findViewById(R.id.view_pager);
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);

        dividerView = findViewById(R.id.divider);
        mSendBtn = (TextView) findViewById(R.id.send_btn);
        mTotalSizeTv = (TextView) findViewById(R.id.total_size);
        mPreviewTv = (TextView) findViewById(R.id.preview);
        mSendNumTv = (TextView) findViewById(R.id.send_num);

        pvFragment = LocalPVFragment.newInstance(mRequestCode, true, mText, mAttachmentSize);
        fileFragment = LocalFileFragment.newInstance(mRequestCode, true, mText, mAttachmentSize);

        this.tabList = new ArrayList<>();
        this.tabList.add(new TabInfo("图片/视频", pvFragment));
        this.tabList.add(new TabInfo("其他", fileFragment));

        this.adapter = new MyPagerAdapter(getSupportFragmentManager(), tabList);
        pager.setAdapter(adapter);
        tabs.setViewPager(pager);
    }

    @Override
    protected void initListener() {

        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    // 当切换到选择图片时，重置选择文件的选择器，此操作是解决选中多个图片之后，再选文件超过9个限制的问题
                    fileFragment.mAdapter.setState();
                    mPreviewTv.setVisibility(View.VISIBLE);
                }
                else {
                    // 当切换到选择文件时，重置选择图片的选择器，此操作是解决选中多个文件之后，再选图片超过9个限制的问题
                    pvFragment.mAdapter.setState();
                    mPreviewTv.setVisibility(View.GONE);
                }
                super.onPageSelected(position);
            }
        });
        mPreviewTv.setOnClickListener(this);
        mSendBtn.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    private void getArguments() {
        Intent intent = getIntent();
        mRequestCode = intent.getIntExtra(FileActivity.REQUEST_CODE, -1);
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        if (vId == R.id.preview) {
            if (imgList != null && imgList.size() > 0) {
                int hasSelectedVideoNum = 0;
                for (LocalMedia selectedMedis : imgList) {
                    if (selectedMedis.getType() == TYPE_VIDEO) {
                        hasSelectedVideoNum++;
                    }
                }
                if (hasSelectedVideoNum > 1) {
                    ToastUtil.showToast(this, "视频只支持单个预览");
                    return;
                }
                if (hasSelectedVideoNum == 1 && imgList.size() == 1) {
                    PreviewVideoActivity.start(this, imgList.get(0).getPath());
                    return;
                }
//                PvPagerActivity.start(this, 0, mRequestCode, imgList, imgList);
            }
            else {
                ToastUtil.showToast(this, "请先选择预览对象");
            }
        }
        else if (vId == R.id.send_btn) {
            ArrayList<String> paths = new ArrayList<>();
            for (File file : files) {
                paths.add(file.getAbsolutePath());
            }
            returnFilePath(paths);
        }
    }

    /**
     * 返回文件路径
     *
     * @param localPath
     */
    public void returnFilePath(ArrayList<String> localPath) {
        LogUtil.i("returnFilePath: " + localPath);
        Intent data = new Intent();
        data.putExtra(TAKE_FILE_LIST, localPath);
        if (getParent() == null) {
            setResult(TAKE_FILE_CODE, data);
        }
        else {
            getParent().setResult(TAKE_FILE_CODE, data);
        }
        finish();
    }

    @Override
    public void onImgSelected(List<LocalMedia> imgList) {
        this.imgList = imgList;
        if (imgList != null) {
            remainPicNum = 9 - imgList.size();
            resetFoot();
        }
    }

    @Override
    public void onFileSelected(Map<Integer, File> mSelectedFileMap) {
        this.fileMap = mSelectedFileMap;
        if (mSelectedFileMap != null) {
            remainFileNum = 9 - mSelectedFileMap.size();
            resetFoot();
        }
    }

    private void resetFoot() {
        files = new ArrayList<>();

        if (imgList != null) {
            for (LocalMedia localMedia : imgList) {
                File file = new File(localMedia.getPath());
                if (file.exists()) {
                    files.add(file);
                }
            }
        }

        if (fileMap != null) {
            files.addAll(fileMap.values());
        }

        if (null != files) {
            mTotalSize = 0;
            for (File file : files) {
                mTotalSize += file.length();
            }
            remainSize = FILE_MAX_SIZE - mTotalSize;
            String size = FileUtil.formatFileSize(this, mTotalSize);

            this.mTotalSizeTv.setText("已选: " + size);
            this.mTotalSizeTv.setVisibility(View.VISIBLE);
            this.mSendNumTv.setText(files.size() + "");
            this.mSendNumTv.setVisibility(View.VISIBLE);
            this.mSendBtn.setEnabled(true);
        }
        else {
            this.mTotalSizeTv.setVisibility(View.GONE);
            this.mSendNumTv.setVisibility(View.GONE);
            this.mSendBtn.setEnabled(false);
        }
    }


    /**
     * tab信息
     */
    public static class TabInfo {
        private String   title;
        private Fragment fragment;

        public TabInfo(String title, Fragment fragment) {
            this.title = title;
            this.fragment = fragment;
        }

        public String getTitle() {
            return title;
        }

        public Fragment getFragment() {
            return fragment;
        }
    }

    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private List<FileActivity.TabInfo> tabList;

        public MyPagerAdapter(FragmentManager fm, List<FileActivity.TabInfo> tabList) {
            super(fm);
            this.tabList = tabList;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabList.get(position).getTitle();
        }

        @Override
        public Fragment getItem(int position) {
            return tabList.get(position).getFragment();
        }

        @Override
        public int getCount() {
            return tabList.size();
        }
    }
}
