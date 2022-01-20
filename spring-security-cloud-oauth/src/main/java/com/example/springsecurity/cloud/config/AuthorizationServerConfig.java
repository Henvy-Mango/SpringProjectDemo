package com.example.springsecurity.cloud.config;

import com.example.springsecurity.cloud.enhancer.JwtTokenEnhancer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Naomi
 * @date 2022/1/13 15:06
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    @Qualifier("jwtTokenStore")
    // @Qualifier("redisTokenStore")
    private TokenStore tokenStore;

    @Autowired
    private JwtAccessTokenConverter jwtAccessTokenConverter;
    @Autowired
    private JwtTokenEnhancer jwtTokenEnhancer;

    // 密码模式配置
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        // 配置JWT的内容增强器
        TokenEnhancerChain enhancerChain = new TokenEnhancerChain();
        List<TokenEnhancer> delegates = new ArrayList<>();
        delegates.add(jwtTokenEnhancer);
        delegates.add(jwtAccessTokenConverter);
        enhancerChain.setTokenEnhancers(delegates);

        endpoints.authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService)
                // 配置存储令牌策略
                .tokenStore(tokenStore)
                // 应用JWT增强器
                .accessTokenConverter(jwtAccessTokenConverter)
                .tokenEnhancer(enhancerChain);
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients
                // client信息存在内存
                .inMemory()
                // 配置client_id
                .withClient("admin")
                // 配置client-secret
                .secret(passwordEncoder.encode("admin"))
                // 配置redirect_uri，用于授权成功后跳转
                .redirectUris("http://www.baidu.com")
                // 配置访问token的有效期
                .accessTokenValiditySeconds(3600)
                // 配置刷新token的有效期
                .refreshTokenValiditySeconds(864000)
                // 配置申请的权限范围
                .scopes("all")
                // 配置grant_type，表示授权类型
                .authorizedGrantTypes("authorization_code", "password", "refresh_token");
    }

}
