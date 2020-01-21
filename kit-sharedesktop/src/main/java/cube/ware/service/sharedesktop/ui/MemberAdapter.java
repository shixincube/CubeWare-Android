package cube.ware.service.sharedesktop.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.common.utils.utils.glide.GlideUtil;
import cube.ware.service.sharedesktop.R;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zzy on 2018/8/30.
 */

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder> {

    private List<String> membersList;
    private Context      mContext;

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView item_head;

        public ViewHolder(View view) {
            super(view);
            item_head = view.findViewById(R.id.item_image);
        }
    }

    public MemberAdapter(List<String> memberList, Context context) {
        this.membersList = memberList;
        this.mContext = context;
    }

    /**
     * 刷新数据列表
     *
     * @param dataList
     *
     * @return
     */
    public void refreshDataList(List<String> dataList) {
        this.membersList = dataList == null ? new ArrayList<String>() : dataList;
        this.notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_members_layout, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        GlideUtil.loadCircleImage(membersList.get(position), mContext, holder.item_head, DiskCacheStrategy.NONE, true, R.drawable.default_head_user);
    }

    @Override
    public int getItemCount() {
        return membersList.size();
    }
}
