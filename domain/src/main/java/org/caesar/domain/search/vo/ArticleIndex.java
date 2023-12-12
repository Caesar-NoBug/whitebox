package org.caesar.domain.search.vo;


import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.CompletionField;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.core.completion.Completion;

import java.time.LocalDateTime;

/**
 * 文章索引
 */
@FieldNameConstants
@Document(indexName = ArticleIndex.INDEX_NAME, createIndex = false)
public class ArticleIndex implements Index{

    public static final String INDEX_NAME = "article_index";

    /**
     * 文章主键
     */
    @Id
    private Long id;

    /**
     * 检索凭据
     */
    @Field(type = FieldType.Text, analyzer = "text_analyzer", searchAnalyzer = "ik_smart")
    private String all;

    @CompletionField(analyzer = "completion_analyzer")
    private Completion suggestion;

    /**
     * 文章标题
     */
    @Field(type = FieldType.Text, store = true, copyTo = "all")
    private String title;

    /**
     * 文章摘要
     */
    @Field(type = FieldType.Text, store = true, copyTo = "all")
    private String digest;

    /**
     * 文章内容
     */
    @Field(type = FieldType.Text, copyTo = "all")
    private String content;

    /**
     * 文章标签
     */
    //  TODO: 想办法增加tag在索引中时的权重
    @Field(type = FieldType.Keyword, store = true, copyTo = "all")
    private String[] tag;

    /**
     * 收藏数
     */
    @Field(type = FieldType.Integer, store = true)
    private Integer favorNum;

    /**
     * 浏览数
     */
    @Field(type = FieldType.Integer, store = true)
    private Integer viewNum;

    /**
     * 点赞数
     */
    @Field(type = FieldType.Integer, store = true)
    private Integer likeNum;

    /**
     * 修改时间
     */
    @Field(type = FieldType.Date, store = true)
    private LocalDateTime updateAt;

}
