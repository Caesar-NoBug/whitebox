import org.caesar.ArticleServiceApplication;
import org.caesar.common.repository.CacheRepository;
import org.caesar.constant.RedisPrefix;
import org.caesar.mapper.ArticleMapper;
import org.caesar.model.entity.Article;
import org.caesar.repository.ArticleRepository;
import org.caesar.service.ArticleService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

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
        article.setId(3423423L);
        article.setTitle("一个标题");
        article.setTags("好看/爱看");
        article.setContent("文章内容");
        article.setCreateBy(0L);
        repository.addArticle(article);
        //repository.markArticle(0L, 3423423L);
    }

    @Test
    public void testTransaction() {
        addArticle();
        repository.deleteArticle(0, 3423423L);
    }

    @Test
    public void testViewArticle() {
        service.viewArticle(0, 3423423L);
        System.out.println(cacheRepo.getLogLogCount(RedisPrefix.ARTICLE_VIEW_COUNT + 3423423));
        service.viewArticle(0, 3423423L);
        System.out.println(cacheRepo.getLogLogCount(RedisPrefix.ARTICLE_VIEW_COUNT + 3423423));
        service.viewArticle(0, 3423423L);
        System.out.println(cacheRepo.getLogLogCount(RedisPrefix.ARTICLE_VIEW_COUNT + 3423423));
        service.viewArticle(0, 3423423L);
        System.out.println(cacheRepo.getLogLogCount(RedisPrefix.ARTICLE_VIEW_COUNT + 3423423));
    }
}
