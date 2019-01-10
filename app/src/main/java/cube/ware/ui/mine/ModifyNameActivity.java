package cube.ware.ui.mine;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.common.mvp.rx.RxManager;
import com.common.utils.utils.ToastUtil;

import cube.service.CubeEngine;
import cube.service.group.model.Group;
import cube.service.user.model.User;
import cube.ware.AppConstants;
import cube.ware.R;
import cube.ware.eventbus.Event;
import cube.ware.utils.SpUtil;
import rx.functions.Action1;

@Route(path = AppConstants.Router.ModifyNameActivity)
public class ModifyNameActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    private TextView mTvCancel;
    private TextView mTvSave;
    private EditText mEtName;
    private TextView mTvCount;
    private TextView mTvTitle;
    private ImageView mIvClear;
    private int mMaxLength = 20;
    RxManager rxManager = new RxManager();//创建群
    private Group mGroup;
    private int mType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_re_name);
        initView();
        initData();
        initListener();

    }

    private void initView() {
        mTvCancel = findViewById(R.id.tv_cancel);
        mTvSave = findViewById(R.id.tv_save);
        mEtName = findViewById(R.id.et_name);
        mTvCount = findViewById(R.id.tv_count);
        mIvClear = findViewById(R.id.iv_clear);
        mTvTitle = findViewById(R.id.tv_title);
    }

    private void initData() {
        String displayname = getIntent().getBundleExtra(AppConstants.Value.BUNDLE).getString("displayname");
        //0 修改昵称，1 修改群名
        mType = getIntent().getBundleExtra(AppConstants.Value.BUNDLE).getInt("type",0);
        mGroup = (Group) getIntent().getBundleExtra(AppConstants.Value.BUNDLE).getSerializable("group");
        mTvTitle.setText(mType == 0 ? R.string.user_name : R.string.group_name);
        mMaxLength = mType == 0 ? 20 : 24;
        mEtName.setText(displayname);
        mEtName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mMaxLength)});
        if(!TextUtils.isEmpty(displayname)){
            mEtName.setSelection(displayname.length());
        }
        int count = mMaxLength-(mEtName.getText().toString().length());
        mTvCount.setText(count+"");
    }

    private void initListener() {
        mIvClear.setOnClickListener(this);
        mTvCancel.setOnClickListener(this);
        mTvSave.setOnClickListener(this);
        mEtName.addTextChangedListener(this);
        rxManager.on(Event.EVENT_REFRESH_CUBE_USER, new Action1<Object>() {
            @Override
            public void call(Object o) {
                finish();
            }
        });

        rxManager.on(Event.EVENT_UPDATE_GROUP, new Action1<Object>() {
            @Override
            public void call(Object o) {
               finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_cancel:
                //隐藏软键盘
                closeSoftKey();
                finish();
                break;
            case R.id.tv_save:
                String newName = mEtName.getText().toString().trim();
                if (TextUtils.isEmpty(newName)) {
                    ToastUtil.showToast(this,"名字不能为空");
                    return;
                }

                if (mType == 0) {
                    User user = new User(SpUtil.getCubeId());
                    user.displayName = newName;
                    CubeEngine.getInstance().getUserService().update(user);
                } else {
                    mGroup.displayName = newName;
                    CubeEngine.getInstance().getGroupService().update(mGroup);
                }

                closeSoftKey();
                break;
            case R.id.iv_clear:
                mEtName.setText("");
                break;
        }
    }

    @Override
    protected void onDestroy() {
        rxManager.clear();
        super.onDestroy();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        int countallow = mMaxLength-(String.valueOf(s).trim().length());//剩余可输入的字符数
        mTvCount.setText(countallow+"");
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (Integer.parseInt(String.valueOf(mTvCount.getText()).trim())==0){
            ToastUtil.showToast(this,"最多只能输入"+ mMaxLength +"个字符");
        }

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
