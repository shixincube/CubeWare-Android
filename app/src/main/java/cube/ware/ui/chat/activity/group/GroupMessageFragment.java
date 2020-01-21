package cube.ware.ui.chat.activity.group;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import com.alibaba.android.arouter.launcher.ARouter;
import com.common.mvp.eventbus.Event;
import com.common.mvp.eventbus.EventBusUtil;
import com.common.sdk.RouterUtil;
import com.common.utils.utils.log.LogUtil;
import cube.service.CubeEngine;
import cube.service.common.CubeCallback;
import cube.service.common.model.CubeError;
import cube.service.conference.model.Conference;
import cube.service.group.GroupType;
import cube.service.whiteboard.model.WhiteBoardInfo;
import cube.service.whiteboard.model.Whiteboard;
import cube.ware.AppConstants;
import cube.ware.R;
import cube.ware.core.CubeConstants;
import cube.ware.data.model.dataModel.enmu.CallStatus;
import cube.ware.data.model.dataModel.enmu.CubeSessionType;
import cube.ware.ui.chat.message.MessageFragment;
import cube.ware.utils.SpUtil;
import java.util.ArrayList;
import java.util.List;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by dth
 * Des: 群组聊天fragment
 * Date: 2018/9/17.
 */

public class GroupMessageFragment extends MessageFragment {

    private TextView mTipView;
    private String   groupId;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBusUtil.register(this);
        mTipView = (TextView) this.mRootView.findViewById(R.id.group_call_tip);
        getArgument();
        List<String> groupIds = new ArrayList<>();
        groupIds.add(groupId);
        queryConferenceByFroupId(groupIds);
    }

    /**
     * 实例化GroupMessageFragment
     *
     * @param sessionType 聊天类型
     * @param arguments   聊天页面定制化信息
     *
     * @return
     */
    public static GroupMessageFragment newInstance(CubeSessionType sessionType, Bundle arguments) {
        GroupMessageFragment fragment = new GroupMessageFragment();
        arguments.putSerializable(AppConstants.EXTRA_CHAT_TYPE, sessionType);
        fragment.setArguments(arguments);
        return fragment;
    }

    private void getArgument() {
        this.groupId = getArguments().getString(AppConstants.EXTRA_CHAT_ID);
    }

    @Override
    public void onResume() {
        super.onResume();
        //回到界面需要刷新横幅
        List<String> groupIds = new ArrayList<>();
        groupIds.add(groupId);
        queryConferenceByFroupId(groupIds);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveEvent(Event event) {
        switch (event.eventName) {
            case CubeConstants.Event.UpdateWhiteBoardTipView:
                queryWhiteBoardByGroupId((List<String>) event.data);
                break;

            case CubeConstants.Event.UpdateConferenceTipView:
                queryConferenceByFroupId((List<String>) event.data);
                break;

            default:
                break;
        }
    }

    private void queryConferenceByFroupId(List<String> groupIds) {
        if (!isAdded()) {
            return;
        }
        List<GroupType> typesList = new ArrayList<>();
        typesList.add(GroupType.SHARE_SCREEN);
        typesList.add(GroupType.VIDEO_CALL);
        typesList.add(GroupType.VOICE_CALL);

        CubeEngine.getInstance().getConferenceService().queryConferencesByGroupIds(groupIds, new CubeCallback<List<Conference>>() {
            @Override
            public void onSucceed(List<Conference> conferenceList) {
                if (conferenceList != null && conferenceList.size() > 0) {
                    //表示有会议
                    handleQureyedConference(conferenceList);
                }
                else {
                    //表示没有会议
                    queryWhiteBoardByGroupId(groupIds);
                }
            }

            @Override
            public void onFailed(CubeError error) {
                LogUtil.d("===查询群组会议失败==" + error.desc);
                mTipView.setVisibility(View.GONE);
                queryWhiteBoardByGroupId(groupIds);
            }
        });

        //        CubeEngine.getInstance().getConferenceService().queryConferenceByGroupId(groupIds, typesList, new CubeCallback<ConferenceData>() {
        //            @Override
        //            public void onSucceed(ConferenceData conferenceData) {
        //                if (conferenceData.conferences.size() !=0){
        //                    //表示有会议
        //                    handleQureyedConference(conferenceData.conferences);
        //                }else{
        //                    //表示没有会议
        //                    queryWhiteBoardByGroupId(groupIds);
        //                }
        //            }
        //
        //            @Override
        //            public void onFailed(CubeError error) {
        //                LogUtil.d("===查询群组会议失败=="+error.desc);
        //                mTipView.setVisibility(View.GONE);
        //                queryWhiteBoardByGroupId(groupIds);
        //            }
        //        });
    }

    private void queryWhiteBoardByGroupId(List<String> groupIds) {
        //查询白板
        CubeEngine.getInstance().getWhiteboardService().queryWhiteboardByGroupId(groupIds, SpUtil.getCubeId(), new CubeCallback<WhiteBoardInfo>() {
            @Override
            public void onSucceed(WhiteBoardInfo whiteBoardData) {
                if (whiteBoardData.whiteboards.size() != 0 && null != whiteBoardData.whiteboards.get(0)) {
                    handleWhiteBoard(whiteBoardData.whiteboards);
                }
                else {
                    //当白板销毁之后再次去查询白板为空，此时需要隐藏tipview
                    mTipView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailed(CubeError error) {
                LogUtil.d("===查询群组白板失败==" + error.desc);
            }
        });
    }

    private void handleQureyedConference(List<Conference> conference) {
        if (conference.size() > 0 && conference.get(0).bindGroupId.equals(groupId)) {
            int res = R.drawable.ic_audio_group_call;
            Drawable d = getResources().getDrawable(res);
            // 这一步必须要做,否则不会显示.
            d.setBounds(0, 0, d.getMinimumWidth(), d.getMinimumHeight());
            mTipView.setCompoundDrawables(d, null, null, null);
            mTipView.setVisibility(View.VISIBLE);
            if (conference.get(0).type.equals(GroupType.SHARE_SCREEN)) {
                mTipView.setText(getString(R.string.share_Screen_now_num, conference.get(0).getMembers().size()));
            }
            else if (conference.get(0).type.equals(GroupType.VIDEO_CALL)) {
                mTipView.setText(getString(R.string.call_video_num, conference.get(0).getMembers().size()));
            }
            else if (conference.get(0).type.equals(GroupType.VOICE_CALL)) {
                mTipView.setText(getString(R.string.call_now_num, conference.get(0).getMembers().size()));
            }
            //没有人，就隐藏
            if (conference.get(0).getMembers().size() == 0) {
                mTipView.setVisibility(View.GONE);
            }
            mTipView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //别人创建的，可以拿到会议，直接加入的会议
                    if (conference.get(0).type.equals(GroupType.SHARE_SCREEN)) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("shaerdesketop", conference.get(0));
                        bundle.putString("inviteId", "");
                        bundle.putSerializable("statues", CallStatus.REMOTE_DESKTOP_JOIN);
                        ARouter.getInstance().build(AppConstants.Router.ShareScreenActivity).withBundle("desketop_data", bundle).navigation();
                    }
                    else {
                        Bundle bundle = new Bundle();
                        bundle.putString(AppConstants.Value.CONFERENCE_GROUP_ID, conference.get(0).bindGroupId);
                        bundle.putString(AppConstants.Value.CONFERENCE_INVITE_Id, conference.get(0).founder); //发起者
                        bundle.putSerializable(AppConstants.Value.CONFERENCE_CALLSTATA, CallStatus.GROUP_CALL_JOIN);
                        bundle.putSerializable(AppConstants.Value.CONFERENCE_CONFERENCE, conference.get(0));
                        RouterUtil.navigation(getActivity(), bundle, AppConstants.Router.ConferenceActivity);
                    }
                }
            });
        }
        else {
            mTipView.setVisibility(View.GONE);
        }
    }

    private void handleWhiteBoard(List<Whiteboard> whiteboards) {
        if (null != whiteboards && whiteboards.size() > 0) {
            int res = R.drawable.ic_audio_group_call;
            Drawable d = getResources().getDrawable(res);
            // 这一步必须要做,否则不会显示.
            d.setBounds(0, 0, d.getMinimumWidth(), d.getMinimumHeight());
            mTipView.setCompoundDrawables(d, null, null, null);
            if (null != whiteboards.get(0).getMembers()) {
                LogUtil.d("===获取到的白板的成员大小===" + whiteboards.get(0).getMembers().size());
                if (whiteboards.get(0).getMembers().size() == 0) {
                    mTipView.setVisibility(View.GONE);
                }
                else {
                    mTipView.setVisibility(View.VISIBLE);
                    mTipView.setText(getString(R.string.whiteborad_now_num, whiteboards.get(0).getMembers().size()));
                    mTipView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //别人创建的，可以拿到会议，直接加入的会议
                            Bundle bundle = new Bundle();
                            bundle.putString(AppConstants.Value.GROUP_ID, whiteboards.get(0).bindGroupId); //发起者
                            bundle.putSerializable(AppConstants.Value.CALLSTATA_WHITE_BOARD, AppConstants.Value.CALLSTATE_JOIN);
                            bundle.putSerializable(AppConstants.Value.WHITEBOARD, whiteboards.get(0));//
                            bundle.putSerializable(AppConstants.Value.CHAT_TYPE, CubeSessionType.Group);//
                            RouterUtil.navigation(getActivity(), bundle, CubeConstants.Router.WhiteBoardActivity);
                        }
                    });
                }
            }
            else {
                mTipView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBusUtil.unregister(this);
    }
}
