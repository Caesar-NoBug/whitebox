package org.caesar.question.model;

import com.alibaba.fastjson.JSON;
import org.caesar.domain.executor.enums.SubmitCodeResultType;
import org.caesar.domain.question.vo.SubmitCodeResultVO;
import org.caesar.question.model.entity.SubmitCodeResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MsResultStruct {

    @Mapping(target = "type", source = "type", qualifiedByName = "obj2Str")
    @Mapping(target = "time", source = "time", qualifiedByName = "obj2Str")
    @Mapping(target = "memory", source = "memory", qualifiedByName = "obj2Str")
    SubmitCodeResult VOtoDO(SubmitCodeResultVO submitCodeResultVO);

    @Mapping(target = "type", source = "type", qualifiedByName = "str2TypeList")
    @Mapping(target = "time", source = "time", qualifiedByName = "str2LongList")
    @Mapping(target = "memory", source = "memory", qualifiedByName = "str2LongList")
    SubmitCodeResultVO DOtoVO(SubmitCodeResult submitCodeResult);

    @Named("str2LongList")
    default List<Long> str2LongList(String str) {
        return JSON.parseArray(str, Long.class);
    }

    @Named("obj2Str")
    default String obj2Str(Object object) {
        return JSON.toJSONString(object);
    }

    @Named("str2TypeList")
    default List<SubmitCodeResultType> str2TypeList(String str) {
        return JSON.parseArray(str, SubmitCodeResultType.class);
    }

}
