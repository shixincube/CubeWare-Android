package cube.ware.ui.mine.name;

import android.content.Context;
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
import com.common.mvp.base.BaseActivity;
import com.common.mvp.base.BasePresenter;
import com.common.mvp.eventbus.Event;
import com.common.utils.utils.ToastUtil;
import cube.service.CubeEngine;
import cube.service.group.Group;
import cube.ware.AppConstants;
import cube.ware.R;
import cube.ware.common.MessageConstants;
import cube.ware.core.CubeConstants;

@Route(path = AppConstants.Router.ModifyNameActivity)
public class ModifyNameActivity extends BaseActivity implements View.OnClickListener, TextWatcher {

    private TextView  mTvCancel;
    private TextView  mTvSave;
    private EditText  mEtName;
    private TextView  mTvCount;
    private TextView  mTvTitle;
    private ImageView mIvClear;

    private int   mMaxLength = 20;
    private Group mGroup;
    private int   mType;

    String BUNDLE = "bundle";

    @Override
    protected int getContentViewId() {
        return R.layout.activity_re_name;
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    protected void initView() {
        mTvCancel = findViewById(R.id.tv_cancel);
        mTvSave = findViewById(R.id.tv_save);
        mEtName = findViewById(R.id.et_name);
        mTvCount = findViewById(R.id.tv_count);
        mIvClear = findViewById(R.id.iv_clear);
        mTvTitle = findViewById(R.id.tv_title);
    }

    protected void initData() {
        String displayname = getIntent().getBundleExtra(BUNDLE).getString("displayname");
        //0 修改昵称，1 修改群名
        mType = getIntent().getBundleExtra(BUNDLE).getInt("type", 0);
        mGroup = (Group) getIntent().getBundleExtra(BUNDLE).getSerializable("group");
        mTvTitle.setText(mType == 0 ? R.string.user_name : R.string.group_name);
        mMaxLength = mType == 0 ? 20 : 24;
        mEtName.setText(displayname);
        mEtName.setFilters(new InputFilter[] { new InputFilter.LengthFilter(mMaxLength) });
        if (!TextUtils.isEmpty(displayname)) {
            mEtName.setSelection(displayname.length());
        }
        int count = mMaxLength - (mEtName.getText().toString().length());
        mTvCount.setText(count + "");
    }

    protected void initListener() {
        mIvClear.setOnClickListener(this);
        mTvCancel.setOnClickListener(this);
        mTvSave.setOnClickListener(this);
        mEtName.addTextChangedListener(this);
    }

    @Override
    public void onReceiveEvent(Event event) {
        switch (event.eventName) {
            case CubeConstants.Event.UPDATE_GROUP:
                finish();
                break;

            case MessageConstants.Event.EVENT_REFRESH_CUBE_USER:
                finish();
                break;

            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                //隐藏软键盘
                closeSoftKey();
                finish();
                break;
            case R.id.tv_save:
                String newName = mEtName.getText().toString().trim();
                if (TextUtils.isEmpty(newName)) {
                    ToastUtil.showToast(this, "名字不能为空");
                    return;
                }

                if (mType == 0) {
                    //User user = new User(SpUtil.getCubeId());
                    //user.displayName = newName;
                    //CubeEngine.getInstance().getAccountService().update(user);
                }
                else {
                    CubeEngine.getInstance().getGroupService().changeGroupName(mGroup.getGroupId(), newName);
                }

                closeSoftKey();
                break;
            case R.id.iv_clear:
                mEtName.setText("");
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        int countallow = mMaxLength - (String.valueOf(s).trim().length());//剩余可输入的字符数
        mTvCount.setText(countallow + "");
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (Integer.parseInt(String.valueOf(mTvCount.getText()).trim()) == 0) {
            ToastUtil.showToast(this, "最多只能输入" + mMaxLength + "个字符");
        }
    }

    /**
     * 关闭软键盘
     */
    public void closeSoftKey() {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
