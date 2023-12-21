package org.caesar.domain.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * //分页查询响应
 * @param <T> 结果类型
 * data: 查询结果
 * totalSize: 匹配的结果总数
 */
@Data
@AllArgsConstructor
public class PageVO<T> {
    private List<T> data;
    private int totalSize;
}
