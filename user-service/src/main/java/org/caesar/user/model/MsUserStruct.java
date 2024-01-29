package org.caesar.user.model;

import org.caesar.domain.search.vo.UserIndexVO;
import org.caesar.domain.user.vo.UserMinVO;
import org.caesar.user.model.entity.User;
import org.caesar.domain.user.vo.UserVO;
import org.caesar.user.model.po.UserPO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MsUserStruct {

    UserPO DOtoPO(User user);

    UserIndexVO DOtoDTO(User user);

    UserVO DOtoVO(User user);

    UserMinVO DOtoMinVO(User user);

    User POtoDO(UserPO userPO);
}
