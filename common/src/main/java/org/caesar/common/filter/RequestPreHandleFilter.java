package org.caesar.common.filter;

import org.caesar.common.context.ContextHolder;
import org.caesar.domain.constant.Headers;
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
        chain.doFilter(request, response);
        String uri = request.getRequestURI();

        String userId = request.getHeader(Headers.USERID_HEADER);
        String traceId = request.getHeader(Headers.TRACE_ID_HEADER);

        ContextHolder.set(ContextHolder.USER_ID, userId);
        ContextHolder.set(ContextHolder.TRACE_ID, traceId);

    }

}