package com.user.service.security;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.service.dto.UserDto;
import com.user.service.service.UserService;
import com.user.service.vo.RequestLogin;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;


@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private UserService userService;
    private Environment env;


    @Autowired
    public AuthenticationFilter(AuthenticationManager authenticationManager, UserService userService, Environment env) {
        super.setAuthenticationManager(authenticationManager);
        this.userService = userService;
        this.env = env;
    }
    //    UsernamePasswordAuthenticationFilter 를 extend 하면 2개의 메소드를 override (attemptAuthentication, successfulAuthentication) 해야 한다.

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        try {
            RequestLogin creds = new ObjectMapper().readValue(request.getInputStream(), RequestLogin.class);
//            InputStream으로 받은 이유는, 전달받은 login의 형태는 HTTP POST형식으로 전달이 된다.
//            POST형식으로 전달을 받으면 RequestParameter로 받을수 없기 때문에, InputStream으로 받으면 데이터가 어떤 형식으로 어떤게 들어왔는지 수작업으로 처리 가능하다.


//            사용자가 입력한 값을 UsernamePasswordAuthenticationToken 으로 바꿔서
//            인증 처리 해주는 getAuthenticationManager에 넘긴다.
            return getAuthenticationManager()
                    .authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    creds.getEmail(),
                                    creds.getPassword(),
                                    new ArrayList<>()));

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }


    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {

        log.debug("디버거: " + ((User) authResult.getPrincipal()).getUsername());
        String userName = ((User) authResult.getPrincipal()).getUsername();

        UserDto userDetails = userService.getUserDetailsByEmail(userName);

        String token = Jwts.builder()
                .setSubject(userDetails.getUserId()).setExpiration(new Date(System.currentTimeMillis() +
                        Long.parseLong(env.getProperty("token.expiration_time"))))
                .signWith(SignatureAlgorithm.HS512, env.getProperty("token.secret"))
                .compact();


        response.addHeader("token", token);
        response.addHeader("userId", userDetails.getUserId());

        super.successfulAuthentication(request, response, chain, authResult);
    }
}
