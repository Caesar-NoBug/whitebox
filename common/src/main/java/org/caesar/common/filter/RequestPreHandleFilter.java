package org.caesar.common.filter;

import org.caesar.common.context.ContextHolder;
import org.caesar.domain.constant.Headers;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class RequestPreHandleFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        String uri = request.getRequestURI();
        // TODO: 打印接口访问日志

        String userId = request.getHeader(Headers.USERID_HEADER);
        String traceId = request.getHeader(Headers.TRACE_ID_HEADER);

        ContextHolder.setUserId(Long.parseLong(userId));
        ContextHolder.setTraceId(traceId);

        MDC.put("traceId", traceId);
        MDC.put("userId", userId);

        chain.doFilter(request, response);

        MDC.clear();
        //TODO: 清空ThreadLocal，避免线程污染
        ContextHolder.clear();

    }

}
