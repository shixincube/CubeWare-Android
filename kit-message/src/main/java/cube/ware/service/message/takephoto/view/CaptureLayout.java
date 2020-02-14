package cube.ware.service.message.takephoto.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.common.utils.log.LogUtil;
import cube.ware.service.message.R;
import cube.ware.service.message.takephoto.lisenter.CaptureListener;
import cube.ware.service.message.takephoto.lisenter.ReturnListener;
import cube.ware.service.message.takephoto.lisenter.TypeListener;

/**
 * 集成各个控件的布局
 *
 * @author Wangxx
 * @date 2017/5/25
 */

public class CaptureLayout extends FrameLayout implements CameraInterface.CamOpenOverCallback {
    //拍照按钮监听
    private CaptureListener captureListener;
    //拍照或录制后接结果按钮监听
    private TypeListener    typeListener;
    //退出按钮监听
    private ReturnListener  returnListener;

    public void setTypeListener(TypeListener typeListener) {
        this.typeListener = typeListener;
    }

    public void setCaptureListener(CaptureListener captureListener) {
        this.captureListener = captureListener;
    }

    public void setReturnListener(ReturnListener returnListener) {
        this.returnListener = returnListener;
    }

    private CaptureButton btn_capture;
    private TypeButton    btn_confirm;
    private TypeButton    btn_cancel;
    private ReturnButton  btn_return;
    private ImageView     btn_switch;
    private TextView      txt_tip;

    private int layout_width;
    private int layout_height;
    private int button_size;

    public CaptureLayout(Context context) {
        this(context, null);
    }

    public CaptureLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CaptureLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //get width
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        layout_width = outMetrics.widthPixels;
        button_size = (int) (layout_width / 5.5f);
        layout_height = button_size + (button_size / 5) * 2 + 80;

        initView();
        initEvent();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(layout_width, layout_height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void initEvent() {
        //默认Typebutton为隐藏
        btn_cancel.setVisibility(INVISIBLE);
        btn_confirm.setVisibility(INVISIBLE);
    }

    public void startTypeBtnAnimator() {
        //拍照录制结果后的动画
        btn_capture.setVisibility(INVISIBLE);
        btn_return.setVisibility(INVISIBLE);
        btn_switch.setVisibility(INVISIBLE);
        btn_cancel.setVisibility(VISIBLE);
        btn_confirm.setVisibility(VISIBLE);
        btn_cancel.setClickable(false);
        btn_confirm.setClickable(false);
        ObjectAnimator animator_cancel = ObjectAnimator.ofFloat(btn_cancel, "translationX", layout_width / 4, 0);
        animator_cancel.setDuration(200);
        animator_cancel.start();
        ObjectAnimator animator_confirm = ObjectAnimator.ofFloat(btn_confirm, "translationX", -layout_width / 4, 0);
        animator_confirm.setDuration(200);
        animator_confirm.start();
        animator_confirm.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                btn_cancel.setClickable(true);
                btn_confirm.setClickable(true);
            }
        });
    }

    private boolean isFirst = true;

    public void startAlphaAnimation() {
        if (isFirst) {
            ObjectAnimator animator_txt_tip = ObjectAnimator.ofFloat(txt_tip, "alpha", 1f, 0f);
            animator_txt_tip.setDuration(500);
            animator_txt_tip.start();
            isFirst = false;
        }
    }

    private void initView() {
        setWillNotDraw(false);

        //btn_capture
        btn_capture = new CaptureButton(getContext(), button_size);
        LayoutParams btn_capture_param = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        //btn_capture_param.addRule(CENTER_IN_PARENT, TRUE);
        //btn_capture_param.setMargins(0, 152, 0, 0);
        btn_capture_param.gravity = Gravity.CENTER;
        btn_capture.setLayoutParams(btn_capture_param);
        btn_capture.setCaptureLisenter(new CaptureListener() {
            @Override
            public void takePictures() {
                LogUtil.i("wgkkk", "takePictures");
                if (captureListener != null) {
                    captureListener.takePictures();
                }
            }

            @Override
            public void recordShort(long time) {
                LogUtil.i("wgkkk","recordShort");
                if (captureListener != null) {
                    captureListener.recordShort(time);
                }
                mEnable = false;
                startAlphaAnimation();
                btn_return.setVisibility(VISIBLE);
                btn_switch.setVisibility(VISIBLE);
                btn_capture.setVisibility(VISIBLE);
                btn_confirm.setVisibility(INVISIBLE);
                btn_cancel.setVisibility(INVISIBLE);
            }

            @Override
            public void recordStart() {
                LogUtil.i("wgkkk","recordStart");
                mEnable = true;
                if (captureListener != null) {
                    captureListener.recordStart();
                    btn_return.setVisibility(INVISIBLE);
                    btn_switch.setVisibility(INVISIBLE);
//                    btn_capture.setVisibility(INVISIBLE);
//                    btn_confirm.setVisibility(VISIBLE);
//                    btn_cancel.setVisibility(VISIBLE);
                }
                startAlphaAnimation();
            }

            @Override
            public void recordEnd(long time) {
                LogUtil.i("wgkkk","recordEnd");
                mEnable = false;
                if (captureListener != null) {
                    captureListener.recordEnd(time);
                }
                startAlphaAnimation();
                startTypeBtnAnimator();
            }

            @Override
            public void recordZoom(float zoom) {
                LogUtil.i("wgkkk","recordZoom");
                if (captureListener != null) {
                    captureListener.recordZoom(zoom);
                }
            }
        });

        //btn_cancel

        btn_cancel = new TypeButton(getContext(), TypeButton.TYPE_CANCEL, button_size);
        final LayoutParams btn_cancel_param = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        //btn_cancel_param.addRule(CENTER_VERTICAL, TRUE);
        //btn_cancel_param.addRule(ALIGN_PARENT_LEFT, TRUE);
        btn_cancel_param.gravity = Gravity.CENTER_VERTICAL;
        btn_cancel_param.setMargins(button_size / 2, 0, 0, 0);
        btn_cancel.setLayoutParams(btn_cancel_param);
        btn_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (typeListener != null) {
                    typeListener.cancel();
                }
                startAlphaAnimation();
                btn_cancel.setVisibility(INVISIBLE);
                btn_confirm.setVisibility(INVISIBLE);
                btn_capture.setVisibility(VISIBLE);
                btn_return.setVisibility(VISIBLE);
                btn_switch.setVisibility(VISIBLE);

            }

        });

        //btn_confirm

        btn_confirm = new TypeButton(getContext(), TypeButton.TYPE_CONFIRM, button_size);
        LayoutParams btn_confirm_param = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        //btn_confirm_param.addRule(CENTER_VERTICAL, TRUE);
        //btn_confirm_param.addRule(ALIGN_PARENT_RIGHT, TRUE);
        btn_confirm_param.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
        btn_confirm_param.setMargins(0, 0, button_size / 2, 0);
        btn_confirm.setLayoutParams(btn_confirm_param);
        btn_confirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (typeListener != null) {
                    typeListener.confirm();
                }
                //startAlphaAnimation();
                //btn_cancel.setVisibility(INVISIBLE);
                //btn_confirm.setVisibility(INVISIBLE);
                //btn_capture.setVisibility(VISIBLE);
                //btn_return.setVisibility(VISIBLE);
                //btn_switch.setVisibility(VISIBLE);
            }
        });

        //btn_return
        btn_return = new ReturnButton(getContext(), button_size / 2);
        LayoutParams btn_return_param = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        //btn_return_param.addRule(CENTER_VERTICAL, TRUE);
        btn_return_param.gravity = Gravity.CENTER_VERTICAL;
        btn_return_param.setMargins(layout_width / 6, 0, 0, 0);
        btn_return.setLayoutParams(btn_return_param);
        btn_return.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEnable) {
                    return;
                }
                if (captureListener != null) {
                    if (returnListener != null) {
                        returnListener.onReturn();
                    }
                }
            }
        });

        //btn_switch
        btn_switch = new ImageView(getContext());
        LayoutParams imageCameraParam = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        //imageCameraParam.addRule(CENTER_VERTICAL, TRUE);
        //imageCameraParam.addRule(ALIGN_PARENT_RIGHT, TRUE);
        imageCameraParam.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
        imageCameraParam.setMargins(0, 0, layout_width / 6, 0);
        btn_switch.setLayoutParams(imageCameraParam);
        btn_switch.setImageResource(R.drawable.selector_camera_chage_btn);
        btn_switch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEnable) {
                    return;
                }
                CameraInterface.getInstance().switchCamera(CaptureLayout.this);
                if (captureListener != null) {
                    if (returnListener != null) {
                        returnListener.onSwitch(CameraInterface.getInstance().isCameraPost());
                    }
                }
                //new Thread() {
                //    /**
                //     * switch camera
                //     */
                //    @Override
                //    public void run() {
                //        CameraInterface.getInstance().switchCamera(CaptureLayout.this);
                //        btn_switch.post(new Runnable() {
                //            @Override
                //            public void run() {
                //                if (captureListener != null) {
                //                    if (returnListener != null) {
                //                        returnListener.onSwitch(CameraInterface.getInstance().isCameraPost());
                //                    }
                //                }
                //            }
                //        });
                //    }
                //}.start();
            }
        });

        //txt_tip
        txt_tip = new TextView(getContext());
        LayoutParams txt_param = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        txt_param.gravity = Gravity.CENTER_HORIZONTAL;
        txt_param.setMargins(0, 0, 0, 0);
        txt_tip.setText("轻触拍照，长按摄像");
        txt_tip.setTextColor(0xFFFFFFFF);
        txt_tip.setGravity(Gravity.CENTER);
        txt_tip.setLayoutParams(txt_param);

        this.addView(btn_capture);
        this.addView(btn_cancel);
        this.addView(btn_confirm);
        this.addView(btn_return);
        this.addView(btn_switch);
        this.addView(txt_tip);
    }

    public void setTextWithAnimation() {
        txt_tip.setText("录制时间过短");
        ObjectAnimator animator_txt_tip = ObjectAnimator.ofFloat(txt_tip, "alpha", 0f, 1f, 1f, 0f);
        animator_txt_tip.setDuration(2500);
        animator_txt_tip.start();
    }

    public void setDuration(int duration) {
        btn_capture.setDuration(duration);
    }

    public void onResume() {
        CameraInterface.getInstance().setSwitchView(btn_switch);
    }

    public void onPause() {
        btn_capture.handlerUnPressByState();
    }

    public void isRecord(boolean record) {
        btn_capture.isRecord(record);
    }

    private boolean mEnable = false;

    @Override
    public void cameraHasOpened() {

    }

    @Override
    public void cameraSwitchSuccess() {
        mEnable = false;
    }
}
