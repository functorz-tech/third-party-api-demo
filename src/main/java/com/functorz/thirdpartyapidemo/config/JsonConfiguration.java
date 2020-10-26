package com.functorz.thirdpartyapidemo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.functorz.thirdpartyapidemo.utils.Utils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JsonConfiguration {
  @Bean
  public ObjectMapper objectMapper() {
    return Utils.OBJECT_MAPPER;
  }
}
