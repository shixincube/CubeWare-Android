package cube.ware.ui.login.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.common.utils.utils.glide.GlideUtil;
import cube.ware.AppManager;
import cube.ware.R;
import cube.ware.data.room.model.CubeUser;
import java.util.List;

public class LVCubeIdListAdapter extends BaseAdapter {
    private Context        mContext;
    private List<CubeUser> mUsers;

    public LVCubeIdListAdapter(Context context, List<CubeUser> users) {
        mContext = context;
        mUsers = users;
    }

    @Override
    public int getCount() {
        return mUsers.size();
    }

    @Override
    public Object getItem(int position) {
        return mUsers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.cubeid_list_item, null);
        ;
        TextView tvName = convertView.findViewById(R.id.tv_name);
        TextView tvCubeId = convertView.findViewById(R.id.tv_cubeid);
        ImageView cvAvator = convertView.findViewById(R.id.iv_header);
        tvName.setText(mUsers.get(position).getDisplayName());
        tvCubeId.setText(mUsers.get(position).getCubeId());
        GlideUtil.loadCircleImage(AppManager.getAvatarUrl() + mUsers.get(position).getCubeId(), mContext, cvAvator, DiskCacheStrategy.NONE, true, R.drawable.default_head_user);
        return convertView;
    }
}
