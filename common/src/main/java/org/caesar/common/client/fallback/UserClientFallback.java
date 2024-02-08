package org.caesar.common.client.fallback;

import org.caesar.common.client.UserClient;
import org.caesar.domain.common.enums.ErrorCode;
import org.caesar.domain.common.vo.Response;
import org.caesar.domain.user.vo.RoleVO;
import org.caesar.domain.user.vo.UserMinVO;
import org.caesar.domain.user.vo.UserPreferVO;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
public class UserClientFallback implements UserClient {

    @Override
    public Response<Map<Long, UserMinVO>> getUserMin(List<Long> id) {
        return Response.error(ErrorCode.SERVICE_UNAVAILABLE_ERROR, "[User Service] 'getUserMin' service unavailable");
    }

    @Override
    public Response<UserPreferVO> getUserPrefer() {
        return Response.error(ErrorCode.SERVICE_UNAVAILABLE_ERROR, "[User Service] 'getUserPrefer' service unavailable");
    }

    @Override
    public Response<List<RoleVO>> getUpdatedRole(LocalDateTime updateTime) {
        return Response.error(ErrorCode.SERVICE_UNAVAILABLE_ERROR, "[User Service] 'getUpdatedRole' service unavailable");
    }
}
