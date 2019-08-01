package cube.ware.ui.splash;

import android.text.TextUtils;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import com.common.mvp.base.BaseActivity;
import com.common.mvp.base.BasePresenter;
import com.common.sdk.RouterUtil;
import cube.ware.AppConstants;
import cube.ware.R;
import cube.ware.utils.SpUtil;

/**
 * 应用启动界面
 *
 * @author LiuFeng
 * @data 2019/5/29 9:57
 */
public class SplashActivity extends BaseActivity {

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
        alphaAnimation.setDuration(500);
        this.mFrameLayout.startAnimation(alphaAnimation);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                String cubeToken = SpUtil.getCubeToken();
                String cubeId = SpUtil.getCubeId();
                if (TextUtils.isEmpty(cubeToken) || TextUtils.isEmpty(cubeId)) {
                    goToLogin();
                }
                else {
                    goToMain();
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
}
