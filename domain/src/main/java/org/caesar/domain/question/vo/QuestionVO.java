package org.caesar.domain.question.vo;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class QuestionVO {

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
    private Integer acceptNum;

    /**
     * 执行时间限制(ms)
     */
    private Long timeLimit;

    /**
     * 执行内存限制(MB)
     */
    private Long memoryLimit;

    /**
     * 评价（-1：踩，0：无，1：赞）
     */
    private int mark;

    /**
     * 是否已收藏
     */
    private boolean favored;

    /**
     * 问题上次更新时间
     */
    private LocalDateTime updateTime;
}
