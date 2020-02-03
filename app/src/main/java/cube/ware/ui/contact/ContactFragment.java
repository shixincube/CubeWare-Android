package cube.ware.ui.contact;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.common.mvp.base.BaseFragment;
import com.common.sdk.RouterUtil;
import com.common.utils.utils.ScreenUtil;

import java.util.ArrayList;
import java.util.List;

import cube.ware.AppConstants;
import cube.ware.R;
import cube.ware.ui.contact.adapter.ContactPagerAdapter;
import cube.ware.ui.contact.friend.FriendListFragment;
import cube.ware.utils.TabLayoutUtil;
import cube.ware.widget.CustomViewPager;

/**
 * Created by dth
 * Des: 联系人界面
 * Date: 2018/8/27.
 */

public class ContactFragment extends BaseFragment<ContactContract.Presenter> implements ContactContract.View, AppBarLayout.OnOffsetChangedListener {

    private RelativeLayout  mToolbarLayout;
    private TextView        mTitleTv;        // 标题
    private AppBarLayout    mAppBarLayout;
    private ImageView       mSearch;               // 搜索按钮
    private ImageView       mAdd;                  // +号按钮
    private PopupWindow     popupWindow;
    private TabLayout       mTabLayout;
    private CustomViewPager mViewPager;
    private int mPosition;//当前viewPager的页面下标

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_contact;
    }

    @Override
    protected ContactContract.Presenter createPresenter() {
        return new ContactPresenter(getActivity(),this);
    }

    @Override
    protected void initView() {
        mToolbarLayout = mRootView.findViewById(R.id.toolbar_rl);
        mTitleTv = mRootView.findViewById(R.id.toolbar_title);
        mAdd = mRootView.findViewById(R.id.toolbar_add);
        mAppBarLayout = mRootView.findViewById(R.id.contact_appbar);
        mTabLayout = mRootView.findViewById(R.id.tablayout);
        mViewPager = mRootView.findViewById(R.id.contact_view_pager);
        addFragment();
    }

    @Override
    protected void initListener() {
        super.initListener();
        mAdd.setOnClickListener(this);
        mAppBarLayout.addOnOffsetChangedListener(this);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mAppBarLayout.addOnOffsetChangedListener(this);
    }

    private void addFragment() {
        int page = mViewPager.getCurrentItem();
        List<Fragment> mFragmentList = new ArrayList<>();
        mFragmentList.add(new FriendListFragment());
        //mFragmentList.add(new GroupListFragment());
        ContactPagerAdapter adapter = new ContactPagerAdapter(mFragmentList, getChildFragmentManager(), getActivity());
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.setCurrentItem(page);
        TabLayoutUtil.reflex(mTabLayout);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (verticalOffset > -100) {
            mTitleTv.setVisibility(View.GONE);
        }
        else if (verticalOffset < -100) {
            mTitleTv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.toolbar_add:
                showMorePopWindow();
                break;
        }
    }

    /**
     * 弹出popWindow
     */
    private void showMorePopWindow() {
        if (popupWindow != null && popupWindow.isShowing()) {
            return;
        }
        View popView = LayoutInflater.from(getActivity()).inflate(R.layout.main_plus_popupwindow, null);
        TextView createGroupTv = (TextView) popView.findViewById(R.id.create_group_tv);
        TextView addFriendTv = (TextView) popView.findViewById(R.id.add_friend_tv);
        TextView scanTv = (TextView) popView.findViewById(R.id.scan_tv);

        popupWindow = new PopupWindow(popView, ScreenUtil.dip2px(134), ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);// 设置弹出窗体可触摸
        popupWindow.setOutsideTouchable(true); // 设置点击弹出框之外的区域后，弹出框消失
        popupWindow.setAnimationStyle(R.style.TitleMorePopAnimationStyle); // 设置动画
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));// 设置背景透明
        ScreenUtil.setBackgroundAlpha(getActivity(), 0.9f);
        popupWindow.getContentView().measure(0, 0);
        int popWidth = popupWindow.getContentView().getMeasuredWidth();
        int windowWidth = ScreenUtil.getDisplayWidth();
        int xOff = windowWidth - popWidth - ScreenUtil.dip2px(12);    // x轴的偏移量
        popupWindow.showAsDropDown(mToolbarLayout, xOff, -ScreenUtil.dip2px(4));  // 设置弹出框显示的位置
        popupWindow.setOnDismissListener(() -> ScreenUtil.setBackgroundAlpha(getActivity(), 1.0f));

        // 添加好友
        addFriendTv.setOnClickListener(view -> {
            popupWindow.dismiss();
            RouterUtil.navigation(getContext(), AppConstants.Router.AddFriendActivity);
            getActivity().overridePendingTransition(R.anim.activity_open, 0);
        });
        // 创建群组
        createGroupTv.setOnClickListener(view -> {
            popupWindow.dismiss();
            RouterUtil.navigation(AppConstants.Router.SelectContactActivity);
            getActivity().overridePendingTransition(R.anim.activity_open, 0);
        });
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAppBarLayout.removeOnOffsetChangedListener(this);
    }
}
