package com.functorz.thirdpartyapidemo.controller.input;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.NonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterInput implements Serializable {
  @NonNull
  private String username;
  @NonNull
  private String password;
  private String email;
  private String phoneNumber;
}
