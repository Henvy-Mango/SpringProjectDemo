package com.example.springsecurity.boot.config;

import com.example.springsecurity.boot.handler.MyAccessDeniedHandler;
import com.example.springsecurity.boot.handler.MyAuthenticationFailureHandler;
import com.example.springsecurity.boot.handler.MyAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

/**
 * @author Naomi
 * @date 2022/1/10 22:37
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private MyAccessDeniedHandler myAccessDeniedHandler;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private DataSource dataSource;

    @Lazy
    @Autowired
    private PersistentTokenRepository persistentTokenRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public PersistentTokenRepository getPersistentTokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);

        // 启动时建表，重复建表会报错
        jdbcTokenRepository.setCreateTableOnStartup(true);
        return jdbcTokenRepository;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // SpringSecurity使用X-Frame-Options防止网页被Frame
        // H2数据库管理页面需要开启iframe网页嵌入
        http.headers().frameOptions().disable();

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
                // 登录页面、错误页面放行
                .antMatchers("/login.html", "/error.html").permitAll()

                // H2数据库管理页面放行
                .antMatchers("/h2/**").permitAll()

                // 只允许匿名访问
                // .antMatchers("/about.html").anonymous()

                // 放行文件夹
                // .antMatchers("/js/**", "/css/**", "/image/**").permitAll()

                // png图片放行
                // .antMatchers("/**/*.png").permitAll()
                // .regexMatchers(HttpMethod.GET, ".+[.]png").permitAll()

                // 权限配置，角色配置
                // .antMatchers("/menu.html").hasAnyAuthority("root", "user")
                // .antMatchers("/menu.html").hasAnyRole("ADMIN")

                // 对应IP放行
                // .antMatchers("/main.html").hasIpAddress("127.0.0.1")

                // 其余拦截校验
                .anyRequest().authenticated();

        // 记住我
        http.rememberMe()
                // 自定义登录逻辑
                .userDetailsService(userDetailsService)
                // 持久化对象
                .tokenRepository(persistentTokenRepository)
                // 登录页面 记住我 参数
                .rememberMeParameter("remember-me")
                // token失效时间 秒
                .tokenValiditySeconds(300);

        // 注销
        http.logout()
                // 注销接口地址
                .logoutUrl("/logout")

                // 注销成功跳转地址
                .logoutSuccessUrl("/login.html");


        // 关闭csrf防护，跨域保护
        // 关闭后Header不需要携带_csrf
        http.csrf().disable();

        // 异常处理
        http.exceptionHandling().accessDeniedHandler(myAccessDeniedHandler);
    }

}
