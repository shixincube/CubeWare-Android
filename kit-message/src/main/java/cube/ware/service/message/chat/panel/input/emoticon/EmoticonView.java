//package cube.ware.service.message.chat.panel.input.emoticon;
//
//import android.content.Context;
//import android.support.v4.view.PagerAdapter;
//import android.support.v4.view.ViewPager.OnPageChangeListener;
//import android.text.TextUtils;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.GridView;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.PopupWindow;
//
//import com.common.utils.utils.ScreenUtil;
//import com.common.utils.utils.glide.GlideUtil;
//import com.common.utils.utils.log.LogUtil;
//import com.shixinyun.expression.ui.collect.CollectActivity;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//
//import cube.ware.service.message.R;
//import cube.ware.service.message.chat.panel.input.emoticon.adapter.EmoticonAdapter;
//import cube.ware.service.message.chat.panel.input.emoticon.adapter.StickerAdapter;
//import cube.ware.service.message.chat.panel.input.emoticon.manager.EmoticonManager;
//import cube.ware.service.message.chat.panel.input.emoticon.manager.StickerManager;
//
///**
// * 贴图显示viewpager
// *
// * @author Wangxx
// * @date 2017/1/4
// */
//public class EmoticonView {
//
//    private SlideViewPager emoticonPager;
//    private LinearLayout   pageNumberLayout;
//    private int            pageCount;     // 总页数
//    private boolean        isLongClick;
//    private int pageCrrent = 0;
//
//    /**
//     * 动态表情相关
//     */
//    private int                                gvLocation[]    = new int[2];    // FaceGridView在屏幕中的位置，对应x,y值
//    private ArrayList<HashMap<String, Object>> listDynamicFace = new ArrayList<HashMap<String, Object>>();
//
//    /**
//     * 每页显示的数量，Adapter保持一致.
//     */
//    public static final int EMOJI_PER_PAGE    = 27; // 最后一个是删除键
//    public static final int CHARTLET_PER_PAGE = 8;
//
//    private Context                  context;
//    private EmoticonSelectedListener listener;
//    private EmoticonViewPaperAdapter pagerAdapter = new EmoticonViewPaperAdapter(); //系统的表情都在emoticonPager里 EmoticonViewPaperAdapter为其Adapter
//
//    /**
//     * 所有表情贴图支持横向滑动切换
//     */
//    private int categoryIndex;                           // 当套贴图的在picker中的索引
//    private boolean isDataInitialized = false;             // 数据源只需要初始化一次,变更时再初始化
//    private List<StickerType> categoryDataList;       // 表情贴图数据源
//    private List<Integer>     categoryPageNumberList;           // 每套表情贴图对应的页数
//    private int[] pagerIndexInfo = new int[2];           // 0：category index；1：pager index in category
//    private EmoticonTypeChangedListener typeChangedCallback; // 横向滑动切换时回调picker
//    private StickerManager              manager;
//
//    public EmoticonView(Context context, EmoticonSelectedListener mlistener, SlideViewPager mCurPage, LinearLayout pageNumberLayout) {
//        this.context = context;
//        this.listener = mlistener;
//        this.pageNumberLayout = pageNumberLayout;
//        this.emoticonPager = mCurPage;
//
//        emoticonPager.addOnPageChangeListener(new OnPageChangeListener() {
//
//            @Override
//            public void onPageSelected(int position) {
//                if (categoryDataList != null) {
//                    // 显示所有贴图表情
//                    setCurStickerPage(position);
//                    if (typeChangedCallback != null) {
//                        int currentTypeChecked = pagerIndexInfo[0];// 当前那种类别被选中
//                        typeChangedCallback.onTypeChanged(currentTypeChecked); //通知上层选中的类型变了
//                    }
//                }
//                else {
//                    // 只显示表情
//                    setCurEmotionPage(position);
//                }
//            }
//
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//            }
//        });
//        emoticonPager.setAdapter(pagerAdapter);
//        emoticonPager.setOffscreenPageLimit(1); //设置预加载页面数量
//    }
//
//    public void setPageCrrent(int index) {
//        this.pageCrrent = index;
//    }
//
//    public void showStickers(int index) {
//        // 判断是否需要变化
//        if (isDataInitialized && getPagerInfo(emoticonPager.getCurrentItem()) != null && pagerIndexInfo[0] == index && pagerIndexInfo[1] == 0) {
//            return;
//        }
//
//        this.categoryIndex = index;
//        showStickerGridView();
//    }
//
//    public void showEmojis() {
//        showEmojiGridView();
//    }
//
//    private int getCategoryPageCount(StickerType category) {
//        if (category == null) {
//            return (int) Math.ceil(EmoticonManager.getDisplayCount() / (float) EMOJI_PER_PAGE);
//        }
//        else {
//            if (category.hasStickerList()) {
//                List<StickerItem> chartlets = category.getStickerList();
//                return (int) Math.ceil(chartlets.size() / (float) CHARTLET_PER_PAGE);
//            }
//            else {
//                return 1;
//            }
//        }
//    }
//
//    /**
//     * @param page
//     * @param pageCount 当前有8几个点
//     */
//    private void setCurPage(int page, int pageCount) {
//        int hasCount = pageNumberLayout.getChildCount();  //当前有几个点
//        int forMax = Math.max(hasCount, pageCount);
//
//        ImageView imgCur = null;
//        for (int i = 0; i < forMax; i++) {
//            if (pageCount <= hasCount) {  //当前点比需要的点多 把多出来的点Gone
//                if (i >= pageCount) {
//                    pageNumberLayout.getChildAt(i).setVisibility(View.GONE);
//                    continue;
//                }
//                else {
//                    imgCur = (ImageView) pageNumberLayout.getChildAt(i);
//                }
//            }
//            else { //当前点比需要的点少 向pageNumberLayout里动态添加点
//                if (i < hasCount) {
//                    imgCur = (ImageView) pageNumberLayout.getChildAt(i);
//                }
//                else {
//                    imgCur = new ImageView(context);
//                    imgCur.setBackgroundResource(R.drawable.selector_view_pager_indicator);
//                    pageNumberLayout.addView(imgCur);
//                }
//            }
//
//            imgCur.setId(i);
//            imgCur.setSelected(i == page); // 判断当前页码来更新
//            imgCur.setVisibility(View.VISIBLE);
//        }
//    }
//
//    /**
//     * ******************************** 表情  *******************************
//     */
//    private void showEmojiGridView() {
//        pageCount = (int) Math.ceil(EmoticonManager.getDisplayCount() / (float) EMOJI_PER_PAGE);
//        pagerAdapter.notifyDataSetChanged();
//        resetEmotionPager();
//    }
//
//    private void resetEmotionPager() {
//        setCurEmotionPage(0);
//        emoticonPager.setCurrentItem(0, false);
//    }
//
//    //只显示表情条件下更新选中的那个点
//    private void setCurEmotionPage(int position) {
//        setCurPage(position, pageCount);
//    }
//
//    /**
//     * 表情的点击监听
//     */
//    public OnItemClickListener emojiListener = new OnItemClickListener() {
//        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//            int position = emoticonPager.getCurrentItem();
//            int pos = position; // 如果只有表情，那么用默认方式计算
//            if (categoryDataList != null && categoryPageNumberList != null) {
//                // 包含贴图
//                getPagerInfo(position);
//                pos = pagerIndexInfo[1];
//            }
//
//            int index = arg2 + pos * EMOJI_PER_PAGE;
//
//            if (listener != null) {
//                int count = EmoticonManager.getDisplayCount();
//                if (arg2 == EMOJI_PER_PAGE || index >= count) {
//                    listener.onEmoticonSelected("/DEL");
//                }
//                else {
//                    String text = EmoticonManager.getDisplayText((int) arg3);
//                    if (!TextUtils.isEmpty(text)) {
//                        listener.onEmoticonSelected(text);
//                    }
//                }
//            }
//        }
//    };
//
//    /**
//     * ******************************** 贴图  *******************************
//     */
//    //显示某套特定的表情
//    public void showStickerGridView() {
//        initData();
//        pagerAdapter.notifyDataSetChanged();
//
//        // 计算起始的pager index
//        int position = 0;
//        for (int i = 0; i < categoryPageNumberList.size(); i++) {
//            if (i == categoryIndex) {
//                break;
//            }
//            position += categoryPageNumberList.get(i);
//        }
//
//        setCurStickerPage(position);
//        emoticonPager.setCurrentItem(position, false);
//    }
//
//    /**
//     * 初始化表情面板数据，这里包括了贴图表情的
//     */
//    private void initData() {
//        if (isDataInitialized) {//数据已经初始化，未变动不重新加载数据
//            return;
//        }
//        if (categoryDataList == null) {
//            categoryDataList = new ArrayList<>();
//        }
//        if (categoryPageNumberList == null) {
//            categoryPageNumberList = new ArrayList<>();
//        }
//        categoryDataList.clear();
//        categoryPageNumberList.clear();
//
//        // emoji表情
//        categoryDataList.add(null);
//        categoryPageNumberList.add(getCategoryPageCount(null));
//
//        // 贴图，  这个是贴图表情，内部实现相对简单，有时间你可以看看
//        this.manager = StickerManager.getInstance(context);
//        List<StickerType> categories = manager.getCategories();
//        categoryDataList.addAll(categories);
//        for (StickerType c : categories) {
//            categoryPageNumberList.add(getCategoryPageCount(c));
//        }
//
//        pageCount = 0;//总页数
//        for (Integer count : categoryPageNumberList) {
//            pageCount += count;
//        }
//        isDataInitialized = true;
//    }
//
//    public void refresh(){
//        isDataInitialized = false;
//        // 贴图
//        this.manager.refreshStickerType(new StickerManager.QueryLocalEmoji() {
//            @Override
//            public void onSuccess() {
//                showStickerGridView();
//            }
//        });
//    }
//
//    // 给定pager中的索引，返回categoryIndex和positionInCategory
//    private int[] getPagerInfo(int position) {
//        if (categoryDataList == null || categoryPageNumberList == null) {
//            return pagerIndexInfo;
//        }
//
//        int cIndex = categoryIndex;
//        int startIndex = 0;
//        int pageNumberPerCategory = 0;
//        for (int i = 0; i < categoryPageNumberList.size(); i++) { //categoryPageNumberList是所有表情占的页数的集合
//            pageNumberPerCategory = categoryPageNumberList.get(i);
//            if (position < startIndex + pageNumberPerCategory) {
//                cIndex = i;
//                break;
//            }
//            startIndex += pageNumberPerCategory;
//        }
//
//        this.pagerIndexInfo[0] = cIndex;
//        this.pagerIndexInfo[1] = position - startIndex;
//
//        return pagerIndexInfo;
//    }
//
//    //除了表情外带其他贴图的条件下 更新选中的点
//    private void setCurStickerPage(int position) {
//        getPagerInfo(position);
//        this.pageNumberLayout.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    int categoryIndex = pagerIndexInfo[0]; //这套表情在全部表情中的索引
//                    int pageIndexInCategory = pagerIndexInfo[1];   //这一页是这套表情的第几页
//                    int categoryPageCount = categoryPageNumberList.get(categoryIndex); //这套表情总共需要几个点
//                    setCurPage(pageIndexInCategory, categoryPageCount);
//                }catch (Exception e){
//                    isDataInitialized = false;
//                    showStickerGridView();
//                }
//            }
//        }, 300);
//    }
//
//    public void setCategoryChangCheckedCallback(EmoticonTypeChangedListener callback) {
//        this.typeChangedCallback = callback;
//    }
//
//    private OnItemClickListener stickerListener = new OnItemClickListener() {
//        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//            int position = emoticonPager.getCurrentItem();
//            getPagerInfo(position);
//            int cIndex = pagerIndexInfo[0];
//            int pos = pagerIndexInfo[1];
//            StickerType c = categoryDataList.get(cIndex);
//            int index = arg2 + pos * CHARTLET_PER_PAGE; // 在category中贴图的index
//
//            if (index >= c.getStickerList().size()) {
//                LogUtil.i("sticker", "index " + index + " larger than size " + c.getStickerList().size());
//                return;
//            }
//            if (listener != null) {
//                List<StickerItem> chartlets = c.getStickerList();
//                StickerItem sticker = chartlets.get(index);
//                StickerType real = manager.getCategory(sticker.getCategory());
//                if (real == null) {
//                    return;
//                }
//                if(sticker.getPath().contains("ic_emoji_collect_setting")){
//                    CollectActivity.start(context, CubeSpUtil.getCubeUser().getCubeId());
//                    return;
//                }
//                if(real.getType() == 1){
//                    listener.onStickerSelected(sticker);
//                }
//                else{
//                    listener.onCollectSelected(sticker.getPath());
//                }
//            }
//        }
//    };
//
//    /**
//     * ***************************** PagerAdapter ****************************
//     */
//    private class EmoticonViewPaperAdapter extends PagerAdapter implements View.OnTouchListener, AdapterView.OnItemLongClickListener {
//        private GifView     mGifView;
//        private ImageView   mImageView;
//        private PopupWindow mPopWindow;
//
//        @Override
//        public boolean isViewFromObject(View view, Object object) {
//            return view == object;
//        }
//
//        @Override
//        public int getCount() {
//            return pageCount == 0 ? 1 : pageCount;
//        }
//
//        @Override
//        public Object instantiateItem(ViewGroup container, int position) {
//            if (mPopWindow == null) {
//                View contentView = LayoutInflater.from(context).inflate(R.layout.emoticon_popuplayout, null);
//                mGifView = (GifView) contentView.findViewById(R.id.gif_iv);
//                mImageView = (ImageView) contentView.findViewById(R.id.image_iv);
//                mPopWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
//                mPopWindow.getContentView().measure(0, 0);
//            }
//            StickerType category;
//            int pos;
//            if (categoryDataList != null && categoryDataList.size() > 0 && categoryPageNumberList != null && categoryPageNumberList.size() > 0) {
//                // 显示所有贴图&表情
//                getPagerInfo(position);
//                int cIndex = pagerIndexInfo[0];
//                category = categoryDataList.get(cIndex);
//                pos = pagerIndexInfo[1];
//            }
//            else {
//                // 只显示表情
//                category = null;
//                pos = position;
//            }
//
//            if (category == null) {
//                pageNumberLayout.setVisibility(View.VISIBLE);
//                GridView gridView = new GridView(context);
//                gridView.setOnItemClickListener(emojiListener);
//                gridView.setAdapter(new EmoticonAdapter(context, pos * EMOJI_PER_PAGE));
//                gridView.setNumColumns(7);
//                gridView.setHorizontalSpacing(5);
//                gridView.setVerticalSpacing(5);
//                gridView.setGravity(Gravity.CENTER);
//                gridView.setSelector(R.drawable.selector_emoticon_item);
//                gridView.setOnTouchListener(this);
//                gridView.setOnItemLongClickListener(this);
//                container.addView(gridView);
//                return gridView;
//            }
//            else {
//                pageNumberLayout.setVisibility(View.VISIBLE);
//                GridView gridView = new GridView(context);
//                gridView.setPadding(10, 0, 10, 0);
//                gridView.setOnItemClickListener(stickerListener);
//                gridView.setAdapter(new StickerAdapter(context, category, pos * CHARTLET_PER_PAGE));
//                gridView.setNumColumns(4);
//                gridView.setHorizontalSpacing(5);
//                gridView.setGravity(Gravity.CENTER);
//                gridView.setSelector(R.drawable.selector_emoticon_item);
//                gridView.setOnTouchListener(this);
//                gridView.setOnItemLongClickListener(this);
//                container.addView(gridView);
//                return gridView;
//            }
//        }
//
//        @Override
//        public void destroyItem(ViewGroup container, int position, Object object) {
//            View layout = (View) object;
//            container.removeView(layout);
//        }
//
//        public int getItemPosition(Object object) {
//            return POSITION_NONE;
//        }
//
//        /**
//         * 之所以监听滑动事件是为了滑到其他位置的时候能够弹出相应的popwindow
//         *
//         * @param v
//         * @param event
//         *
//         * @return
//         */
//        @Override
//        public boolean onTouch(View v, MotionEvent event) {
//            switch (event.getAction()) {
//                case MotionEvent.ACTION_MOVE:
//                    if (!isLongClick) {
//                        return false;
//                    }
//                    // 获取触摸点相对于屏幕的坐标
//                    float eventX = gvLocation[0] + event.getX();
//                    float eventY = gvLocation[1] + event.getY();
//
//                    int x = 0;
//                    int y = 0;
//                    int size = listDynamicFace.size();
//                    for (int i = 0; i < size; i++) {
//                        HashMap<String, Object> map = listDynamicFace.get(i);
//                        x = Integer.parseInt((String) map.get("x"));
//                        y = Integer.parseInt((String) map.get("y"));
//                        if (eventX >= x && eventX <= x + ScreenUtil.dip2px(40) && eventY >= y && eventY <= y + ScreenUtil.dip2px(40)) {
//                            if (i == mGifView.getId()) {
//                                // 如果是正在播放的gif则返回
//                                return false;
//                            }
//                            mGifView.setId(i);
//                            // 更新显示的gif
//                            if(map.get("imgRes") instanceof String){
//                                // 获取图片资源
//                                String path = (String) map.get("imgRes");
//                                View view = (View) map.get("imgView");
//                                showPopUp(view, path);
//                            }
//                            else{
//                                // 获取图片资源
//                                StickerItem item = (StickerItem) map.get("imgRes");
//                                View view = (View) map.get("imgView");
//                                showPopUp(view, item);
//                            }
//                            break;
//                        }
//                    }
//                    break;
//                case MotionEvent.ACTION_UP:
//                    dismiss();
//                    break;
//            }
//            return false;
//        }
//
//        @Override
//        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//            int count = EmoticonManager.getDisplayCount();
//            if (position == EmoticonView.EMOJI_PER_PAGE || id == count) {
//                //最后一个删除键不显示长按不显示popwindow
//                return false;
//            }
//
//            // 获取屏幕坐标
//            emoticonPager.getLocationInWindow(gvLocation);
//            // 清空列表
//            listDynamicFace.clear();
//            /**
//             * 获取可见的表情列表
//             */
//            int start = parent.getFirstVisiblePosition();
//            int end = parent.getLastVisiblePosition();
//            for (int i = 0; i <= end - start; i++) {
//                HashMap<String, Object> map = new HashMap<String, Object>();
//                View v = parent.getChildAt(i);
//                int location[] = new int[2];
//                if (null == v) {
//                    continue;
//                }
//                // 获取该表情在窗口中的坐标
//                v.getLocationInWindow(location);
//                if (v == view) {
//                    mGifView.setId(i);
//                }
//                map.put("x", location[0] + "");
//                map.put("y", location[1] + "");
//                if (i != EMOJI_PER_PAGE) {
//                    map.put("imgRes", parent.getItemAtPosition(i + start));
//                }
//                map.put("imgView", v);
//                listDynamicFace.add(map);
//            }
//            if(parent.getItemAtPosition(position) instanceof String){
//                // 获取图片资源
//                String path = (String) parent.getItemAtPosition(position);
//                showPopUp(view, path);
//            }
//            else{
//                // 获取图片资源
//                StickerItem item = (StickerItem) parent.getItemAtPosition(position);
//                showPopUp(view, item);
//            }
//            // 返回true可禁止gridView滚动
//            return true;
//        }
//
//        private void showPopUp(View v, StickerItem item){
//            if(item == null){
//                dismiss();
//                return;
//            }
//            if(item.getPath().contains("ic_emoji_collect_setting")){
//                return;
//            }
//            try {
//                FileInputStream fis;
//                if(new File(item.getPath() + ".gif").exists()){
//                    mGifView.setVisibility(View.VISIBLE);
//                    mImageView.setVisibility(View.GONE);
//                    fis = new FileInputStream(new File(item.getPath() + ".gif"));
//                    mGifView.setGifResource(fis);
//                }
//                else{
//                    mGifView.setVisibility(View.GONE);
//                    mImageView.setVisibility(View.VISIBLE);
//                    GlideUtil.loadImage(new File(item.getPath()), context, mImageView);
//                }
//                isLongClick = true;
//                if (mPopWindow != null) {
//                    mPopWindow.dismiss();
//                }
//                int[] location = new int[2];
//                v.getLocationOnScreen(location);
//                mPopWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0] - 60, location[1] - (v.getHeight() * 4) / 3  - 30);
//                emoticonPager.setSlide(false);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        private void showPopUp(View v, String path) {
//            if (TextUtils.isEmpty(path)) {
//                dismiss();
//                return;
//            }
//            try {
//                InputStream is = context.getAssets().open(path);
//                isLongClick = true;
//                if (mPopWindow != null) {
//                    mPopWindow.dismiss();
//                }
//                mGifView.setVisibility(View.VISIBLE);
//                mImageView.setVisibility(View.GONE);
//                mGifView.setGifResource(is);
//                int[] location = new int[2];
//                v.getLocationOnScreen(location);
//                mPopWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0], location[1] - (v.getHeight() * 4) / 3);
//                emoticonPager.setSlide(false);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        private void dismiss() {
//            isLongClick = false;
//            mPopWindow.dismiss();
//            emoticonPager.setSlide(true);
//        }
//    }
//}
