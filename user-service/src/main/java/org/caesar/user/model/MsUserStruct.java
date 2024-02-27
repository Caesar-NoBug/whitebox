package org.caesar.user.model;

import org.caesar.domain.search.vo.UserIndexVO;
import org.caesar.domain.user.enums.UserRole;
import org.caesar.domain.user.vo.UserMinVO;
import org.caesar.user.model.entity.User;
import org.caesar.domain.user.vo.UserVO;
import org.caesar.user.model.po.UserPO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface MsUserStruct {

    UserPO DOtoPO(User user);

    UserIndexVO DOtoDTO(User user);

    @Mapping(target = "roles", source = "roles", qualifiedByName = "transferRole")
    UserVO DOtoVO(User user);

    UserMinVO DOtoMinVO(User user);

    User POtoDO(UserPO userPO);

    @Named("transferRole")
    default List<UserRole> transferRole(List<Integer> roles) {
        return roles.stream().map(UserRole::of).collect(Collectors.toList());
    }
}
