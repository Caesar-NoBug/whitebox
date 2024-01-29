package org.caesar.user.model;

import org.caesar.domain.user.vo.RoleVO;
import org.caesar.user.model.entity.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MsMenuStruct {
    RoleVO roleDOtoVO(Role role);
}
