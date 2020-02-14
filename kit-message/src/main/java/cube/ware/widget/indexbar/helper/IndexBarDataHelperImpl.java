package cube.ware.widget.indexbar.helper;

import com.common.utils.log.LogUtil;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cube.ware.widget.indexbar.bean.BaseIndexPinyinBean;
import cube.ware.widget.pinyin.LanguageConvent;

/**
 * IndexBar 的数据相关帮助类实现
 * 1 将汉语转成拼音(利用tinyPinyin)
 * 2 填充indexTag (取拼音首字母)
 * 3 排序源数据源
 * 4 根据排序后的源数据源->indexBar的数据源
 *
 * @author Wangxx
 * @date 2017/4/11
 */

public class IndexBarDataHelperImpl implements IIndexBarDataHelper {
    /**
     * 如果需要，
     * 字符->拼音，
     *
     * @param data
     */
    @Override
    public IIndexBarDataHelper convert(List<? extends BaseIndexPinyinBean> data) {
        if (null == data || data.isEmpty()) {
            return this;
        }
        int size = data.size();
        for (int i = 0; i < size; i++) {
            BaseIndexPinyinBean indexPinyinBean = data.get(i);

            // 如果不是拼音才转拼音，否则不用转了
            if (indexPinyinBean.isNeedToPinyin()) {
                String target = indexPinyinBean.getTarget();//取出需要被拼音化的字段
//                String selling = LanguageConvent.getFirstChar(target);
                String selling = LanguageConvent.getPinYin(target);
                indexPinyinBean.setBaseIndexPinyin(selling);//设置拼音
            }
        }
        return this;
    }

    /**
     * 如果需要取出，则
     * 取出首字母->tag,或者特殊字母 "#".
     * 否则，用户已经实现设置好
     *
     * @param datas
     */
    @Override
    public IIndexBarDataHelper fillIndexTag(List<? extends BaseIndexPinyinBean> datas) {
        if (null == datas || datas.isEmpty()) {
            return this;
        }
        int size = datas.size();
        for (int i = 0; i < size; i++) {
            BaseIndexPinyinBean indexPinyinBean = datas.get(i);
            if (indexPinyinBean.isNeedToPinyin()) {
                // 以下代码设置数据拼音首字母
                if (indexPinyinBean.getBaseIndexPinyin() != null && indexPinyinBean.getBaseIndexPinyin().length() > 0) {
                    String tagString = indexPinyinBean.getBaseIndexPinyin().substring(0, 1).toUpperCase();
                    if (tagString.matches("[A-Z]")) {   // 如果是字母开头
                        indexPinyinBean.setBaseIndexTag(tagString);
                    }
                    //else if (tagString.matches("[0-9]")) {   // 如果是数字开头
                    //    indexPinyinBean.setBaseIndexTag(tagString);
                    //}
                    else {  // 特殊字母这里统一用#处理
                        indexPinyinBean.setBaseIndexTag("#");
                    }
                }
                else {
                    indexPinyinBean.setBaseIndexTag("#");
                }
            }
        }
        return this;
    }

    @Override
    public IIndexBarDataHelper sortSourceData(List<? extends BaseIndexPinyinBean> datas) {
        if (null == datas || datas.isEmpty()) {
            return this;
        }
        convert(datas);
        fillIndexTag(datas);
        // 按照字母、数字、“#”排序
        Collections.sort(datas, new Comparator<BaseIndexPinyinBean>() {
            @Override
            public int compare(BaseIndexPinyinBean lhs, BaseIndexPinyinBean rhs) {
                if (lhs.isNeedToPinyin() && !rhs.isNeedToPinyin()) {
                    return 1;
                }
                else if (!lhs.isNeedToPinyin() && rhs.isNeedToPinyin()) {
                    return -1;
                }
                else if (lhs.getBaseIndexTag().equals("#") && !rhs.getBaseIndexTag().equals("#")) {
                    return 1;
                }
                else if (lhs.getBaseIndexTag().matches("[0-9]") && !rhs.getBaseIndexTag().matches("[0-9]")) {
                    return 1;
                }
                else if (!lhs.getBaseIndexTag().matches("[0-9]") && rhs.getBaseIndexTag().matches("[0-9]")) {
                    return -1;
                }
                else if (!lhs.getBaseIndexTag().equals("#") && rhs.getBaseIndexTag().equals("#")) {
                    return -1;
                }
                else {
                    if (lhs.getBaseIndexTag().equals(rhs.getBaseIndexTag())) {
                        return lhs.getTarget().compareTo(rhs.getTarget());
                    }
                    else {
                        return lhs.getBaseIndexTag().compareTo(rhs.getBaseIndexTag());
                    }
                }
            }
        });
        return this;
    }

    @Override
    public IIndexBarDataHelper getSortedIndexData(List<? extends BaseIndexPinyinBean> sourceDatas, List<String> indexDatas) {
        if (null == sourceDatas || sourceDatas.isEmpty()) {
            return this;
        }
        //按数据源来 此时sourceData 已经有序
        int size = sourceDatas.size();
        String baseIndexTag;
        for (int i = 0; i < size; i++) {
            baseIndexTag = sourceDatas.get(i).getBaseIndexTag();
            if (!indexDatas.contains(baseIndexTag)) {//则判断是否已经将这个索引添加进去，若没有则添加
                indexDatas.add(baseIndexTag);
            }
        }
        LogUtil.i("indexDatas.size-->" + indexDatas.size());
        return this;
    }
}
