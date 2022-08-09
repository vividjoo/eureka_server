package com.user.service.security;

import com.user.service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.SecurityBuilder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.servlet.Filter;

@EnableWebSecurity
@Configuration
public class WebSecurity extends WebSecurityConfigurerAdapter {

    private UserService userService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private Environment env;

    @Autowired
    public WebSecurity(Environment env, UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.env = env;
    }

//    HttpSecurity를 파라미터로 갖는 configure 메서드는 권한에 관련된 설정
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        final String IP = "192.168.17.217";
        http.csrf().disable();
//        http.authorizeRequests().antMatchers("/users/**").permitAll();
//        위 주석처리 한 라인은 모든 요청에 대해 permitAll 해주었던 기능이다.

        http.authorizeRequests().antMatchers("/**")
                .access("hasIpAddress('"+IP+"')")
//                        .hasIpAddress("192.168.17.217")        // 해당 IP만 통과 할 수 있다.
                .and()      // 추가로 작업 할 것이 있다면 .and() 로 한다.
                .addFilter(getAuthentionFilter());      // getAuthentionFilter 함수를 Filter로 추가한다.

        http.headers().frameOptions().disable();

    }

//    getAuthentionFilter는 원래 Filter를 Return 하는 함수이지만, AuthenticationFilter가 Filter를 상속 받았기 때문에 AuthenticationFilter로 Return 한다.
    private AuthenticationFilter getAuthentionFilter() throws Exception {
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManager(), userService, env);
//        authenticationFilter.setAuthenticationManager(authenticationManager());
        return authenticationFilter;
    }

//    인증에 관련된 메서드
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//      auth.userDetailsService(): 사용자가 전달한 username과 password를 가지고 사용자의 데이터를 검색, SELECT문과 비슷한 기능
        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);

        super.configure(auth);
    }
}
