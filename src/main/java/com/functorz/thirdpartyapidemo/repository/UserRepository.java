package com.functorz.thirdpartyapidemo.repository;

import com.functorz.thirdpartyapidemo.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  @Query(value = "update \"user\" set username = :username where id = :id", nativeQuery = true)
  @Modifying
  int updateUserById(@Param(value = "id") long id, @Param(value = "username") String username);

  @Query(value = "select * from \"user\" where username = :username", nativeQuery = true)
  Optional<User> findByUsername(@Param(value = "username") String username);
}
