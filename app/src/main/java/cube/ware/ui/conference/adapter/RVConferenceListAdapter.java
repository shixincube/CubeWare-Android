package cube.ware.ui.conference.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import cube.ware.R;
import cube.ware.ui.conference.eventbus.CreateData;

/**
 * author: kun .
 * des：会议列表
 * date:   On 2018/9/8
 */
public class RVConferenceListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context mContext;
    List<CreateData> mCreateData;
    OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public RVConferenceListAdapter(Context context, List<CreateData> mCreateData) {
        mContext = context;
        this.mCreateData=mCreateData;
    }
    public void setData(List<CreateData> conferenceList){
        this.mCreateData=conferenceList;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.conference_item_layout, null);
        return new MyHolder(inflate);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof MyHolder){
            ((MyHolder) holder).mTvTitle.setText(mCreateData.get(position).getConference().displayName);
            String dataTime = dataToString(new Date(mCreateData.get(position).getConference().startTime), mContext);
            ((MyHolder) holder).mTvTime.setText(dataTime);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onClickListener(mCreateData.get(position));
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mOnItemClickListener.onLongClickListener(mCreateData.get(position), position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCreateData.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{
        TextView mTvTime;
        TextView mTvTitle;
        public MyHolder(View itemView) {
            super(itemView);
            mTvTime=itemView.findViewById(R.id.tv_time);
            mTvTitle=itemView.findViewById(R.id.tv_conference_name);
        }
    }

    public String dataToString(Date date, Context context){
        List<String> list= Arrays.asList(context.getResources().getStringArray(R.array.week));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM月dd日");
        SimpleDateFormat hours = new SimpleDateFormat("HH:mm");
        return simpleDateFormat.format(date)+" "+list.get(date.getDay())+" "+hours.format(date);
    }

    public interface OnItemClickListener{
        void onClickListener(CreateData createData);
        void onLongClickListener(CreateData createData, int position);
    }
}
