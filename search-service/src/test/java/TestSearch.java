import org.caesar.SearchServiceApplication;
import org.caesar.domain.constant.StrConstant;
import org.caesar.domain.search.enums.QuestionSortField;
import org.caesar.model.entity.QuestionIndex;
import org.caesar.service.impl.QuestionSearchService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;


import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Random;

@SpringBootTest(classes = SearchServiceApplication.class)
public class TestSearch {

    @Resource
    private ElasticsearchOperations operations;

    @Resource
    private QuestionSearchService questionService;

    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Test
    public void testAddIndex() {
        Random random = new Random();
        QuestionIndex index = new QuestionIndex(random.nextLong(), null, null, "最长回文串", "这个是非常好的内容", new String[]{"动态规划", "表达式"}, 5, 6, 0);
        QuestionIndex index1 = new QuestionIndex(random.nextLong(), null, null, "这个是标题", "这个是内容", new String[]{"字符串", "数组", "正则表达式"}, 0, 0, 1);
        QuestionIndex index2 = new QuestionIndex(random.nextLong(), null, null, "这个是一个标题", "这个是好多内容", new String[]{"数组", "表达式"}, 0, 6, 0);
        index.genSuggestion();
        index1.genSuggestion();
        index2.genSuggestion();
        /*questionService.insertIndex(
                Arrays.asList(
                        index,
                        index1,
                        index2
                )
        );*/

        //List<QuestionIndex> query = questionService.search("表达式", 0, 10);
    }

    @Test
    public void testSearch() {
        //testAddIndex();
        System.out.println(questionService.search("sprinboot", 0, 10));
        System.out.println(questionService.search("字符串", 0, 10));
        System.out.println(questionService.search("字z符", 0, 10));
    }

    @Test
    public void testSortSearch() {
        System.out.println(questionService.sortSearch("表达", QuestionSortField.LIKE_NUM, 0, 10));
        System.out.println(questionService.sortSearch("表d", QuestionSortField.LIKE_NUM, 0, 10));
        System.out.println(questionService.sortSearch("表达式", QuestionSortField.LIKE_NUM, 0, 10));
    }

    @Test
    public void testCompletion() {
        System.out.println(questionService.suggestion("zg", 10));
    }

    @Test
    public void testInit() {
        System.out.println("项目启动");
        IndexCoordinates coordinates = IndexCoordinates.of(StrConstant.QUESTION_INDEX);
        IndexOperations indexOperations = operations.indexOps(coordinates);
        indexOperations.create();
        indexOperations.putMapping(indexOperations.createMapping(QuestionIndex.class));
        indexOperations.refresh();
        System.out.println("创建索引中...");

        operations.save(new QuestionIndex(0L, null, null, "这个是标题", "这个是内容", new String[]{"字符串", "数组", "正则表达式"}, 0, 0, 1));
        operations.save(new QuestionIndex(6L, null, null, "这个是一个标题", "这个是好多内容", new String[]{"数组", "表达式"}, 0, 6, 0));
        operations.save(new QuestionIndex(9L, null, null, "这个是一个大标题", "这个是非常好的内容", new String[]{"数组", "表达式"}, 5, 6, 0));
        System.out.println(questionService.search("字符", 0, 10));
    }

}
