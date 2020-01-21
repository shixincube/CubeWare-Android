package cube.ware.service.conference.create;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.common.sdk.RouterUtil;
import com.common.utils.utils.glide.GlideUtil;
import cube.service.user.model.User;
import cube.ware.core.CubeConstants;
import cube.ware.core.CubeCore;
import cube.ware.service.conference.R;
import java.util.List;

/**
 * author: kun .
 * date:   On 2018/9/3
 */
public class CreateAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context    mContext;
    private List<User> users;

    public CreateAdapter(Context context, List<User> users) {
        this.mContext = context;
        this.users = users;
    }

    public void setData(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.show_conference_members_layout, null);
        return new MyHolder(inflate);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof MyHolder) {
            ((MyHolder) holder).mIvHeader.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            if (users.get(position).cubeId.equals("default")) {
                GlideUtil.loadCircleImage(R.drawable.add_member, mContext, ((MyHolder) holder).mIvHeader, R.drawable.add_member);
            }
            else {
                GlideUtil.loadCircleImage(CubeCore.getInstance().getAvatarUrl() + users.get(position).cubeId, mContext, ((MyHolder) holder).mIvHeader, DiskCacheStrategy.NONE, true, R.drawable.default_head_user);
            }

            if (users.get(position).cubeId.equals("default")) {
                ((MyHolder) holder).mIvHeader.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("select_type", 1);//首次创建
                        RouterUtil.navigation(CubeConstants.Router.SelectMemberActivity, bundle);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        ImageView mIvHeader;

        MyHolder(View itemView) {
            super(itemView);
            mIvHeader = itemView.findViewById(R.id.item_image);
        }
    }
}
