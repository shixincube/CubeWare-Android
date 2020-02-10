package cube.ware.ui.mine.dialog;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.common.mvp.base.BaseBottomDialog;

import cube.ware.R;
import cube.ware.ui.mine.avatar.ModifyAvatarActivity;

public class BottomChooseDialog extends BaseBottomDialog {

    private TextView mTvChoose;
    private TextView mTvCancel;

    public static BottomChooseDialog getInstance(){
        return new BottomChooseDialog();
    }
    @Override
    protected void initView(View inflate, Bundle savedInstanceState) {
        mTvChoose = inflate.findViewById(R.id.tv_choose_photo);
        mTvCancel = inflate.findViewById(R.id.tv_cancel);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {
        mTvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        mTvChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ModifyAvatarActivity)getActivity()).selectImageFromLocal();
                getDialog().dismiss();
            }
        });
    }

    @Override
    public int setLayout() {
        return R.layout.choose_dialog;
    }
}
