package org.caesar.model;

import org.caesar.domain.search.vo.UserIndex;
import org.caesar.domain.user.vo.UserMinVO;
import org.caesar.model.entity.User;
import org.caesar.model.po.UserPO;
import org.caesar.model.vo.UserVO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MsUserStruct {

    UserPO DOtoPO(User user);

    UserIndex DOtoDTO(User user);

    UserVO DOtoVO(User user);

    UserMinVO DOtoMinVO(User user);

    User POtoDO(UserPO userPO);
}
