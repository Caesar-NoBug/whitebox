package org.caesar.question.model;

import org.caesar.domain.question.vo.QuestionVO;
import org.caesar.domain.search.vo.QuestionIndexVO;
import org.caesar.question.model.po.QuestionPO;
import org.caesar.question.model.entity.Question;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MsQuestionStruct {

    QuestionVO DOtoVO(Question question);

    QuestionPO DOtoPO(Question question);

    QuestionIndexVO DOtoDTO(Question question);

    Question VOtoDO(QuestionVO questionVO);

    Question POtoDO(QuestionPO questionPO);
}
