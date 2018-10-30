package cube.ware.service.remoteDesktop.manager;

import java.util.HashMap;
import java.util.Map;

import cube.service.conference.model.Conference;
import cube.service.sharedesktop.model.ShareDesktop;

/**
 * Created by q on 2018/9/3.
 */

public class ShareDesketopManager {
    private Map<String, Conference> mShareDesktopCache       = new HashMap<>();

    private static  ShareDesketopManager mInstance                     = null;

    public static ShareDesketopManager getInstance(){
        if (null == mInstance ){
            synchronized (ShareDesketopManager.class){
                if (null == mInstance){
                    mInstance = new ShareDesketopManager();
                }
            }
        }
        return  mInstance;
    }

    /**
     * 保存远程桌面信息
     * @param conference
     */
    public void saveShareDesktop(Conference conference) {
        if (conference != null) {
            this.mShareDesktopCache.put(conference.conferenceId, conference);
//            RxBus.getInstance().post(CubeEvent.EVENT_GROUP_CALL, shareDesktop);
        }
    }

    /**
     * 删除远程桌面信息
     *
     * @param shareDesktop
     */
    public void removeShareDesktop(ShareDesktop shareDesktop) {
        if (this.mShareDesktopCache.containsKey(shareDesktop.bindGroupId)) {
            this.mShareDesktopCache.remove(shareDesktop.bindGroupId);
//            RxBus.getInstance().post(CubeEvent.EVENT_GROUP_CALL, shareDesktop.bindGroupId);
        }
    }
}
