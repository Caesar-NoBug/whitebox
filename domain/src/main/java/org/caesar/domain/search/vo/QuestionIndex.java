package org.caesar.domain.search.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.caesar.domain.constant.StrConstant;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Document(indexName = StrConstant.QUESTION_INDEX, createIndex = false)
@NoArgsConstructor
@AllArgsConstructor
public class QuestionIndex implements Index{

    public static final String FIELD_ALL = "all";
    public static final String FIELD_TITLE = "title";
    public static final String FIELD_CONTENT = "content";
    public static final String FIELD_TAG = "tag";
    public static final String FIELD_LIKE_NUM = "likeNum";
    public static final String FIELD_FAVOR_NUM = "favorNum";
    public static final String FIELD_SUBMIT_NUM = "submitNum";

    public static final String[] RESULT_FIELDS = new String[] {
        FIELD_TITLE, FIELD_TAG, FIELD_LIKE_NUM, FIELD_FAVOR_NUM, FIELD_SUBMIT_NUM
    };

    /**
     * 问题主键
     */
    @Id
    private Long id;

    /**
     * 检索凭据
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String all;

    /**
     * 问题标题
     */
    @Field(type = FieldType.Text, store = true, analyzer = "ik_max_word", copyTo = "all")
    private String title;

    /**
     * 问题内容
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", copyTo = "all")
    private String content;

    /**
     * 问题标签
     */
    @Field(type = FieldType.Keyword, store = true, copyTo = "all")
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

}
