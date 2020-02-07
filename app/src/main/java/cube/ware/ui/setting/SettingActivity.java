package cube.ware.ui.setting;

import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import com.alibaba.android.arouter.facade.annotation.Route;
import cube.ware.AppConstants;
import cube.ware.R;
import cube.ware.common.BaseToolBarActivity;
import cube.ware.widget.toolbar.ToolBarOptions;

/**
 * Created by dth
 * Des: 设置test类
 * Date: 2018/9/17.
 */
@Route(path = AppConstants.Router.SettingActivity)
public class SettingActivity extends BaseToolBarActivity<SettingContract.Presenter> implements SettingContract.View, View.OnClickListener {

    /**
     * 通知配置
     */
    private TextView mTvNotify;
    /**
     * 提醒
     */
    private CheckBox mCbIsAlerts;
    /**
     * 详情
     */
    private CheckBox mCbIsDetails;
    /**
     * 声音
     */
    private CheckBox mCbIsSound;
    /**
     * 震动
     */
    private CheckBox mCbIsVibrate;
    /**
     * 免打扰配置
     */
    private TextView mTvDisturb;
    /**
     * 免打扰
     */
    private CheckBox mCbIsNotDisturb;
    /**
     * 开始时间
     */
    private EditText mEtStart;
    /**
     * 结束时间
     */
    private EditText mEtEnd;
    /**
     * 更新DeviceToken
     */
    private TextView mTvDeviceToken;
    /**
     * diviceToken
     */
    private EditText mEtDiviceToken;
    private EditText mEtChat;
    private TextView mTvAddTop;
    private TextView mTvRemoveTop;
    private TextView mTvAddMute;
    private TextView mTvRemoveMute;
    private TextView mTvQuery;
    private TextView mTvResult;

    @Override
    protected int getContentViewId() {
        return R.layout.setting_activity;
    }

    @Override
    protected SettingContract.Presenter createPresenter() {
        return new SettingPresenter(this, this);
    }

    @Override
    protected void initToolBar() {
        ToolBarOptions toolBarOptions = new ToolBarOptions();
        toolBarOptions.setTitle(getResources().getString(R.string.settting));
        toolBarOptions.setBackIcon(R.drawable.selector_title_back);
        toolBarOptions.setBackTextColor(R.color.selector_back_text);
        toolBarOptions.setBackVisible(true);
        toolBarOptions.setOnTitleClickListener(v -> {
            if (v.getId() == R.id.back) {
                finish();
            }
        });
        setToolBar(toolBarOptions);
    }

    public void initView() {
        mTvNotify = (TextView) findViewById(R.id.tv_notify);
        mCbIsAlerts = (CheckBox) findViewById(R.id.cb_isAlerts);
        mCbIsDetails = (CheckBox) findViewById(R.id.cb_isDetails);
        mCbIsSound = (CheckBox) findViewById(R.id.cb_isSound);
        mCbIsVibrate = (CheckBox) findViewById(R.id.cb_isVibrate);
        mTvDisturb = (TextView) findViewById(R.id.tv_disturb);
        mCbIsNotDisturb = (CheckBox) findViewById(R.id.cb_isNotDisturb);
        mEtStart = (EditText) findViewById(R.id.et_start);
        mEtEnd = (EditText) findViewById(R.id.et_end);
        mTvDeviceToken = (TextView) findViewById(R.id.tv_deviceToken);
        mEtDiviceToken = (EditText) findViewById(R.id.et_diviceToken);
        mEtChat = (EditText) findViewById(R.id.et_chatId);
        mTvAddTop = (TextView) findViewById(R.id.tv_add_top);
        mTvRemoveTop = (TextView) findViewById(R.id.tv_remove_top);
        mTvAddMute = (TextView) findViewById(R.id.tv_add_mute);
        mTvRemoveMute = (TextView) findViewById(R.id.tv_remove_mute);
        mTvQuery = (TextView) findViewById(R.id.tv_query);
        mTvResult = (TextView) findViewById(R.id.tv_result);
    }

    @Override
    protected void initListener() {
        mTvNotify.setOnClickListener(this);
        mTvDisturb.setOnClickListener(this);
        mTvDeviceToken.setOnClickListener(this);
        mTvAddTop.setOnClickListener(this);
        mTvRemoveTop.setOnClickListener(this);
        mTvAddMute.setOnClickListener(this);
        mTvRemoveMute.setOnClickListener(this);
        mTvQuery.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String chatId = mEtChat.getText().toString();
        /*switch (v.getId()) {
            default:
                break;
            case R.id.tv_notify:
                Setting.NotifyConfig notifyConfig = new Setting.NotifyConfig();
                notifyConfig.isAlerts = mCbIsAlerts.isChecked();
                notifyConfig.isDetails = mCbIsDetails.isChecked();
                notifyConfig.isSound = mCbIsSound.isChecked();
                notifyConfig.isVibrate = mCbIsVibrate.isChecked();
                CubeEngine.getInstance().getSettingService().updateNotifyConfig(notifyConfig);
                break;
            case R.id.tv_disturb:
                String startTime = mEtStart.getText().toString();
                String endTime = mEtEnd.getText().toString();

                Setting.DisturbConfig disturbConfig = new Setting.DisturbConfig();
                disturbConfig.isNotDisturb = mCbIsNotDisturb.isChecked();
                disturbConfig.startTime = startTime;
                disturbConfig.endTime = endTime;
                CubeEngine.getInstance().getSettingService().updateDisturbConfig(disturbConfig);
                break;
            case R.id.tv_deviceToken:
                String deviceToken = mEtDiviceToken.getText().toString();
                CubeEngine.getInstance().getSettingService().updateDeviceTokenConfig(deviceToken);
                break;
            case R.id.tv_add_top:
                if(TextUtils.isEmpty(chatId))return;
                CubeEngine.getInstance().getSettingService().addTopSession(chatId);
                break;
            case R.id.tv_remove_top:
                if(TextUtils.isEmpty(chatId))return;
                CubeEngine.getInstance().getSettingService().removeTopSession(chatId);
                break;
            case R.id.tv_add_mute:
                if(TextUtils.isEmpty(chatId))return;
                CubeEngine.getInstance().getSettingService().addMuteSession(chatId);
                break;
            case R.id.tv_remove_mute:
                if(TextUtils.isEmpty(chatId))return;
                CubeEngine.getInstance().getSettingService().removeMuteSession(chatId);
                break;
            case R.id.tv_query:
                Setting setting = CubeEngine.getInstance().getSettingService().getSetting();
                if (setting == null) {
                    CubeEngine.getInstance().getSettingService().syncSetting();
                } else {
                    mTvResult.setText(setting.toString());
                }
                break;
        }*/
    }
}
