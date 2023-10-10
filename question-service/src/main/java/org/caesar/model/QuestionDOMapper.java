package org.caesar.model;

import org.caesar.model.dao.QuestionDO;
import org.caesar.model.entity.Question;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface QuestionDOMapper {

    QuestionDOMapper INSTANCE = Mappers.getMapper(QuestionDOMapper.class);

    QuestionDO toDO(Question question);

    Question toEntity(QuestionDO questionDO);

   /* public static void main(String[] args) {
        Question question = new Question();
        question.setId(2132L);
        question.setContent("dsfsdfsdfsdfs");
        QuestionDO questionDO = QuestionDOMapper.INSTANCE.toDO(question);
        System.out.println(questionDO);
        System.out.println(QuestionDOMapper.INSTANCE.toEntity(questionDO));
    }*/
}
