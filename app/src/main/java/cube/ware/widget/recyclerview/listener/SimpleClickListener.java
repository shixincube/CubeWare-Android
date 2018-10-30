package cube.ware.widget.recyclerview.listener;

import android.view.View;

import cube.ware.widget.recyclerview.BaseRecyclerViewAdapter;
import cube.ware.widget.recyclerview.BaseRecyclerViewHolder;

/**
 * 简单的点击事件
 *
 * @author PengZhenjin
 * @date 2016-12-27
 */
public class SimpleClickListener implements BaseRecyclerViewAdapter.OnItemClickListener {

    @Override
    public void onItemClick(View view, BaseRecyclerViewHolder viewHolder, int position) {

    }

    @Override
    public boolean onItemLongClick(View view, BaseRecyclerViewHolder viewHolder, int position) {
        return false;
    }
}


