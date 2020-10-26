package com.functorz.thirdpartyapidemo.service;

import com.functorz.thirdpartyapidemo.entity.User;
import com.functorz.thirdpartyapidemo.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
  @Autowired
  private UserRepository userRepo;

  public List<User> getAllUser() {
    return userRepo.findAll();
  }

  public Optional<User> getUserById(long id) {
    return userRepo.findById(id);
  }

  @Transactional
  public User updateUsername(Long id, String username) {
    userRepo.updateUserById(id, username);
    return userRepo.findById(id).orElseThrow();
  }

  public Optional<User> findByUsername(String username) {
    return userRepo.findByUsername(username);
  }

  @Transactional
  public User save(User user) {
    return userRepo.save(user);
  }
}
