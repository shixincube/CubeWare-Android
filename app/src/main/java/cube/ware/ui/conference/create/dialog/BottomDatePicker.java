package cube.ware.ui.conference.create.dialog;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.common.mvp.base.BaseBottomDialog;
import com.common.utils.utils.ToastUtil;
import com.contrarywind.listener.OnItemSelectedListener;
import com.contrarywind.view.WheelView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cube.ware.R;
import cube.ware.ui.conference.create.CreateConferenceActivity;

/**
 * author: kun .
 * date:   On 2018/9/19
 */
public class BottomDatePicker extends BaseBottomDialog implements View.OnClickListener {

    private TextView mBtnCancel;
    private TextView mBtnComplete;
    private WheelView mPvOptions;
    private List<String> mOptionsItems;

    public static BottomDatePicker getInstance(){
        BottomDatePicker bottomDatePicker=new BottomDatePicker();
        return bottomDatePicker;
    }

    @Override
    protected void initView(View inflate, Bundle savedInstanceState) {
        mBtnCancel = inflate.findViewById(R.id.btn_cancel);
        mBtnComplete = inflate.findViewById(R.id.btn_complete);
        mPvOptions = inflate.findViewById(R.id.wheelview);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {

        mPvOptions.setCyclic(false);
        mOptionsItems = new ArrayList<>();
        mOptionsItems= Arrays.asList(getActivity().getResources().getStringArray(R.array.time));

        mPvOptions.setAdapter(new ArrayWheelAdapter(mOptionsItems));
        mPvOptions.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                ((CreateConferenceActivity)getActivity()).onDurationSelect(mOptionsItems.get(index),index);
            }
        });
        mBtnCancel.setOnClickListener(this);
        mBtnComplete.setOnClickListener(this);
    }

    @Override
    public int setLayout() {
        return R.layout.bootom_date_picker;
    }

    @Override
    public void onStart() {
        super.onStart();
        //得到dialog对应的window
        Window window = getDialog().getWindow();
        if (window != null) {
            //得到LayoutParams
            WindowManager.LayoutParams params = window.getAttributes();
            params.dimAmount =0.3f;
            //修改gravity
            params.gravity = Gravity.BOTTOM;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            window.setAttributes(params);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_cancel:
            case R.id.btn_complete:
                getDialog().dismiss();
                break;
        }
    }
}
