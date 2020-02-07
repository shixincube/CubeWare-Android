package cube.ware.service.message.preview.video.videoplay;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import cube.ware.service.message.R;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by Nathen
 * On 2016/04/18 16:15
 */
public class JCVideoPlayerStandard extends JCVideoPlayer {

    protected static Timer DISMISS_CONTROL_VIEW_TIMER;

    public ProgressBar loadingProgressBar;
    public ImageView   thumbImageView;

    protected DismissControlViewTimerTask mDismissControlViewTimerTask;

    public JCVideoPlayerStandard(Context context) {
        super(context);
    }

    public JCVideoPlayerStandard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void init(Context context) {
        super.init(context);

        thumbImageView = (ImageView) findViewById(R.id.thumb);
        loadingProgressBar = (ProgressBar) findViewById(R.id.loading);

        thumbImageView.setOnClickListener(this);
    }

    @Override
    public void setUp(String url, int screen, Object... objects) {
        super.setUp(url, screen, objects);
        if (objects.length == 0) {
            return;
        }
        if (currentScreen == SCREEN_WINDOW_FULLSCREEN) {
            changeStartButtonSize((int) getResources().getDimension(R.dimen.view_space_64));
        }
        else if (currentScreen == SCREEN_LAYOUT_NORMAL || currentScreen == SCREEN_LAYOUT_LIST) {
            changeStartButtonSize((int) getResources().getDimension(R.dimen.view_space_52));
        }
        else if (currentScreen == SCREEN_WINDOW_TINY) {
            setAllControlsVisible(INVISIBLE, INVISIBLE, INVISIBLE, INVISIBLE, INVISIBLE, INVISIBLE, INVISIBLE);
        }
    }

    public void changeStartButtonSize(int size) {
        ViewGroup.LayoutParams lp = startButton.getLayoutParams();
        lp.height = size;
        lp.width = size;
        lp = loadingProgressBar.getLayoutParams();
        lp.height = size;
        lp.width = size;
    }

    @Override
    public int getLayoutId() {
        return R.layout.jc_layout_standard;
    }

    @Override
    public void onStateNormal() {
        super.onStateNormal();
        changeUiToNormal();
    }

    @Override
    public void onStatePreparing() {
        super.onStatePreparing();
        changeUiToPreparingShow();
        startDismissControlViewTimer();
    }

    @Override
    public void onStatePlaying() {
        super.onStatePlaying();
        changeUiToPlayingShow();
        startDismissControlViewTimer();
    }

    @Override
    public void onStatePause() {
        super.onStatePause();
        changeUiToPauseShow();
        cancelDismissControlViewTimer();
    }

    @Override
    public void onStatePlaybackBufferingStart() {
        super.onStatePlaybackBufferingStart();
        changeUiToPlayingBufferingShow();
    }

    @Override
    public void onStateError() {
        super.onStateError();
        changeUiToError();
    }

    @Override
    public void onStateAutoComplete() {
        super.onStateAutoComplete();
        changeUiToCompleteClear();
        cancelDismissControlViewTimer();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int id = v.getId();
        if (id == R.id.surface_container) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    startDismissControlViewTimer();
                    if (!mChangePosition && !mChangeVolume) {
                        onEvent(JCUserActionStandard.ON_CLICK_BLANK);
                        onClickUiToggle();
                    }
                    break;
            }
        }
        else if (id == R.id.bottom_seek_progress) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    cancelDismissControlViewTimer();
                    break;
                case MotionEvent.ACTION_UP:
                    startDismissControlViewTimer();
                    break;
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int i = v.getId();
        if (i == R.id.thumb) {
            if (TextUtils.isEmpty(url)) {
                Toast.makeText(getContext(), "视频正在努力加载中，请稍等。。。", Toast.LENGTH_SHORT).show();
                return;
            }
            if (currentState == CURRENT_STATE_NORMAL) {
                if (!url.startsWith("file") && !url.startsWith("/") && !JCUtils.isWifiConnected(getContext()) && !WIFI_TIP_DIALOG_SHOWED) {
                    showWifiDialog(JCUserActionStandard.ON_CLICK_START_THUMB);
                    return;
                }
                onEvent(JCUserActionStandard.ON_CLICK_START_THUMB);
                startVideo();
            }
            else if (currentState == CURRENT_STATE_AUTO_COMPLETE) {
                onClickUiToggle();
            }
        }
        else if (i == R.id.surface_container) {
            startDismissControlViewTimer();
        }
    }

    @Override
    public void showWifiDialog(int action) {
        super.showWifiDialog(action);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(getResources().getString(R.string.call_phone_tip));
        builder.setPositiveButton("继续", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                onEvent(JCUserActionStandard.ON_CLICK_START_THUMB);
                startVideo();
                WIFI_TIP_DIALOG_SHOWED = true;
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (currentScreen == SCREEN_WINDOW_FULLSCREEN) {
                    dialog.dismiss();
                    clearFullscreenLayout();
                }
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
                if (currentScreen == SCREEN_WINDOW_FULLSCREEN) {
                    dialog.dismiss();
                    clearFullscreenLayout();
                }
            }
        });
        builder.create().show();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        super.onStartTrackingTouch(seekBar);
        cancelDismissControlViewTimer();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        super.onStopTrackingTouch(seekBar);
        startDismissControlViewTimer();
    }

    public void onClickUiToggle() {
        if (currentState == CURRENT_STATE_PREPARING) {
            if (bottomContainer.getVisibility() == VISIBLE) {
                changeUiToPreparingClear();
            }
            else {
                changeUiToPreparingShow();
            }
        }
        else if (currentState == CURRENT_STATE_PLAYING) {
            if (bottomContainer.getVisibility() == VISIBLE) {
                changeUiToPlayingClear();
            }
            else {
                changeUiToPlayingShow();
            }
        }
        else if (currentState == CURRENT_STATE_PAUSE) {
            if (bottomContainer.getVisibility() == VISIBLE) {
                changeUiToPauseClear();
            }
            else {
                changeUiToPauseShow();
            }
        }
        else if (currentState == CURRENT_STATE_PLAYING_BUFFERING_START) {
            if (bottomContainer.getVisibility() == VISIBLE) {
                changeUiToPlayingBufferingClear();
            }
            else {
                changeUiToPlayingBufferingShow();
            }
        }
    }

    public void onCLickUiToggleToClear() {
        if (currentState == CURRENT_STATE_PREPARING) {
            if (bottomContainer.getVisibility() == VISIBLE) {
                changeUiToPreparingClear();
            }
            else {
            }
        }
        else if (currentState == CURRENT_STATE_PLAYING) {
            if (bottomContainer.getVisibility() == VISIBLE) {
                changeUiToPlayingClear();
            }
            else {
            }
        }
        else if (currentState == CURRENT_STATE_PAUSE) {
            if (bottomContainer.getVisibility() == VISIBLE) {
                changeUiToPauseClear();
            }
            else {
            }
        }
        else if (currentState == CURRENT_STATE_AUTO_COMPLETE) {
            if (bottomContainer.getVisibility() == VISIBLE) {
                changeUiToCompleteClear();
            }
            else {
            }
        }
        else if (currentState == CURRENT_STATE_PLAYING_BUFFERING_START) {
            if (bottomContainer.getVisibility() == VISIBLE) {
                changeUiToPlayingBufferingClear();
            }
            else {
            }
        }
    }

    @Override
    public void setProgressAndText(int progress, int position, int duration) {
        super.setProgressAndText(progress, position, duration);
    }

    @Override
    public void setBufferProgress(int bufferProgress) {
        super.setBufferProgress(bufferProgress);
    }

    @Override
    public void resetProgressAndTime() {
        super.resetProgressAndTime();
    }

    //Unified management Ui
    public void changeUiToNormal() {
        switch (currentScreen) {
            case SCREEN_LAYOUT_NORMAL:
            case SCREEN_LAYOUT_LIST:
                setAllControlsVisible(VISIBLE, INVISIBLE, VISIBLE, INVISIBLE, VISIBLE, VISIBLE, INVISIBLE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setAllControlsVisible(VISIBLE, INVISIBLE, VISIBLE, INVISIBLE, VISIBLE, VISIBLE, INVISIBLE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_TINY:
                break;
        }
    }

    public void changeUiToPreparingShow() {
        switch (currentScreen) {
            case SCREEN_LAYOUT_NORMAL:
            case SCREEN_LAYOUT_LIST:
                setAllControlsVisible(VISIBLE, INVISIBLE, INVISIBLE, VISIBLE, VISIBLE, VISIBLE, INVISIBLE);
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setAllControlsVisible(VISIBLE, INVISIBLE, INVISIBLE, VISIBLE, VISIBLE, VISIBLE, INVISIBLE);
                break;
            case SCREEN_WINDOW_TINY:
                break;
        }
    }

    public void changeUiToPreparingClear() {
        switch (currentScreen) {
            case SCREEN_LAYOUT_NORMAL:
            case SCREEN_LAYOUT_LIST:
                setAllControlsVisible(VISIBLE, INVISIBLE, INVISIBLE, VISIBLE, VISIBLE, VISIBLE, INVISIBLE);
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setAllControlsVisible(VISIBLE, INVISIBLE, INVISIBLE, VISIBLE, VISIBLE, VISIBLE, INVISIBLE);
                break;
            case SCREEN_WINDOW_TINY:
                break;
        }
    }

    //JustPreparedUi
    @Override
    public void onPrepared() {
        super.onPrepared();
        setAllControlsVisible(VISIBLE, INVISIBLE, INVISIBLE, INVISIBLE, INVISIBLE, INVISIBLE, VISIBLE);
        startDismissControlViewTimer();
    }

    public void changeUiToPlayingShow() {
        switch (currentScreen) {
            case SCREEN_LAYOUT_NORMAL:
            case SCREEN_LAYOUT_LIST:
                setAllControlsVisible(VISIBLE, VISIBLE, VISIBLE, INVISIBLE, INVISIBLE, INVISIBLE, INVISIBLE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setAllControlsVisible(VISIBLE, VISIBLE, VISIBLE, INVISIBLE, INVISIBLE, INVISIBLE, INVISIBLE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_TINY:
                break;
        }
    }

    public void changeUiToPlayingClear() {
        switch (currentScreen) {
            case SCREEN_LAYOUT_NORMAL:
            case SCREEN_LAYOUT_LIST:
                setAllControlsVisible(INVISIBLE, INVISIBLE, INVISIBLE, INVISIBLE, INVISIBLE, INVISIBLE, VISIBLE);
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setAllControlsVisible(INVISIBLE, INVISIBLE, INVISIBLE, INVISIBLE, INVISIBLE, INVISIBLE, VISIBLE);
                break;
            case SCREEN_WINDOW_TINY:
                break;
        }
    }

    public void changeUiToPauseShow() {
        switch (currentScreen) {
            case SCREEN_LAYOUT_NORMAL:
            case SCREEN_LAYOUT_LIST:
                setAllControlsVisible(VISIBLE, VISIBLE, VISIBLE, INVISIBLE, INVISIBLE, INVISIBLE, INVISIBLE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setAllControlsVisible(VISIBLE, VISIBLE, VISIBLE, INVISIBLE, INVISIBLE, INVISIBLE, INVISIBLE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_TINY:
                break;
        }
    }

    public void changeUiToPauseClear() {
        switch (currentScreen) {
            case SCREEN_LAYOUT_NORMAL:
            case SCREEN_LAYOUT_LIST:
                setAllControlsVisible(INVISIBLE, INVISIBLE, INVISIBLE, INVISIBLE, INVISIBLE, INVISIBLE, INVISIBLE);
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setAllControlsVisible(INVISIBLE, INVISIBLE, INVISIBLE, INVISIBLE, INVISIBLE, INVISIBLE, INVISIBLE);
                break;
            case SCREEN_WINDOW_TINY:
                break;
        }
    }

    public void changeUiToPlayingBufferingShow() {
        switch (currentScreen) {
            case SCREEN_LAYOUT_NORMAL:
            case SCREEN_LAYOUT_LIST:
                setAllControlsVisible(VISIBLE, VISIBLE, INVISIBLE, VISIBLE, INVISIBLE, INVISIBLE, INVISIBLE);
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setAllControlsVisible(VISIBLE, VISIBLE, INVISIBLE, VISIBLE, INVISIBLE, INVISIBLE, INVISIBLE);
                break;
            case SCREEN_WINDOW_TINY:
                break;
        }
    }

    public void changeUiToPlayingBufferingClear() {
        switch (currentScreen) {
            case SCREEN_LAYOUT_NORMAL:
            case SCREEN_LAYOUT_LIST:
                setAllControlsVisible(INVISIBLE, INVISIBLE, INVISIBLE, VISIBLE, INVISIBLE, INVISIBLE, VISIBLE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setAllControlsVisible(INVISIBLE, INVISIBLE, INVISIBLE, VISIBLE, INVISIBLE, INVISIBLE, VISIBLE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_TINY:
                break;
        }
    }

    public void changeUiToCompleteShow() {
        switch (currentScreen) {
            case SCREEN_LAYOUT_NORMAL:
            case SCREEN_LAYOUT_LIST:
                setAllControlsVisible(VISIBLE, VISIBLE, VISIBLE, INVISIBLE, VISIBLE, INVISIBLE, INVISIBLE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setAllControlsVisible(VISIBLE, VISIBLE, VISIBLE, INVISIBLE, VISIBLE, INVISIBLE, INVISIBLE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_TINY:
                break;
        }
    }

    public void changeUiToCompleteClear() {
        switch (currentScreen) {
            case SCREEN_LAYOUT_NORMAL:
            case SCREEN_LAYOUT_LIST:
                setAllControlsVisible(VISIBLE, INVISIBLE, VISIBLE, INVISIBLE, VISIBLE, INVISIBLE, INVISIBLE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setAllControlsVisible(VISIBLE, INVISIBLE, VISIBLE, INVISIBLE, VISIBLE, INVISIBLE, INVISIBLE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_TINY:
                break;
        }
    }

    public void changeUiToError() {
        switch (currentScreen) {
            case SCREEN_LAYOUT_NORMAL:
            case SCREEN_LAYOUT_LIST:
                setAllControlsVisible(INVISIBLE, INVISIBLE, VISIBLE, INVISIBLE, INVISIBLE, VISIBLE, INVISIBLE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setAllControlsVisible(INVISIBLE, INVISIBLE, VISIBLE, INVISIBLE, INVISIBLE, VISIBLE, INVISIBLE);
                updateStartImage();
                break;
            case SCREEN_WINDOW_TINY:
                break;
        }
    }

    public void setAllControlsVisible(int topCon, int bottomCon, int startBtn, int loadingPro, int thumbImg, int coverImg, int bottomPro) {
        bottomContainer.setVisibility(bottomCon);
        startButton.setVisibility(startBtn);
        loadingProgressBar.setVisibility(loadingPro);
        thumbImageView.setVisibility(thumbImg);
    }

    public void updateStartImage() {
        if (currentState == CURRENT_STATE_PLAYING) {
            startButton.setImageResource(R.drawable.jc_click_pause_selector);
        }
        else if (currentState == CURRENT_STATE_ERROR) {
            startButton.setImageResource(R.drawable.jc_click_error_selector);
        }
        else if (currentState == CURRENT_STATE_AUTO_COMPLETE) {
            startButton.setImageResource(R.drawable.jc_click_replay_selector);
        }
        else {
            startButton.setImageResource(R.drawable.jc_click_play_selector);
        }
    }

    protected Dialog      mProgressDialog;
    protected ProgressBar mDialogProgressBar;
    protected TextView    mDialogSeekTime;
    protected TextView    mDialogTotalTime;
    protected ImageView   mDialogIcon;

    @Override
    public void showProgressDialog(float deltaX, String seekTime, int seekTimePosition, String totalTime, int totalTimeDuration) {
        super.showProgressDialog(deltaX, seekTime, seekTimePosition, totalTime, totalTimeDuration);
        if (mProgressDialog == null) {
            View localView = LayoutInflater.from(getContext()).inflate(R.layout.jc_dialog_progress, null);
            mDialogProgressBar = ((ProgressBar) localView.findViewById(R.id.duration_progressbar));
            mDialogSeekTime = ((TextView) localView.findViewById(R.id.tv_current));
            mDialogTotalTime = ((TextView) localView.findViewById(R.id.tv_duration));
            mDialogIcon = ((ImageView) localView.findViewById(R.id.duration_image_tip));
            mProgressDialog = createDialogWithView(localView);
        }
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }

        mDialogSeekTime.setText(seekTime);
        mDialogTotalTime.setText(" / " + totalTime);
        mDialogProgressBar.setProgress(totalTimeDuration <= 0 ? 0 : (seekTimePosition * 100 / totalTimeDuration));
        if (deltaX > 0) {
            mDialogIcon.setBackgroundResource(R.drawable.jc_forward_icon);
        }
        else {
            mDialogIcon.setBackgroundResource(R.drawable.jc_backward_icon);
        }
        onCLickUiToggleToClear();
    }

    @Override
    public void dismissProgressDialog() {
        super.dismissProgressDialog();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    protected Dialog      mVolumeDialog;
    protected ProgressBar mDialogVolumeProgressBar;
    protected TextView    mDialogVolumeTextView;
    protected ImageView   mDialogVolumeImageView;

    @Override
    public void showVolumeDialog(float deltaY, int volumePercent) {
        super.showVolumeDialog(deltaY, volumePercent);
        if (mVolumeDialog == null) {
            View localView = LayoutInflater.from(getContext()).inflate(R.layout.jc_dialog_volume, null);
            mDialogVolumeImageView = ((ImageView) localView.findViewById(R.id.volume_image_tip));
            mDialogVolumeTextView = ((TextView) localView.findViewById(R.id.tv_volume));
            mDialogVolumeProgressBar = ((ProgressBar) localView.findViewById(R.id.volume_progressbar));
            mVolumeDialog = createDialogWithView(localView);
        }
        if (!mVolumeDialog.isShowing()) {
            mVolumeDialog.show();
        }
        if (volumePercent <= 0) {
            mDialogVolumeImageView.setBackgroundResource(R.drawable.jc_close_volume);
        }
        else {
            mDialogVolumeImageView.setBackgroundResource(R.drawable.jc_add_volume);
        }
        if (volumePercent > 100) {
            volumePercent = 100;
        }
        else if (volumePercent < 0) {
            volumePercent = 0;
        }
        mDialogVolumeTextView.setText(volumePercent + "%");
        mDialogVolumeProgressBar.setProgress(volumePercent);
        onCLickUiToggleToClear();
    }

    @Override
    public void dismissVolumeDialog() {
        super.dismissVolumeDialog();
        if (mVolumeDialog != null) {
            mVolumeDialog.dismiss();
        }
    }

    protected Dialog      mBrightnessDialog;
    protected ProgressBar mDialogBrightnessProgressBar;
    protected TextView    mDialogBrightnessTextView;

    @Override
    public void showBrightnessDialog(int brightnessPercent) {
        super.showBrightnessDialog(brightnessPercent);
        if (mBrightnessDialog == null) {
            View localView = LayoutInflater.from(getContext()).inflate(R.layout.jc_dialog_brightness, null);
            mDialogBrightnessTextView = ((TextView) localView.findViewById(R.id.tv_brightness));
            mDialogBrightnessProgressBar = ((ProgressBar) localView.findViewById(R.id.brightness_progressbar));
            mBrightnessDialog = createDialogWithView(localView);
        }
        if (!mBrightnessDialog.isShowing()) {
            mBrightnessDialog.show();
        }
        if (brightnessPercent > 100) {
            brightnessPercent = 100;
        }
        else if (brightnessPercent < 0) {
            brightnessPercent = 0;
        }
        mDialogBrightnessTextView.setText(brightnessPercent + "%");
        mDialogBrightnessProgressBar.setProgress(brightnessPercent);
        onCLickUiToggleToClear();
    }

    @Override
    public void dismissBrightnessDialog() {
        super.dismissBrightnessDialog();
        if (mBrightnessDialog != null) {
            mBrightnessDialog.dismiss();
        }
    }

    public Dialog createDialogWithView(View localView) {
        Dialog dialog = new Dialog(getContext(), R.style.jc_style_dialog_progress);
        dialog.setContentView(localView);
        Window window = dialog.getWindow();
        window.addFlags(Window.FEATURE_ACTION_BAR);
        window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        window.setLayout(-2, -2);
        WindowManager.LayoutParams localLayoutParams = window.getAttributes();
        localLayoutParams.gravity = Gravity.CENTER;
        window.setAttributes(localLayoutParams);
        return dialog;
    }

    public void startDismissControlViewTimer() {
        cancelDismissControlViewTimer();
        DISMISS_CONTROL_VIEW_TIMER = new Timer();
        mDismissControlViewTimerTask = new DismissControlViewTimerTask();
        DISMISS_CONTROL_VIEW_TIMER.schedule(mDismissControlViewTimerTask, 2500);
    }

    public void cancelDismissControlViewTimer() {
        if (DISMISS_CONTROL_VIEW_TIMER != null) {
            DISMISS_CONTROL_VIEW_TIMER.cancel();
        }
        if (mDismissControlViewTimerTask != null) {
            mDismissControlViewTimerTask.cancel();
        }
    }

    public class DismissControlViewTimerTask extends TimerTask {

        @Override
        public void run() {
            if (currentState != CURRENT_STATE_NORMAL && currentState != CURRENT_STATE_ERROR && currentState != CURRENT_STATE_AUTO_COMPLETE) {
                if (getContext() != null && getContext() instanceof Activity) {
                    ((Activity) getContext()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            bottomContainer.setVisibility(INVISIBLE);
                            startButton.setVisibility(INVISIBLE);
                        }
                    });
                }
            }
        }
    }

    @Override
    public void onAutoCompletion() {
        super.onAutoCompletion();
        cancelDismissControlViewTimer();
    }

    @Override
    public void onCompletion() {
        super.onCompletion();
        cancelDismissControlViewTimer();
    }
}
