package cube.ware.service.message.chat.panel.input.emoticon.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.common.utils.ClickUtil;
import com.common.utils.ScreenUtil;
import com.common.utils.UIHandler;
import com.common.utils.log.LogUtil;
import cube.ware.core.CubeCore;
import cube.ware.service.message.R;
import cube.ware.service.message.chat.panel.input.emoticon.EmoticonSelectedListener;
import cube.ware.service.message.chat.panel.input.emoticon.EmoticonTypeChangedListener;
import cube.ware.service.message.chat.panel.input.emoticon.EmoticonView;
import cube.ware.service.message.chat.panel.input.emoticon.manager.StickerManager;
import cube.ware.service.message.chat.panel.input.emoticon.model.StickerType;
import cube.ware.widget.SlideViewPager;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 贴图表情选择控件
 *
 * @author Wangxx
 * @date 2017/1/4
 */
public class EmoticonPickerView extends FrameLayout implements EmoticonTypeChangedListener {

    public  EmoticonView         gifView;
    private SlideViewPager       emoticonPager;
    private LinearLayout         pagerNumberLayout; //页面布局
    private HorizontalScrollView scrollView;
    private LinearLayout         tabView;
    private ImageView            emoticonAdd;       // 添加表情按钮

    private EmoticonSelectedListener listener;

    private       Context           context;
    private       boolean           hasSticker; //默认无官方贴图
    private       int               categoryIndex;
    private       boolean           loaded;
    public static int               mIndex;
    private       ArrayList<String> groupName = new ArrayList<>();

    public EmoticonPickerView(Context context) {
        super(context);
        init(context);
    }

    public EmoticonPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public EmoticonPickerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        this.hasSticker = true;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.cube_emoticon_layout, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setupEmojView();
    }

    protected void setupEmojView() {
        emoticonPager = (SlideViewPager) findViewById(R.id.emoticon_pager);
        pagerNumberLayout = (LinearLayout) findViewById(R.id.layout_bottom);
        tabView = (LinearLayout) findViewById(R.id.emoticon_tab_view);
        scrollView = (HorizontalScrollView) findViewById(R.id.emoticon_tab_view_container);
        emoticonAdd = (ImageView) findViewById(R.id.emoticon_add_btn);
        emoticonAdd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.isNormalClick(v)) {
                    //CenterActivity.start(context, CubeSpUtil.getToken(), CubeSpUtil.getCubeUser().getCubeId(), groupName);
                }
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public void show(EmoticonSelectedListener listener) {
        setListener(listener);  // 表情的选择器监听
        if (loaded) {  // 是否是加载过的
            return;
        }
        loadStickers();
        loaded = true;
        show();
    }

    public void setListener(EmoticonSelectedListener listener) {
        if (listener != null) {
            this.listener = listener;
        }
        else {
            LogUtil.i("sticker", "listener is null");
        }
    }

    public void onDestroy() {

    }

    // 添加各个tab按钮
    OnClickListener tabCheckListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            onEmoticonBtnChecked(v.getId());
        }
    };

    private void loadStickers() {
        int index = 0;
        tabView.removeAllViews();

        // emoji表情
        CheckedImageButton btn = addEmoticonTabBtn(index++, tabCheckListener);
        btn.setNormalImageId(R.drawable.ic_emoji_icon);
        btn.setCheckedImageId(R.drawable.ic_emoji_icon);
        btn.setChecked(true);
        // 收藏
        CheckedImageButton btn2 = addEmoticonTabBtn(index++, tabCheckListener);
        btn2.setNormalImageId(R.drawable.ic_emoji_collect_icon);
        btn2.setCheckedImageId(R.drawable.ic_emoji_collect_icon);

        // 贴图
        groupName.clear();
        File stickerFile = new File(context.getFilesDir() + "/sticker/" + CubeCore.getInstance().getCubeId());
        if (!stickerFile.exists()) {
            return;
        }
        StickerManager manager = StickerManager.getInstance(context);
        List<StickerType> categories = manager.getCategories();
        for (StickerType stickerType : categories) {
            if (stickerType.getType() == 1) {
                CheckedImageButton emoticonTabBtn = addEmoticonTabBtn(index++, tabCheckListener);
                File chatPanelFile = new File(stickerFile.getPath() + "/" + stickerType.getPackageId() + "/chat_panel.png");
                if (!chatPanelFile.exists()) {
                    chatPanelFile = new File(stickerFile.getPath() + "/" + stickerType.getName() + "/chat_panel.png");
                }
                groupName.add(stickerType.getName());
                emoticonTabBtn.setNormalImage(chatPanelFile);
                emoticonTabBtn.setCheckedImage(chatPanelFile);
            }
        }
    }

    private CheckedImageButton addEmoticonTabBtn(int index, OnClickListener listener) {
        CheckedImageButton emotBtn = new CheckedImageButton(context);
        emotBtn.setNormalBkResId(R.drawable.sticker_btn_bg_pressed_layer_list);
        emotBtn.setCheckedBkResId(R.drawable.sticker_btn_bg_normal_layer_list);
        emotBtn.setId(index);
        emotBtn.setOnClickListener(listener);
        emotBtn.setScaleType(ImageView.ScaleType.FIT_CENTER);
        emotBtn.setPaddingValue(ScreenUtil.dip2px(10));

        final int emojiBtnWidth = ScreenUtil.dip2px(43);
        final int emojiBtnHeight = ScreenUtil.dip2px(43);

        tabView.addView(emotBtn);

        ViewGroup.LayoutParams emojBtnLayoutParams = emotBtn.getLayoutParams();
        emojBtnLayoutParams.width = emojiBtnWidth;
        emojBtnLayoutParams.height = emojiBtnHeight;
        emotBtn.setLayoutParams(emojBtnLayoutParams);

        return emotBtn;
    }

    private void onEmoticonBtnChecked(int index) {
        LogUtil.i("onEmoticonBtnChecked -- > " + index);
        mIndex = index;
        updateTabButton(index);
        showEmotPager(index);
    }

    private void updateTabButton(int index) {
        for (int i = 0; i < tabView.getChildCount(); ++i) {
            View child = tabView.getChildAt(i);
            if (child instanceof FrameLayout) {
                child = ((FrameLayout) child).getChildAt(0);
            }
            if (child != null && child instanceof CheckedImageButton) {
                CheckedImageButton tabButton = (CheckedImageButton) child;
                if (tabButton.isChecked()) {
                    if (i == index) {
                        tabButton.setChecked(true);
                    }
                    else {
                        tabButton.setChecked(false);
                    }
                }
                else {
                    if (i != index) {
                        tabButton.setChecked(false);
                    }
                    else {
                        tabButton.setChecked(true);
                    }
                }
            }
        }
    }

    private void showEmotPager(int index) {
        if (index == 0) {
            gifView = new EmoticonView(context, listener, emoticonPager, pagerNumberLayout);
            gifView.setCategoryChangCheckedCallback(this);
            gifView.showStickers(index);
            return;
        }
        if (gifView == null) {
            gifView = new EmoticonView(context, listener, emoticonPager, pagerNumberLayout);
            gifView.setCategoryChangCheckedCallback(this);
        }
        gifView.showStickers(index);
    }

    private void showEmojiView() {
        if (gifView == null) {
            gifView = new EmoticonView(context, listener, emoticonPager, pagerNumberLayout);
        }
        gifView.showEmojis();
    }

    /**
     * 加载Emoji表情
     */
    private void show() {
        if (listener == null) {
            LogUtil.i("sticker", "show picker view when listener is null");
        }
        if (!hasSticker) {
            showEmojiView();
        }
        onEmoticonBtnChecked(0);
        setSelectedVisible(0);
    }

    private void setSelectedVisible(final int index) {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (scrollView.getChildAt(0).getWidth() == 0) {
                    UIHandler.getInstance().postDelayed(this, 100);
                }
                int x = -1;
                View child = tabView.getChildAt(index);
                if (child != null) {
                    if (child.getRight() > scrollView.getWidth()) {
                        x = child.getRight() - scrollView.getWidth();
                    }
                }
                if (x != -1) {
                    scrollView.smoothScrollTo(x, 0);
                }
            }
        };
        UIHandler.getInstance().postDelayed(runnable, 100);
    }

    public void setHasSticker(boolean hasSticker) {
        this.hasSticker = hasSticker;
    }

    @Override
    public void onTypeChanged(int index) {
        if (categoryIndex == index) {
            return;
        }
        categoryIndex = index;
        updateTabButton(index);
    }
}
