import org.caesar.SearchServiceApplication;
import org.caesar.domain.search.vo.QuestionIndex;
import org.caesar.repository.QuestionRepository;
import org.caesar.service.impl.QuestionSearchService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


import javax.annotation.Resource;
import java.util.Random;

@SpringBootTest(classes = SearchServiceApplication.class)
public class TestSearch {

    @Resource
    private QuestionSearchService questionService;

    @Resource
    private QuestionRepository questionRepository;

    @Test
    public void testAddIndex() {
        QuestionIndex index = new QuestionIndex();
        Random random = new Random();
        //index.setId(random.nextLong());
        index.setId(-747772288042112386L);
        index.setTitle("最长回文串");
        index.setContent("好好好");
        index.setTag(new String[]{"字符串", "动态规划"});
        index.setLikeNum(random.nextInt(100));
        index.setFavorNum(random.nextInt(100));
        index.setSubmitNum(random.nextInt(100));
        questionRepository.save(index);

        //List<QuestionIndex> query = questionService.search("表达式", 0, 10);
    }

    @Test
    public void testSearch() {
        //testAddIndex();
        questionService.search("动态规划", 0, 10);
    }

}
