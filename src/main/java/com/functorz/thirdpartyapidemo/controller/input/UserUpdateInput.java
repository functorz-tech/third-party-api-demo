package com.functorz.thirdpartyapidemo.controller.input;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateInput implements Serializable {
  private Long id;
  private String username;
}
