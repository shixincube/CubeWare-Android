package cube.ware.widget.indexbar.bean;

/**
 * 索引类的标志位的实体基类
 *
 * @author Wangxx
 * @date 2017/4/11
 */

public abstract class BaseIndexBean implements ISuspensionInterface {
    protected String baseIndexTag;//所属的分类

    public String getBaseIndexTag() {
        return baseIndexTag;
    }

    public BaseIndexBean setBaseIndexTag(String baseIndexTag) {
        this.baseIndexTag = baseIndexTag;
        return this;
    }

    @Override
    public String getSuspensionTag() {
        return baseIndexTag;
    }

    @Override
    public boolean isShowSuspension() {
        return true;
    }

    @Override
    public String toString() {
        return "BaseIndexBean{" + "baseIndexTag='" + baseIndexTag + '\'' + '}';
    }
}
