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
import com.common.mvp.eventbus.Event;
import com.common.mvp.rx.RxManager;
import com.common.sdk.RouterUtil;
import com.common.utils.utils.glide.GlideUtil;
import cube.service.CubeEngine;
import cube.service.CubeError;
import cube.service.Session;
import cube.service.account.AccountListener;
import cube.ware.AppConstants;
import cube.ware.AppManager;
import cube.ware.R;
import cube.ware.data.room.model.CubeUser;
import cube.ware.eventbus.CubeEvent;
import cube.ware.utils.SpUtil;
import org.greenrobot.eventbus.EventBus;
import rx.functions.Action1;

/**
 * Created by dth
 * Des:
 * Date: 2018/8/27.
 */

public class MineFragment extends BaseFragment<MineContract.Presenter> implements MineContract.View, AccountListener {

    private TextView       mTvId;
    private TextView       mTvUserName;
    private TextView       mTvNickName;
    private RelativeLayout mRlNameLayout;
    private ImageView      mIvAvator;
    private Button         mTvLoginOut;
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

    @Override
    public <T> void onReceiveEvent(Event<T> event) {
        //修改头像的回调
        if (TextUtils.equals(event.eventName, CubeEvent.EVENT_REFRESH_CUBE_USER)) {
            CubeUser user = (CubeUser) event.data;
            mTvUserName.setText(user.getDisplayName());
            GlideUtil.loadCircleImage(user.getAvatar(), getContext(), mIvAvator, DiskCacheStrategy.NONE, true, R.drawable.default_head_user);
        }
    }

    @Override
    protected void initListener() {
        CubeEngine.getInstance().getAccountService().addAccountListener(this);
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
    public void onLogin(Session cubeSession) {

    }

    @Override
    public void onLogout(Session cubeSession) {

    }

    @Override
    public void onAccountFailed(CubeError cubeError) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        rxManager.clear();
        EventBus.getDefault().unregister(this);
        CubeEngine.getInstance().getAccountService().removeAccountListener(this);
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("退出登录").setMessage("是否退出登录?").setPositiveButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CubeEngine.getInstance().getAccountService().logout();
            }
        }).setNegativeButton("取消", null).show();
    }

    @Override
    public void getUserData(CubeUser user) {
        mTvId.setText(user.getCubeId());
        mTvUserName.setText(user.getDisplayName());
        mTvNickName.setText(user.getDisplayName());
    }
}
