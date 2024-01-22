package org.caesar.question.model;

import org.caesar.domain.search.vo.QuestionIndexVO;
import org.caesar.question.model.po.QuestionPO;
import org.caesar.question.model.entity.Question;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MsQuestionStruct {

    QuestionPO DOtoPO(Question question);
    //@Mapping(source = "tag", target = "tag", qualifiedByName = "mapTag")
    QuestionIndexVO DOtoDTO(Question question);

    Question POtoDO(QuestionPO questionPO);
}
