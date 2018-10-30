package cube.ware.ui.group.adapter;

import android.text.TextUtils;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.common.utils.utils.glide.GlideUtil;

import cube.service.group.model.Member;
import cube.ware.AppConstants;
import cube.ware.R;

/**
 * Created by dth
 * Des:
 * Date: 2018/8/29.
 */

public class GroupMembersAdapter extends BaseQuickAdapter<Member,BaseViewHolder>{

    public GroupMembersAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, Member item) {

        int layoutPosition = helper.getLayoutPosition();
        ImageView ivFace = helper.getView(R.id.iv_face);
        if (layoutPosition == getData().size() - 1) {

            ivFace.setImageResource(R.drawable.add_member);
            helper.setText(R.id.tv_name,"");
        } else {
            GlideUtil.loadCircleImage(AppConstants.AVATAR_URL+item.cubeId,mContext,ivFace,R.drawable.default_head_user);
            helper.setText(R.id.tv_name, TextUtils.isEmpty(item.remarkName) ? TextUtils.isEmpty(item.displayName) ? item.cubeId : item.displayName : item.remarkName);
        }
    }
}
