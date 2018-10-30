package cube.ware.widget.indexbar.helper;


import java.util.List;

import cube.ware.widget.indexbar.bean.BaseIndexPinyinBean;

/**
 * CubeIndexBar 的 数据相关帮助类
 * 1 将汉语转成拼音
 * 2 填充indexTag
 * 3 排序源数据源
 * 4 根据排序后的源数据源->indexBar的数据源
 *
 * @author Wangxx
 * @date 2017/4/11
 */

public interface IIndexBarDataHelper {
    //汉语-》拼音
    IIndexBarDataHelper convert(List<? extends BaseIndexPinyinBean> data);

    //拼音->tag
    IIndexBarDataHelper fillIndexTag(List<? extends BaseIndexPinyinBean> data);

    //对源数据进行排序（RecyclerView）
    IIndexBarDataHelper sortSourceData(List<? extends BaseIndexPinyinBean> data);

    //对IndexBar的数据源进行排序(右侧栏),在 sortSourceData 方法后调用
    IIndexBarDataHelper getSortedIndexData(List<? extends BaseIndexPinyinBean> sourceData, List<String> data);
}
