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
import java.util.Objects;

@Component
public class RequestPreHandleFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        String userId = request.getHeader(Headers.USERID_HEADER);
        if(Objects.nonNull(userId)) ContextHolder.setUserId(Long.parseLong(userId));

        String traceId = request.getHeader(Headers.TRACE_ID_HEADER);
        if (Objects.nonNull(traceId)) ContextHolder.setTraceId(traceId);

        chain.doFilter(request, response);

        ContextHolder.clear();
    }

}
