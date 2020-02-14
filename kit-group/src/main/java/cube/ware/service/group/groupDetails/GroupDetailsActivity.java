package cube.ware.service.group.groupDetails;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.common.base.BaseActivity;
import com.common.router.RouterUtil;
import com.common.utils.ToastUtil;
import com.common.utils.glide.GlideUtil;
import com.common.utils.log.LogUtil;
import cube.service.CubeEngine;
import cube.service.CubeError;
import cube.service.group.Group;
import cube.service.group.GroupDetailsListener;
import cube.service.group.GroupListener;
import cube.ware.api.CubeUI;
import cube.ware.core.CubeConstants;
import cube.ware.core.CubeCore;
import cube.ware.service.group.R;
import cube.ware.service.widget.bottomPopupDialog.BottomPopupDialog;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by dth
 * Des: 群组详情页面
 * Date: 2018/8/28.
 */
@Route(path = CubeConstants.Router.GroupDetailsActivity)
public class GroupDetailsActivity extends BaseActivity<GroupDetailsContract.Presenter> implements GroupDetailsContract.View, GroupListener {

    private ImageView      mTitleBack;
    private ImageView      mTitleMore;
    private ImageView      mFaceIv;
    private TextView       mDisplayNameTv;
    private TextView       mSendMessageTv;
    private TextView       mGroupNumCodeTv;
    private TextView       mGroupChatNameTv;
    private ImageView      mGroupChatArrowIv;
    private RelativeLayout mGroupNameRl;

    @Autowired(name = "group")
    public Group  mGroup;
    @Autowired(name = "groupId")
    public String mGroupId;

    private GroupMembersAdapter mGroupMembersAdapter;
    List<String> mMembersData = new ArrayList<>();

    String EXTRA_CHAT_ID   = "chat_id";
    String EXTRA_CHAT_NAME = "chat_name";
    String EXTRA_CHAT_TYPE = "chat_type";

    String NOT_CHECKED_LIST = "not_checked_list";
    String TYPE             = "type";
    String GROUP_ID         = "group_id";

    @Override
    protected int getContentViewId() {
        return R.layout.activity_group_details;
    }

    @Override
    protected GroupDetailsContract.Presenter createPresenter() {
        return new GroupDetailsPresenter(this, this);
    }

    @Override
    protected void initView() {
        ARouter.getInstance().inject(this);

        mTitleBack = (ImageView) findViewById(R.id.title_back);
        ImageView mTitleIv = (ImageView) findViewById(R.id.title_iv);
        mTitleMore = (ImageView) findViewById(R.id.title_more);
        RelativeLayout mToolbarLayout = (RelativeLayout) findViewById(R.id.toolbar_layout);
        mFaceIv = (ImageView) findViewById(R.id.face_iv);
        mDisplayNameTv = (TextView) findViewById(R.id.display_name_tv);
        CollapsingToolbarLayout mCoolToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.cool_toolbar_layout);
        mSendMessageTv = (TextView) findViewById(R.id.send_message_tv);
        AppBarLayout mAppbarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
        mGroupNumCodeTv = (TextView) findViewById(R.id.group_num_code_tv);
        RelativeLayout mGroupNumCodeRl = (RelativeLayout) findViewById(R.id.group_num_code_rl);
        mGroupChatNameTv = (TextView) findViewById(R.id.group_chat_name_tv);
        mGroupChatArrowIv = (ImageView) findViewById(R.id.group_chat_arrow_iv);
        mGroupNameRl = (RelativeLayout) findViewById(R.id.group_name_rl);
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayout mGroupMemberRl = (LinearLayout) findViewById(R.id.group_member_rl);
        LinearLayout mDetailGroupLayout = (LinearLayout) findViewById(R.id.detail_group_layout);
        mGroupMembersAdapter = new GroupMembersAdapter(R.layout.item_group_members);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 6);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL);
        mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mGroupMembersAdapter);
    }

    @Override
    protected void initData() {
        LogUtil.d("Group: " + mGroup + " --- mGroupId: " + mGroupId);
        CubeEngine.getInstance().getGroupService().queryGroupDetails(mGroupId, new GroupDetailsListener() {
            @Override
            public void onGroupDetails(Group group, List<String> members) {
                mGroup = group;
                if (mGroup == null) {
                    ToastUtil.showToast(CubeCore.getContext(), "查询结果为null");
                    return;
                }

                showData(group);
            }

            @Override
            public void onGroupDetailFailed(CubeError error) {

            }
        });
    }

    @Override
    protected void initListener() {
        CubeEngine.getInstance().getGroupService().addGroupListener(this);
        mTitleBack.setOnClickListener(this);
        mTitleMore.setOnClickListener(this);
        mSendMessageTv.setOnClickListener(this);
        mGroupNameRl.setOnClickListener(this);

        mGroupMembersAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                List<String> cubeIds = mGroupMembersAdapter.getData();
                if (position == cubeIds.size() - 1) {
                    ARouter.getInstance().build(CubeConstants.Router.SelectContactActivity).withObject(NOT_CHECKED_LIST, cubeIds).withString(GROUP_ID, mGroup.getGroupId()).withInt(TYPE, 1).navigation();
                }
                else {
                    //                    Member member = data.get(position);
                    //
                    //                    ARouter.getInstance().build(AppConstants.Router.FriendDetailsActivity)
                    //                            .withObject("user", member)
                    //                            .navigation();
                }
            }
        });

        mGroupMembersAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                List<String> data = mGroupMembersAdapter.getData();
                String member = data.get(position);
                if (position != data.size() - 1 && isMaster(mGroup) && !TextUtils.equals(member, CubeCore.getInstance().getCubeId())) {
                    //                    new AlertDialog.Builder(GroupDetailsActivity.this)
                    //                            .setMessage("确认删除群成员" + member.cubeId + "吗")
                    //                            .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    //                                @Override
                    //                                public void onClick(DialogInterface dialog, int which) {
                    //                                    CubeEngine.getInstance().getGroupService().removeMembers(mGroupId, Collections.singletonList(member.cubeId));
                    //                                    dialog.dismiss();
                    //                                }
                    //                            })
                    //                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    //                                @Override
                    //                                public void onClick(DialogInterface dialog, int which) {
                    //                                    dialog.dismiss();
                    //                                }
                    //                            })
                    //                            .create()
                    //                            .show();
                    showPop(mGroupId, member);
                }
                return true;
            }
        });
    }

    private void showData(Group group) {
        //自己创建的群才能修改群名称
        if (group.getFounder().equals(CubeCore.getInstance().getCubeId())) {
            mGroupChatArrowIv.setVisibility(View.VISIBLE);
        }
        else {
            mGroupChatArrowIv.setVisibility(View.GONE);
        }
        mGroup = group;
        GlideUtil.loadCircleImage(group.getGroupId(), mContext, mFaceIv, R.drawable.default_head_group);
        mDisplayNameTv.setText(group.getDisplayName());
        mGroupChatNameTv.setText(group.getDisplayName());
        mGroupNumCodeTv.setText(group.getGroupId());

        mMembersData.clear();
        mMembersData.addAll(group.getMasters());
        mMembersData.addAll(group.getMembers());
        refreshData(mMembersData);
    }

    @Override
    protected void onDestroy() {
        CubeEngine.getInstance().getGroupService().removeGroupListener(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.title_back) {
            finish();
        }
        else if (id == R.id.title_more) {
            if (isMaster(mGroup)) {
                showMasterMorePop();
            }
            else {
                showMemberMorePop();
            }
        }
        else if (id == R.id.send_message_tv) {
            String chatName = TextUtils.isEmpty(mGroup.getDisplayName()) ? mGroupId : mGroup.getDisplayName();
            CubeUI.getInstance().startGroupChat(this, mGroupId, chatName);
        }
        else if (id == R.id.group_name_rl) {
            if (isMaster(mGroup)) {
                Bundle bundle = new Bundle();
                bundle.putString("displayname", String.valueOf(mGroup.getDisplayName()));
                bundle.putSerializable("group", mGroup);
                bundle.putInt("type", 1);
                RouterUtil.navigation(this, bundle, CubeConstants.Router.ModifyNameActivity);
                //                    showUpdateGroupName();
            }
        }
    }

    private boolean isMaster(Group group) {
        String cubeId = CubeCore.getInstance().getCubeId();
        return TextUtils.equals(group.getFounder(), cubeId) || group.getMasters().contains(cubeId);
    }

    private void refreshData(List<String> members) {
        members.add("我是加号");
        mGroupMembersAdapter.replaceData(members);
    }

    /**
     * 弹出更多对话框
     */
    private void showMemberMorePop() {
        final BottomPopupDialog bottomPopupDialog;
        List<String> bottomDialogContents;//弹出列表的内容
        bottomDialogContents = new ArrayList<>();
        bottomDialogContents.add("退出该群");
        bottomPopupDialog = new BottomPopupDialog(this, bottomDialogContents);
        bottomPopupDialog.showCancelBtn(true);
        bottomPopupDialog.setCancelable(true);
        bottomPopupDialog.setRedPosition(0);
        bottomPopupDialog.setRedPosition(1);
        bottomPopupDialog.show();
        bottomPopupDialog.setOnItemClickListener(new BottomPopupDialog.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                switch (position) {
                    case 0:
                        CubeEngine.getInstance().getGroupService().removeMembers(mGroup.getGroupId(), Collections.singletonList(CubeCore.getInstance().getCubeId()));
                        bottomPopupDialog.dismiss();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void showMasterMorePop() {
        final BottomPopupDialog bottomPopupDialog;
        List<String> bottomDialogContents;//弹出列表的内容
        bottomDialogContents = new ArrayList<>();
        bottomDialogContents.add("解散该群");
        bottomDialogContents.add("退出该群");
        bottomPopupDialog = new BottomPopupDialog(this, bottomDialogContents);
        bottomPopupDialog.showCancelBtn(true);
        bottomPopupDialog.setCancelable(true);
        bottomPopupDialog.setRedPosition(0);
        bottomPopupDialog.setRedPosition(1);
        bottomPopupDialog.show();
        bottomPopupDialog.setOnItemClickListener(new BottomPopupDialog.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                switch (position) {
                    case 0:
                        CubeEngine.getInstance().getGroupService().deleteGroup(mGroup.getGroupId());
                        bottomPopupDialog.dismiss();
                        break;
                    case 1:
                        ToastUtil.showToast(mContext, position + "");
                        CubeEngine.getInstance().getGroupService().removeMembers(mGroup.getGroupId(), Collections.singletonList(CubeCore.getInstance().getCubeId()));
                        bottomPopupDialog.dismiss();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void showPop(final String groupId, final String cubeId) {
        final BottomPopupDialog bottomPopupDialog;
        List<String> bottomDialogContents;//弹出列表的内容
        bottomDialogContents = new ArrayList<>();
        if (TextUtils.equals(mGroup.getFounder(), CubeCore.getInstance().getCubeId())) {
            if (mGroup.getMasters().contains(cubeId)) {
                bottomDialogContents.add("删除管理员");
            }
            else {
                bottomDialogContents.add("添加管理员");
            }
        }
        bottomDialogContents.add("删除群成员");
        bottomPopupDialog = new BottomPopupDialog(this, bottomDialogContents);
        bottomPopupDialog.showCancelBtn(true);
        bottomPopupDialog.setCancelable(true);
        bottomPopupDialog.setRedPosition(0);
        bottomPopupDialog.setRedPosition(1);
        bottomPopupDialog.show();
        bottomPopupDialog.setOnItemClickListener(new BottomPopupDialog.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                switch (position) {
                    case 0:
                        if (mGroup.getMasters().contains(cubeId)) {
                            CubeEngine.getInstance().getGroupService().removeMaster(groupId, cubeId);
                        }
                        else {
                            CubeEngine.getInstance().getGroupService().addMaster(groupId, cubeId);
                        }
                        bottomPopupDialog.dismiss();
                        break;
                    case 1:
                        CubeEngine.getInstance().getGroupService().removeMembers(mGroupId, Collections.singletonList(cubeId));
                        bottomPopupDialog.dismiss();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void showUpdateGroupName() {
        View view = View.inflate(this, R.layout.dialog_update_group_name, null);
        final EditText editText = view.findViewById(R.id.et_group_name);

        AlertDialog alertDialog = new AlertDialog.Builder(this).setView(view).setTitle("修改群名称").setPositiveButton("修改", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = editText.getText().toString();
                if (!TextUtils.isEmpty(name)) {
                    //mGroup.displayName = name;
                    //CubeEngine.getInstance().getGroupService().update(mGroup);
                    dialog.dismiss();
                }
                else {
                    ToastUtil.showToast(CubeCore.getContext(), "群名称不能为空");
                }
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create();
        alertDialog.show();
    }

    @Override
    public void onGroupFailed(CubeError error) {
        ToastUtil.showToast(CubeCore.getContext(), error.desc);
    }

    @Override
    public void onGroupCreated(Group group) {
        showData(group);
    }

    @Override
    public void onGroupDeleted(Group group) {
        finish();
    }

    @Override
    public void onMemberAdded(Group group, List<String> addedMembers) {
        showData(group);
    }

    @Override
    public void onMemberRemoved(Group group, List<String> removedMembers) {
        showData(group);
    }

    @Override
    public void onMasterAdded(Group group, String addedMaster) {
        showData(group);
    }

    @Override
    public void onMasterRemoved(Group group, String removedMaster) {
        showData(group);
    }

    @Override
    public void onGroupNameChanged(Group group) {
        showData(group);
    }
}
