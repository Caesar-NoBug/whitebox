package org.caesar.search.model.entity;


import lombok.experimental.FieldNameConstants;
import org.caesar.domain.search.vo.ArticleIndexVO;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.data.elasticsearch.core.completion.Completion;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 文章索引
 */
@FieldNameConstants
@Document(indexName = ArticleIndex.INDEX_NAME, createIndex = false)
public class ArticleIndex implements Index {

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
    @Field(type = FieldType.Date, store = true, format = DateFormat.basic_date)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateAt;

    public ArticleIndex(ArticleIndexVO indexVO) {
        this.id = indexVO.getId();
        this.title = indexVO.getTitle();
        this.digest = indexVO.getDigest();
        this.content = indexVO.getContent();
        this.tag = indexVO.getTag().split("/");
        this.favorNum = indexVO.getFavorNum();
        this.viewNum = indexVO.getViewNum();
        this.likeNum = indexVO.getLikeNum();
        this.updateAt = indexVO.getUpdateAt();
        String[] suggestion = new String[tag.length + 1];
        suggestion[0] = title;
        System.arraycopy(tag, 0, suggestion, 1, tag.length);
        this.suggestion = new Completion(suggestion);
    }

    public ArticleIndexVO toArticleIndexVO() {
        ArticleIndexVO articleIndexVO = new ArticleIndexVO();
        articleIndexVO.setId(id);
        articleIndexVO.setTitle(title);
        articleIndexVO.setDigest(digest);
        articleIndexVO.setTag(String.join("/", tag));
        articleIndexVO.setFavorNum(favorNum);
        articleIndexVO.setViewNum(viewNum);
        articleIndexVO.setLikeNum(likeNum);
        return articleIndexVO;
    }
}
