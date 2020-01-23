package cube.ware.ui.mine;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.common.mvp.base.BaseFragment;
import com.common.mvp.rx.RxManager;
import com.common.sdk.RouterUtil;
import com.common.utils.utils.glide.GlideUtil;
import com.common.utils.utils.log.LogUtil;
import cube.service.CubeEngine;
import cube.service.common.model.CubeError;
import cube.service.common.model.CubeSession;
import cube.service.common.model.DeviceInfo;
import cube.service.user.model.User;
import cube.ware.AppConstants;
import cube.ware.AppManager;
import cube.ware.R;
import cube.ware.data.room.model.CubeUser;
import cube.ware.eventbus.CubeEvent;
import cube.ware.eventbus.MessageEvent;
import cube.ware.service.user.UserHandle;
import cube.ware.service.user.UserStateListener;
import cube.ware.utils.SpUtil;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import rx.functions.Action1;

/**
 * Created by dth
 * Des:
 * Date: 2018/8/27.
 */

public class MineFragment extends BaseFragment<MineContract.Presenter> implements MineContract.View, UserStateListener {

    private TextView       mTvId;
    private TextView       mTvUserName;
    private TextView       mTvNickName;
    private RelativeLayout mRlNameLayout;
    private ImageView      mIvAvator;
    private Button         mTvLoginOut;
    private User           mUser = new User();
    RxManager rxManager = new RxManager();

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_mine;
    }

    @Override
    protected MineContract.Presenter createPresenter() {
        return new MinePresenter(getActivity(), this);
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        mTvId = mRootView.findViewById(R.id.tv_id);
        mTvUserName = mRootView.findViewById(R.id.tv_user_name);
        mRlNameLayout = mRootView.findViewById(R.id.rl_name_layout);
        mIvAvator = mRootView.findViewById(R.id.iv_avator);
        mTvLoginOut = mRootView.findViewById(R.id.bt_login_out);
        mTvNickName = mRootView.findViewById(R.id.tv_nickname);
    }

    @Override
    protected void initData() {
        mPresenter.getUserData(SpUtil.getCubeId());
        GlideUtil.loadCircleImage(AppManager.getAvatarUrl() + SpUtil.getCubeId(), getContext(), mIvAvator, DiskCacheStrategy.NONE, true, R.drawable.default_head_user);
    }

    //修改头像的回调
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void changeAvatarEvent(MessageEvent<User> messageEvent) {
        if (messageEvent == null) {
            return;
        }
        if (TextUtils.equals(messageEvent.getMsg(), CubeEvent.EVENT_REFRESH_CUBE_USER)) {
            User user = messageEvent.getData();
            mTvUserName.setText(user.displayName);
            GlideUtil.loadCircleImage(user.avatar, getContext(), mIvAvator, DiskCacheStrategy.NONE, true, R.drawable.default_head_user);
        }
    }

    @Override
    protected void initListener() {
        UserHandle.getInstance().addUserStateListener(this);
        //头像点击
        mIvAvator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RouterUtil.navigation(getContext(), AppConstants.Router.ChangeAvatorActivity);
            }
        });

        //更改名字
        mRlNameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("displayname", String.valueOf(mTvUserName.getText()));
                bundle.putInt("type", 0);
                RouterUtil.navigation(getContext(), bundle, AppConstants.Router.ModifyNameActivity);
            }
        });
        //登出
        mTvLoginOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

      /*  mRootView.findViewById(R.id.btn_settting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build(AppConstants.Router.SettingActivity).navigation();
            }
        });*/

        rxManager.on(CubeEvent.EVENT_REFRESH_CUBE_USER, new Action1<Object>() {
            @Override
            public void call(Object o) {
                if (o instanceof CubeUser) {
                    mTvUserName.setText(((CubeUser) o).getDisplayName());
                    mTvNickName.setText(((CubeUser) o).getDisplayName());
                    GlideUtil.loadCircleImage(((CubeUser) o).getAvatar(), getContext(), mIvAvator, DiskCacheStrategy.NONE, true, R.drawable.default_head_user);
                }
            }
        });
    }

    @Override
    public void onLogin(CubeSession cubeSession, User user) {

    }

    @Override
    public void onLogout(CubeSession cubeSession, User user) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        rxManager.clear();
        EventBus.getDefault().unregister(this);
        UserHandle.getInstance().removeUserStateListener(this);
    }

    @Override
    public void onUserFailed(CubeError cubeError, User user) {
        LogUtil.i("logout fail:" + cubeError.toString());
    }

    @Override
    public void onDeviceOnline(DeviceInfo loginDevice, List<DeviceInfo> onlineDevices, User from) {

    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("退出登录").setMessage("是否退出登录?").setPositiveButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CubeEngine.getInstance().getUserService().logout();
            }
        }).setNegativeButton("取消", null).show();
    }

    @Override
    public void getUserData(User user) {
        this.mUser = user;
        mTvId.setText(user.cubeId);
        mTvUserName.setText(user.displayName);
        mTvNickName.setText(user.displayName);
    }
}
