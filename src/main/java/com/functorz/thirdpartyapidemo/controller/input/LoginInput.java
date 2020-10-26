package com.functorz.thirdpartyapidemo.controller.input;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.NonNull;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class LoginInput implements Serializable {
  @NonNull
  private String username;
  @NonNull
  private String password;
}
