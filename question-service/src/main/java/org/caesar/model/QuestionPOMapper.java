package org.caesar.model;

import org.caesar.domain.vo.search.QuestionIndex;
import org.caesar.model.po.QuestionPO;
import org.caesar.model.entity.Question;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface QuestionPOMapper {

    //TODO: 改成自动注入

    //QuestionPOMapper INSTANCE = Mappers.getMapper(QuestionPOMapper.class);

    QuestionPO DOtoPO(Question question);
    @Mapping(source = "tag", target = "tag", qualifiedByName = "mapTag")
    QuestionIndex DOtoDTO(Question question);

    Question POtoDO(QuestionPO questionPO);

    @Named("mapTag")
    static String[] mapTage(String tag) {
        return tag.split("/");
    }

}
