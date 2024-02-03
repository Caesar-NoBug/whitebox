package org.caesar.article.model.entity;

import cn.hutool.http.HtmlUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.caesar.domain.article.request.AddArticleRequest;
import org.caesar.domain.article.request.UpdateArticleRequest;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class Article {

    /**
     * 文章主键
     */
    private Long id;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章内容
     */
    private String content;

    /**
     * 文章摘要
     */
    private String digest;

    /**
     *  文章类型
     */
    private Integer type;

    /**
     * 文章标签
     */
    private String tag;

    /**
     * 收藏数
     */
    private Long favorNum = 0L;

    /**
     * 浏览数
     */
    private Long viewNum = 0L;

    /**
     * 点赞数
     */
    private Long likeNum = 0L;

    /**
     * 创建者id
     */
    private Long createBy;

    /**
     * 创建时间
     */
    private LocalDateTime createAt;

    /**
     * 更新时间
     */
    private LocalDateTime updateAt;

    /**
     * 逻辑删除字段
     */
    private Boolean isDelete;

    public static Article fromAddRequest(long id, long authorId, AddArticleRequest request) {

        Article article = new Article();
        article.setId(id);
        article.setTitle(request.getTitle());
        article.setContent(request.getContent());
        article.setDigest(request.getDigest());
        article.setTag(request.getTags());
        article.setCreateBy(authorId);
        LocalDateTime now = LocalDateTime.now();
        article.setCreateAt(now);
        article.setUpdateAt(now);

        return article;
    }

    public static Article fromUpdateRequest(UpdateArticleRequest request) {

        Article article = new Article();
        article.setId(request.getId());
        article.setTitle(request.getTitle());
        article.setContent(request.getContent());
        article.setDigest(request.getDigest());
        article.setTag(request.getTags());
        article.setUpdateAt(LocalDateTime.now());

        return article;
    }

    public void filterHtml() {
        content = HtmlUtil.filter(content);
    }

}