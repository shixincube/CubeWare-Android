package cube.ware.ui.chat.activity.p2p;

import android.content.Context;
import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;

import java.util.ArrayList;

import cube.ware.AppConstants;
import cube.ware.R;
import cube.ware.data.repository.CubeUserRepository;
import cube.ware.data.room.model.CubeUser;
import cube.ware.ui.chat.ChatCustomization;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by dth
 * Des: p2p聊天定制化
 * Date: 2018/9/26.
 */

public class P2PChatCustomization extends ChatCustomization {

    public P2PChatCustomization() {
        this.typ = ChatStatusType.Normal;
        this.buildOptionButtonList();
    }

    /**
     * 构建标题栏右侧可操作按钮列表数据
     */
    protected ArrayList<OptionButton> buildOptionButtonList() {
        super.optionButtonList = new ArrayList<>();
        super.optionButtonList.add(new TitleP2PDetailButton(R.drawable.nav_chat_person_icon));
        return super.optionButtonList;
    }

    /**
     * 构建标题个人详情操作按钮
     */
    private class TitleP2PDetailButton extends ChatCustomization.OptionButton {

        TitleP2PDetailButton(int iconId) {
            super(iconId);
        }

        @Override
        public void onClick(Context context, View view, String chatId) {
            CubeUserRepository.getInstance().queryUser(chatId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<CubeUser>() {
                        @Override
                        public void call(CubeUser cubeUser) {
                            ARouter.getInstance().build(AppConstants.Router.FriendDetailsActivity)
                                    .withObject("user",cubeUser)
                                    .navigation(context);
                        }
                    });
        }
    }
}
