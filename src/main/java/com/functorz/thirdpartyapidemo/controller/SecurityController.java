package com.functorz.thirdpartyapidemo.controller;

import com.functorz.thirdpartyapidemo.config.security.JwtManager;
import com.functorz.thirdpartyapidemo.config.security.TokenSignature;
import com.functorz.thirdpartyapidemo.config.security.UserAuthentication;
import com.functorz.thirdpartyapidemo.config.security.UserToken;
import com.functorz.thirdpartyapidemo.controller.input.LoginInput;
import com.functorz.thirdpartyapidemo.controller.input.RegisterInput;
import com.functorz.thirdpartyapidemo.entity.User;
import com.functorz.thirdpartyapidemo.service.UserService;
import io.jsonwebtoken.JwtException;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecurityController {
  @Autowired
  private UserService userService;
  @Value("${password.salt}")
  private String passwordSalt;
  @Autowired
  private JwtManager jwtManager;
  @Autowired
  private RedisTemplate<String, String> redisTemplate;

  /**
   * Simplest way to get access token, but it can be unsafe
   *
   * @param input
   * @return
   */
  @PostMapping(path = "/login", consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResultBody<UserToken> login(@RequestBody LoginInput input) {
    Optional<User> userOptional = userService.findByUsername(input.getUsername());
    if (userOptional.isEmpty()) {
      return new ResultBody<>(ResponseStatus.FAILED, null,
          String.format("user with name:%s does not exists", input.getUsername()));
    }
    User user = userOptional.get();
    String hashPassword = BCrypt.hashpw(input.getPassword(), passwordSalt);
    if (!hashPassword.equals(user.getPassword())) {
      return new ResultBody<>(ResponseStatus.FAILED, null, "password does not correct");
    }
    UserToken token = jwtManager.createToken(user.getId());
    return new ResultBody<>(ResponseStatus.SUCCESS, token, null);
  }

  @PostMapping(path = "/register", consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResultBody<UserToken> register(@RequestBody RegisterInput input) {
    User user = new User();
    user.setUsername(input.getUsername());
    user.setEmail(input.getEmail());
    BCrypt.gensalt();
    user.setPassword(BCrypt.hashpw(input.getPassword(), passwordSalt));
    user.setPhoneNumber(input.getPhoneNumber());
    User savedUser = userService.save(user);
    UserToken token = jwtManager.createToken(savedUser.getId());
    return new ResultBody<>(ResponseStatus.SUCCESS, token, null);
  }

  @GetMapping(path = "/token/refresh", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResultBody<UserToken> refreshToken(@RequestParam(name = "refresh_token") String refreshToken) {
    try {
      long userId = jwtManager.validateRefreshTokenAndParseUseId(refreshToken);
      UserToken token = jwtManager.createToken(userId);
      token.setRefreshToken(refreshToken);
      return new ResultBody<>(ResponseStatus.SUCCESS, token, null);
    } catch (JwtException e) {
      return new ResultBody<>(ResponseStatus.FAILED, null, e.getMessage());
    }
  }

  /**
   * token generator example:
   *  TokenSignature is for thirdParty to generate user's access token.
   *  For safety, TokenSignature should be useless when it has been consumed.
   *
   * @return
   */
  @GetMapping(path = "/generate/token/signature", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResultBody<TokenSignature> generateTokenSignature() {
    UserAuthentication authentication = (UserAuthentication) SecurityContextHolder
        .getContext().getAuthentication();
    long userId = authentication.getUserId();
    TokenSignature tokenSignature = jwtManager.generateTokenSignature(userId);
    redisTemplate.opsForValue().set(tokenSignature.generateCacheKey(), "true");
    return new ResultBody<>(ResponseStatus.SUCCESS, tokenSignature, null);
  }

  /**
   * consume TokenSignature to produce UserToken.
   *
   * @param tokenSignature
   * @return
   */
  @PostMapping(path = "/generate/token")
  public ResultBody<UserToken> generateTokenBySignature(@RequestBody TokenSignature tokenSignature) {
    try {
      String cachedValue = redisTemplate.opsForValue().get(tokenSignature.generateCacheKey());
      UserToken userToken = jwtManager.consumeTokenSignature(tokenSignature);
      if (cachedValue == null) {
        return new ResultBody<>(ResponseStatus.FAILED, null, "token signature has been consumed");
      }
      return new ResultBody<>(ResponseStatus.SUCCESS, userToken, null);
    } catch (IllegalArgumentException e) {
      return new ResultBody<>(ResponseStatus.FAILED, null, e.getMessage());
    }
  }
}
