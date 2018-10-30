package cube.ware.ui.splash;

import android.text.TextUtils;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import com.common.mvp.base.BaseActivity;
import com.common.mvp.base.BasePresenter;
import com.common.sdk.RouterUtil;
import com.common.utils.utils.GsonUtil;
import com.common.utils.utils.ToastUtil;
import com.common.utils.utils.log.LogUtil;

import java.util.List;

import cube.service.common.model.CubeError;
import cube.service.common.model.CubeSession;
import cube.service.common.model.DeviceInfo;
import cube.service.user.model.User;
import cube.ware.AppConstants;
import cube.ware.CubeUI;
import cube.ware.R;
import cube.ware.service.user.UserHandle;
import cube.ware.service.user.UserStateListener;
import cube.ware.utils.SpUtil;

public class SplashActivity extends BaseActivity implements UserStateListener {

    private FrameLayout mFrameLayout;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_splash;
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected void initView() {
        mFrameLayout = findViewById(R.id.splash_layout);
    }

    @Override
    protected void initData() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.6f, 1.0f);
        alphaAnimation.setDuration(800);
        this.mFrameLayout.startAnimation(alphaAnimation);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                String cubeToken = SpUtil.getCubeToken();
                String  cubeId= SpUtil.getCubeId();
                if (TextUtils.isEmpty(cubeToken)||TextUtils.isEmpty(cubeId)) {
                    goToLogin();
                }
                else {
                    UserHandle.getInstance().addUserStateListener(SplashActivity.this);
                    CubeUI.getInstance().login(SpUtil.getCubeId(), SpUtil.getCubeToken(), SpUtil.getCubeId());
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /**
     * 跳转登录界面
     */
    public void goToLogin() {
        RouterUtil.navigation(this, AppConstants.Router.LoginActivity);
        finish();
    }

    /**
     * 跳转主界面
     */
    public void goToMain() {
        RouterUtil.navigation(this, AppConstants.Router.MainActivity);
        finish();
    }

    @Override
    public void onLogin(CubeSession session, User from) {
        LogUtil.i(from.toString());
        goToMain();
    }

    @Override
    public void onLogout(CubeSession session, User from) {

    }

    @Override
    public void onUserFailed(CubeError error, User from) {
        //重新登录
        SpUtil.clear();
        LogUtil.i("login fail:" + error.toString());
        ToastUtil.showToast(this, "login fail:" + error.toString());
    }

    @Override
    public void onDeviceOnline(DeviceInfo loginDevice, List<DeviceInfo> onlineDevices, User from) {

    }
}
