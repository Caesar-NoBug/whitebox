package org.caesar.common.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.caesar.common.constant.Headers;
import org.caesar.common.constant.StrConstant;
import org.springframework.core.Ordered;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Objects;

//校验请求是否来自网关
public class CheckSourceFilter extends OncePerRequestFilter implements Ordered {

    //TODO: 在网关中定时更新source的值，更新后发出全局事件，由各服务监听全局事件，接收后更改对应储存的值
    private String gatewaySource = "gateway";

    public CheckSourceFilter(String source) {
        this.gatewaySource = source;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String source = request.getHeader(Headers.SOURCE_HEADER);

        System.out.println(request.getRequestURI());

        if(Objects.isNull(source) || !source.equals(gatewaySource)){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }

    @Override
    public int getOrder() {
        return -1;
    }

}
