package cube.ware.ui.contact.addfriend;

import android.content.Context;

import com.common.mvp.base.BasePresenter;
import com.common.mvp.base.BaseView;

import cube.service.CubeEngine;
import cube.service.common.CubeCallback;
import cube.service.common.model.CubeError;
import cube.service.group.model.Group;

public class AddFriendPresener extends AddFriendContract.Presenter {

    /**
     * 构造方法
     *
     * @param context
     * @param view
     */
    public AddFriendPresener(Context context, AddFriendContract.View view) {
        super(context, view);
    }

    @Override
    public void getGroupDate(String groupId){
        CubeEngine.getInstance().getGroupService().queryGroupDetails(groupId, new CubeCallback<Group>() {
            @Override
            public void onSucceed(Group group) {
                if(group!=null){
                    mView.searchGroup(group);
                }else {
                    mView.showMessage("没有搜索到相关的群组");
                }
            }

            @Override
            public void onFailed(CubeError cubeError) {
                mView.showMessage("没有搜索到相关的群组");
            }
        });
    }
}
