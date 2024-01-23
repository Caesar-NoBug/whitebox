import org.caesar.ArticleServiceApplication;
import org.caesar.common.client.UserClient;
import org.caesar.common.repository.CacheRepository;
import org.caesar.domain.common.vo.Response;
import org.caesar.domain.user.vo.UserMinVO;
import org.caesar.mapper.ArticleMapper;
import org.caesar.model.entity.Article;
import org.caesar.repository.ArticleRepository;
import org.caesar.service.ArticleService;
import org.caesar.common.str.StrUtil;
import org.caesar.task.HotArticleTask;
import org.caesar.task.IncSyncArticleTask;
import org.caesar.util.RedisKey;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.BoundZSetOperations;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;

@SpringBootTest(classes = ArticleServiceApplication.class)
public class ArticleTest {

    @Resource
    private ArticleRepository repository;

    @Resource
    private ArticleService service;

    @Resource
    private CacheRepository cacheRepo;

    @Resource
    private ArticleMapper mapper;

    @Resource
    private HotArticleTask hotArticleTask;

    @Resource
    private IncSyncArticleTask incSyncArticleTask;

    @Resource
    private UserClient userClient;

    @Test
    public void testMark() {
        System.out.println("--------------------\n\n");
        System.out.println(repository.markArticle(0L, 3423423L, 1));
        System.out.println(repository.markArticle(0L, 3423423L, 1));
        System.out.println("\n\n--------------------");
        //System.out.println(repository.markArticle(0L, 3423423L, 1));
        //System.out.println(repository.markArticle(0L, 3423423L, -1));
    }

    @Test
    public void addArticle() {
        Article article = new Article();

        for (long i = 12; i < 40; i++) {
            article.setId(i);
            article.setDigest("摘要:" + StrUtil.genRandCNStr(10));
            article.setTitle("标题:" + StrUtil.genRandCNStr(10));
            article.setTag("标签/" + StrUtil.genRandCNStr(10));
            article.setContent("内容:" + StrUtil.genRandCNStr(10));
            article.setCreateBy(0L);
            repository.addArticle(article);
        }
        //repository.markArticle(0L, 3423423L);
    }

    @Test
    public void testTransaction() {
        addArticle();
        repository.deleteArticle(0, 3423423L);
    }

    @Test
    public void testViewArticle() {
        /*service.viewArticle(0, 3423423L);
        System.out.println(cacheRepo.getLogLogCount(RedisPrefix.ARTICLE_VIEW_COUNT + 3423423));
        service.viewArticle(0, 3423423L);
        System.out.println(cacheRepo.getLogLogCount(RedisPrefix.ARTICLE_VIEW_COUNT + 3423423));
        service.viewArticle(0, 3423423L);
        System.out.println(cacheRepo.getLogLogCount(RedisPrefix.ARTICLE_VIEW_COUNT + 3423423));
        service.viewArticle(0, 3423423L);
        System.out.println(cacheRepo.getLogLogCount(RedisPrefix.ARTICLE_VIEW_COUNT + 3423423));
    */}

    @Test
    public void testHotArticle() {
        Random random = new Random();
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < 1000; i++) {
            long articleId = random.nextInt(40);
            long userId = random.nextInt(1000);
            repository.addViewHistory(userId, articleId, now);

            if(i % 100 == 0) {
                hotArticleTask.run();
                System.out.println(service.getHotArticle() + "\n---------------------------------------------------------\n");
            }

        }
        /*service.viewArticle(0, 0);
        service.viewArticle(1, 0);
        service.viewArticle(2, 0);
        service.viewArticle(3, 0);
        service.viewArticle(4, 0);*/
    }

    @Test
    public void initOps() {
        Random random = new Random();
        for (long i = 0; i < 40; i++) {

            cacheRepo.setLongValue(RedisKey.articleFavorCount(i), random.nextInt(1000));
            cacheRepo.setLongValue(RedisKey.articleLikeCount(i), random.nextInt(50000));
        }
    }

    @Test
    public void testSync() {
        incSyncArticleTask.run();
    }

    @Test
    public void testUserClient() {
        System.out.println(userClient.testCircuitBreaker());
        //Response<Map<Long, UserMinVO>> userMin = userClient.getUserMin(Arrays.asList(0L, 2L));
    }

}
