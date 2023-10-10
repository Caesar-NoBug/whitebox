import org.caesar.SearchServiceApplication;
import org.caesar.common.model.vo.QuestionIndex;
import org.caesar.service.impl.QuestionServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest(classes = SearchServiceApplication.class)
public class MyTest {

    @Autowired
    private QuestionServiceImpl questionService;

    @Test
    public void testES() {
        /*QuestionIndex index = new QuestionIndex();
        index.setId(995L);
        index.setTitle("最长回文串");
        index.setContent("给你一个字符串 s，找到 s 中最长的回文子串。\n" +
                "\n" +
                "如果字符串的反序与原始字符串相同，则该字符串称为回文字符串。\n" +
                "\n" +
                "示例 1：\n" +
                "\n" +
                "输入：s = \"babad\"\n" +
                "输出：\"bab\"\n" +
                "解释：\"aba\" 同样是符合题意的答案。\n" +
                "示例 2：\n" +
                "\n" +
                "输入：s = \"cbbd\"\n" +
                "输出：\"bb\"\n" +
                "提示：\n" +
                "\n" +
                "1 <= s.length <= 1000\n" +
                "s 仅由数字和英文字母组成");
        index.setTag(new String[]{"字符串", "动态规划"});
        index.setThumbNum(0);
        index.setFavorNum(0);
        index.setSubmitNum(0);
        questionService.insertQuestionIndex(index);*/

        List<QuestionIndex> query = questionService.query("表达式", 0, 10);
    }

}
