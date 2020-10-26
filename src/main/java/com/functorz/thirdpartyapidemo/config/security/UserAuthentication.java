package com.functorz.thirdpartyapidemo.config.security;

import java.util.List;
import javax.security.auth.Subject;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;

public class UserAuthentication extends AbstractAuthenticationToken {
  @Getter
  private final long userId;
  @Getter
  private final String credentials;

  public UserAuthentication(Long userId, String credentials) {
    super(List.of());
    this.userId = userId;
    this.credentials = credentials;
    setAuthenticated(true);
  }

  @Override
  public Long getPrincipal() {
    return userId;
  }

  @Override
  public boolean implies(Subject subject) {
    return false;
  }
}
