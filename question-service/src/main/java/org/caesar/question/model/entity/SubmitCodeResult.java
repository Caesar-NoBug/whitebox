package org.caesar.question.model.entity;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;
import org.caesar.domain.executor.enums.CodeLanguage;
import org.caesar.domain.executor.enums.SubmitCodeResultType;
import org.caesar.question.model.enums.JudgeStatus;

/**
 * 
 * @TableName question_submit_result
 */
@TableName(value ="question_submit_result")
@Data
public class SubmitCodeResult implements Serializable {

    public static final String[] MIN_FIELDS = new String[] {
        "user_id", "question_id", "submit_id", "language", "status", "create_at"
    };

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
     * 用户代码
     */
    private String code;

    /**
     * 代码语言
     */
    private CodeLanguage language;

    /**
     * 用户代码执行结果
     */
    private String result;

    /**
     * 是否通过
     */
    private int status;

    /**
     * 结果类型
     */
    private String type;

    /**
     * 错误信息
     */
    private String message;

    /**
     * 运行时间
     */
    private String time;

    /**
     * 占用内存
     */
    private String memory;

    /**
     * 判题时间
     */
    private LocalDateTime createAt;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    public void setJudgeStatus(JudgeStatus judgeStatus) {
        this.status = judgeStatus.getCode();
    }

    public void setExecTime(List<Long> time) {
        this.time = JSON.toJSONString(time);
    }

    public void setExecMemory(List<Long> memory) {
        this.memory = JSON.toJSONString(memory);
    }

    public void setResultType(List<SubmitCodeResultType> type) {
        this.type = JSON.toJSONString(type);
    }
}
