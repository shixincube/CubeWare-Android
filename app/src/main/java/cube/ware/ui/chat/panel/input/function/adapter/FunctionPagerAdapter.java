package cube.ware.ui.chat.panel.input.function.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import cube.ware.R;
import cube.ware.ui.chat.panel.input.function.BaseFunction;

/**
 * 功能视图适配器
 *
 * @author Wangxx
 * @date 2017/1/3
 */
public class FunctionPagerAdapter extends PagerAdapter {

    private final int ITEM_COUNT_VIEW = 8;

    private final Context context;

    private final List<BaseFunction> actions;
    private final int                gridViewCount;

    public FunctionPagerAdapter(Context context, List<BaseFunction> actions) {
        this.context = context;
        this.actions = new ArrayList<>(actions);
        this.gridViewCount = (actions.size() + ITEM_COUNT_VIEW - 1) / ITEM_COUNT_VIEW;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        int end = (position + 1) * ITEM_COUNT_VIEW > actions.size() ? actions.size() : (position + 1) * ITEM_COUNT_VIEW;
        List<BaseFunction> subBaseActions = actions.subList(position * ITEM_COUNT_VIEW, end);

        GridView gridView = new GridView(context);
        gridView.setAdapter(new FunctionGridviewAdapter(context, subBaseActions));
        gridView.setNumColumns(4);
        gridView.setSelector(R.color.transparent);
        gridView.setColumnWidth(0);
        gridView.setHorizontalSpacing(0);
        gridView.setVerticalSpacing(0);
        gridView.setGravity(Gravity.CENTER);
        gridView.setTag(position);
        gridView.setOnItemClickListener(new GridView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int index = ((Integer) parent.getTag()) * ITEM_COUNT_VIEW + position;
                actions.get(index).onClick();
            }
        });

        container.addView(gridView);
        return gridView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // TODO
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getCount() {
        return gridViewCount;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
