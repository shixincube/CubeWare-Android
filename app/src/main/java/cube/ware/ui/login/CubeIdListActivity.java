package cube.ware.ui.login;

import android.app.ProgressDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.common.mvp.base.BaseActivity;
import com.common.sdk.RouterUtil;
import com.common.utils.utils.ClickUtil;
import com.common.utils.utils.GsonUtil;
import com.common.utils.utils.ToastUtil;
import com.common.utils.utils.log.LogUtil;

import java.util.ArrayList;
import java.util.List;

import cube.service.common.model.CubeError;
import cube.service.common.model.CubeSession;
import cube.service.common.model.DeviceInfo;
import cube.service.user.model.User;
import cube.ware.AppConstants;
import cube.ware.CubeUI;
import cube.ware.R;
import cube.ware.data.repository.CubeUserRepository;
import cube.ware.data.room.model.CubeUser;
import cube.ware.service.user.UserHandle;
import cube.ware.service.user.UserStateListener;
import cube.ware.ui.login.adapter.LVCubeIdListAdapter;
import cube.ware.utils.SpUtil;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

@Route(path = AppConstants.Router.CubIdListActivity)
public class CubeIdListActivity extends BaseActivity<CubeIdListPresenter> implements CubeIdListContract.View, UserStateListener {

    private ListView mLvCubeId;
    List<CubeUser> mUsers = new ArrayList<>();
    private LVCubeIdListAdapter mLvCubeIdListAdapter;
    private ImageView           mIvBack;
    private String              cubeId;
    private String              disPlayName;
    private ProgressDialog mProgressDialog;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_cube_id_list;
    }

    @Override
    protected CubeIdListPresenter createPresenter() {
        return new CubeIdListPresenter(this, this);
    }

    @Override
    protected void initView() {
        super.initView();
        mLvCubeId = findViewById(R.id.lv_cubeid);
        mIvBack = findViewById(R.id.iv_back);
        mLvCubeIdListAdapter = new LVCubeIdListAdapter(this, mUsers);
        mLvCubeId.setAdapter(mLvCubeIdListAdapter);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.logining));
    }

    @Override
    protected void initData() {
        super.initData();
        //获取cubeIdList数据
        mPresenter.getCubeIdList();
    }

    @Override
    protected void initListener() {
        //添加监听
        UserHandle.getInstance().addUserStateListener(this);

        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mLvCubeId.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (ClickUtil.isShaking(view)) {
                    return;
                }
                cubeId = mUsers.get(position).getCubeId();
                disPlayName = mUsers.get(position).getDisplayName();
                mPresenter.getCubetoken(mUsers.get(position).getCubeId());
                mProgressDialog.show();
            }
        });
    }

    @Override
    public void getCubeIdListSuccess(List<CubeUser> userList) {
        mUsers.addAll(userList);
        mLvCubeIdListAdapter.notifyDataSetChanged();
    }

    @Override
    public void showToast(String msg) {
        ToastUtil.showToast(this, msg);
    }

    @Override
    public void getCubeToken(String cubeToken) {
        //login 引擎
        CubeUI.getInstance().login(cubeId, cubeToken, disPlayName);
    }

    @Override
    public void loginCubeEngineSuccess(User user) {
        LogUtil.i("loginSuccess");
        RouterUtil.navigation(this, AppConstants.Router.MainActivity);
        finish();
    }

    @Override
    public void onLogin(CubeSession session, User from) {
        String userJson = GsonUtil.toJson(from);
        SpUtil.setUserJson(userJson);
        SpUtil.setCubeId(from.cubeId);
        SpUtil.setUserName(from.displayName);
        SpUtil.setUserAvator(from.avatar);
        LogUtil.i(from.toString());
        CubeUserRepository.getInstance().saveUser(mUsers)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<CubeUser>>() {
                    @Override
                    public void call(List<CubeUser> cubeUsers) {
                        goToMain();
                    }
                });
        if(mProgressDialog!=null){
            mProgressDialog.dismiss();
        }
    }

    /**
     * 跳转主界面
     */

    public void goToMain() {
        RouterUtil.navigation(this, AppConstants.Router.MainActivity);
        finish();
    }

    @Override
    public void onLogout(CubeSession session, User from) {

    }

    @Override
    public void onUserFailed(CubeError error, User from) {
        LogUtil.e("登录失败：" + error);
        ToastUtil.showToast(this, "登录失败：" + error);
        if(mProgressDialog!=null){
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onDeviceOnline(DeviceInfo loginDevice, List<DeviceInfo> onlineDevices, User from) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UserHandle.getInstance().removeUserStateListener(this);
    }
}
