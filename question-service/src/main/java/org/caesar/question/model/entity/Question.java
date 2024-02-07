package org.caesar.question.model.entity;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.caesar.common.log.Logger;
import org.caesar.domain.executor.enums.CodeResultType;
import org.caesar.common.exception.ThrowUtil;
import org.caesar.domain.executor.response.ExecuteCodeResponse;
import org.caesar.domain.question.response.SubmitCodeResult;
import org.caesar.common.str.JSONUtil;
import org.caesar.common.vo.StatusMap;
import org.caesar.question.model.vo.JudgeParam;
import org.caesar.question.judge.QuestionJudgeManager;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class Question implements Serializable {

    private static final long serialVersionUID = 1L;

    //默认时间限制为2s
    public static final Long DEFAULT_TIME_LIMIT = 2000L;

    //默认空间限制为128MB
    public static final Long DEFAULT_MEMORY_LIMIT = 1L << 7;

    //答案错误信息
    public static final String WRONG_ANSWER_MESSAGE = "运行结果错误\n\t运行结果:\n%s\n\t目标结果:\n%s";

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

    public Question(QuestionJudgeManager judgeManager, Long id, String title, String content, String inputCase, String outputCase, Integer qType, String tag, Integer difficulty, Integer likeNum, Integer favorNum, Integer submitNum, Long timeLimit, Long memoryLimit, Integer isDelete, LocalDateTime createTime, LocalDateTime updateTime) {

        ThrowUtil.validate(JSONUtil.checkJSONArray(inputCase) & JSONUtil.checkJSONArray(outputCase),
                "非法输入，输入的输入用例和输出用例必须为JSON字符串数组格式");

        List<String> outputArray = JSON.parseArray(getOutputCase(), String.class);

        ThrowUtil.validate(judgeManager.checkOutputCase(qType, outputArray), "非法输入，问题校验参数不符合要求");

        this.id = id;
        this.title = title;
        this.content = content;
        this.inputCase = inputCase;
        this.outputCase = outputCase;
        this.qType = qType;
        this.tag = tag;
        this.difficulty = difficulty;
        this.likeNum = likeNum;
        this.favorNum = favorNum;
        this.submitNum = submitNum;
        this.timeLimit = timeLimit;
        this.memoryLimit = memoryLimit;
        this.isDelete = isDelete;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public List<String> getInputArray() {
        return JSON.parseArray(getInputCase(), String.class);
    }

    public List<String> getOutputArray() {
        return JSON.parseArray(getOutputCase(), String.class);
    }

    @Logger(value = "judge code", visit = false, args = true, result = true, time = true)
    public SubmitCodeResult judge(QuestionJudgeManager judgeManager, ExecuteCodeResponse executeResponse) {
        List<CodeResultType> resultTypes = executeResponse.getType();
        List<Long> time = executeResponse.getTime();
        List<Long> memory = executeResponse.getMemory();
        List<String> outputArray = getOutputArray();
        //执行出现异常则直接返回
        if (!executeResponse.isSuccess()) {
            return new SubmitCodeResult(true, false, executeResponse.getType(),  executeResponse.getMessage(), time, memory);
        }

        List<String> codeResult = executeResponse.getResult();

        StatusMap map = judgeManager.judge(new JudgeParam(codeResult, outputArray, qType));

        boolean success = true;

        SubmitCodeResult submitCodeResult = new SubmitCodeResult();
        submitCodeResult.setTime(time);
        submitCodeResult.setMemory(memory);

        for (int i = 0; i < codeResult.size(); i++) {

            //无异常则判断结果是否正确
            if (CodeResultType.TEMPORARY_ACCEPTED.equals(resultTypes.get(i))) {
                if (map.isFail(i)) {
                    resultTypes.set(i, CodeResultType.WRONG_ANSWER);

                    //记录第一个不匹配的错误信息
                    if (success) {
                        String wrongResult = codeResult.get(i);
                        String correctResult = judgeManager.generateAnswer(qType, outputArray.get(i));
                        submitCodeResult.setMessage(String.format(WRONG_ANSWER_MESSAGE, wrongResult, correctResult));
                        success = false;
                    }

                } else
                    resultTypes.set(i, CodeResultType.ACCEPTED);
            }
            //有异常则直接返回
            else if (success) {
                submitCodeResult.setMessage(executeResponse.getMessage());
                success = false;
            }

        }

        submitCodeResult.setComplete(true);
        submitCodeResult.setSuccess(success);

        if(!success) submitCodeResult.setType(resultTypes);

        return submitCodeResult;
    }

}
