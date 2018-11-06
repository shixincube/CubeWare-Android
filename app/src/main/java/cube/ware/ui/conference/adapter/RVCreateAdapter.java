package cube.ware.ui.conference.adapter;

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
import java.util.List;
import cube.service.user.model.User;
import cube.ware.AppConstants;
import cube.ware.R;

/**
 * author: kun .
 * date:   On 2018/9/3
 */
public class RVCreateAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context mContext;
    List<User> users;
    public RVCreateAdapter(Context context, List<User> users) {
        this.mContext=context;
        this.users=users;
    }
    public void setData(List<User> users){
        this.users=users;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.show_conference_members_layout, null);
        return new MyHolder(inflate);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof MyHolder){
            ((MyHolder) holder).mIvHeader.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            if(users.get(position).cubeId.equals("default")){
                GlideUtil.loadCircleImage(R.drawable.add_member,mContext,((MyHolder) holder).mIvHeader,R.drawable.add_member);
            }else {
                GlideUtil.loadCircleImage(AppConstants.AVATAR_URL+users.get(position).cubeId,mContext,((MyHolder) holder).mIvHeader, DiskCacheStrategy.NONE,true,R.drawable.default_head_user);
            }

            if(users.get(position).cubeId.equals("default")){
                ((MyHolder) holder).mIvHeader.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle=new Bundle();
                        bundle.putInt("select_type",1);//首次创建
                        RouterUtil.navigation(AppConstants.Router.SelectMemberActivity,bundle);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{
        ImageView mIvHeader;
        public MyHolder(View itemView) {
            super(itemView);
            mIvHeader=itemView.findViewById(R.id.item_image);
        }
    }
}
