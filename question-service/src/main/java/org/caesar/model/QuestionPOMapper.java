package org.caesar.model;

import org.caesar.domain.search.vo.QuestionIndexVO;
import org.caesar.model.po.QuestionPO;
import org.caesar.model.entity.Question;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.Qualifier;

@Mapper(componentModel = "spring")
public interface QuestionPOMapper {

    QuestionPO DOtoPO(Question question);
    //@Mapping(source = "tag", target = "tag", qualifiedByName = "mapTag")
    QuestionIndexVO DOtoDTO(Question question);

    Question POtoDO(QuestionPO questionPO);

    /*@Named("mapTag")
    default String[] mapTag(String tag) {
        return tag.split("/");
    }*/

}
