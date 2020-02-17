package cube.ware.ui.contact.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.common.utils.ToastUtil;
import com.common.utils.glide.GlideUtil;
import cube.ware.AppManager;
import cube.ware.R;
import cube.ware.data.room.model.CubeUser;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by dth
 * Des:
 * Date: 2018/8/28.
 */

public class SelectContactsAdapter extends BaseQuickAdapter<CubeUser, BaseViewHolder> {

    /**
     * 存储已选中的信息
     * key: cube
     * value: name
     */
    public  LinkedHashMap<String, CubeUser> mSelectedCubeMap = new LinkedHashMap<>();
    public  int                             mUserLimit       = -1;   // 选择人数限制
    private List<String>                    mNotChecked      = new ArrayList<>();//不能选择的联系人

    public SelectContactsAdapter(int layoutResId) {
        super(layoutResId);
    }

    public SelectContactsAdapter(int layoutResId, LinkedHashMap<String, CubeUser> selectedCubeMap, int userLimit) {
        super(layoutResId);
        mSelectedCubeMap = selectedCubeMap;
        mUserLimit = userLimit;
    }

    @Override
    protected void convert(BaseViewHolder helper, CubeUser item) {
        RelativeLayout mItemLayout = helper.getView(R.id.friend_root_layout);
        final CheckBox mContactsCb = helper.getView(R.id.friend_cb);
        ImageView mContactsHeadIv = helper.getView(R.id.friend_head_iv);
        TextView mContactsNameTv = helper.getView(R.id.friend_name_tv);

        if (item != null) {

            GlideUtil.loadCircleImage(AppManager.getAvatarUrl() + item.getCubeId(), mContext, mContactsHeadIv, R.drawable.default_head_user);
            mContactsNameTv.setText(TextUtils.isEmpty(item.getDisplayName()) ? item.getCubeId() : item.getDisplayName());

            final String userCube = item.getCubeId();

            if (this.mSelectedCubeMap.containsKey(userCube)) {
                mContactsCb.setChecked(true);
            }
            else {
                mContactsCb.setChecked(false);
            }

            if (mNotChecked.contains(userCube)) {
                mItemLayout.setEnabled(false);
                mContactsCb.setEnabled(false);
            }
            else {
                mItemLayout.setEnabled(true);
                mContactsCb.setEnabled(true);
            }

            /**
             * 点击事件
             */
            mItemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSelectedCubeMap.containsKey(userCube)) {
                        mSelectedCubeMap.remove(userCube);
                        mContactsCb.setChecked(false);
                        if (mOnItemSelectedListener != null) {
                            mOnItemSelectedListener.onItemUnselected(userCube);
                        }
                    }
                    else {
                        if (mUserLimit != -1 && mSelectedCubeMap.size() > mUserLimit - 1) {
                            ToastUtil.showToast( mContext.getString(R.string.select_forward_user, mUserLimit));
                            return;
                        }
                        mSelectedCubeMap.put(userCube, item);
                        mContactsCb.setChecked(true);
                        if (mOnItemSelectedListener != null) {
                            mOnItemSelectedListener.onItemSelected(userCube);
                        }
                    }
                    if (null != mOnItemSelectedListener) {
                        mOnItemSelectedListener.onSelectedList(mSelectedCubeMap);
                    }
                }
            });
        }
    }

    public void setNotChecked(List<String> notChecked) {
        mNotChecked.clear();
        mNotChecked.addAll(notChecked);
    }

    public int getselectSize() {return mSelectedCubeMap.size();}

    /**
     * 移除已选中的成员
     *
     * @return
     */
    public void removeSelectedMember(String cubeId) {
        if (this.mSelectedCubeMap != null && !this.mSelectedCubeMap.isEmpty()) {
            this.mSelectedCubeMap.remove(cubeId);
            if (this.mOnItemSelectedListener != null) {
                this.mOnItemSelectedListener.onItemUnselected(cubeId);
                this.mOnItemSelectedListener.onSelectedList(mSelectedCubeMap);
            }
        }
        this.notifyDataSetChanged();
    }

    public OnItemSelectedListener mOnItemSelectedListener;

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.mOnItemSelectedListener = listener;
    }

    /**
     * item选中状态监听器
     */
    public interface OnItemSelectedListener {

        void onItemSelected(String selectedCube);

        void onItemUnselected(String selectedCube);

        void onSelectedList(LinkedHashMap<String, CubeUser> list);
    }
}
