package cube.ware.widget.recyclerview;

import android.support.annotation.DrawableRes;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

/**
 * RecyclerView的基础的ViewHolder
 *
 * 具体用法请参考：https://github.com/pengzhenjin/BaseRecyclerViewAdapter
 *
 * @author PengZhenjin
 * @date 2016-12-26
 */
public class BaseRecyclerViewHolder extends RecyclerView.ViewHolder {

    private View              mConvertView;
    private SparseArray<View> mViews;

    public BaseRecyclerViewHolder(View itemView) {
        super(itemView);
        this.mConvertView = itemView;
        this.mViews = new SparseArray<>();
    }

    /**
     * 获取convertView
     *
     * @return
     */
    public View getConvertView() {
        return this.mConvertView;
    }

    /**
     * 获取view
     *
     * @param viewId
     * @param <T>
     *
     * @return
     */
    public <T extends View> T getView(int viewId) {
        View view = this.mViews.get(viewId);
        if (view == null) {
            view = this.mConvertView.findViewById(viewId);
            this.mViews.put(viewId, view);
        }
        return (T) view;
    }

    /**
     * 设置是否可见
     *
     * @param viewId
     * @param isVisible
     *
     * @return
     */
    public BaseRecyclerViewHolder setVisible(int viewId, boolean isVisible) {
        View view = this.getView(viewId);
        view.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        return this;
    }

    /**
     * 设置backgroundResource
     *
     * @param viewId
     * @param backgroundResourceId
     *
     * @return
     */
    public BaseRecyclerViewHolder setBackgroundResource(int viewId, @DrawableRes int backgroundResourceId) {
        View view = this.getView(viewId);
        view.setBackgroundResource(backgroundResourceId);
        return this;
    }

    /**
     * 设置backgroundColor
     *
     * @param viewId
     * @param backgroundColor
     *
     * @return
     */
    public BaseRecyclerViewHolder setBackgroundColor(int viewId, int backgroundColor) {
        View view = this.getView(viewId);
        view.setBackgroundColor(backgroundColor);
        return this;
    }
}
