package cube.ware.ui.whiteboard.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.common.utils.utils.ToastUtil;
import com.common.utils.utils.glide.GlideUtil;
import com.common.utils.utils.log.LogUtil;

import java.util.List;

import cube.service.user.model.User;
import cube.ware.R;

/**
 * author: kun .
 * date:   On 2018/8/30
 */
public class RVWaitJoinedMemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context mContext;
    List<User> mUserList;

    public RVWaitJoinedMemAdapter(Context context, List<User> userList) {
        mContext = context;
        mUserList = userList;
    }

    public void removeDate(User user){
        if(mUserList!=null && isContains(user)){
            remove(user);
            notifyDataSetChanged();
        }
    }

    public void addDate(User user){
        if(mUserList!=null&&!isContains(user)){
            mUserList.add(user);
            notifyDataSetChanged();
        }
    }

    public void addListDate(List<User> users){
        for (int i = 0; i < users.size(); i++) {
            if(mUserList!=null&&!isContains(users.get(i))){
                mUserList.add(users.get(i));
            }
        }
        notifyDataSetChanged();
    }




    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.show_members_layout, null);
        return new MyHolder(inflate);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof RVJoinedMemAdapter.MyHolder){
            GlideUtil.loadCircleImage(mUserList.get(position).avatar,mContext,((MyHolder) holder).mIvHeader,R.drawable.default_head_user);
            ((MyHolder) holder).mIvHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastUtil.showToast(mContext,"id:"+mUserList.get(position).cubeId);
                }
            });
        }
    }
    @Override
    public int getItemCount() {
        return mUserList.size();
    }
    public class MyHolder extends RecyclerView.ViewHolder{
        ImageView mIvHeader;
        public MyHolder(View itemView) {
            super(itemView);
            mIvHeader=itemView.findViewById(R.id.item_image);
        }
    }
    private boolean isContains(User user){
        if(mUserList!=null){
            for (int i = 0; i < mUserList.size(); i++) {
                if(mUserList.get(i).cubeId.equals(user.cubeId)){
                    return true;
                }
            }
        }
        return false;
    }
    //删除下标，删除对象要出错
    private void remove(User user){
        if(mUserList!=null){
            for (int i = 0; i < mUserList.size(); i++) {
                if(mUserList.get(i).cubeId.equals(user.cubeId)){
                    mUserList.remove(i);
                }
            }
        }
    }
}
