package com.datascope.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    // 禁用所有安全检查，允许所有请求
    http.csrf().disable()
        .authorizeRequests()
        .anyRequest().permitAll()
        .and()
        .httpBasic().disable()
        .formLogin().disable()
        .logout().disable()
        .anonymous();
  }
}
