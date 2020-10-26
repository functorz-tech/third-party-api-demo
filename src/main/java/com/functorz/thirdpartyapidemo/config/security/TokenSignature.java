package com.functorz.thirdpartyapidemo.config.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TokenSignature {
  private long userId;
  private String signature;

  public String generateCacheKey() {
    return String.format("%d-%s", userId, signature);
  }
}
