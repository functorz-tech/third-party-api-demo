package com.functorz.thirdpartyapidemo.entity;

import java.io.Serializable;
import java.time.OffsetDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity(name = "\"user\"")
@Getter
@Setter
@NoArgsConstructor
public class User implements Serializable {
  @Id
  @GeneratedValue(generator = "user_pk_seq", strategy = GenerationType.SEQUENCE)
  @SequenceGenerator(name = "user_pk_seq", sequenceName = "user_pk_seq", allocationSize = 1)
  private Long id;

  @Column
  private String username;

  @Column
  private String password;

  @Column
  private String email;

  @Column
  private String phoneNumber;

  @Column(columnDefinition = "timestamp with time zone", updatable = false)
  @CreationTimestamp
  private OffsetDateTime createdAt;
}
