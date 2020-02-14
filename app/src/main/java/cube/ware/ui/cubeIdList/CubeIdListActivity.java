package cube.ware.ui.cubeIdList;

import android.app.ProgressDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.common.base.BaseActivity;
import com.common.router.RouterUtil;
import com.common.utils.ClickUtil;
import com.common.utils.ToastUtil;
import com.common.utils.log.LogUtil;
import cube.ware.AppConstants;
import cube.ware.R;
import cube.ware.data.room.model.CubeUser;
import java.util.ArrayList;
import java.util.List;

@Route(path = AppConstants.Router.CubIdListActivity)
public class CubeIdListActivity extends BaseActivity<CubeIdListPresenter> implements CubeIdListContract.View {

    private CubeIdListAdapter mAdapter;
    private ImageView         mIvBack;
    private String            cubeId;
    private String            disPlayName;
    private ProgressDialog    mProgressDialog;

    private List<CubeUser> mUsers = new ArrayList<>();

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
        mIvBack = findViewById(R.id.iv_back);
        mAdapter = new CubeIdListAdapter();
        RecyclerView cubeIdsRv = findViewById(R.id.lv_cubeid);
        cubeIdsRv.setAdapter(mAdapter);
        cubeIdsRv.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL);
        cubeIdsRv.addItemDecoration(itemDecoration);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.logining));
    }

    @Override
    protected void initData() {
        super.initData();
        //获取cubeIdList数据
        mPresenter.queryCubeIdList();
    }

    @Override
    protected void initListener() {
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (ClickUtil.isShaking(view)) {
                    return;
                }

                CubeUser user = mAdapter.getItem(position);
                if (user != null) {
                    cubeId = user.getCubeId();
                    disPlayName = user.getDisplayName();
                    mPresenter.queryCubeToken(user.getCubeId());
                    mProgressDialog.show();
                }
            }
        });
    }

    @Override
    public void queryCubeIdListSuccess(List<CubeUser> userList) {
        mUsers.addAll(userList);
        mAdapter.setNewData(mUsers);
    }

    @Override
    public void showMessage(String msg) {
        ToastUtil.showToast(this, msg);
    }

    @Override
    public void queryCubeTokenSuccess(String cubeToken) {
        //login 引擎
        mPresenter.login(cubeId, cubeToken, disPlayName);
    }

    @Override
    public void loginSuccess() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }

        mPresenter.saveUsers(mUsers);
        goToMain();
    }

    @Override
    public void loginFailed(String desc) {
        LogUtil.e("登录失败：" + desc);
        ToastUtil.showToast(this, "登录失败：" + desc);
        if (mProgressDialog != null) {
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
}
