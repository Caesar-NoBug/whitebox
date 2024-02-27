package org.caesar.question.task;
import lombok.extern.slf4j.Slf4j;
import org.caesar.common.client.SearchClient;
import org.caesar.common.util.DataFilter;
import org.caesar.domain.search.vo.QuestionIndexVO;
import org.caesar.question.model.MsQuestionStruct;
import org.caesar.question.model.entity.Question;
import org.caesar.question.repository.QuestionRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class IncSyncQuestionTask {

    @Resource
    private QuestionRepository questionRepository;

    @Resource
    private SearchClient searchClient;

    @Resource
    private MsQuestionStruct poMapper;
    //TODO: 把 log.info 改成 LogUtil.info, 设置系统日志自己的格式

    /**
     * 每分钟执行一次
     */
    @Scheduled(fixedRate = 60 * 1000)
    public void run() {
        // 查询近 5 分钟内的数据
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);

        //syncQuestion(fiveMinutesAgo);
        syncQuestion(LocalDateTime.of(2000, 1, 1, 0, 0, 0, 0));
    }

    @Resource
    private DataFilter<Long> questionFilter;

    public void syncQuestion(LocalDateTime startTime) {

        List<Question> changedQuestion = questionRepository.getUpdatedQuestion(startTime);

        if (CollectionUtils.isEmpty(changedQuestion)) {
            log.info("no sync question");
            return;
        }

        List<QuestionIndexVO> removedQuestion = new ArrayList<>();
        List<QuestionIndexVO> updatedQuestion = new ArrayList<>();

        for (Question question : changedQuestion) {

            QuestionIndexVO questionIndex = poMapper.DOtoDTO(question);

            if(question.getIsDelete() == 0)
                updatedQuestion.add(questionIndex);
            else
                removedQuestion.add(questionIndex);

        }

        final int pageSize = 500;
        int removedSize = removedQuestion.size();
        int updatedSize = updatedQuestion.size();

        log.info("SyncQuestionToEs start, total {}", updatedSize);
        for (int i = 0; i < updatedSize; i += pageSize) {
            int end = Math.min(i + pageSize, updatedSize);
            log.info("sync from {} to {}", i, end);
            searchClient.syncQuestionIndex(updatedQuestion.subList(i, end));
        }
        log.info("SyncQuestionToEs end, total {}", updatedSize);

        log.info("RemoveQuestionToEs start, total {}", removedSize);
        for (int i = 0; i < removedSize; i += pageSize) {
            int end = Math.min(i + pageSize, removedSize);
            log.info("remove from {} to {}", i, end);
            searchClient.syncQuestionIndex(removedQuestion.subList(i, end));
        }
        log.info("RemoveQuestionToEs end, total {}", removedSize);
    }
}