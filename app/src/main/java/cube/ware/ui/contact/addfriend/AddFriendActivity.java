package cube.ware.ui.contact.addfriend;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.common.mvp.base.BaseActivity;
import com.common.utils.utils.ToastUtil;

import cube.service.group.Group;
import cube.ware.App;
import cube.ware.AppConstants;
import cube.ware.R;
import cube.ware.ui.contact.adapter.GroupListAdapter;

@Route(path = AppConstants.Router.AddFriendActivity)
public class AddFriendActivity extends BaseActivity<AddFriendPresener> implements AddFriendContract.View,View.OnClickListener, TextView.OnEditorActionListener {

    private ImageView mIvBack;
    private EditText mEtSearch;
    private RecyclerView mRvMem;
    private LinearLayoutManager mLinearLayoutManager;
    private GroupListAdapter mGroupListAdapter;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_add_friend;
    }

    @Override
    protected AddFriendPresener createPresenter() {
        return new AddFriendPresener(this,this);
    }

    @Override
    protected void initView() {
        mIvBack = findViewById(R.id.iv_back);
        mEtSearch = findViewById(R.id.et_search);
        mRvMem = findViewById(R.id.rv_member);
        mGroupListAdapter = new GroupListAdapter(R.layout.item_friend_list);
        mRvMem.setAdapter(mGroupListAdapter);
    }

    @Override
    protected void initListener() {
        mIvBack.setOnClickListener(this);
        mEtSearch.setOnEditorActionListener(this);
        mGroupListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Toast.makeText(App.getContext().getApplicationContext(), "当前不支持加群操作", Toast.LENGTH_SHORT).show();
//                Group group = mGroupListAdapter.getData().get(position);
//                //跳转页面
//                ARouter.getInstance().build(AppConstants.Router.GroupDetailsActivity)
//                        .withString("groupId",group.groupId)
//                        .withObject("group",group)
//                        .navigation();
            }
        });
    }

    @Override
    protected void initData() {
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRvMem.setLayoutManager(mLinearLayoutManager);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                closeSoftKey();
                finish();
                break;
            case R.id.tv_save:
                break;
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            String groupId = mEtSearch.getText().toString();
            closeSoftKey();
            if(!TextUtils.isEmpty(groupId)){
                mPresenter.getGroupDate(groupId);
            }else {
                showMessage(this.getResources().getString(R.string.enter_groupid));
            }
            return true;
        }
        return false;
    }

    @Override
    public void showMessage(String message) {
        //清楚之前搜索结果
        if(mGroupListAdapter.getData().size()!= 0){
            mGroupListAdapter.remove(0);
        }
        ToastUtil.showToast(this,message);
    }

    @Override
    public void searchGroup(Group group) {
        //todo mGroupListAdapter清除数据的方法
        if(mGroupListAdapter.getData().size()==1){
            mGroupListAdapter.remove(0);
        }
        mGroupListAdapter.addData(group);
    }

    /**
     * 关闭软键盘
     *
     */
    public void closeSoftKey(){
        ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
