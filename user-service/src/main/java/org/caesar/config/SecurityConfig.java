package org.caesar.config;

import org.caesar.constant.PathList;
import org.caesar.common.constant.StrConstant;
import org.caesar.filter.JwtAuthenticationFilter;
import org.caesar.security.provider.EmailAuthenticationProvider;
import org.caesar.security.provider.PhoneAuthenticationProvider;
import org.caesar.security.provider.UsernameAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends SecurityConfigurerAdapter {

    @Autowired
    private UsernameAuthenticationProvider usernameAuthenticationProvider;

    @Autowired
    private EmailAuthenticationProvider emailAuthenticationProvider;

    @Autowired
    private PhoneAuthenticationProvider phoneAuthenticationProvider;

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        return authenticationManagerBuilder.authenticationProvider(usernameAuthenticationProvider)
                .authenticationProvider(emailAuthenticationProvider)
                .authenticationProvider(phoneAuthenticationProvider).build();
    }

    @Autowired
    public JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        return http
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(
                authorize -> {
                    try {
                        for (String path : PathList.START_WHITE_LIST) {
                            authorize.requestMatchers(path).permitAll();
                        }
//todo：把逻辑放到gateway中，放行所有接口，并校验接口是否来自网关（在父项目中实现一个校验来源的filter）
                        authorize
                                //注意一定要放行OPTIONS请求，否则会出现很多跨域问题
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                .anyRequest().authenticated()
                                .and().formLogin().loginPage(StrConstant.LOGIN_PAGE_PATH)
                                .and().csrf(csrf -> csrf.disable());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        ).build();
    }

    /*@Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOriginPattern("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.setAllowCredentials(true);
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(source);
    }*/
}
