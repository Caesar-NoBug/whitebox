package org.caesar.model;

import org.caesar.domain.vo.search.UserIndex;
import org.caesar.model.entity.User;
import org.caesar.model.po.UserPO;
import org.caesar.model.vo.UserVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface MsUserStruct {

    UserPO DOtoPO(User user);

    UserIndex DOtoDTO(User user);

    UserVO DOtoVO(User user);

    User POtoDO(UserPO userPO);
}
