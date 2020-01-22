package cube.ware.service.message.chat.activity.group;

import android.content.Context;
import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;

import java.util.ArrayList;

import cube.ware.AppConstants;
import cube.ware.R;
import cube.ware.service.message.chat.ChatCustomization;

/**
 * Created by dth
 * Des: 群组聊天页面定制化
 * Date: 2018/9/26.
 */

public class GroupChatCustomization extends ChatCustomization{

    public GroupChatCustomization() {
        this.typ = ChatStatusType.Group;
        this.buildOptionButtonList();
    }

    /**
     * 构建标题栏右侧可操作按钮列表数据
     */
    private ArrayList<OptionButton> buildOptionButtonList() {
        super.optionButtonList = new ArrayList<>();
        super.optionButtonList.add(new TitleGroupDetailButton(R.drawable.nav_chat_group_icon));
        return super.optionButtonList;
    }

    /**
     * 构建标题群组详情操作按钮
     */
    private class TitleGroupDetailButton extends ChatCustomization.OptionButton {

        TitleGroupDetailButton(int iconId) {
            super(iconId);
        }

        @Override
        public void onClick(Context context, View view, String chatId) {
            ARouter.getInstance().build(AppConstants.Router.GroupDetailsActivity)
                    .withString("groupId",chatId)
                    .navigation(context);
        }
    }
}
