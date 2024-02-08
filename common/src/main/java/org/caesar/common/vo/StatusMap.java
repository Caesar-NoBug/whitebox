package org.caesar.common.vo;

import java.util.ArrayList;
import java.util.List;

//状态表，高效记录一组数据的状态（成功或失败）
public class StatusMap {

    private long map;
    private int size;

    public StatusMap(int size) {

        if (size <= 64) {
            this.size = size;
        } else
            throw new IllegalArgumentException("size 过大，不支持该容量的StatusMap, 最大容量为64");
    }

    //将第{index}个数据标记为失败,注：index从0开始
    public void setFail(int index) {
        if(index > size) return;

        map |= 1L << index;
    }

    //第{index}个数据中是否为失败数据
    public boolean isFail(int index) {
        return (map >> index & 1) == 1;
    }

    //所有数据中是否包含失败数据
    public boolean containsFail() {
        return map != 0;
    }

    //获取所有失败数据的编号
    public List<Number> getFailIndex() {
        ArrayList<Number> result = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            if ((map << i & 1) == 1)
                result.add(i);
        }

        return result;
    }

    //获取第一个失败数据的编号
    public int getFirstFailIndex() {
        int i = -1;
        while (++ i < size) {
            if ((map << i & 1) == 1)
                return i;
        }
        return -1;
    }

    public int getFailCount() {
        long temp = map;
        int count = 0;

        while (temp > 0) {
            temp -= lowBit(temp);
            count ++;
        }

        return count;
    }

    private static long lowBit(long x) {
        return x & -x;
    }

    public List<Boolean> getStatus() {

        List<Boolean> status = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            status.add((map << i & 1) == 1);
        }

        return status;
    }
}
