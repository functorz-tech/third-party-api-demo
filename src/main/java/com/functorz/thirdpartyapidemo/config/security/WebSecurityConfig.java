package com.functorz.thirdpartyapidemo.config.security;

import java.util.List;
import javax.servlet.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
  @Autowired
  private List<TokenAuthenticationFilter> filters;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    filters.stream().forEachOrdered(filter -> {
      http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
    });

    http.csrf().disable()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .authorizeRequests()
        .antMatchers(HttpMethod.POST, "/update/**")
        .authenticated()
        .antMatchers(HttpMethod.DELETE, "/delete/**")
        .authenticated()
        .antMatchers(HttpMethod.POST, "/add/**")
        .authenticated()
        .antMatchers(HttpMethod.GET, "/get/**", "/token/refresh", "/generate/token")
        .permitAll()
        .antMatchers(HttpMethod.POST, "/login", "/register")
        .permitAll()
        .antMatchers(HttpMethod.GET, "/generate/token/signature")
        .authenticated()
        .anyRequest()
        .authenticated();
  }

  public interface TokenAuthenticationFilter extends Filter {}
}
