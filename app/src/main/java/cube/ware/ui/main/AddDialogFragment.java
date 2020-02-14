package cube.ware.ui.main;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.common.router.RouterUtil;
import com.common.utils.ToastUtil;
import cube.ware.AppConstants;
import cube.ware.R;

/**
 * author: kun .
 * date:   On 2018/9/11
 */
public class AddDialogFragment extends DialogFragment implements View.OnClickListener {

    private ImageView      mIvCancel;
    private LinearLayout   mLlAudio;
    private LinearLayout   mLlWhiteBoard;
    private LinearLayout   mLlSfu;
    private RelativeLayout mRlBgLayout;

    public static AddDialogFragment getInstance() {
        return new AddDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//设置背景透明
        View inflate = inflater.inflate(R.layout.add_fragment_dialog, null);
        initView(inflate);
        initListener();
        return inflate;
    }

    private void initView(View inflate) {
        mIvCancel = inflate.findViewById(R.id.iv_cancel);
        mLlAudio = inflate.findViewById(R.id.ll_audio);
        mLlWhiteBoard = inflate.findViewById(R.id.ll_whiteboard);
        mRlBgLayout = inflate.findViewById(R.id.rl_bg_layout);
        mLlSfu = inflate.findViewById(R.id.ll_sfu);
    }

    private void initListener() {
        mIvCancel.setOnClickListener(this);
        mLlAudio.setOnClickListener(this);
        mLlWhiteBoard.setOnClickListener(this);
        mRlBgLayout.setOnClickListener(this);
        mLlSfu.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_cancel:
                getDialog().dismiss();
                break;
            case R.id.ll_audio:
                Bundle bundle = new Bundle();
                bundle.putInt("select_type", 9);//首次创建
                RouterUtil.navigation(AppConstants.Router.SelectMemberActivity, bundle);
                getDialog().dismiss();
                break;
            case R.id.ll_whiteboard:
                Bundle bundleWB = new Bundle();
                bundleWB.putInt("select_type", 10);//首次创建
                RouterUtil.navigation(AppConstants.Router.SelectMemberActivity, bundleWB);
                getDialog().dismiss();
                break;
            case R.id.ll_sfu:
                //startActivity(new Intent(getContext(),TestConferenceActivity.class));
                dismiss();
                break;
            case R.id.rl_bg_layout:
                getDialog().dismiss();
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //得到dialog对应的window
        Window window = getDialog().getWindow();
        if (window != null) {
            //得到LayoutParams
            WindowManager.LayoutParams params = window.getAttributes();
            params.dimAmount = 0f;
            //修改gravity
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            window.setAttributes(params);
        }
    }

    private void showToast(String msg) {
        ToastUtil.showToast(getContext(), msg);
    }
}
