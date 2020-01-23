package cube.ware.widget.recyclerview;

import android.support.annotation.LayoutRes;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.util.List;

import cube.ware.widget.recyclerview.entity.MultiItemEntity;

/**
 * 多类型的itemAdapter
 *
 * 具体用法请参考：https://github.com/pengzhenjin/BaseRecyclerViewAdapter
 *
 * @author PengZhenjin
 * @date 2016-12-28
 */
public abstract class BaseMultiItemAdapter<T extends MultiItemEntity, VH extends BaseRecyclerViewHolder> extends BaseRecyclerViewAdapter<T, VH> {

    private SparseArray<Integer> mLayouts;

    public BaseMultiItemAdapter(List<T> dataList) {
        super(dataList);
    }

    @Override
    protected int getDefaultItemViewType(int position) {
        int numHeaders = getHeaderViewCount();
        if (position >= numHeaders) {
            int adjPosition = position - numHeaders;
            int adapterCount = super.mDataList.size();
            if (adjPosition < adapterCount) {
                T item = super.mDataList.get(adjPosition);
                if (item != null) {
                    return item.getItemType();
                }
            }
        }
        return super.getDefaultItemViewType(position);
    }

    @Override
    protected VH createDefaultViewHolder(ViewGroup parent, int viewType) {
        return super.createBaseViewHolder(parent, this.getLayoutId(viewType));
    }

    /**
     * 添加item类型
     *
     * @param viewType
     * @param layoutResId
     */
    protected void addItemType(int viewType, @LayoutRes int layoutResId) {
        if (this.mLayouts == null) {
            this.mLayouts = new SparseArray<>();
        }
        this.mLayouts.put(viewType, layoutResId);
    }

    /**
     * 获取布局id
     *
     * @param viewType
     *
     * @return
     */
    private int getLayoutId(int viewType) {
        return this.mLayouts.get(viewType);
    }
}
