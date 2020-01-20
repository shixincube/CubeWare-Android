package cube.ware.core;

/**
 * 常量池
 *
 * @author LiuFeng
 * @data 2020/1/19 18:18
 */
public interface CubeConstants {

    /**
     * 路由地址
     */
    interface Router {
        //一对一语音通话
        String P2PCallActivity = "/app/P2PCallActivity";

        //白板
        String WhiteBoardActivity = "/app/WhiteBoardActivity";

        //选择人员
        String SelectMemberActivity = "/app/SelectMemberActivity";
    }

    /**
     * 事件通知Key
     */
    interface Event {
        String UpdateWhiteBoardTipView = "UpdateWhiteBoardTipView";
    }
}
