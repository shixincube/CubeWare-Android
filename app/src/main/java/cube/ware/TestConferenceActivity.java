package cube.ware;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

import cube.impl.mesh.MeshAgentService;
import cube.service.CubeEngine;
import cube.service.common.model.CubeError;
import cube.service.conference.ConferenceListener;
import cube.service.conference.model.Conference;
import cube.service.conference.model.ConferenceConfig;
import cube.service.conference.model.ConferenceStream;
import cube.service.group.GroupType;
import cube.service.user.model.User;
import cube.ware.utils.SpUtil;

/**
 * Created by dth
 * Des:
 * Date: 2019/3/14.
 */

public class TestConferenceActivity extends AppCompatActivity implements ConferenceListener {

    private FrameLayout         mLocalContainer;
    private RecyclerView        mRecyclerView;
    private LicodeAdapter       mLicodeAdapter;
    public  boolean             isStarted;
    private Button              mButton;
    private Button              mUpdate;
    private Conference mConference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_licode);

        CubeEngine.getInstance().getConferenceService().addConferenceListener(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mLocalContainer = (FrameLayout) findViewById(R.id.local_container);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mButton = findViewById(R.id.button);
        mUpdate = findViewById(R.id.update);
        mButton.setOnClickListener(v -> {
            if (isStarted) {
                CubeEngine.getInstance().getConferenceService().quit(mConference.conferenceId);
                MeshAgentService.getInstance().close();
                isStarted = false;
                mButton.setText("start");
                mLicodeAdapter.getData().clear();
                mLicodeAdapter.notifyDataSetChanged();
                mLocalContainer.removeAllViews();
            } else {
                showJoinDialog();
            }
        });


        mRecyclerView.setLayoutManager(new GridLayoutManager(this,3));
        mLicodeAdapter = new LicodeAdapter();
        mRecyclerView.setAdapter(mLicodeAdapter);

        mLicodeAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                ConferenceStream conferenceStream = mLicodeAdapter.getData().get(position);
                long streamId = conferenceStream.getStreamId();
                switch (view.getId()) {
                    case R.id.tv_audio:
                        view.setSelected(!view.isSelected());
                        MeshAgentService.getInstance().setAudioEnabled(streamId,!MeshAgentService.getInstance().isAudioEnabled(streamId));
                        break;
                    case R.id.tv_video:
                        view.setSelected(!view.isSelected());
                        MeshAgentService.getInstance().setVideoEnabled(streamId,!MeshAgentService.getInstance().isVideoEnabled(streamId));
                        break;
                    default:
                }
            }
        });
    }

    private void createConference() {

        List<String> master=new ArrayList<>();
        master.add(SpUtil.getCubeId());
        String groupId = "";//g2744768

        ConferenceConfig conferenceConfig = new ConferenceConfig(GroupType.VIDEO_CONFERENCE, groupId);
        conferenceConfig.isMux = false;
        conferenceConfig.force = false;  //是否强制开启
        conferenceConfig.number = "0"; //创建传0即可,会议号
        if(!TextUtils.isEmpty(groupId)){
            conferenceConfig.bindGroupId = groupId;
        }
        conferenceConfig.maxMember= 9;
        conferenceConfig.startTime= 0;
        conferenceConfig.duration= 0;
        conferenceConfig.autoNotify =false;
        conferenceConfig.maxNumber = 9;
        conferenceConfig.members = master; // 群组的id集合，创建者可以只添加自己的id号

        CubeEngine.getInstance().getConferenceService().create(conferenceConfig);
    }


    @Override
    public void onConferenceCreated(Conference conference, User from) {
        mConference = conference;
        CubeEngine.getInstance().getConferenceService().join(conference.conferenceId);
        mUpdate.setText(conference.conferenceId);

    }

    @Override
    public void onConferenceDestroyed(Conference conference, User from) {
//        mConference = conference;
//        isStarted = false;
//        mButton.setText("start");
//        mLicodeAdapter.getData().clear();
//        mLicodeAdapter.notifyDataSetChanged();
//        mLocalContainer.removeAllViews();

    }

    @Override
    public void onConferenceInvited(Conference conference, User from, List<User> invites) {
        mConference = conference;
        CubeEngine.getInstance().getConferenceService().join(conference.conferenceId);
    }

    @Override
    public void onConferenceRejectInvited(Conference conference, User from, User rejectMember) {

    }

    @Override
    public void onConferenceAcceptInvited(Conference conference, User from, User joinedMember) {

    }

    @Override
    public void onConferenceJoined(Conference conference, User joinedMember) {
        mConference = conference;
//        if (joinedMember != null && TextUtils.equals(SpUtil.getCubeId(), joinedMember.cubeId)) {
//            List<String> invites =new ArrayList<>();
//            invites.add("20138837");
//            invites.add("20138838");
//            invites.add("20138839");
//            invites.remove(SpUtil.getCubeId());
//            CubeEngine.getInstance().getConferenceService().inviteMembers(conference.conferenceId,invites);
//        }

    }

    @Override
    public void onVideoEnabled(Conference conference, boolean videoEnabled) {

    }

    @Override
    public void onAudioEnabled(Conference conference, boolean videoEnabled) {

    }

    @Override
    public void onConferenceUpdated(Conference conference) {

    }

    @Override
    public void onConferenceQuited(Conference conference, User quitMember) {
        mConference = conference;

//        if (TextUtils.equals(quitMember.cubeId, SpUtil.getCubeId())) {
//            isStarted = false;
//            mButton.setText("start");
//            mLicodeAdapter.getData().clear();
//            mLicodeAdapter.notifyDataSetChanged();
//            mLocalContainer.removeAllViews();
//        }
    }

    @Override
    public void onConferenceFailed(Conference conference, CubeError error) {

//        if (error.code == -1) {
//            CubeEngine.getInstance().getConferenceService().quit(conference.conferenceId);
//        }

    }

    @Override
    public void onConferenceAddStream(ConferenceStream conferenceStream) {
        if (!isStarted) {
            isStarted = true;
            mButton.setText("close");
        }

        if (mLocalContainer.getChildCount() == 0 && conferenceStream.getLocalView() != null) {
            TextView textView = new TextView(this);
            textView.setText(conferenceStream.getConferenceId());
            mLocalContainer.addView(conferenceStream.getLocalView());
        }
        mLicodeAdapter.addData(conferenceStream);
//        if (TextUtils.equals(conferenceStream.getCubeId(),SpUtil.getCubeId())) {
//            mLocalContainer.removeAllViews();
//            TextView textView = new TextView(this);
//            textView.setText(conferenceStream.getConferenceId());
//            mLocalContainer.addView(conferenceStream.getView());
//            mLocalContainer.addView(textView);
//        } else {
//            mLicodeAdapter.addData(conferenceStream);
//        }

    }

    @Override
    public void onConferenceRemoveStream(ConferenceStream conferenceStream) {
        int position = findPosition(conferenceStream);
        if (position != -1) {
            mLicodeAdapter.remove(position);
        }
    }

    private int findPosition(ConferenceStream conferenceStream) {
        List<ConferenceStream> data = mLicodeAdapter.getData();
        int position = data.indexOf(conferenceStream);
        return position;
    }

    class LicodeAdapter extends BaseQuickAdapter<ConferenceStream, BaseViewHolder> {

        public LicodeAdapter() {
            super(R.layout.item_licode);
        }

        @Override
        protected void convert(BaseViewHolder helper, ConferenceStream item) {
            FrameLayout frameLayout = helper.getView(R.id.container);
            ViewGroup parent = (ViewGroup) item.getRemoteView().getParent();
            if (parent != null) {
                parent.removeView(item.getRemoteView());
            }
            frameLayout.addView(item.getRemoteView());


            helper.setText(R.id.tv_id,item.getStreamId()+"")
                    .addOnClickListener(R.id.tv_audio)
                    .addOnClickListener(R.id.tv_video);
        }
    }

    private void showJoinDialog() {
        View view = View.inflate(this, R.layout.dialog_update_group_name, null);
        EditText editText = view.findViewById(R.id.et_group_name);

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(view)
                .setTitle("加入会议")
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String conferenceId = editText.getText().toString();
                        if (!TextUtils.isEmpty(conferenceId)) {
                            CubeEngine.getInstance().getConferenceService().join(conferenceId);
                        } else {
                            createConference();
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        alertDialog.show();

    }

}
