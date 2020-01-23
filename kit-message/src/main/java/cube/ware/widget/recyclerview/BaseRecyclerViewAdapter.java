package cube.ware.widget.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView基础的adapter
 *
 * 具体用法请参考：https://github.com/pengzhenjin/BaseRecyclerViewAdapter
 *
 * @author PengZhenjin
 * @date 2016-12-26
 */
public abstract class BaseRecyclerViewAdapter<T, VH extends BaseRecyclerViewHolder> extends RecyclerView.Adapter<VH> {

    protected static final int TYPE_HEADER_VIEW  = 0x10000001;
    protected static final int TYPE_FOOTER_VIEW  = 0x10000002;
    protected static final int TYPE_LOADING_VIEW = 0x10000003;
    protected static final int TYPE_EMPTY_VIEW   = 0x10000004;

    /**
     * 上下文
     */
    protected Context mContext;

    /**
     * 数据集
     */
    protected List<T> mDataList;

    /**
     * item布局文件id
     */
    protected int mLayoutResId;

    /**
     * Item点击事件监听器
     */
    private OnItemClickListener mOnItemClickListener = null;

    /**
     * 头部视图
     */
    private View mHeaderView;

    /**
     * 底部视图
     */
    private View mFooterView;

    /**
     * 加载更多视图
     */
    private View mLoadingView;

    /**
     * 构造方法
     *
     * @param dataList
     */
    public BaseRecyclerViewAdapter(List<T> dataList) {
        this(0, dataList);
    }

    /**
     * 构造方法
     *
     * @param layoutResId
     * @param dataList
     */
    public BaseRecyclerViewAdapter(int layoutResId, List<T> dataList) {
        if (layoutResId != 0) {
            this.mLayoutResId = layoutResId;
        }
        this.mDataList = dataList == null ? new ArrayList<T>() : dataList;
    }

    /**
     * 获取数据列表
     *
     * @return
     */
    public List<T> getDataList() {
        return this.mDataList;
    }

    /**
     * 获取一条数据
     *
     * @param position
     *
     * @return
     */
    public T getData(int position) {
        return (this.mDataList == null || this.mDataList.isEmpty()) ? null : this.mDataList.get(position);
    }

    /**
     * 刷新数据列表
     *
     * @param dataList
     *
     * @return
     */
    public void refreshDataList(List<T> dataList) {
        this.mDataList = dataList == null ? new ArrayList<T>() : dataList;
        this.notifyDataSetChanged();
    }

    /**
     * 添加数据列表
     *
     * @param dataList
     */
    public void addDataList(List<T> dataList) {
        if (this.mDataList != null) {
            this.mDataList.addAll(dataList);
            this.notifyItemRangeInserted(this.mDataList.size() - dataList.size() + this.getHeaderViewCount(), dataList.size());
        }
    }

    /**
     * 添加一条数据
     *
     * @param data
     *
     * @return
     */
    public void addData(T data) {
        if (this.mDataList != null) {
            this.mDataList.add(data);
            this.notifyItemInserted(this.mDataList.size() + this.getHeaderViewCount());
        }
    }

    /**
     * 添加一条数据
     *
     * @param position
     * @param data
     */
    public void addData(int position, T data) {
        if (this.mDataList != null) {
            this.mDataList.add(position, data);
            this.notifyItemInserted(position + this.getHeaderViewCount());
        }
    }

    /**
     * 设置一条数据
     *
     * @param index
     * @param data
     */
    public void setData(int index, T data) {
        if (this.mDataList != null) {
            this.mDataList.set(index, data);
            this.notifyItemChanged(index + this.getHeaderViewCount());
        }
    }

    /**
     * 移除一条数据
     *
     * @param position
     */
    public void removeData(int position) {
        if (this.mDataList != null) {
            this.mDataList.remove(position);
            this.notifyItemRemoved(position + this.getHeaderViewCount());
        }
    }

    /**
     * 添加头部view
     *
     * @param headerView
     */
    public void addHeaderView(View headerView) {
        this.mHeaderView = headerView;
        this.notifyItemInserted(0);
    }

    /**
     * 添加底部view
     *
     * @param footerView
     */
    public void addFooterView(View footerView) {
        this.mFooterView = footerView;
        this.notifyItemInserted(this.getItemCount());
    }

    /**
     * 添加正在加载view
     *
     * @param loadingView
     */
    public void addLoadingView(View loadingView) {
        this.mLoadingView = loadingView;
        this.notifyItemInserted(this.getItemCount());
    }

    /**
     * 移除头部view
     *
     * @param headerView
     */
    public void removeHeaderView(View headerView) {
        this.mHeaderView = null;
        this.notifyItemRemoved(0);
    }

    /**
     * 移除尾部view
     *
     * @param footerView
     */
    public void removeFooterView(View footerView) {
        this.mFooterView = null;
        this.notifyItemRemoved(this.getItemCount());
    }

    /**
     * 移除正在加载view
     *
     * @param loadingView
     */
    public void removeLoadingView(View loadingView) {
        this.mLoadingView = null;
        this.notifyItemRemoved(this.getItemCount());
    }

    /**
     * 获取头部view
     *
     * @return
     */
    public View getHeaderView() {
        return this.mHeaderView;
    }

    /**
     * 获取尾部view
     *
     * @return
     */
    public View getFooterView() {
        return this.mFooterView;
    }

    /**
     * 获取正在加载View
     *
     * @return
     */
    public View getLoadingView() {
        return this.mLoadingView;
    }

    /**
     * 获取头部view的总数
     *
     * @return
     */
    public int getHeaderViewCount() {
        return this.isHasHeaderView() ? 1 : 0;
    }

    /**
     * 获取尾部view的总数
     *
     * @return
     */
    public int getFooterViewCount() {
        return this.isHasFooterView() ? 1 : 0;
    }

    /**
     * 获取正在加载view的总数
     *
     * @return
     */
    public int getLoadingViewCount() {
        return this.isHasLoadingView() ? 1 : 0;
    }

    /**
     * 是否有头部view
     *
     * @return
     */
    private boolean isHasHeaderView() {
        return mHeaderView != null;
    }

    /**
     * 是否有尾部view
     *
     * @return
     */
    private boolean isHasFooterView() {
        return mFooterView != null;
    }

    /**
     * 是否有正在加载View
     *
     * @return
     */
    private boolean isHasLoadingView() {
        return mLoadingView != null;
    }

    @Override
    public int getItemCount() {
        return this.getHeaderViewCount() + this.mDataList.size() + this.getFooterViewCount() + this.getLoadingViewCount();
    }

    @Override
    public int getItemViewType(int position) {
        if (this.isHasHeaderView() && position == 0) {
            return TYPE_HEADER_VIEW;
        }
        if (this.isHasLoadingView() && position == getItemCount() - 1) {
            return TYPE_LOADING_VIEW;
        }
        if (this.isHasFooterView() && position == getItemCount() - 2) {
            return TYPE_FOOTER_VIEW;
        }

        return this.getDefaultItemViewType(position);
    }

    /**
     * 获取默认的item类型
     *
     * @param position
     *
     * @return
     */
    protected int getDefaultItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        VH baseViewHolder = null;
        this.mContext = parent.getContext();
        switch (viewType) {
            case TYPE_HEADER_VIEW:
                baseViewHolder = this.createBaseViewHolder(this.mHeaderView);
                break;
            case TYPE_FOOTER_VIEW:
                baseViewHolder = this.createBaseViewHolder(this.mFooterView);
                break;
            case TYPE_LOADING_VIEW:
                baseViewHolder = this.createBaseViewHolder(this.mLoadingView);
                break;
            default:
                baseViewHolder = this.createDefaultViewHolder(parent, viewType);
                this.initItemClickListener(parent, baseViewHolder, viewType);
        }
        return baseViewHolder;
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        int viewType = holder.getItemViewType();
        switch (viewType) {
            case TYPE_HEADER_VIEW:
                break;
            case TYPE_FOOTER_VIEW:
                break;
            case TYPE_LOADING_VIEW:
                break;
            default:
                this.convert(holder, this.mDataList.get(holder.getLayoutPosition() - this.getHeaderViewCount()), position);
                break;
        }
    }

    /**
     * 获取itemView
     *
     * @param layoutResId
     * @param parent
     *
     * @return
     */
    protected View getItemView(int layoutResId, ViewGroup parent) {
        return LayoutInflater.from(this.mContext).inflate(layoutResId, parent, false);
    }

    /**
     * 创建默认的viewHolder
     *
     * @param parent
     * @param viewType
     *
     * @return
     */
    protected VH createDefaultViewHolder(ViewGroup parent, int viewType) {
        return createBaseViewHolder(parent, this.mLayoutResId);
    }

    /**
     * 创建基础的viewHolder
     *
     * @param parent
     * @param layoutResId
     *
     * @return
     */
    protected VH createBaseViewHolder(ViewGroup parent, int layoutResId) {
        return createBaseViewHolder(this.getItemView(layoutResId, parent));
    }

    /**
     * 创建基础的viewHolder
     *
     * @param view
     *
     * @return
     */
    protected VH createBaseViewHolder(View view) {
        return (VH) new BaseRecyclerViewHolder(view);
    }

    /**
     * 转换数据
     *
     * @param viewHolder
     * @param data
     * @param position
     */
    protected abstract void convert(VH viewHolder, T data, int position);

    public void addOrUpdateItemWithOutAnimator(int i, RecyclerView mContentRv) {
        closeRecyclerViewAnimator(mContentRv);
        notifyItemChanged(i);
    }

    public static void closeRecyclerViewAnimator(RecyclerView recyclerView) {
        recyclerView.getItemAnimator().setAddDuration(0);
        recyclerView.getItemAnimator().setChangeDuration(0);
        recyclerView.getItemAnimator().setMoveDuration(0);
        recyclerView.getItemAnimator().setRemoveDuration(0);
    }

    /**
     * 点击事件
     *
     * @author PengZhenjin
     * @date 2016-12-27
     */
    public interface OnItemClickListener {
        void onItemClick(View view, BaseRecyclerViewHolder viewHolder, int position);

        boolean onItemLongClick(View view, BaseRecyclerViewHolder viewHolder, int position);
    }

    /**
     * 设置点击事件
     *
     * @param listener
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    /**
     * 初始化item事件
     *
     * @param parent
     * @param viewHolder
     * @param viewType
     */
    private void initItemClickListener(final ViewGroup parent, final BaseRecyclerViewHolder viewHolder, int viewType) {
        viewHolder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    int position = viewHolder.getLayoutPosition() - getHeaderViewCount();
                    mOnItemClickListener.onItemClick(v, viewHolder, position);
                }
            }
        });
        viewHolder.getConvertView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnItemClickListener != null) {
                    int position = viewHolder.getLayoutPosition() - getHeaderViewCount();
                    return mOnItemClickListener.onItemLongClick(v, viewHolder, position);
                }
                return false;
            }
        });
    }

    /**
     * 设置StaggeredGridLayoutManager占满全格
     *
     * @param holder
     */
    protected void setFullSpan(RecyclerView.ViewHolder holder) {
        if (holder.itemView.getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
            params.setFullSpan(true);
        }
    }
}
