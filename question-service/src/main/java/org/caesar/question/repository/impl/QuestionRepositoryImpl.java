package org.caesar.question.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.caesar.common.batch.cache.CacheIncTask;
import org.caesar.common.batch.cache.CacheIncTaskHandler;
import org.caesar.common.cache.CacheRepository;
import org.caesar.common.exception.ThrowUtil;
import org.caesar.domain.common.enums.ErrorCode;
import org.caesar.question.constant.CacheKey;
import org.caesar.question.mapper.QuestionMapper;
import org.caesar.question.mapper.QuestionSubmitResultMapper;
import org.caesar.question.model.MsQuestionStruct;
import org.caesar.question.model.entity.QuestionOps;
import org.caesar.question.model.po.QuestionPO;
import org.caesar.question.model.entity.Question;
import org.caesar.question.model.entity.SubmitCodeResult;
import org.caesar.question.repository.QuestionRepository;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author caesar
 * @description 针对表【question】的数据库操作Repository实现
 * @createDate 2023-08-30 10:04:07
 */
@Repository
public class QuestionRepositoryImpl extends ServiceImpl<QuestionMapper, QuestionPO> implements QuestionRepository {

    @Resource
    private MsQuestionStruct questionStruct;

    @Resource
    private QuestionSubmitResultMapper resultMapper;

    @Resource
    private CacheRepository cacheRepo;

    @Resource
    private CacheIncTaskHandler cacheIncTaskHandler;

    @Override
    public void addQuestion(Question question) {
        ThrowUtil.ifFalse(save(questionStruct.DOtoPO(question)), ErrorCode.SYSTEM_ERROR,
                "Fail to insert question to database. " + question);

        // 初始化redis中对应的计数
        long questionId = question.getId();
        cacheRepo.setLongValue(CacheKey.questionLikeCount(questionId), 0);
        cacheRepo.setLongValue(CacheKey.questionFavorCount(questionId), 0);
        cacheRepo.setLongValue(CacheKey.questionSubmitCount(questionId), 0);
        cacheRepo.setLongValue(CacheKey.questionPassCount(questionId), 0);
    }

    @Override
    public void deleteQuestion(long id) {
        ThrowUtil.ifFalse(removeById(id), ErrorCode.NOT_FIND_ERROR, "The question does not exists.");
    }

    @Override
    public void updateQuestion(Question question) {
        ThrowUtil.ifFalse(updateById(questionStruct.DOtoPO(question)), ErrorCode.NOT_FIND_ERROR,
                "The question does not exists. question id:" + question.getId());
    }

    @Override
    public Question getQuestionById(long id) {
        return questionStruct.POtoDO(baseMapper.selectById(id));
    }

    @Override
    public QuestionOps getQuestionOps(long userId, long questionId) {
        return baseMapper.getQuestionOps(userId, questionId);
    }

    @Override
    public void markQuestion(long userId, long questionId, int mark) {

        QuestionOps ops = baseMapper.getQuestionOps(userId, questionId);
        int prevMark = Objects.isNull(ops) ? 0 : ops.getMark();

        // 如果点赞状态一致，无需修改
        ThrowUtil.ifTrue(mark == prevMark, ErrorCode.ALREADY_EXIST_ERROR, "Question has already been marked.");

        cacheIncTaskHandler.addTask(CacheKey.questionLikeCount(questionId), new CacheIncTask(mark - prevMark));

        ThrowUtil.ifFalse(baseMapper.markQuestion(userId, questionId, mark, LocalDateTime.now()), ErrorCode.SYSTEM_ERROR, "Fail to update question mark status in database.");
    }

    @Override
    public void favorQuestion(long userId, long questionId, boolean isFavor) {

        QuestionOps ops = baseMapper.getQuestionOps(userId, questionId);
        boolean prevFavor = Objects.isNull(ops) ? false : ops.isFavored();

        // 如果点赞状态一致，无需修改
        ThrowUtil.ifTrue(isFavor == prevFavor, ErrorCode.ALREADY_EXIST_ERROR, "Question has already been favored.");

        cacheIncTaskHandler.addTask(CacheKey.questionFavorCount(questionId), new CacheIncTask(isFavor ? 1 : -1));

        ThrowUtil.ifFalse(baseMapper.favorQuestion(userId, questionId, isFavor, LocalDateTime.now()), ErrorCode.SYSTEM_ERROR, "Fail to update question favor status in database.");
    }

    @Override
    public List<Question> getUpdatedQuestion(LocalDateTime afterTime) {
        List<QuestionPO> questionDOList = baseMapper.selectQuestionByUpdateTime(afterTime);
        return questionDOList.stream().map(questionStruct::POtoDO).collect(Collectors.toList());
    }

    @Override
    public SubmitCodeResult getSubmitResult(long userId, long questionId, int submitId) {

        QueryWrapper<SubmitCodeResult> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .eq("question_id", questionId)
                .eq("submit_id", submitId);

        return resultMapper.selectOne(queryWrapper);
    }

    @Override
    public Page<SubmitCodeResult> listSubmitResult(long userId, long questionId, int from, int size) {
        QueryWrapper<SubmitCodeResult> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .select(SubmitCodeResult.MIN_FIELDS)
                .eq("user_id", userId)
                .eq("question_id", questionId)
                .orderByDesc("create_at");

        Page<SubmitCodeResult> page = new Page<>(from, size);

        return resultMapper.selectPage(page, queryWrapper);
    }

    @Override
    public void addSubmitResult(SubmitCodeResult submitCodeResult) {

        submitCodeResult.setCreateAt(LocalDateTime.now());

        boolean insertFlag = resultMapper.insert(submitCodeResult) > 0;

        ThrowUtil.ifFalse(insertFlag, ErrorCode.SYSTEM_ERROR, "Fail to insert the submit result to database.");
    }

    @Override
    public void updateSubmitResult(SubmitCodeResult submitCodeResult) {

        UpdateWrapper<SubmitCodeResult> updateWrapper = new UpdateWrapper<>();
        updateWrapper
                .eq("user_id", submitCodeResult.getUserId())
                .eq("question_id", submitCodeResult.getQuestionId())
                .eq("submit_id", submitCodeResult.getSubmitId());

        updateWrapper.set("result", submitCodeResult.getResult())
                .set("status", submitCodeResult.getStatus())
                .set("type", submitCodeResult.getType())
                .set("message", submitCodeResult.getMessage())
                .set("time", submitCodeResult.getTime())
                .set("memory", submitCodeResult.getMemory());

        boolean updateFlag = resultMapper.update(submitCodeResult, updateWrapper) > 0;

        ThrowUtil.ifFalse(updateFlag, ErrorCode.SYSTEM_ERROR, "Fail to update the submit result to database.");
    }

}
