package org.caesar.model.entity;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.caesar.common.constant.enums.ErrorCode;
import org.caesar.common.exception.BusinessException;
import org.caesar.common.exception.ThrowUtil;
import org.caesar.common.util.JSONUtil;
import org.caesar.constant.RedisPrefix;
import org.caesar.util.QuestionJudgeManager;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class Question implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    //默认时间限制为2s
    public static final Long DEFAULT_TIME_LIMIT = 2000L;

    //默认空间限制为128MB
    public static final Long DEFAULT_MEMORY_LIMIT = 1L << 7;

    /**
     * 问题主键
     */
    private Long id;

    /**
     * 问题标题
     */
    private String title;

    /**
     * 问题内容
     */
    private String content;

    /**
     * 输入用例(JSON数组)
     */
    private String inputCase;

    /**
     * 输出用例(JSON数组)
     */
    private String outputCase;

    /**
     * 问题判断类型(0:精准匹配，1:范围匹配，2:无序匹配，3:包含匹配)
     */
    private Integer qType;

    /**
     * 问题标签
     */
    private String tag;

    /**
     * 问题难度(0:简单，1:普通，2:困难)
     */
    private Integer difficulty;

    /**
     * 点赞数
     */
    private Integer thumbNum;

    /**
     * 收藏数
     */
    private Integer favorNum;

    /**
     * 提交数
     */
    private Integer submitNum;

    /**
     * 执行时间限制(ms)
     */
    private Long timeLimit;

    /**
     * 执行内存限制(MB)
     */
    private Long memoryLimit;

    /**
     * 删除标记位
     */
    private Integer isDelete;

    /**
     * 创建问题时间
     */
    @JsonIgnore
    private LocalDateTime createTime;

    /**
     * 问题上次更新时间
     */
    private LocalDateTime updateTime;

    public Question(Long id, String title, String content, String inputCase, String outputCase, Integer qType, String tag, Integer difficulty, Integer thumbNum, Integer favorNum, Integer submitNum, Long timeLimit, Long memoryLimit, Integer isDelete, LocalDateTime createTime, LocalDateTime updateTime) {

        ThrowUtil.validate(JSONUtil.checkJSONArray(inputCase) & JSONUtil.checkJSONArray(outputCase),
                "非法输入，输入的输入用例和输出用例必须为JSON字符串数组格式");

        this.id = id;
        this.title = title;
        this.content = content;
        this.inputCase = inputCase;
        this.outputCase = outputCase;
        this.qType = qType;
        this.tag = tag;
        this.difficulty = difficulty;
        this.thumbNum = thumbNum;
        this.favorNum = favorNum;
        this.submitNum = submitNum;
        this.timeLimit = timeLimit;
        this.memoryLimit = memoryLimit;
        this.isDelete = isDelete;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }
}
