package cube.ware.ui.contact.select;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.common.base.BaseActivity;
import com.common.utils.ToastUtil;
import cube.service.CubeEngine;
import cube.service.CubeError;
import cube.service.group.Group;
import cube.ware.AppConstants;
import cube.ware.R;
import cube.ware.core.CubeConstants;
import cube.ware.core.CubeCore;
import cube.ware.data.room.model.CubeUser;
import cube.ware.service.group.GroupListenerAdapter;
import cube.ware.ui.contact.adapter.SelectContactsAdapter;
import cube.ware.utils.SpUtil;
import cube.ware.widget.indexbar.CubeIndexBar;
import cube.ware.widget.indexbar.SuspensionDecoration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by dth
 * Des:
 * Date: 2018/8/28.
 */
@Route(path = AppConstants.Router.SelectContactActivity)
public class SelectContactActivity extends BaseActivity<SelectContactContract.Presenter> implements SelectContactContract.View, SelectContactsAdapter.OnItemSelectedListener {

    private TextView                        mBack;
    private TextView                        mTitle;
    private TextView                        mComplete;
    private SwipeRefreshLayout              mRefreshLayout;
    private RecyclerView                    mFriendRv;
    private TextView                        mSideBarDialogTv;
    private CubeIndexBar                    mCubeIndexBar;
    private SuspensionDecoration            mDecoration;
    private RelativeLayout                  mEmpty;//无数据
    private String                          mName;
    private SelectContactsAdapter           mAdapter;
    private LinkedHashMap<String, CubeUser> mSelectedList = new LinkedHashMap<>();

    public static final String NOT_CHECKED_LIST = "not_checked_list";
    public static final String TYPE             = "type";
    public static final String GROUP_ID         = "group_id";

    @Autowired(name = NOT_CHECKED_LIST)
    public ArrayList<String> mNotCheckedList;

    @Autowired(name = TYPE)
    public int    mType = 0;
    @Autowired(name = GROUP_ID)
    public String mGroupId;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_select_contact;
    }

    @Override
    protected SelectContactContract.Presenter createPresenter() {
        return new SelectContactPresenter(this, this);
    }

    @Override
    protected void initView() {
        ARouter.getInstance().inject(this);
        mBack = (TextView) findViewById(R.id.title_back);
        mTitle = (TextView) findViewById(R.id.toolbar_title);
        mComplete = (TextView) findViewById(R.id.title_complete);
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        mRefreshLayout.setColorSchemeResources(R.color.primary);
        mFriendRv = (RecyclerView) findViewById(R.id.contacts_rv);
        mSideBarDialogTv = (TextView) findViewById(R.id.sidebar_dialog_tv);
        mCubeIndexBar = (CubeIndexBar) findViewById(R.id.sidebar);
        mEmpty = (RelativeLayout) findViewById(R.id.empty_rl);
        mAdapter = new SelectContactsAdapter(R.layout.item_select_contact);
        if (mNotCheckedList == null) {
            mNotCheckedList = new ArrayList<>();
            mNotCheckedList.add(SpUtil.getCubeId());
        }
        mAdapter.setNotChecked(mNotCheckedList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mFriendRv.setLayoutManager(layoutManager);
        mFriendRv.addItemDecoration(mDecoration = new SuspensionDecoration(this, null));
        mFriendRv.setAdapter(mAdapter);

        // indexBar初始化
        mCubeIndexBar.setPressedShowTextView(mSideBarDialogTv) // 设置HintTextView
                     .setNeedRealIndex(true)   // 设置需要真实的索引
                     .setLayoutManager(layoutManager); // 设置RecyclerView的LayoutManager
    }

    @Override
    protected void initData() {
        mPresenter.getCubeList();
    }

    @Override
    protected void initListener() {
        mBack.setOnClickListener(this);
        mComplete.setOnClickListener(this);
        mAdapter.setOnItemSelectedListener(this);
        CubeEngine.getInstance().getGroupService().addGroupListener(groupListenerAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CubeEngine.getInstance().getGroupService().removeGroupListener(groupListenerAdapter);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.title_complete:
                Set<String> cubeIds = mSelectedList.keySet();
                List<String> strings = new ArrayList<>(cubeIds);
                if (strings.size() == 0) {
                    return;
                }

                if (mType == 0) {//create group
                    CubeEngine.getInstance().getGroupService().createGroup(SpUtil.getUserName() + "创建的群", strings, true);
                }
                else if ((mType == 1)) {//add member
                    CubeEngine.getInstance().getGroupService().addMembers(mGroupId, strings);
                }
                break;
        }
    }

    @Override
    public void onResponseUserList(List<CubeUser> list) {
        //移除自己
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getCubeId().equals(SpUtil.getCubeId())) {
                list.remove(i);
            }
        }
        mAdapter.setNewData(list);
    }

    @Override
    public void onItemSelected(String selectedCube) {
        if (mAdapter.getselectSize() == 0) {
            mComplete.setTextColor(getResources().getColor(R.color.assist_text));
            mComplete.setText("确定");
        }
        else {
            mComplete.setText("确定(" + mAdapter.getselectSize() + ")");
            mComplete.setTextColor(getResources().getColor(R.color.C8));
        }
    }

    @Override
    public void onItemUnselected(String selectedCube) {
        if (mAdapter.getselectSize() == 0) {
            mComplete.setTextColor(getResources().getColor(R.color.assist_text));
            mComplete.setText("确定");
        }
        else {
            mComplete.setText("确定(" + mAdapter.getselectSize() + ")");
            mComplete.setTextColor(getResources().getColor(R.color.C8));
        }
    }

    @Override
    public void onSelectedList(LinkedHashMap<String, CubeUser> list) {
        mSelectedList = list;
    }

    GroupListenerAdapter groupListenerAdapter = new GroupListenerAdapter() {

        @Override
        public void onGroupCreated(Group group) {
            //创建群组成功
            Toast.makeText(CubeCore.getContext(), "群组创建成功", Toast.LENGTH_SHORT).show();
            ARouter.getInstance().build(CubeConstants.Router.GroupDetailsActivity).withObject("group", group).withString("groupId", group.getGroupId()).navigation();
            finish();
        }

        @Override
        public void onMemberAdded(Group group, List<String> addedMembers) {
            Toast.makeText(CubeCore.getContext(), "添加群成员成功", Toast.LENGTH_SHORT).show();
            finish();
        }

        @Override
        public void onGroupFailed(CubeError cubeError) {
            ToastUtil.showToast(CubeCore.getContext(), cubeError.desc);
        }
    };
}
