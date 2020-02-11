package cube.ware.ui.appid;

import android.annotation.TargetApi;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.common.mvp.base.BaseActivity;
import com.common.sdk.RouterUtil;
import com.common.utils.utils.ToastUtil;
import com.common.utils.utils.log.LogUtil;
import cube.ware.AppConstants;
import cube.ware.AppManager;
import cube.ware.R;
import cube.ware.utils.runtimepermission.PermissionsManager;
import cube.ware.utils.runtimepermission.PermissionsResultAction;

@Route(path = AppConstants.Router.AppIdActivity)
public class AppIdActivity extends BaseActivity<AppIdPresenter> implements AppIdContract.View {
    private Button   loginBtn;
    private EditText mEtAppKey;
    private EditText mEtAppId;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_appid;
    }

    @Override
    protected AppIdPresenter createPresenter() {
        return new AppIdPresenter(this, this);
    }

    @Override
    protected void initView() {
        loginBtn = findViewById(R.id.login_btn);
        mEtAppId = findViewById(R.id.et_appid);
        mEtAppKey = findViewById(R.id.et_appkey);
    }

    @Override
    protected void initListener() {
        loginBtn.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        mEtAppId.setText(AppManager.getAppId());
        mEtAppKey.setText(AppManager.getAppKey());
        requestPermissions();
    }

    /**
     * 去抖动点击
     *
     * @param v 点击事件view
     */
    @Override
    public void onNormalClick(View v) {
        if (v.getId() == loginBtn.getId()) {
            if (!TextUtils.isEmpty(mEtAppId.getText().toString()) && !TextUtils.isEmpty(mEtAppKey.getText().toString())) {
                mPresenter.checkUsers(mEtAppId.getText().toString().trim(), mEtAppKey.getText().toString().trim());
            }
            else {
                showToast("AppId 和 AppKey 不能为空");
            }
        }
    }

    @Override
    public void checkUsersSuccess() {
        LogUtil.i("checkUsersSuccess");
        RouterUtil.navigation(this, AppConstants.Router.CubIdListActivity);
        finish();
    }

    @Override
    public void showToast(String msg) {
        ToastUtil.showToast(this, msg);
    }

    @TargetApi(23)
    private void requestPermissions() {
        PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(this, new PermissionsResultAction() {
            @Override
            public void onGranted() {
                LogUtil.d("All permissions have been granted");
            }

            @Override
            public void onDenied(String permission) {
                LogUtil.d("permission: " + permission);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }
}
