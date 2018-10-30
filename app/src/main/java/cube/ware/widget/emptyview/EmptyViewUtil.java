package cube.ware.widget.emptyview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * @author PengZhenjin
 * @date 2016/8/2
 */
public class EmptyViewUtil {

    public static void setEmptyView(AdapterView listView) {
        if (EmptyView.hasDefaultConfig()) {
            EmptyView.getConfig().bindView(listView);
        }
        else {
            EmptyView view = genSimpleEmptyView(listView);
            listView.setEmptyView(view);
        }
    }

    @NonNull
    private static EmptyView genSimpleEmptyView(View view) {
        EmptyView emptyView = new EmptyView(view.getContext(), null);
        ViewParent parent = view.getParent();
        if (parent instanceof ViewGroup && parent instanceof SwipeRefreshLayout) {//刷新布局
            ViewParent parent1 = parent.getParent();
            if (parent1 instanceof FrameLayout) {
                ((FrameLayout) parent1).addView(emptyView, 0);
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) emptyView.getLayoutParams();
                lp.height = -1;
                lp.gravity = Gravity.CENTER;
                emptyView.setLayoutParams(lp);
            }
            else if (parent1 instanceof RelativeLayout) {
                ((RelativeLayout) parent1).addView(emptyView, 0);
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) emptyView.getLayoutParams();
                lp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                emptyView.setLayoutParams(lp);
            }
        }
        else if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).addView(emptyView);
            if (parent instanceof LinearLayout) {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) emptyView.getLayoutParams();
                lp.height = -1;
                lp.gravity = Gravity.CENTER;
                emptyView.setLayoutParams(lp);
            }
            else if (parent instanceof RelativeLayout) {
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) emptyView.getLayoutParams();
                lp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                emptyView.setLayoutParams(lp);
            }
            else if (parent instanceof FrameLayout) {
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) emptyView.getLayoutParams();
                lp.height = -1;
                lp.gravity = Gravity.CENTER;
                emptyView.setLayoutParams(lp);
            }
        }
        return emptyView;
    }

    public static void setEmptyView(AdapterView listView, EmptyViewBuilder builder) {
        builder.bindView(listView);
    }

    public static final class EmptyViewBuilder {
        private Context                mContext;
        private ViewGroup.LayoutParams layoutParams;
        private String                 emptyText;
        private int                    iconSrc;
        private Drawable               iconDrawable;
        private int emptyTextColor = -1;
        private int emptyTextSize;
        private ShowType mShowIcon   = ShowType.DEFAULT;
        private ShowType mShowText   = ShowType.DEFAULT;
        private ShowType mShowButton = ShowType.DEFAULT;
        private View.OnClickListener mAction;
        private int mBtnBackColorResId = -1;
        private int mBtnTextColorResId = -1;
        private String actionText;

        private int mMinCount = 0;//显示空布局时的条目个数

        private enum ShowType {
            DEFAULT, SHOW, HIDE
        }

        public static EmptyViewBuilder getInstance(Context context) {
            return new EmptyViewBuilder(context);
        }

        public void bindView(final AdapterView listView) {
            final EmptyView emptyView = genSimpleEmptyView(listView);
            removeExistEmptyView(listView);
            listView.setEmptyView(emptyView);
            setEmptyViewStyle(emptyView);
        }

        public EmptyView bindView(final RecyclerView recyclerView) {
            final EmptyView emptyView = genSimpleEmptyView(recyclerView);
            final RecyclerView.Adapter adapter = recyclerView.getAdapter();
            if (adapter != null) {
                RecyclerView.AdapterDataObserver observer = new RecyclerView.AdapterDataObserver() {
                    @Override
                    public void onChanged() {
                        super.onChanged();
                        if (adapter.getItemCount() > mMinCount) {
                            //recyclerView.setVisibility(View.VISIBLE);
                            emptyView.setVisibility(View.GONE);
                        }
                        else {
                            //recyclerView.setVisibility(View.GONE);
                            emptyView.setVisibility(View.VISIBLE);
                        }
                    }
                };
                adapter.registerAdapterDataObserver(observer);
                observer.onChanged();
                emptyView.setVisibility(View.VISIBLE);
            }
            else {
                throw new RuntimeException("This RecyclerView has no mAdapter, you must call setAdapter first!");
            }
            setEmptyViewStyle(emptyView);
            return emptyView;
        }

        public void bindView(final RecyclerView recyclerView, final View emptyView) {
            //final EmptyView emptyView = genSimpleEmptyView(recyclerView);
            final RecyclerView.Adapter adapter = recyclerView.getAdapter();
            if (adapter != null) {
                RecyclerView.AdapterDataObserver observer = new RecyclerView.AdapterDataObserver() {
                    @Override
                    public void onChanged() {
                        super.onChanged();
                        if (adapter.getItemCount() > mMinCount) {
                            recyclerView.setVisibility(View.VISIBLE);
                            emptyView.setVisibility(View.GONE);
                        }
                        else {
                            recyclerView.setVisibility(View.GONE);
                            emptyView.setVisibility(View.VISIBLE);
                        }
                    }
                };
                adapter.registerAdapterDataObserver(observer);
                observer.onChanged();
            }
            else {
                throw new RuntimeException("This RecyclerView has no mAdapter, you must call setAdapter first!");
            }
            //setEmptyViewStyle(emptyView);
        }

        private void setEmptyViewStyle(EmptyView emptyView) {
            boolean canShowText = (mShowText == ShowType.SHOW || (mShowText == ShowType.DEFAULT && EmptyView.hasDefaultConfig() && EmptyView.getConfig().mShowText == ShowType.SHOW));
            emptyView.setShowText(canShowText);
            if (canShowText) {
                if (emptyTextColor != -1) {
                    emptyView.setTextColor(emptyTextColor);
                }
                else if (EmptyView.hasDefaultConfig() && EmptyView.getConfig().emptyTextColor != -1) {
                    emptyView.setTextColor(EmptyView.getConfig().emptyTextColor);
                }

                if (emptyTextSize != 0) {
                    emptyView.setTextSize(emptyTextSize);
                }
                else if (EmptyView.hasDefaultConfig() && EmptyView.getConfig().emptyTextSize != 0) {
                    emptyView.setTextSize(EmptyView.getConfig().emptyTextSize);
                }

                if (!TextUtils.isEmpty(emptyText)) {
                    emptyView.setEmptyText(emptyText);
                }
                else if (EmptyView.hasDefaultConfig() && !TextUtils.isEmpty(EmptyView.getConfig().emptyText)) {
                    emptyView.setEmptyText(EmptyView.getConfig().emptyText);
                }
            }

            boolean canShowIcon = (mShowIcon == ShowType.SHOW || (mShowIcon == ShowType.DEFAULT && EmptyView.hasDefaultConfig() && EmptyView.getConfig().mShowIcon == ShowType.SHOW));
            emptyView.setShowIcon(canShowIcon);
            if (canShowIcon) {
                if (iconSrc != 0) {
                    emptyView.setIcon(iconSrc);
                }
                else if (EmptyView.hasDefaultConfig() && EmptyView.getConfig().iconSrc != 0) {
                    emptyView.setIcon(EmptyView.getConfig().iconSrc);
                }

                if (iconDrawable != null) {
                    emptyView.setIcon(iconDrawable);
                }
                else if (EmptyView.hasDefaultConfig() && EmptyView.getConfig().iconDrawable != null) {
                    emptyView.setIcon(EmptyView.getConfig().iconDrawable);
                }
            }

            boolean canShowButton = (mShowButton == ShowType.SHOW || (mShowButton == ShowType.DEFAULT && EmptyView.hasDefaultConfig() && EmptyView.getConfig().mShowButton == ShowType.SHOW));
            emptyView.setShowButton(canShowButton);
            if (canShowButton) {
                if (!TextUtils.isEmpty(actionText)) {
                    emptyView.setActionText(actionText);
                }
                else if (EmptyView.hasDefaultConfig() && !TextUtils.isEmpty(EmptyView.getConfig().actionText)) {
                    emptyView.setActionText(EmptyView.getConfig().actionText);
                }

                if (mBtnBackColorResId != -1) {
                    emptyView.setActionBackground(mBtnBackColorResId);
                }
                if (mBtnTextColorResId != -1) {
                    emptyView.setActionColor(mBtnTextColorResId);
                }
                if (mAction != null) {
                    emptyView.setAction(mAction);
                }
                else if (EmptyView.hasDefaultConfig() && EmptyView.getConfig().mAction != null) {
                    emptyView.setAction(EmptyView.getConfig().mAction);
                }
            }

            if (layoutParams != null) {
                emptyView.setLayoutParams(layoutParams);
            }
            else if (EmptyView.hasDefaultConfig() && EmptyView.getConfig().layoutParams != null) {
                emptyView.setLayoutParams(EmptyView.getConfig().layoutParams);
            }
        }

        private EmptyViewBuilder(Context context) {
            this.mContext = context;
        }

        public EmptyViewBuilder setEmptyText(String text) {
            this.emptyText = text;
            return this;
        }

        public EmptyViewBuilder setEmptyText(int textResID) {
            this.emptyText = mContext.getString(textResID);
            return this;
        }

        public EmptyViewBuilder setEmptyTextColor(int color) {
            this.emptyTextColor = color;
            return this;
        }

        public EmptyViewBuilder setEmptyTextSize(int textSize) {
            this.emptyTextSize = textSize;
            return this;
        }

        public EmptyViewBuilder setEmptyTextSizePX(int textSizePX) {
            this.emptyTextSize = px2sp(mContext, textSizePX);
            return this;
        }

        public EmptyViewBuilder setIconSrc(int iconSrc) {
            this.iconSrc = iconSrc;
            this.iconDrawable = null;
            return this;
        }

        public EmptyViewBuilder setIconDrawable(Drawable iconDrawable) {
            this.iconDrawable = iconDrawable;
            this.iconSrc = 0;
            return this;
        }

        public EmptyViewBuilder setLayoutParams(ViewGroup.LayoutParams layoutParams) {
            this.layoutParams = layoutParams;
            return this;
        }

        public EmptyViewBuilder setShowIcon(boolean mShowIcon) {
            this.mShowIcon = mShowIcon ? ShowType.SHOW : ShowType.HIDE;
            return this;
        }

        public EmptyViewBuilder setShowText(boolean showText) {
            this.mShowText = showText ? ShowType.SHOW : ShowType.HIDE;
            return this;
        }

        public EmptyViewBuilder setShowButton(boolean showButton) {
            this.mShowButton = showButton ? ShowType.SHOW : ShowType.HIDE;
            return this;
        }

        public EmptyViewBuilder setAction(View.OnClickListener onClickListener) {
            this.mAction = onClickListener;
            return this;
        }

        public EmptyViewBuilder setActionText(String actionText) {
            this.actionText = actionText;
            return this;
        }

        public EmptyViewBuilder setActionBackground(int resId) {
            this.mBtnBackColorResId = resId;
            return this;
        }

        public EmptyViewBuilder setActionColor(int textResId) {
            this.mBtnTextColorResId = textResId;
            return this;
        }

        /**
         * 设置有多少个条目时显示空布局，默认为0个条目时显示
         *
         * @param count 显示空布局时的条目个数
         *
         * @return
         */
        public EmptyViewBuilder setItemCountToShowEmptyView(int count) {
            this.mMinCount = count;
            return this;
        }
    }

    private static void removeExistEmptyView(AdapterView listView) {
        if (listView.getEmptyView() != null) {
            ViewParent rootView = listView.getParent();
            if (rootView instanceof ViewGroup) {
                ((ViewGroup) rootView).removeView(listView.getEmptyView());
            }
        }
    }

    public static int sp2px(Context context, float spValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, context.getResources().getDisplayMetrics());
    }

    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }
}
