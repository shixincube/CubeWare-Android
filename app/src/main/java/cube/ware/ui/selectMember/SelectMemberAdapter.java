package cube.ware.ui.selectMember;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.common.utils.utils.ToastUtil;
import com.common.utils.utils.glide.GlideUtil;
import cube.ware.R;
import cube.ware.core.CubeCore;
import cube.ware.data.room.model.CubeUser;
import cube.ware.ui.contact.adapter.SelectContactsAdapter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * author: kun .
 * date:   On 2018/9/5
 */
public class SelectMemberAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /**
     * 存储已选中的信息
     * key: cube
     * value: name
     */
    public  LinkedHashMap<String, CubeUser> mSelectedCubeMap = new LinkedHashMap<>();
    public  int                             mUserLimit       = -1;   // 选择人数限制
    private List<String>                    mNotChecked      = new ArrayList<>(); //不能选择的联系人
    private Context                         mContext;
    private List<CubeUser>                  mCubeUsers       = new ArrayList<>();
    private OnItemSelectedShowToast         mOnItemSelectedShowToast;

    public SelectMemberAdapter(Context context, LinkedHashMap<String, CubeUser> selectedCubeMap, List<CubeUser> mCubeUsers, int userLimit) {
        mContext = context;
        mUserLimit = userLimit;
        mSelectedCubeMap = selectedCubeMap;
        this.mCubeUsers = mCubeUsers;
    }

    public void setOnItemSelectedShowToast(OnItemSelectedShowToast onItemSelectedShowToast) {
        mOnItemSelectedShowToast = onItemSelectedShowToast;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_select_contact, null);
        return new MyHolder(inflate);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyHolder) {
            convert((MyHolder) holder, mCubeUsers.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mCubeUsers.size();
    }

    protected void convert(MyHolder helper, final CubeUser item) {
        RelativeLayout mItemLayout = helper.getView(R.id.friend_root_layout);
        final CheckBox mContactsCb = helper.getView(R.id.friend_cb);
        ImageView mContactsHeadIv = helper.getView(R.id.friend_head_iv);
        TextView mContactsNameTv = helper.getView(R.id.friend_name_tv);

        if (item != null) {
            GlideUtil.loadCircleImage(CubeCore.getInstance().getAvatarUrl() + item.getCubeId(), mContext, mContactsHeadIv, DiskCacheStrategy.NONE, true, R.drawable.default_head_user);
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
                        if (mSelectedCubeMap.size() + mNotChecked.size() > 8) {
                            mOnItemSelectedShowToast.onItemSelectedToast();
                            return;
                        }
                        if (mUserLimit != -1 && mSelectedCubeMap.size() > mUserLimit - 1) {
                            ToastUtil.showToast(mContext, mContext.getString(R.string.select_forward_user, mUserLimit));
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

    public SelectContactsAdapter.OnItemSelectedListener mOnItemSelectedListener;

    public void setOnItemSelectedListener(SelectContactsAdapter.OnItemSelectedListener listener) {
        this.mOnItemSelectedListener = listener;
    }

    class MyHolder extends RecyclerView.ViewHolder {
        View              itemView;
        SparseArray<View> views = new SparseArray<>();

        public MyHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
        }

        public <T extends View> T getView(@IdRes int viewId) {
            View view = views.get(viewId);
            if (view == null) {
                view = itemView.findViewById(viewId);
                views.put(viewId, view);
            }
            return (T) view;
        }
    }

    /**
     * item选中状态监听器
     */
    public interface OnItemSelectedListener {

        void onItemSelected(String selectedCube);

        void onItemUnselected(String selectedCube);

        void onSelectedList(LinkedHashMap<String, CubeUser> list);
    }

    /**
     * item选中状态监听器
     */
    public interface OnItemSelectedShowToast {

        void onItemSelectedToast();
    }
}
