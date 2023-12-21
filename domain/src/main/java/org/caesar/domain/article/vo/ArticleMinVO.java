package org.caesar.domain.article.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.caesar.domain.search.vo.ArticleIndexVO;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
@NoArgsConstructor
public class ArticleMinVO implements Serializable {

    /**
     * 文章主键
     */
    private Long id;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章摘要
     */
    private String digest;

    /**
     * 文章标签
     */
    private String tag;

    /**
     * 浏览数
     */
    private Integer viewNum;

    /**
     * 点赞数
     */
    private Integer likeNum;

    /**
     * 更新时间
     */
    private LocalDateTime updateAt;

    public ArticleMinVO(ArticleIndexVO articleIndexVO) {

        if (Objects.nonNull(articleIndexVO)) {
            this.id = articleIndexVO.getId();
            this.title = articleIndexVO.getTitle();
            this.digest = articleIndexVO.getDigest();
            this.tag = articleIndexVO.getTag();
            this.viewNum = articleIndexVO.getViewNum();
            this.likeNum = articleIndexVO.getLikeNum();
            this.updateAt = articleIndexVO.getUpdateAt();
        }

    }

}
