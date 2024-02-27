package org.caesar.domain.search.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * //分页查询响应
 * @param <T> 结果类型
 * data: 查询结果
 * totalSize: 匹配的结果总数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "PageVO", description = "分页查询结果")
public class PageVO<T> {

    @ApiModelProperty("响应数据")
    private List<T> data;

    @ApiModelProperty("数据总数")
    private int totalSize;
}
