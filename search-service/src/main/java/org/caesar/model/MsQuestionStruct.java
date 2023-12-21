package org.caesar.model;

import org.caesar.domain.search.vo.QuestionIndexVO;
import org.caesar.model.entity.QuestionIndex;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface MsQuestionStruct {

    @Mapping(source = "tag", target = "tag", qualifiedByName = "splitTag")
    QuestionIndex VOtoDO(QuestionIndexVO questionIndexVO);

    @Mapping(source = "tag", target = "tag", qualifiedByName = "joinTag")
    QuestionIndexVO DOtoVO(QuestionIndex questionIndex);

    @Named("splitTag")
    static String[] splitTag(String tag) {
        return tag.split("/");
    }

    @Named("joinTag")
    static String joinTag(String[] tag) {
        return String.join("/", tag);
    }

}
