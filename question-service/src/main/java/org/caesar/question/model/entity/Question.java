package org.caesar.question.model.entity;

import com.alibaba.fastjson.JSON;
import lombok.*;
import org.caesar.domain.executor.enums.SubmitCodeResultType;
import org.caesar.common.exception.ThrowUtil;
import org.caesar.domain.executor.response.ExecuteCodeResponse;
import org.caesar.common.str.JSONUtil;
import org.caesar.common.vo.StatusMap;
import org.caesar.question.model.enums.JudgeStatus;
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
    private Integer isDelete;

    /**
     * 创建问题时间
     */
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

    public SubmitCodeResult judge(QuestionJudgeManager judgeManager, ExecuteCodeResponse executeResponse, SubmitCodeResult submitCodeResult) {
        List<SubmitCodeResultType> resultTypes = executeResponse.getType();
        List<String> outputArray = getOutputArray();

        submitCodeResult.setExecTime(executeResponse.getTime());
        submitCodeResult.setExecMemory(executeResponse.getMemory());

        //代码执行时出现异常则直接返回
        if (!executeResponse.isSuccess()) {
            submitCodeResult.setJudgeStatus(JudgeStatus.FAILED);
            submitCodeResult.setResultType(executeResponse.getType());
            submitCodeResult.setMessage(executeResponse.getMessage());
            return submitCodeResult;
        }

        List<String> codeResult = executeResponse.getResult();

        StatusMap map = judgeManager.judge(new JudgeParam(codeResult, outputArray, qType));

        boolean success = true;

        for (int i = 0; i < codeResult.size(); i++) {

            //无异常则判断结果是否正确
            if (SubmitCodeResultType.TEMPORARY_ACCEPTED.equals(resultTypes.get(i))) {
                if (map.isFail(i)) {
                    resultTypes.set(i, SubmitCodeResultType.WRONG_ANSWER);

                    //记录第一个不匹配的错误信息
                    if (success) {
                        String wrongResult = codeResult.get(i);
                        String correctResult = judgeManager.generateAnswer(qType, outputArray.get(i));
                        submitCodeResult.setResult(wrongResult);
                        submitCodeResult.setMessage(String.format(WRONG_ANSWER_MESSAGE, wrongResult, correctResult));
                        success = false;
                    }

                } else
                    resultTypes.set(i, SubmitCodeResultType.ACCEPTED);
            }
            //有异常则直接返回
            else if (success) {
                submitCodeResult.setMessage(executeResponse.getMessage());
                success = false;
            }

        }

        submitCodeResult.setJudgeStatus(success ? JudgeStatus.ACCEPTED : JudgeStatus.FAILED);

        submitCodeResult.setResultType(resultTypes);

        return submitCodeResult;
    }

}
