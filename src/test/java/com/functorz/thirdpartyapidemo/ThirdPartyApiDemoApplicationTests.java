package com.functorz.thirdpartyapidemo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCrypt;

class ThirdPartyApiDemoApplicationTests {

  @Test
  public void s() {
    System.out.println(BCrypt.gensalt());
  }
}
