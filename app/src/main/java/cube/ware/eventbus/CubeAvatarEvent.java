package cube.ware.eventbus;

/**
 * author: kun .
 * des: 修改头像的回调类
 * date:   On 2018/9/7
 */
public class CubeAvatarEvent {
    public String avatar;

    public CubeAvatarEvent(String avatar) {
        this.avatar = avatar;
    }
}
