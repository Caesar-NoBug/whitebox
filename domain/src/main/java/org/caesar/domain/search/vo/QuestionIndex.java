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

    /**
     * 问题主键
     */
    @Id
    private Long id;

    /**
     * 检索凭据
     */
    //TODO: 修改es的默认分词器
    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String all;

    /**
     * 问题标题
     */
    @Field(type = FieldType.Text, analyzer = "ik_smart",store = true, copyTo = "all")
    private String title;

    /**
     * 问题内容
     */
    @Field(type = FieldType.Text, analyzer = "ik_smart", copyTo = "all")
    private String content;

    /**
     * 问题标签
     */
    @Field(type = FieldType.Keyword, store = true, copyTo = "all")
    private String[] tag;

    /**
     * 点赞数
     */
    @Field(type = FieldType.Integer)
    private Integer thumbNum;

    /**
     * 收藏数
     */
    @Field(type = FieldType.Integer)
    private Integer favorNum;

    /**
     * 提交数
     */
    @Field(type = FieldType.Integer)
    private Integer submitNum;

}
