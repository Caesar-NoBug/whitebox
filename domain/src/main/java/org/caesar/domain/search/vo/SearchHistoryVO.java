package org.caesar.domain.search.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SearchHistoryVO {
    /**
     * 用户搜索的内容
     */
    private String content;

    /**
     * 搜索时间
     */
    private LocalDateTime searchTime;

    /**
     * 查询数据类型
     */
    private Integer dataSource;
}
