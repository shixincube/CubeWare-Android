package cube.ware.service.message;

import cube.utils.SpUtil;

/**
 * 消息相关常量池
 *
 * @author LiuFeng
 * @data 2020/1/21 17:53
 */
public interface MessageConstants {
    interface REGEX {
        String REGEX_AT_MEMBER = "@\\{cube:[^,]*,name:[^\\}]*\\}"; // @成员
        String REGEX_AT_ALL    = "@\\{group:[^,]*,name:[^\\}]*\\}"; // @全体成员
    }

    /**
     * SharedPreferences常量
     */
    interface Sp {
        String PATH_APP   = "CubeWare";                // 根目录
        String PATH_LOG   = "log";                     // 日志目录
        String PATH_IMAGE = "image";                   // 图片目录
        String PATH_FILE  = "file";                    // 文件目录
        String PATH_THUMB = ".thumb";                  // 缩略图目录，隐藏目录

        String CUBE_TOKEN  = "cubeToken";                 //cubeToken
        String USER_CUBEID = "userCubeId";              //userCubeId
        String CUBE_NAME   = "cubeName";                 //cubeToken
        String USER_AVATOR = "userAvator";              //userAvator
        String USER_JSON   = "userJson";              //userAvator

        String SP_CUBE               = SpUtil.getCubeId();
        String SP_CUBE_AT            = "sp_cube_at" + SP_CUBE;
        String SP_CUBE_AT_ALL        = "sp_cube_at_all" + SP_CUBE;    // @全体成员的数量
        String SP_CUBE_RECEIVE_ATALL = "sp_cube_receive_at_all" + SP_CUBE;//接收到的@All

        // 草稿消息
        String MESSAGE_DRAFT = "message_draft_";
    }
}
