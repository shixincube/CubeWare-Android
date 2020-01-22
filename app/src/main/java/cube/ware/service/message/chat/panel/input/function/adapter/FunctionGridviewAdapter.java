package cube.ware.service.message.chat.panel.input.function.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cube.ware.R;
import cube.ware.service.message.chat.panel.input.function.BaseFunction;

public class FunctionGridviewAdapter extends BaseAdapter {

    private Context context;

    private List<BaseFunction> baseActions;

    public FunctionGridviewAdapter(Context context, List<BaseFunction> baseActions) {
        this.context = context;
        this.baseActions = baseActions;
    }

    @Override
    public int getCount() {
        return baseActions.size();
    }

    @Override
    public Object getItem(int position) {
        return baseActions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemlayout;
        if (convertView == null) {
            itemlayout = LayoutInflater.from(context).inflate(R.layout.item_function_grid_layout, null);
        }
        else {
            itemlayout = convertView;
        }
        AbsListView.LayoutParams param = new AbsListView.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, parent.getHeight() / 2);//传入自己需要的宽高
        itemlayout.setLayoutParams(param);

        BaseFunction viewHolder = baseActions.get(position);
        itemlayout.findViewById(R.id.imageView).setBackgroundResource(viewHolder.getIconResId());
        ((TextView) itemlayout.findViewById(R.id.textView)).setText(context.getString(viewHolder.getTitleId()));
        return itemlayout;
    }
}

