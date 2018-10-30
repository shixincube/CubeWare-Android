package cube.ware.service.whiteboard;

import java.util.List;

import cube.service.common.model.CubeError;
import cube.service.user.model.User;
import cube.service.whiteboard.model.Whiteboard;
import cube.service.whiteboard.model.WhiteboardSlide;
/**
 * author: kun .
 * date:   On 2018/8/29
 */
public interface WhiteBoardStateListener {

    void onWhiteboardCreated(Whiteboard var1, User var2);

    void onWhiteboardDestroyed(Whiteboard var1, User var2);

    void onWhiteboardInvited(Whiteboard var1, User var2, List<User> var3);

    void onWhiteboardRejectInvited(Whiteboard var1, User var2, User var3);

    void onWhiteboardAcceptInvited(Whiteboard var1, User var2, User var3);

    void onWhiteboardJoined(Whiteboard var1, User var2);

    void onWhiteboardQuited(Whiteboard var1, User var2);

    void onSlideUploading(Whiteboard var1, WhiteboardSlide var2, long var3, long var5);

    void onSlideUploadCompleted(Whiteboard var1, WhiteboardSlide var2);

    void onSlideUpdated(Whiteboard var1, WhiteboardSlide var2);

    void onWhiteboardFailed(Whiteboard var1, CubeError var2);
}
