package com.example.springsecurity.config;

import com.example.springsecurity.handler.MyAuthenticationFailureHandler;
import com.example.springsecurity.handler.MyAuthenticationSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author Naomi
 * @date 2022/1/10 22:37
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.formLogin()
                .usernameParameter("username")
                .passwordParameter("password")
                .loginPage("/login.html")
                .loginProcessingUrl("/login")
                // .successForwardUrl("/toMain")
                .successHandler(new MyAuthenticationSuccessHandler("http://localhost:8080/main.html"))
                // .failureForwardUrl("/toError");
                .failureHandler(new MyAuthenticationFailureHandler("http://localhost:8080/error.html"));

        http.authorizeRequests()
                .antMatchers("/login.html", "/error.html").permitAll()
                .anyRequest().authenticated();

        http.csrf().disable();
    }

}
