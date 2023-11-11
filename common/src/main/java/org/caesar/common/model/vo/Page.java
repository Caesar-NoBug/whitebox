package org.caesar.common.model.vo;

import lombok.Data;

import java.util.List;

/**
 * //分页查询响应
 * @param <T> 结果类型
 * data: 查询结果
 * totalSize: 匹配的结果总数
 */
@Data
public class Page<T> {
    private List<T> data;
    private int totalSize;
}
