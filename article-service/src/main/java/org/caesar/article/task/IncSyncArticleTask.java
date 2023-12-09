package org.caesar.article.task;

import lombok.extern.slf4j.Slf4j;
import org.caesar.article.model.MsArticleStruct;
import org.caesar.article.model.entity.Article;
import org.caesar.article.repository.ArticleRepository;
import org.caesar.common.client.SearchServiceClient;
import org.caesar.domain.search.enums.DataSource;
import org.caesar.domain.search.vo.ArticleIndex;
import org.caesar.domain.search.vo.QuestionIndex;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class IncSyncArticleTask {

    @Resource
    private ArticleRepository articleRepo;

    @Resource
    private SearchServiceClient searchServiceClient;

    @Resource
    private MsArticleStruct articleStruct;

    /**
     * 每分钟执行一次
     */
    @Scheduled(fixedRate = 60 * 1000)
    public void run() {
        // 查询近 5 分钟内的数据
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);
        List<Article> changedArticle = articleRepo.getUpdatedArticle(fiveMinutesAgo);

        if (CollectionUtils.isEmpty(changedArticle)) {
            log.info("no sync article");
            return;
        }

        List<ArticleIndex> removedArticle = new ArrayList<>();
        List<ArticleIndex> updatedArticle = new ArrayList<>();

        for (Article article : changedArticle) {

            ArticleIndex questionIndex = articleStruct.DOtoIndex(article);

            if (article.getIsDelete())
                removedArticle.add(questionIndex);
            else
                updatedArticle.add(questionIndex);
        }

        final int pageSize = 20;
        int removedSize = removedArticle.size();
        int updatedSize = updatedArticle.size();

        log.info("SyncArticleToEs start, total {}", updatedSize);
        for (int i = 0; i < updatedSize; i += pageSize) {
            int end = Math.min(i + pageSize, updatedSize);
            log.info("sync from {} to {}", i, end);
            searchServiceClient.syncIndex(updatedArticle.subList(i, end), DataSource.ARTICLE);
        }
        log.info("SyncArticleToEs end, total {}", updatedSize);

        log.info("RemoveQuestionToEs start, total {}", removedSize);
        for (int i = 0; i < removedSize; i += pageSize) {
            int end = Math.min(i + pageSize, removedSize);
            log.info("remove from {} to {}", i, end);
            searchServiceClient.syncIndex(removedArticle.subList(i, end), DataSource.ARTICLE);
        }
        log.info("RemoveArticleToEs end, total {}", removedSize);
    }

}
