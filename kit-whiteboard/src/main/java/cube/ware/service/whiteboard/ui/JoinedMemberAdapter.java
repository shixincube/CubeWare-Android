package cube.ware.service.whiteboard.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.common.utils.utils.glide.GlideUtil;
import cube.ware.core.CubeCore;
import cube.ware.service.whiteboard.R;
import java.util.List;

/**
 * author: kun .
 * date:   On 2018/8/30
 */
public class JoinedMemberAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context      mContext;
    private List<String> mUserList;

    public JoinedMemberAdapter(Context context, List<String> userList) {
        mContext = context;
        mUserList = userList;
    }

    public void removeDate(String cubeId) {
        if (mUserList != null && mUserList.contains(cubeId)) {
            mUserList.remove(cubeId);
            notifyDataSetChanged();
        }
    }

    public void addDate(String cubeId) {
        if (mUserList != null && !mUserList.contains(cubeId)) {
            mUserList.add(cubeId);
            notifyDataSetChanged();
        }
    }

    public void addListDate(List<String> cubeIds) {
        if (cubeIds != null) {
            for (int i = 0; i < cubeIds.size(); i++) {
                if (mUserList != null && !mUserList.contains(cubeIds.get(i))) {
                    mUserList.add(cubeIds.get(i));
                }
            }
            notifyDataSetChanged();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.show_members_layout, null);
        return new MyHolder(inflate);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof MyHolder) {
            GlideUtil.loadCircleImage(CubeCore.getInstance().getAvatarUrl() + mUserList.get(position), mContext, ((MyHolder) holder).mIvHeader, DiskCacheStrategy.NONE, true, R.drawable.default_head_user);
            ((MyHolder) holder).mIvHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "id:" + mUserList.get(position), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        ImageView mIvHeader;

        MyHolder(View itemView) {
            super(itemView);
            mIvHeader = itemView.findViewById(R.id.item_image);
        }
    }
}
