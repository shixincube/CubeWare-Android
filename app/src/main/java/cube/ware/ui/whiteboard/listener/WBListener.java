package cube.ware.ui.whiteboard.listener;

import android.content.Context;
import android.os.Bundle;
import com.common.sdk.RouterUtil;
import com.common.utils.utils.ToastUtil;
import com.common.utils.utils.log.LogUtil;

import java.util.ArrayList;
import java.util.List;

import cube.service.CubeEngine;
import cube.service.common.model.CubeError;
import cube.service.user.model.User;
import cube.service.whiteboard.model.Whiteboard;
import cube.service.whiteboard.model.WhiteboardSlide;
import cube.ware.AppConstants;
import cube.ware.data.model.dataModel.enmu.CubeSessionType;
import cube.ware.service.whiteboard.WhiteBoardStateListener;
import cube.ware.ui.chat.BaseChatActivity;
import cube.ware.ui.chat.ChatContainer;

/**
 * author: kun .
 * des ChatMoreFunctionFragment 开启一对一白板
 * date:   On 2018/9/10
 */
public class WBListener implements WhiteBoardStateListener{

    private CubeSessionType mChatType;
    private Context mContext;
    private String mGroupId;
    private ArrayList<String> mInviteIdList;
    private CreateCallback mCreateCallback;

    public void setCreateCallback(CreateCallback createCallback) {
        mCreateCallback = createCallback;
    }

    public WBListener(Context context, CubeSessionType chatType, List<String> mInviteIdList, String mGroupId) {
        this.mContext =  context;
        this.mChatType=chatType;
        this.mGroupId = mGroupId;
        this.mInviteIdList= (ArrayList<String>) mInviteIdList;
    }

    @Override
    public void onWhiteboardCreated(Whiteboard whiteboard, User user) {
        if(user.cubeId.equals(CubeEngine.getInstance().getSession().getUser().cubeId)){
            CubeEngine.getInstance().getWhiteboardService().join(whiteboard.whiteboardId);
            mCreateCallback.onWBCreate(whiteboard);
        }
    }

    @Override
    public void onWhiteboardDestroyed(Whiteboard var1, User var2) {

    }

    @Override
    public void onWhiteboardInvited(Whiteboard var1, User var2, List<User> var3) {

    }

    @Override
    public void onWhiteboardRejectInvited(Whiteboard var1, User var2, User var3) {

    }

    @Override
    public void onWhiteboardAcceptInvited(Whiteboard var1, User var2, User var3) {

    }

    @Override
    public void onWhiteboardJoined(Whiteboard whiteboard, User user) {
        //判断是自己创建
        if(user.cubeId.equals(CubeEngine.getInstance().getSession().getUser().cubeId)){
            ToastUtil.showToast(mContext,"创建白板成功");
            mCreateCallback.onWBFinish(whiteboard);
            startWhiteBoard(whiteboard);
        }
    }

    @Override
    public void onWhiteboardQuited(Whiteboard var1, User var2) {

    }

    @Override
    public void onSlideUploading(Whiteboard var1, WhiteboardSlide var2, long var3, long var5) {

    }

    @Override
    public void onSlideUploadCompleted(Whiteboard var1, WhiteboardSlide var2) {

    }

    @Override
    public void onSlideUpdated(Whiteboard var1, WhiteboardSlide var2) {

    }

    @Override
    public void onWhiteboardFailed(Whiteboard whiteboard, CubeError cubeError) {
        mCreateCallback.onWBError(whiteboard,cubeError);
    }

    //跳转页面
    private void startWhiteBoard(Whiteboard whiteboard){
        Bundle bundle=new Bundle();
        bundle.putInt(AppConstants.Value.CALLSTATA_WHITE_BOARD,AppConstants.Value.CALLSTATE_CREATE);
        bundle.putSerializable(AppConstants.Value.WHITEBOARD,whiteboard);
        bundle.putStringArrayList(AppConstants.Value.INVITE_LIST,mInviteIdList);
        bundle.putString(AppConstants.Value.GROUP_ID,mGroupId);
        bundle.putSerializable(AppConstants.Value.CHAT_TYPE,mChatType);
        RouterUtil.navigation(mContext,bundle,AppConstants.Router.WhiteBoardActivity);
    }
}
