package org.caesar.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RefreshCacheTask implements Comparable<RefreshCacheTask>{

    // 缓存key
    private String key;

    // 开始刷新的时间
    private long startTime;

    // 最后允许刷新的时间
    private long endTime;

    // 缓存的过期时间(以秒为单位)
    private int expire;

    // 过期前执行的操作
    private Runnable beforeExpireTask;

    @Override
    public int compareTo(RefreshCacheTask o) {
        return (int) (this.startTime - o.startTime);
    }

    public void refreshExpire() {
        startTime = startTime + expire * 1000L;
    }

}
