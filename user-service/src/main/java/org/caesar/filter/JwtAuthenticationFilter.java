package org.caesar.filter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.caesar.constant.RedisPrefix;
import org.caesar.domain.constant.StrConstant;
import org.caesar.model.dto.AuthUser;
import org.caesar.common.util.JwtUtil;
import org.caesar.common.util.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private RedisCache redisCache;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("token");
        if(!StringUtils.hasText(token)){
            filterChain.doFilter(request,response);
            return;
        }

        String userId;

        try {
            Claims claims = JwtUtil.parseJWT(token);
            userId = claims.getSubject();
        } catch (Exception e) {
            //token 不合法
            response.sendRedirect(StrConstant.DEFAULT_AVATAR_URL);
            return;
        }

        String jwt = redisCache.getCacheObject(RedisPrefix.LOGIN_JWT + userId);

        //jwt已失效
        if(!token.equals(jwt)){
            filterChain.doFilter(request, response);
            return;
        }

        AuthUser user = redisCache.getCacheObject(RedisPrefix.LOGIN_USER + userId);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request,response);
    }
}
