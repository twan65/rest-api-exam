package com.rest.api.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable() // rest apiなので、基本設定を使わない。 基本設定は非認証時、ログイン画面にリダイレクトされる。
                .csrf().disable() // rest apiなので、CSRFセキュリティが必要ないのでdisable。
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // jwt tokenで認証を行うためセッションは必要ない→生成しない。
                .and()
                    .authorizeRequests() // 次のリクエストに対して使用権限をチェック
                        .antMatchers("/*/signin", "/*/signin/**", "/*/signup", "/social/**").permitAll() // ←は誰でも接続可能
                        .antMatchers(HttpMethod.GET,  "/exception/**", "helloworld/**").permitAll() // hellowworldで始まるGETリクエストのリソースは誰でも接続可能
                    .antMatchers("/*/users").hasRole("ADMIN")
                    .anyRequest().hasRole("USER") // その他のリクエストに対しては認証されているメンバーのみ接続可
                .and()
                    .exceptionHandling().accessDeniedHandler(new CustomAccessDeniedHandler())
                .and()
                    .exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                .and()
                    .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class); // jwt token filterをid/password認証filter前に入れる。

    }

    @Override // ignore check swagger resource
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/v2/api-docs", "/swagger-resources/**",
                "/swagger-ui.html", "/webjars/**", "/swagger/**");
    }
}
