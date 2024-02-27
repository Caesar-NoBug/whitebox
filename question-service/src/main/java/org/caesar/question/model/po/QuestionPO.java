package org.caesar.question.model.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.*;

/**
 * 
 * @TableName question
 */
@TableName(value ="question")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionPO implements Serializable {

    /**
     * 问题主键
     */
    @TableId
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
     *  答案(正确代码)
     */
    private String correctCode;

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
    private Integer likeNum;

    /**
     * 收藏数
     */
    private Integer favorNum;

    /**
     * 提交数
     */
    private Integer submitNum;

    /**
     * 通过数
     */
    private Integer passNum;

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
    @TableLogic
    private Integer isDelete;

    /**
     * 创建账号时间
     */
    private LocalDateTime createTime;

    /**
     * 账号上次更新时间
     */
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}