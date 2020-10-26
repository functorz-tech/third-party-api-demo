package com.functorz.thirdpartyapidemo.config.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserToken {
  private String accessToken;
  private String refreshToken;
}
