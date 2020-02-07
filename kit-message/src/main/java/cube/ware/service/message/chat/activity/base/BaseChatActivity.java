package cube.ware.service.message.chat.activity.base;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.common.utils.receiver.HeadsetReceiver;
import com.common.utils.utils.ReflectionUtil;
import com.common.utils.utils.ScreenUtil;
import com.common.utils.utils.ToastUtil;
import com.common.utils.utils.log.LogUtil;
import cube.ware.service.message.manager.MessageListenerManager;
import cube.ware.common.BaseToolBarActivity;
import cube.ware.service.message.R;
import cube.ware.service.message.chat.fragment.MessageFragment;
import cube.ware.service.message.chat.listener.ChatEventListener;
import cube.ware.service.message.manager.PlayerManager;
import java.util.List;

/**
 * Created by dth
 * Des:
 * Date: 2018/8/30.
 */

public abstract class BaseChatActivity extends BaseToolBarActivity implements SensorEventListener, HeadsetReceiver.HeadsetListener, ChatEventListener {

    /**
     * 聊天id
     */
    protected String mChatId;

    /**
     * 会话名
     */
    protected String mChatName;

    /**
     * 聊天页面定制化信息
     */
    protected ChatCustomization mChatCustomization;

    /**
     * 是否支持匿名聊天
     */
    protected boolean         isAnonymous;
    /**
     * 消息fragment
     */
    protected MessageFragment mMessageFragment;

    protected PowerManager          powerManager;
    protected PowerManager.WakeLock wakeLock;
    protected SensorManager         sensorManager;
    protected Sensor                sensor;

    public static final String EXTRA_CHAT_ID            = "chat_id";
    public static final String EXTRA_CHAT_NAME          = "chat_name";
    public static final String EXTRA_CHAT_TYPE          = "chat_type";
    public static final String EXTRA_CHAT_CUSTOMIZATION = "chat_customization";
    public static final String EXTRA_CHAT_MESSAGE       = "chat_message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMessageFragment = (MessageFragment) switchContent(buildFragment(), false, "MessageFragment");
        parseIntent();
        MessageListenerManager.getInstance().addChatEventListener(this);
    }

    /**
     * 构建一个MessageFragment
     *
     * @return
     */
    protected abstract MessageFragment buildFragment();

    /**
     * 解析Intent
     */
    private void parseIntent() {
        this.mChatId = getIntent().getStringExtra(EXTRA_CHAT_ID);
        this.mChatName = getIntent().getStringExtra(EXTRA_CHAT_NAME);
        this.mChatCustomization = (ChatCustomization) getIntent().getSerializableExtra(EXTRA_CHAT_CUSTOMIZATION);
        this.isAnonymous = mChatCustomization.typ == ChatCustomization.ChatStatusType.Anonymous;
        LogUtil.i("聊天ID: " + mChatId + "  是否支持匿名: " + isAnonymous);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (mSavedInstanceState != null) {
            //说明系统将此Activity回收
            mMessageFragment = (MessageFragment) getSupportFragmentManager().findFragmentByTag("MessageFragment");
        }
        else {
            this.mMessageFragment = (MessageFragment) switchContent(buildFragment(), false, "MessageFragment");
        }
        parseIntent();
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.powerManager = (PowerManager) getSystemService(POWER_SERVICE);

        this.sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        this.sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        this.sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        HeadsetReceiver.getInstance().addHeadsetListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (this.sensorManager != null) {
            this.sensorManager.unregisterListener(this);
            this.sensorManager = null;
        }
        HeadsetReceiver.getInstance().removeHeadsetListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MessageListenerManager.getInstance().removeChatEventListener(this);
    }

    @Override
    public void onBackPressed() {
        invokeFragmentManagerNoteStateNotSaved();
        if (this.mMessageFragment == null || !this.mMessageFragment.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (this.mMessageFragment != null) {
            this.mMessageFragment.onActivityResult(requestCode, resultCode, data);
        }

        if (this.mChatCustomization != null) {
            this.mChatCustomization.onActivityResult(this, requestCode, resultCode, data);
        }
    }

    /**
     * 切换fragment
     *
     * @param fragment
     * @param needAddToBackStack
     * @param tag
     *
     * @return
     */
    protected MessageFragment switchContent(MessageFragment fragment, boolean needAddToBackStack, String tag) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        if (tag != null) {
            fragmentTransaction.replace(fragment.getContainerId(), fragment, tag);
        }
        else {
            fragmentTransaction.replace(fragment.getContainerId(), fragment);
        }
        if (needAddToBackStack) {
            fragmentTransaction.addToBackStack(null);
        }
        try {
            fragmentTransaction.commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fragment;
    }

    private void invokeFragmentManagerNoteStateNotSaved() {
        FragmentManager fm = getSupportFragmentManager();
        ReflectionUtil.invokeMethod(fm, "noteStateNotSaved", null);
    }

    @Override
    public void onInOrOut(int state) {
        if (state == PlayerManager.MODE_HEADSET) {
            ToastUtil.showToast(this, 0, "耳机已插入");
            PlayerManager.getInstance().changeToHeadsetMode();
        }
        else if (state == 0) {
            PlayerManager.getInstance().resume();
        }
    }

    @Override
    public void onPullOut() {
        ToastUtil.showToast(this, 0, "耳机已拔出");
        PlayerManager.getInstance().pause();
        PlayerManager.getInstance().changeToSpeakerMode();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // 耳机模式下直接返回
        if (PlayerManager.getInstance().getCurrentMode() == PlayerManager.MODE_HEADSET) {
            return;
        }
        float value = event.values[0];
        if (PlayerManager.getInstance().isPlaying()) {
            if (value == sensor.getMaximumRange()) {
                PlayerManager.getInstance().changeToSpeakerMode();
                this.setScreenOn();
            }
            else {
                PlayerManager.getInstance().changeToEarpieceMode();
                this.setScreenOff();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * 亮屏
     */
    protected void setScreenOn() {
        if (null != this.wakeLock) {
            this.wakeLock.setReferenceCounted(false);
            this.wakeLock.release();
            this.wakeLock = null;
        }
    }

    /**
     * 熄屏
     */
    protected void setScreenOff() {
        if (null == this.wakeLock) {
            this.wakeLock = powerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "BaseChatActivity");
        }
        this.wakeLock.acquire();
    }

    public void setChatName(String chatName) {
        this.mChatName = chatName;
    }

    /**
     * 添加标题栏右侧的操作按钮及响应事件
     *
     * @param activity
     * @param buttonList
     */
    public LinearLayout addTitleOptionButton(BaseChatActivity activity, List<ChatCustomization.OptionButton> buttonList) {
        if (buttonList == null || buttonList.size() == 0) {
            return null;
        }

        if (mToolbar == null) {
            return null;
        }

        LinearLayout titleOptionLayout = (LinearLayout) LayoutInflater.from(activity).inflate(R.layout.cube_action_bar_custom_view, null);
        for (final ChatCustomization.OptionButton button : buttonList) {
            ImageView imageView = new ImageView(activity);
            imageView.setImageResource(button.iconId);
            imageView.setPadding(ScreenUtil.dip2px(8), 0, ScreenUtil.dip2px(8), 0);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    button.onClick(BaseChatActivity.this, v, mChatId);
                }
            });
            titleOptionLayout.addView(imageView);
        }
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        mToolbar.addView(titleOptionLayout, layoutParams);
        return titleOptionLayout;
    }
}
