package cube.ware.service.message.preview.image;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import com.common.mvp.base.BaseActivity;
import com.common.mvp.base.BasePresenter;
import com.common.utils.utils.ScreenUtil;
import com.common.utils.utils.log.LogUtil;

import java.util.ArrayList;
import java.util.List;

import cube.ware.service.message.R;
import cube.ware.data.model.dataModel.enmu.CubeMessageType;
import cube.ware.data.repository.CubeMessageRepository;
import cube.ware.data.room.model.CubeMessage;
import cube.ware.widget.SlideViewPager;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by dth
 * Des:
 * Date: 2018/9/10.
 */

public class PreviewImageActivity extends BaseActivity{

    private static final String TAG = PreviewImageActivity.class.getSimpleName();

    private SlideViewPager       mViewPager;
    private TextView             mIndicatorTv;
    private ImageFragmentAdapter mAdapter;

    private String mChatId;
    private long   mMessageSn;
    private int    mChatType;

    /**
     * 启动PreviewMessageImageActivity
     *
     * @param context
     * @param chatId
     * @param messageSn
     */
    public static void start(Context context, String chatId, int chatType, long messageSn) {
        Intent intent = new Intent(context, PreviewImageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle messageData = new Bundle();
        messageData.putString("chat_id", chatId);
        messageData.putLong("chat_sn", messageSn);
        messageData.putInt("chat_type", chatType);
        intent.putExtra("message_data", messageData);
        context.startActivity(intent);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_preview_image;
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ScreenUtil.setNoTitle(this);    // 设置无标题
        super.onCreate(savedInstanceState);
    }

    /**
     * 初始化组件
     */
    @Override
    protected void initView() {
        this.getArguments();
        this.mViewPager = (SlideViewPager) findViewById(R.id.viewPager);
        this.mIndicatorTv = (TextView) findViewById(R.id.indicator_tv);
        this.initData();
    }

    /**
     * 获取参数
     */
    private void getArguments() {
        Bundle bundle = getIntent().getBundleExtra("message_data");
        this.mChatId = bundle.getString("chat_id");
        this.mMessageSn = bundle.getLong("chat_sn");
        this.mChatType = bundle.getInt("chat_type");
        LogUtil.i(TAG, "getArguments chatId" + this.mChatId + " ### chatType：" + this.mChatType + " ### sn：" + this.mMessageSn);
    }

    /**
     * 初始化数据
     */
    protected void initData() {
        CubeMessageRepository.getInstance().queryMessageListByType(mChatId,CubeMessageType.Image)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<CubeMessage>>() {
                    @Override
                    public void call(List<CubeMessage> cubeMessages) {
                        if (null != cubeMessages && !cubeMessages.isEmpty()) {
                            LogUtil.i(TAG, "queryMessageListByType" + cubeMessages.size());
                            final List<Long> messageSnList = new ArrayList<Long>();
                            for (CubeMessage message : cubeMessages) {
                                messageSnList.add(message.getMessageSN());
                            }
                            mAdapter = new ImageFragmentAdapter(getSupportFragmentManager());
                            mViewPager.setAdapter(mAdapter);
                            mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                                @Override
                                public void onPageSelected(int position) {
                                    super.onPageSelected(position);
                                    setIndicator(position + 1, messageSnList.size());
                                }
                            });
                            mAdapter.setData(messageSnList);
                            int currentPosition = getCurrentPosition(mMessageSn, messageSnList);
                            setCurrentItem(currentPosition);
                            setIndicator(currentPosition + 1, messageSnList.size());
                        }
                    }
                });
    }

    /**
     * 获取当前位置
     *
     * @param messageSn
     * @param messageSnList
     *
     * @return
     */
    private int getCurrentPosition(long messageSn, List<Long> messageSnList) {
        if (null != messageSnList && !messageSnList.isEmpty()) {
            return messageSnList.indexOf(messageSn);
        }
        return 0;
    }

    /**
     * 设置当前显示的Item
     */
    private void setCurrentItem(int currentPosition) {
        mViewPager.setCurrentItem(currentPosition);
    }

    /**
     * 设置指示器
     *
     * @param currentPosition
     * @param count
     */
    private void setIndicator(int currentPosition, int count) {
        mIndicatorTv.setText(currentPosition + "/" + count);
    }

    public class ImageFragmentAdapter extends FragmentStatePagerAdapter {
        private List<Long> mMessageSnList;
        private int mCount = 0;

        public ImageFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * 设置数据
         *
         * @param messageSnList
         */
        public void setData(List<Long> messageSnList) {
            this.mMessageSnList = messageSnList;
            this.mCount = mMessageSnList.size();
            this.notifyDataSetChanged();
        }

        @Override
        public Fragment getItem(int position) {
            return PreviewImageFragment.newInstance(mMessageSnList.get(position % mCount));
        }

        @Override
        public int getCount() {
            return mCount;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
