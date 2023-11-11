package org.caesar.domain.request.question;

import lombok.Data;


@Data
public class UpdateQuestionRequest {

    //@NumberChecker(name = "问题id", min = 0)
    private long id;

    /**
     * 问题标题
     */
    //@StringChecker(name = "标题", maxLength = 128)
    private String title;

    /**
     * 问题内容
     */
    //@StringChecker(name = "内容")
    private String content;

    /**
     * 输入用例(JSON数组)
     */
    //@StringChecker(name = "输入用例")
    private String inputCase;

    /**
     * 输出用例(JSON数组)
     */
    //@StringChecker(name = "输出用例")
    private String outputCase;

    /**
     * 问题判断类型(0:精准匹配，1:范围匹配，2:无序匹配，3:包含匹配)
     */
    //@NumberChecker(name = "问题类型", min = 0, max = 3)
    private Integer qType;

    /**
     * 问题标签
     */
    //@StringChecker(name = "标签")
    private String tag;

    /**
     * 问题难度(0:简单，1:普通，2:困难)
     */
    //@NumberChecker(min = 0, max = 3)
    private Integer difficulty;

    /**
     * 点赞数
     */
    //@NumberChecker(name = "点赞数", min = 0)
    private Integer thumbNum;

    /**
     * 收藏数
     */
    //@NumberChecker(name = "收藏数", min = 0)
    private Integer favorNum;

    /**
     * 提交数
     */
    //@NumberChecker(name = "提交数", min = 0)
    private Integer submitNum;


    /**
     * 执行时间限制(ms)
     */
    private Long timeLimit;

    /**
     * 执行内存限制(MB)
     */
    private Long memoryLimit;
}
