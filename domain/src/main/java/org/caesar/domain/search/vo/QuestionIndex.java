package org.caesar.domain.search.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.apache.lucene.util.ArrayUtil;
import org.caesar.domain.constant.StrConstant;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.CompletionField;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.core.completion.Completion;

import javax.annotation.PostConstruct;
import java.util.Collections;

@Data
@FieldNameConstants
@Document(indexName = QuestionIndex.INDEX_NAME, createIndex = false)
@NoArgsConstructor
@AllArgsConstructor
public class QuestionIndex implements Index{

    public static final String INDEX_NAME = "question_index";

    /**
     * 问题主键
     */
    @Id
    private Long id;

    /**
     * 检索凭据
     */
    @Field(type = FieldType.Text, analyzer = "text_analyzer", searchAnalyzer = "ik_smart")
    private String all;

    /**
     * 补全字段
     */
    @CompletionField(analyzer = "completion_analyzer")
    private Completion suggestion;

    /**
     * 问题标题
     */
    @Field(type = FieldType.Text, store = true, copyTo = {"all", "suggestion"})
    private String title;

    /**
     * 问题内容
     */
    @Field(type = FieldType.Text, copyTo = "all")
    private String content;

    /**
     * 问题标签
     */
    @Field(type = FieldType.Keyword, store = true, copyTo = {"all", "suggestion"})
    private String[] tag;

    /**
     * 点赞数
     */
    @Field(type = FieldType.Integer, store = true)
    private Integer likeNum;

    /**
     * 收藏数
     */
    @Field(type = FieldType.Integer, store = true)
    private Integer favorNum;

    /**
     * 提交数
     */
    @Field(type = FieldType.Integer, store = true)
    private Integer submitNum;

    public void genSuggestion() {
        String[] suggestion = new String[tag.length + 1];

        suggestion[0] = title;
        System.arraycopy(tag, 0, suggestion, 1, tag.length);

        this.suggestion = new Completion(suggestion);
    }

}
