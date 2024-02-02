package org.caesar.common.vo;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Date;

@Data
@AllArgsConstructor
public class CacheInfo {

    // 过期时间点
    private long expireTime;

    // 过期时间
    private int expire;

    // 是否被访问
    private boolean accessed;
}
