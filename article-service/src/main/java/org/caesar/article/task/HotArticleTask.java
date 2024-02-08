package org.caesar.article.task;

import lombok.extern.slf4j.Slf4j;
import org.caesar.domain.article.vo.ArticleMinVO;
import org.caesar.article.service.ArticleService;
import org.caesar.article.constant.CacheKey;
import org.caesar.common.cache.CacheRepository;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
// 更新热门文章数据
public class HotArticleTask {

    // 更新热门文章的时间为 1 小时
    public static final int HOT_UPDATE_FREQUENCY = 60 * 60 * 1000;
    // 热门文章统计范围为 12 小时
    public static final int HOT_RANGE = HOT_UPDATE_FREQUENCY * 12;
    // 热门文章数量为 10
    public static final int HOT_ARTICLE_COUNT = 10;
    //2023-01-01 00:00
    public static final long BEGIN_TIMESTAMP = 1672531200L;

    @Resource
    private CacheRepository cacheRepo;

    @Resource
    private ArticleService articleService;

    @Scheduled(fixedRate = HOT_UPDATE_FREQUENCY)
    public void run() {
        // 候选文章集合
        BoundZSetOperations<String, Long> candidateZSet = cacheRepo.getSortedSet(CacheKey.candidateArticleSet());

        // 当前时间
        long now = System.currentTimeMillis() / 1000 - BEGIN_TIMESTAMP;

        Set<Long> articleIds = candidateZSet.range(0, Math.max(0, candidateZSet.size() - 1));
        //System.out.println("articleIds:" + articleIds);

        articleIds.forEach(articleId -> {
            BoundZSetOperations<String, Long> historySet = cacheRepo.getSortedSet(CacheKey.articleHistorySet(articleId));
            // 删除过期浏览历史
            historySet.removeRangeByScore(0, now - HOT_RANGE);
            double viewCount = historySet.size();

            // 删除没有浏览量的文章
            if(viewCount <= 0) {
                candidateZSet.remove(articleId);
            }
            // 更新近期文章中的浏览数据
            else {
                candidateZSet.add(articleId, viewCount);
            }
        });

        // 更新热门文章数据
        Set<Long> hotArticleIds = candidateZSet.reverseRange(0, HOT_ARTICLE_COUNT - 1);
        System.out.println("hotArticleIds:" + hotArticleIds);
        List<ArticleMinVO> hotArticles = articleService.getArticleMin(hotArticleIds);

        cacheRepo.updateObject(CacheKey.hotArticleSet(), hotArticles);
    }

}
