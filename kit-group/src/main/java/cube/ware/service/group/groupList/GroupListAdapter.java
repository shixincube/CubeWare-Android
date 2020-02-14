package cube.ware.service.group.groupList;

import android.text.TextUtils;
import android.widget.ImageView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.common.utils.glide.GlideUtil;
import cube.service.group.Group;
import cube.ware.core.CubeCore;
import cube.ware.service.group.R;
import java.util.List;

/**
 * Created by dth
 * Des:
 * Date: 2018/8/28.
 */

public class GroupListAdapter extends BaseQuickAdapter<Group, BaseViewHolder> {

    public GroupListAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, Group item) {
        ImageView avatarView = helper.getView(R.id.iv_contact_head);
        GlideUtil.loadCircleImage(CubeCore.getInstance().getAvatarUrl() + item.getGroupId(), mContext, avatarView, R.drawable.default_head_group);
        helper.setText(R.id.tv_username, item.getDisplayName()).setText(R.id.tv_cubeId, item.getGroupId());
    }

    public int findPosition(String groupId) {
        List<Group> groups = getData();
        if (!groups.isEmpty()) {
            for (int i = 0; i < groups.size(); i++) {
                if (TextUtils.equals(groups.get(i).getGroupId(), groupId)) {
                    return i;
                }
            }
        }
        return -1;
    }
}
