package cube.ware.impl.cache;

import android.text.TextUtils;
import cube.ware.data.room.model.CubeUser;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserCache {
    private static Map<String, CubeUser> userCache = new ConcurrentHashMap<>();

    public static void remove(String cube) {
        userCache.remove(cube);
    }

    public static CubeUser getUser(String cubeId) {
        return userCache.get(cubeId);
    }

    public static void saveUser(CubeUser user) {
        if (user != null && !TextUtils.isEmpty(user.getCubeId())) {
            userCache.put(user.getCubeId(), user);
        }
    }

    public static void saveUsers(List<CubeUser> users) {
        if (users != null && users.size() > 0) {
            for (CubeUser user : users) {
                saveUser(user);
            }
        }
    }
}
