package org.caesar.question.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 
 * @TableName question_submit_result
 */
@TableName(value ="question_submit_result")
@Data
public class QuestionSubmitResult implements Serializable {
    /**
     * 用户id
     */
    private Long userId;

    /**
     * 问题id
     */
    private Long questionId;

    /**
     * 提交id
     */
    private Integer submitId;

    /**
     * 判题结果
     */
    private String result;

    /**
     * 判题时间
     */
    private LocalDateTime createAt;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}