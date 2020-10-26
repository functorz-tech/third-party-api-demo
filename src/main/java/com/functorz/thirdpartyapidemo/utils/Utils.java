package com.functorz.thirdpartyapidemo.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.OffsetDateTimeSerializer;
import java.time.OffsetDateTime;
import java.util.TimeZone;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

public class Utils {
  public static final ObjectMapper OBJECT_MAPPER =
      Jackson2ObjectMapperBuilder.json()
      .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
      .serializerByType(OffsetDateTime.class, OffsetDateTimeSerializer.INSTANCE)
      .deserializerByType(OffsetDateTime.class, InstantDeserializer.OFFSET_DATE_TIME)
      .timeZone(TimeZone.getTimeZone("Asia/Shanghai"))
      .build();
}
