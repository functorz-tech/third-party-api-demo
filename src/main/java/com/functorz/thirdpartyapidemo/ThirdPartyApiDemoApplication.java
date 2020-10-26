package com.functorz.thirdpartyapidemo;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ThirdPartyApiDemoApplication {

  public static void main(String[] args) {
    Dotenv.configure()
        .ignoreIfMissing()
        .load()
        .entries()
        .forEach(e -> System.setProperty(e.getKey(), e.getValue()));
    SpringApplication.run(ThirdPartyApiDemoApplication.class, args);
  }

}
