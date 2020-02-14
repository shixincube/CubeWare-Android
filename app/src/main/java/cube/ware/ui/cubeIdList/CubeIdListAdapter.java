package cube.ware.ui.cubeIdList;

import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.common.utils.glide.GlideUtil;
import cube.ware.R;
import cube.ware.data.room.model.CubeUser;

public class CubeIdListAdapter extends BaseQuickAdapter<CubeUser, BaseViewHolder> {

    public CubeIdListAdapter() {
        super(R.layout.cubeid_list_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, CubeUser item) {
        TextView tvName = helper.itemView.findViewById(R.id.tv_name);
        TextView tvCubeId = helper.itemView.findViewById(R.id.tv_cubeid);
        ImageView avatar = helper.itemView.findViewById(R.id.iv_header);
        tvName.setText(item.getDisplayName());
        tvCubeId.setText(item.getCubeId());
        GlideUtil.loadCircleImage(item.getAvatar(), helper.itemView.getContext(), avatar, DiskCacheStrategy.NONE, true, R.drawable.default_head_user);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
