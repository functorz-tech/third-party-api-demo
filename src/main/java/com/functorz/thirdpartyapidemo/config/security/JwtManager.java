package com.functorz.thirdpartyapidemo.config.security;

import com.google.common.base.Preconditions;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtManager {
  private static long ACCESS_TOKEN_EXPIRATION_MILL_SECONDS = 2 * 3600L;
  private static long REFRESH_TOKEN_EXPIRATION_MILL_SECONDS = 24 * 3600L;

  @Value("${jwt.secret}")
  private String secret;
  @Value("${jwt.issuer}")
  private String issuer;
  @Value("${jwt.type}")
  private SignatureAlgorithm algorithm;

  /**
   * generate user token signature for thirdParty to generate user access token
   *
   * @param userId
   * @return
   */
  public TokenSignature generateTokenSignature(long userId) {
    HashCode hashCode = Hashing.sha256().hashLong(userId);
    return new TokenSignature(userId, hashCode.toString());
  }

  /**
   * consume TokenSignature to generate UserToken
   * @param tokenSignature
   * @return
   */
  public UserToken consumeTokenSignature(TokenSignature tokenSignature) {
    long userId = tokenSignature.getUserId();
    HashCode hashCode = Hashing.sha256().hashLong(userId);
    Preconditions.checkArgument(hashCode.toString().equals(tokenSignature.getSignature()));
    return createToken(userId);
  }

  public UserToken createToken(Long userId) {
    Instant now = Instant.now();
    String accessToken = Jwts.builder()
        .setIssuer(issuer)
        .setIssuedAt(Date.from(now))
        .setExpiration(Date.from(now.plusSeconds(ACCESS_TOKEN_EXPIRATION_MILL_SECONDS)))
        .setClaims(Map.of("userId", userId, "scope", TokenScope.REQUEST_API))
        .signWith(algorithm, secret.getBytes())
        .compact();

    String refreshToken = Jwts.builder()
        .setIssuer(issuer)
        .setIssuedAt(Date.from(now))
        .setExpiration(Date.from(now.plusSeconds(REFRESH_TOKEN_EXPIRATION_MILL_SECONDS)))
        .setClaims(Map.of("userId", userId, "scope", TokenScope.TOKEN_REFRESH))
        .signWith(algorithm, secret.getBytes())
        .compact();

    return new UserToken(accessToken, refreshToken);
  }

  public Claims parseClaims(String tokenValue) {
    return Jwts.parser()
        .setSigningKey(secret.getBytes())
        .parseClaimsJws(tokenValue)
        .getBody();
  }

  public long validateRefreshTokenAndParseUseId(String refreshToken) {
    Jws<Claims> claimsJws = Jwts.parser()
        .setSigningKey(secret.getBytes())
        .parseClaimsJws(refreshToken);
    Claims claims = claimsJws.getBody();
    long userId = claims.get("userId", Long.class);

    if (claims.getExpiration().before(Date.from(Instant.now()))) {
      throw new ExpiredJwtException(null, claims, "refresh token expired");
    }
    return userId;
  }
}
