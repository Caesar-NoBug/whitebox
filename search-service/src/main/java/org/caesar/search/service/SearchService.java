package org.caesar.search.service;

import org.caesar.domain.search.enums.DataSource;
import org.caesar.domain.search.enums.SortField;
import org.caesar.domain.search.vo.IndexVO;
import org.caesar.domain.search.vo.PageVO;

import java.util.List;

// 搜索服务接口(V为对外VO数据类型)
public interface SearchService<V extends IndexVO> {

    /**
     * 综合搜索
     * @param text 查询关键词
     * @param from 页数
     * @param size 页大小
     * @return 查询结果
     */
    PageVO<V> search(String text, int from, int size);

    /**
     * 排序搜索
     * @param text  用户文本
     * @param field 排序字段
     * @param from  开始页
     * @param size  页大小
     * @return      搜索结果
     */
    PageVO<V> sortSearch(String text, SortField field, int from, int size);

    /**
     * @param text 用户文本
     * @param size 建议数量
     * @return     自动补全
     */
    List<String> suggestion(String text, int size);

    /**
     * 插入索引，若指定id已存在，则会覆盖旧数据
     * @param indices 新增或更新的索引数据
     */
    void insertIndex(List<V> indices);

    /**
     * 删除索引
     * @param ids       待删除索引id
     */
    void deleteIndex(List<Long> ids);

    /**
     * @return 服务能够处理的数据类型
     */
    DataSource getDataSource();
}
