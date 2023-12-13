package org.caesar.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.caesar.domain.article.vo.ArticleMinVO;
import org.caesar.model.entity.ArticleOps;
import org.caesar.model.po.ArticlePO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
* @author caesar
* @description 针对表【article】的数据库操作Mapper
* @createDate 2023-11-29 20:02:28
* @Entity org.caesar.model.entity.Article
*/
@Mapper
public interface ArticleMapper extends BaseMapper<ArticlePO> {

    List<ArticlePO> getArticleHistory(long userId, int size, int offset);

    ArticleOps getArticleOps(long userId, long articleId);

    List<ArticlePO> getUpdatedArticle(LocalDateTime afterTime);

    List<ArticlePO> getArticleMin(Set<Long> articleIds);

    /**
     * @param userId    用户id
     * @param articleId 文章id
     * @param mark      评价
     * @return          是否有评价不同的文章【没有评价也视为不同评价】(有则返回1，没有则返回0)
     */
    boolean hasDiffArticleMark(long userId, long articleId, int mark);

    /**
     * @param userId 用户id
     * @param articleId 文章id
     * @param mark 是否喜欢，-1：不喜欢，0：中等，1：喜欢
     */
    int markArticle(long userId, long articleId, Integer mark);

    /**
     * @param userId    用户id
     * @param articleId 文章id
     * @param favor      评价
     * @return          是否有评价不同的文章【没有评价也视为不同评价】(有则返回1，没有则返回0)
     */
    boolean hasDiffArticleFavor(long userId, long articleId, boolean favor);

    /**
     * @param userId 用户id
     * @param articleId 文章id
     * @param favored 是否收藏
     */
    int favorArticle(long userId, long articleId, Boolean favored);

    int deleteArticleOps(long articleId);

    int addViewHistory(long userId, long articleId, LocalDateTime viewAt);
}




