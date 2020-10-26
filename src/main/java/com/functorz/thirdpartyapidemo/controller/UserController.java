package com.functorz.thirdpartyapidemo.controller;

import com.functorz.thirdpartyapidemo.controller.input.UserUpdateInput;
import com.functorz.thirdpartyapidemo.entity.User;
import com.functorz.thirdpartyapidemo.service.UserService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ResponseBody
public class UserController {
  @Autowired
  private UserService userService;

  @GetMapping(path = "/get/all/user", produces = MediaType.APPLICATION_JSON_VALUE)
  public List<User> queryAllUser() {
    return userService.getAllUser();
  }

  @GetMapping(path = "/get/user", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResultBody<User> getUserById(@RequestParam(name = "id") long id) {
    Optional<User> userOptional = userService.getUserById(id);
    return userOptional.map(user -> new ResultBody<>(ResponseStatus.SUCCESS, user, null))
        .orElse(new ResultBody<>(ResponseStatus.FAILED, null, "user not exists"));
  }

  @PostMapping(path = "/update/user/name", consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResultBody<User> updateUser(@RequestBody UserUpdateInput input) {
    User user = userService.updateUsername(input.getId(), input.getUsername());
    return new ResultBody<>(ResponseStatus.SUCCESS, user, null);
  }
}
