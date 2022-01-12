package com.example.springsecurity.config;

import com.example.springsecurity.handler.MyAuthenticationFailureHandler;
import com.example.springsecurity.handler.MyAuthenticationSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

        // 表单登录
        http.formLogin()
                // 用户密码 参数名配置
                .usernameParameter("username")
                .passwordParameter("password")

                // 登录页面
                .loginPage("/login.html")

                // 登录接口地址
                .loginProcessingUrl("/login")

                // 登录成功处理器
                // .successForwardUrl("/toMain")
                .successHandler(new MyAuthenticationSuccessHandler("http://localhost:8080/main.html"))

                // 登录失败处理器
                // .failureForwardUrl("/toError");
                .failureHandler(new MyAuthenticationFailureHandler("http://localhost:8080/error.html"));

        http.authorizeRequests()
                // 全部放行
                .antMatchers("/login.html", "/error.html").permitAll()

                // 只允许匿名访问
                .antMatchers("/about.html").anonymous()

                // 放行文件夹
                // .antMatchers("/js/**", "/css/**", "/image/**").permitAll()

                // png图片放行
                // .antMatchers("/**/*.png").permitAll()
                .regexMatchers(HttpMethod.GET, ".+[.]png").permitAll()

                // 其余拦截校验
                .anyRequest().authenticated();

        http.csrf().disable();
    }

}
