package cube.ware.widget.indexbar.bean;

/**
 * 分类悬停的接口
 *
 * @author Wangxx
 * @date 2017/4/11
 */

public interface ISuspensionInterface {
    //是否需要显示悬停title
    boolean isShowSuspension();

    //悬停的title
    String getSuspensionTag();
}
