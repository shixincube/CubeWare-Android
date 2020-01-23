//package cube.ware.service.message.chat.panel.input.emoticon;
//
//import android.annotation.TargetApi;
//import android.content.Context;
//import android.os.Build;
//import android.os.Handler;
//import android.util.AttributeSet;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.FrameLayout;
//import android.widget.HorizontalScrollView;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//
//import com.common.utils.utils.ScreenUtil;
//import com.common.utils.utils.log.LogUtil;
//import com.shixinyun.expression.ui.center.CenterActivity;
//import com.shixinyun.expression.utils.rx.ExpressionEvent;
//import com.shixinyun.expression.utils.rx.RxManager;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//
//import cube.ware.service.message.R;
//import rx.functions.Action1;
//
///**
// * 贴图表情选择控件
// *
// * @author Wangxx
// * @date 2017/1/4
// */
//public class EmoticonPickerView extends FrameLayout implements EmoticonTypeChangedListener {
//
//    private EmoticonSelectedListener listener;
//    private Context                  context;
//    private boolean                  hasSticker;
//    public  EmoticonView             gifView;
//    private SlideViewPager           emoticonPager;
//    private LinearLayout             pagerNumberLayout;//页面布局
//    private HorizontalScrollView     scrollView;
//    private LinearLayout             tabView;
//    private ImageView                emoticonAdd; // 添加表情按钮
//    private int                      categoryIndex;
//    private Handler                  uiHandler;
//    private RxManager                mRxManager;
//    private boolean                  loaded          = false;
//    private ArrayList<String>        groupName       = new ArrayList<>();
//    private List<CubeEmojiStructure> emojiStructures = new ArrayList<>();
//    private int                      index           = 0;
//
//    public EmoticonPickerView(Context context) {
//        super(context);
//        init(context);
//    }
//
//    public EmoticonPickerView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        init(context);
//    }
//
//    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//    public EmoticonPickerView(Context context, AttributeSet attrs, int defStyle) {
//        super(context, attrs, defStyle);
//        init(context);
//    }
//
//    private void init(Context context) {
//        this.context = context;
//        this.uiHandler = new Handler(context.getMainLooper());
//        this.hasSticker = true;
//        this.mRxManager = new RxManager();
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        inflater.inflate(R.layout.cube_emoticon_layout, this);
//    }
//
//    @Override
//    protected void onFinishInflate() {
//        super.onFinishInflate();
//        setupEmojView();
//    }
//
//    protected void setupEmojView() {
//        emoticonPager = (SlideViewPager) findViewById(R.id.emoticon_pager);
//        pagerNumberLayout = (LinearLayout) findViewById(R.id.layout_bottom);
//        tabView = (LinearLayout) findViewById(R.id.emoticon_tab_view);
//        scrollView = (HorizontalScrollView) findViewById(R.id.emoticon_tab_view_container);
//        emoticonAdd = (ImageView) findViewById(R.id.emoticon_add_btn);
//        emoticonAdd.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                CenterActivity.start(context, CubeSpUtil.getToken(), CubeSpUtil.getCubeUser().getCubeId(), groupName);
//            }
//        });
//    }
//
//    @Override
//    protected void onDetachedFromWindow() {
//        super.onDetachedFromWindow();
//    }
//
//    public void show(EmoticonSelectedListener listener) {
//        setListener(listener);  // 表情的选择器监听
//        if (loaded) {  // 是否是加载过的
//            return;
//        }
//        loadStickers(); // TODO: 2018/5/25   加载贴图表情，1.0版本没做   下个版本会打开
//        loaded = true;
//        show();
//    }
//
//    public void setListener(EmoticonSelectedListener listener) {
//        if (listener != null) {
//            this.listener = listener;
//        }
//        else {
//            LogUtil.i("sticker", "listener is null");
//        }
//        this.mRxManager.on(ExpressionEvent.EVENT_REFRESH_EMOJI_VIEW, new Action1<Object>() {
//            @Override
//            public void call(Object o) {
//                loadStickers();
//                gifView.refresh();
//            }
//        });
//        this.mRxManager.on(ExpressionEvent.EVENT_DELETE_LOCAL, new Action1<Object>() {
//            @Override
//            public void call(Object o) {
//                loadStickers();
//                gifView.refresh();
//            }
//        });
//        this.mRxManager.on(ExpressionEvent.EVENT_CHANGE_COLLECT, new Action1<Object>() {
//            @Override
//            public void call(Object o) {
//                gifView.refresh();
//            }
//        });
//    }
//
//    public void onDestroy() {
//        if (mRxManager != null) {
//            mRxManager.clear();
//            mRxManager = null;
//        }
//    }
//
//    // 添加各个tab按钮
//    OnClickListener tabCheckListener = new OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            onEmoticonBtnChecked(v.getId());
//        }
//    };
//
//    private void loadStickers() {
//        int index = 0;
//        // 如果没有贴图
//        if (!hasSticker) {
//            CheckedImageButton btn = addEmoticonTabBtn(0, null);
//            btn.setNormalImageId(R.drawable.ic_emoji_icon);
//            btn.setCheckedImageId(R.drawable.ic_emoji_icon);
//            btn.setChecked(true);
//            return;
//        }
//        tabView.removeAllViews();
//
//        // emoji表情
//        CheckedImageButton btn = addEmoticonTabBtn(index++, tabCheckListener);
//        btn.setNormalImageId(R.drawable.ic_emoji_icon);
//        btn.setCheckedImageId(R.drawable.ic_emoji_icon);
//        btn.setChecked(true);
//
//        CheckedImageButton btn2 = addEmoticonTabBtn(index++, tabCheckListener);
//        btn2.setNormalImageId(R.drawable.ic_emoji_collect_icon);
//        btn2.setCheckedImageId(R.drawable.ic_emoji_collect_icon);
//
//        // 贴图
//        groupName.clear();
//        File stickerFile = new File(context.getFilesDir() + "/sticker/" + CubeSpUtil.getCubeUser().getCubeId());
//        if (!stickerFile.exists()) {
//            return;
//        }
//        File[] stickerFiles = stickerFile.listFiles();
//        for (File file : stickerFiles) {
//            File chatPanelFile = new File(file.getPath() + "/chat_panel.png");
//            if (chatPanelFile.exists()) {
//                groupName.add(file.getName());
//                CheckedImageButton emoticonTabBtn = addEmoticonTabBtn(index++, tabCheckListener);
//                emoticonTabBtn.setNormalImage(chatPanelFile);
//                emoticonTabBtn.setCheckedImage(chatPanelFile);
//            }
//        }
//    }
//
//    private CheckedImageButton addEmoticonTabBtn(int index, OnClickListener listener) {
//        CheckedImageButton emotBtn = new CheckedImageButton(context);
//        emotBtn.setNormalBkResId(R.drawable.sticker_btn_bg_pressed_layer_list);
//        emotBtn.setCheckedBkResId(R.drawable.sticker_btn_bg_normal_layer_list);
//        emotBtn.setId(index);
//        emotBtn.setOnClickListener(listener);
//        emotBtn.setScaleType(ImageView.ScaleType.FIT_CENTER);
//        emotBtn.setPaddingValue(ScreenUtil.dip2px(10));
//
//        final int emojiBtnWidth = ScreenUtil.dip2px(50);
//        final int emojiBtnHeight = ScreenUtil.dip2px(44);
//
//        tabView.addView(emotBtn);
//
//        ViewGroup.LayoutParams emojBtnLayoutParams = emotBtn.getLayoutParams();
//        emojBtnLayoutParams.width = emojiBtnWidth;
//        emojBtnLayoutParams.height = emojiBtnHeight;
//        emotBtn.setLayoutParams(emojBtnLayoutParams);
//
//        return emotBtn;
//    }
//
//    private void onEmoticonBtnChecked(int index) {
//        LogUtil.i("onEmoticonBtnChecked -- > " + index);
//        updateTabButton(index);
//        showEmotPager(index);
//    }
//
//    private void updateTabButton(int index) {
//        for (int i = 0; i < tabView.getChildCount(); ++i) {
//            View child = tabView.getChildAt(i);
//            if (child instanceof FrameLayout) {
//                child = ((FrameLayout) child).getChildAt(0);
//            }
//
//            if (child != null && child instanceof CheckedImageButton) {
//                CheckedImageButton tabButton = (CheckedImageButton) child;
//                if (tabButton.isChecked() && i != index) {
//                    tabButton.setChecked(false);
//                }
//                else if (!tabButton.isChecked() && i == index) {
//                    tabButton.setChecked(true);
//                }
//            }
//        }
//    }
//
//    private void showEmotPager(int index) {
//        if (index == 1) {
//            gifView = new EmoticonView(context, listener, emoticonPager, pagerNumberLayout);
//            gifView.setCategoryChangCheckedCallback(this);
//            gifView.showStickers(index);
//            return;
//        }
//        if (gifView == null) {
//            gifView = new EmoticonView(context, listener, emoticonPager, pagerNumberLayout);
//            gifView.setCategoryChangCheckedCallback(this);
//        }
//        gifView.showStickers(index);
//    }
//
//    private void showEmojiView() {
//        if (gifView == null) {
//            gifView = new EmoticonView(context, listener, emoticonPager, pagerNumberLayout);
//        }
//        gifView.showEmojis();
//    }
//
//    /**
//     * 加载Emoji表情
//     */
//    private void show() {
//        if (listener == null) {
//            LogUtil.i("sticker", "show picker view when listener is null");
//        }
//        if (!hasSticker) {
//            showEmojiView();
//        }
//        else {
//            onEmoticonBtnChecked(0);
//            setSelectedVisible(0);
//        }
//    }
//
//    private void setSelectedVisible(final int index) {
//        final Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                if (scrollView.getChildAt(0).getWidth() == 0) {
//                    uiHandler.postDelayed(this, 100);
//                }
//                int x = -1;
//                View child = tabView.getChildAt(index);
//                if (child != null) {
//                    if (child.getRight() > scrollView.getWidth()) {
//                        x = child.getRight() - scrollView.getWidth();
//                    }
//                }
//                if (x != -1) {
//                    scrollView.smoothScrollTo(x, 0);
//                }
//            }
//        };
//        uiHandler.postDelayed(runnable, 100);
//    }
//
//    public void setHasSticker(boolean hasSticker) {
//        this.hasSticker = hasSticker;
//    }
//
//    @Override
//    public void onTypeChanged(int index) {
//        if (categoryIndex == index) {
//            return;
//        }
//
//        categoryIndex = index;
//        updateTabButton(index);
//    }
//}
