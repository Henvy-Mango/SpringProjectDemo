package com.example.springsecurity.config;

import com.example.springsecurity.handler.MyAccessDeniedHandler;
import com.example.springsecurity.handler.MyAuthenticationFailureHandler;
import com.example.springsecurity.handler.MyAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private MyAccessDeniedHandler myAccessDeniedHandler;

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
                // 匹配放行
                .antMatchers("/login.html", "/error.html").permitAll()

                // 只允许匿名访问
                .antMatchers("/about.html").anonymous()

                // 放行文件夹
                // .antMatchers("/js/**", "/css/**", "/image/**").permitAll()

                // png图片放行
                // .antMatchers("/**/*.png").permitAll()
                .regexMatchers(HttpMethod.GET, ".+[.]png").permitAll()

                // 权限配置，角色配置
                // .antMatchers("/menu.html").hasAnyAuthority("root", "user")
                .antMatchers("/menu.html").hasAnyRole("ADMIN")

                // 对应IP放行
                // .antMatchers("/main.html").hasIpAddress("127.0.0.1")

                // 其余拦截校验
                .anyRequest().authenticated();

        // 关闭csrf防护
        http.csrf().disable();

        // 异常处理
        http.exceptionHandling().accessDeniedHandler(myAccessDeniedHandler);
    }

}
