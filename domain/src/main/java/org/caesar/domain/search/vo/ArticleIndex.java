package org.caesar.domain.search.vo;


import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

/**
 * 文章索引
 */
@Document(indexName = "article_index", createIndex = false)
public class ArticleIndex implements Index{

    public static final String FIELD_ALL = "all";
    public static final String FIELD_TITLE = "title";
    public static final String FIELD_CONTENT = "content";
    public static final String FIELD_DIGEST = "digest";
    public static final String FIELD_TAG = "tag";
    public static final String FIELD_LIKE_NUM = "likeNum";
    public static final String FIELD_FAVOR_NUM = "favorNum";
    public static final String FIELD_VIEW_NUM = "viewNum";
    public static final String FIELD_UPDATE_TIME = "updateTime";

    public static final String[] RESULT_FIELDS = new String[] {
            FIELD_TITLE, FIELD_DIGEST, FIELD_TAG, FIELD_LIKE_NUM, FIELD_FAVOR_NUM, FIELD_VIEW_NUM, FIELD_UPDATE_TIME
    };

    /**
     * 文章主键
     */
    @Id
    private Long id;

    /**
     * 检索凭据
     */
    @Field(type = FieldType.Text)
    private String all;

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
