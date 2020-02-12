package cube.ware.data.model;

import android.text.TextUtils;
import cube.ware.data.room.model.CubeRecentSession;

/**
 * Created by dth
 * Des:
 * Date: 2018/9/5.
 */

public class CubeRecentViewModel {

    public CubeRecentSession cubeRecentSession;

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CubeRecentViewModel)) {
            return false;
        }

        return TextUtils.equals(this.cubeRecentSession.getSessionId(), ((CubeRecentViewModel) obj).cubeRecentSession.getSessionId());
    }
}
