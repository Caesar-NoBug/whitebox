package org.caesar.article.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.caesar.article.model.entity.ArticleHistory;
import org.caesar.article.model.entity.ArticleOps;
import org.caesar.article.model.po.ArticlePO;
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

    /**
     * @param userId 用户id
     * @param size   页大小
     * @param offset 偏移量
     * @return       用户浏览过的文章
     */
    List<ArticleHistory> getArticleHistory(long userId, int size, int offset);

    /**
     * @param userId    用户id
     * @param articleId 文章id
     * @return          用户对文章的操作数据
     */
    ArticleOps getArticleOps(long userId, long articleId);

    /**
     * @param afterTime 最后更新时间
     * @return          afterTime以后更新的文章数
     */
    List<ArticlePO> getUpdatedArticle(LocalDateTime afterTime);

    List<ArticlePO> getRandPreferArticle(long userId, int size);

    // 文章去重（出除用户已经看过的文章）
    List<Long> getUniqueArticle(long userId, List<Long> articleIds);

    /**
     * @param articleIds 文章id
     * @return           文章最小信息（用于展示文章基本信息）
     */
    List<ArticlePO> getArticleMin(Set<Long> articleIds);

    /**
     * @param userId  用户id
     * @param size    最大文章数量
     * @return 获取近期点赞或收藏的文章
     */
    List<ArticlePO> getRecentPreferArticle(long userId, int size);

    /**
     * @param userId  用户id
     * @param size    最大文章数量
     * @return 获取近期浏览过的文章
     */
    List<ArticlePO> getRecentViewedArticle(long userId, int size);

    /**
     * @param historyCount 最大浏览历史数量
     * @return  浏览历史数量超过viewCount的用户id
     */
    List<Long> getTopUsersByHistoryCount(int historyCount);

    /**
     * @param userIds 待删除记录的用户id
     * @param historyCount 最大浏览历史数量
     * 根据浏览时间降序，删除超过最大数量的早期用户浏览历史
     */
    void deleteViewHistory(List<Long> userIds, int historyCount);

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


    /**
     * @param articleId 文章id
     * @return 是否删除文章操作记录成功
     */
    int deleteArticleOps(long articleId);

    /**
     * @param userId    用户id
     * @param articleId 文章id
     * @param viewAt    浏览时间
     * @return          是否成功添加文章浏览记录
     */
    int addViewHistory(long userId, long articleId, LocalDateTime viewAt);
}




