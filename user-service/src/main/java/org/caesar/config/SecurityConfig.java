package org.caesar.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

//TODO: 删掉这个类
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends SecurityConfigurerAdapter {

    /*@Autowired
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
    }*/

}
