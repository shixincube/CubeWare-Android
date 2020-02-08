package cube.ware.service.message.chat.activity.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import cube.ware.service.message.chat.panel.input.function.BaseFunction;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * 聊天界面定制化参数
 * 1. 聊天背景
 * 2. 更多操作按钮
 * 3. 标题栏右侧按钮
 *
 * @author Wangxx
 * @date 2017/1/3
 */
public class ChatCustomization implements Serializable {

    /**
     * 聊天背景uri。优先使用uri，如果没有提供uri，使用color。如果没有color，使用默认。uri暂时支持以下格式
     * drawable: android.resource://包名/drawable/资源名
     * assets: file:///android_asset/{asset文件路径}
     * file: file:///文件绝对路径
     */
    public String backgroundUri;

    /**
     * 聊天背景颜色
     */
    public int backgroundColor;

    /**
     * 是否有自定义的贴图表情
     */
    public boolean hasCustomSticker = false;

    /**
     * 聊天会话状态
     */
    public ChatStatusType typ = ChatStatusType.Normal;

    /**
     * 点击顶部按钮监听
     */
    public TopBtnClickListener mTopBtnClickListener;

    /**
     * 标题栏右侧可定制的操作按钮列表
     */
    public ArrayList<OptionButton> optionButtonList;

    /**
     * 表情布局更多展开后可定制的功能组件列表
     */
    public ArrayList<BaseFunction> functionViewList;

    /**
     * 如果OptionsButton的点击响应中需要startActivityForResult，可在此函数中处理结果
     *
     * @param activity    当前的聊天Activity
     * @param requestCode 请求码
     * @param resultCode  结果码
     * @param data        返回的结果数据
     */
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {}

    /**
     * 标题栏右侧操作按钮
     *
     * @author PengZhenjin
     * @date 2017-1-4
     */
    public static abstract class OptionButton implements Serializable {

        /**
         * 图标资源id
         */
        public int iconId;

        public OptionButton() {

        }

        public OptionButton(int iconId) {
            this.iconId = iconId;
        }

        /**
         * 图标点击事件
         *
         * @param context
         * @param view
         * @param chatId
         */
        public abstract void onClick(Context context, View view, String chatId);
    }

    public abstract class TopBtnClickListener implements Serializable {
        public abstract void onTopBtnClick(Activity activity, View v, String cubeId);
    }
}
