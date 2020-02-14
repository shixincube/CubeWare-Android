package com.common.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 集合工具类
 *
 * @author LiuFeng
 * @date 2017-11-01
 */
public class CollectionUtil {

    /**
     * 将一个List按照固定的大小拆成很多个小的List
     *
     * @param listData 需要拆分的List数据
     * @param groupNum 拆分后的每个List的最大长度
     *
     * @return 拆分后的List的集合
     */
    public static <T> List<List<T>> getSubList(final List<T> listData, final int groupNum) {
        List<List<T>> resultList = new ArrayList<>();
        // 获取需要拆分的List个数
        int loopCount = (listData.size() % groupNum == 0) ? (listData.size() / groupNum) : ((listData.size() / groupNum) + 1);
        // 开始拆分
        for (int i = 0; i < loopCount; i++) {
            // 子List的起始值
            int startNum = i * groupNum;
            // 子List的终止值
            int endNum = (i + 1) * groupNum;
            // 不能整除的时候最后一个List的终止值为原始List的最后一个
            if (i == loopCount - 1) {
                endNum = listData.size();
            }
            // 拆分List
            List<T> subListData = listData.subList(startNum, endNum);
            // 保存差分后的List
            resultList.add(subListData);
        }
        return resultList;
    }

    /**
     * list集合交换元素下标
     */
    public static <T> List<T> indexExChange(List<T> list, int index1, int index2) {
        T t = list.get(index1);
        list.set(index1, list.get(index2));
        list.set(index2, t);
        return list;
    }
}
