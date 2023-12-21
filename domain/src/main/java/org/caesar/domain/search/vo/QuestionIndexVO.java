package org.caesar.domain.search.vo;

import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.elasticsearch.core.completion.Completion;

@Data
@FieldNameConstants
public class QuestionIndexVO implements IndexVO {

    /**
     * 问题主键
     */
    private Long id;

    /**
     * 检索凭据
     */
    private String all;

    /**
     * 补全字段
     */
    private Completion suggestion;

    /**
     * 问题标题
     */
    private String title;

    /**
     * 问题内容
     */
    private String content;

    /**
     * 问题标签
     */
    private String tag;

    /**
     * 点赞数
     */
    private Integer likeNum;

    /**
     * 收藏数
     */
    private Integer favorNum;

    /**
     * 提交数
     */
    private Integer submitNum;
}
