package cube.ware.ui.login;

import android.annotation.TargetApi;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

@Route(path = AppConstants.Router.LoginActivity)
public class LoginActivity extends BaseActivity<LoginPresenter> implements LoginContract.View {
    private Button   loginBtn;
    private EditText mEtAppKey;
    private EditText mEtAppId;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_login;
    }

    @Override
    protected LoginPresenter createPresenter() {
        return new LoginPresenter(this, this);
    }

    @Override
    protected void initView() {
        //暂时不需要去获取这些权限
        requestPermissions();
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
                showToast("登录中。。。");
                mPresenter.login(mEtAppId.getText().toString().trim(),mEtAppKey.getText().toString().trim());
            }
            else {
                showToast("AppId 和 AppKey 不能为空");
            }
        }
    }

    @Override
    public void loginSuccess() {
        LogUtil.i("loginSuccess");
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
                //				Toast.makeText(MainActivity.this, "All permissions have been granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDenied(String permission) {
                LogUtil.d("permission: "+permission);
//                new AlertDialog.Builder(LoginActivity.this)
//                        .setMessage("申请权限异常，将影响App正常运作，点击确定进入权限管理")
//                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                try {
//                                    Intent intent = new Intent();
//                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                                    intent.setData(Uri.fromParts("package", getPackageName(), null));
//                                    startActivity(intent);
//                                } catch (ActivityNotFoundException ignore) {
//                                    dialog.dismiss();
//                                }
//                            }
//                        })
//                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        })
//                        .create()
//                        .show();

//                         Toast.makeText(LoginActivity.this, permission + " has been denied", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }
}
