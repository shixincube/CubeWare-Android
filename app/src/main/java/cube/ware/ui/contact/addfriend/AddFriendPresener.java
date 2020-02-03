package cube.ware.ui.contact.addfriend;

import android.content.Context;

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
    public void getGroupDate(String groupId) {
        /*CubeEngine.getInstance().getGroupService().queryGroupDetails(groupId, new CubeCallback<Group>() {
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
        });*/
    }
}
