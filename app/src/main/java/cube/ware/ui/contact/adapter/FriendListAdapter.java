package cube.ware.ui.contact.adapter;

import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.common.utils.utils.glide.GlideUtil;

import cube.ware.AppConstants;
import cube.ware.R;
import cube.ware.data.room.model.CubeUser;

/**
 * Created by dth
 *
 *
 * Des:
 * Date: 2018/8/28.
 */

public class FriendListAdapter extends BaseQuickAdapter<CubeUser,BaseViewHolder>{

    public FriendListAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, CubeUser item) {
        ImageView avatarView = helper.getView(R.id.iv_contact_head);
        GlideUtil.loadCircleImage(AppConstants.AVATAR_URL+item.getCubeId(), mContext, avatarView, DiskCacheStrategy.NONE, true, R.drawable.default_head_user);
        helper.setText(R.id.tv_username, TextUtils.isEmpty(item.getDisplayName()) ? item.getCubeId() : item.getDisplayName())
                .setText(R.id.tv_cubeId, item.getCubeId());
    }
}
