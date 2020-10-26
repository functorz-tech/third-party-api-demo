package com.functorz.thirdpartyapidemo.config.security;

import io.jsonwebtoken.Claims;
import java.io.IOException;
import java.util.Optional;
import java.util.regex.Pattern;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter implements WebSecurityConfig.TokenAuthenticationFilter {
  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String BEARER_TOKEN_PREFIX = "Bearer ";
  private static final Pattern JWT_TOKEN_PATTERN = Pattern.compile("^[-=\\w]+\\.[-=\\w]+\\.[-=\\w]+$");

  @Autowired
  private JwtManager jwtManager;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {
      Optional.ofNullable(request.getHeader(AUTHORIZATION_HEADER))
          .map(this::trimBearerPrefix)
          .filter(jwtToken -> JWT_TOKEN_PATTERN.matcher(jwtToken).matches())
          .ifPresent(token -> {
            Claims claims = jwtManager.parseClaims(token);
            TokenScope scope = TokenScope.valueOf(claims.get("scope", String.class));
            Long userId = claims.get("userId", Long.class);
            if (scope == TokenScope.REQUEST_API) {
              SecurityContextHolder.getContext()
                  .setAuthentication(new UserAuthentication(userId, token));
            }
          });
      filterChain.doFilter(request, response);
  }

  private String trimBearerPrefix(String token) {
    if (token.startsWith(BEARER_TOKEN_PREFIX)) {
      return token.substring(BEARER_TOKEN_PREFIX.length()).trim();
    }
    return token;
  }
}
