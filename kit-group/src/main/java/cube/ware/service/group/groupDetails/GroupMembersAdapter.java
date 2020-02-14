package cube.ware.service.group.groupDetails;

import android.widget.ImageView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.common.utils.glide.GlideUtil;
import cube.ware.core.CubeCore;
import cube.ware.service.group.R;

/**
 * Created by dth
 * Des:
 * Date: 2018/8/29.
 */

public class GroupMembersAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public GroupMembersAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, String cubeId) {
        int layoutPosition = helper.getLayoutPosition();
        ImageView ivFace = helper.getView(R.id.iv_face);
        if (layoutPosition == getData().size() - 1) {
            ivFace.setImageResource(R.drawable.add_member);
            helper.setText(R.id.tv_name, "");
        }
        else {
            GlideUtil.loadCircleImage(CubeCore.getInstance().getAvatarUrl() + cubeId, mContext, ivFace, R.drawable.default_head_user);
            helper.setText(R.id.tv_name, cubeId);
        }
    }
}
