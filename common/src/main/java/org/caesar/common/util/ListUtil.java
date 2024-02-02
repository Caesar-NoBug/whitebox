package org.caesar.common.util;

import org.caesar.common.vo.RefreshCacheTask;

import java.util.ArrayList;
import java.util.List;

public class ListUtil {

    /**
     * 二分插入有序数组
     * @param list  待插入的有序数组
     * @param value 待插入的值
     * @param <T>   待插入的值类型
     */
    public static<T extends Comparable<T>> void binaryInsert(List<T> list, T value) {

        if (list.size() <= 1) {

            if(list.isEmpty() || list.get(0).compareTo(value) > 0)
                list.add(0, value);
            else
                list.add(1, value);

            return;
        }

        int l = 0;
        int r = list.size() - 1;

        while(l < r) {
            int mid = l + r + 1 >> 1;
            if(list.get(mid).compareTo(value) <= 0) l = mid;
            else r = mid - 1;
        }

        list.add(l + 1, value);
    }

}
