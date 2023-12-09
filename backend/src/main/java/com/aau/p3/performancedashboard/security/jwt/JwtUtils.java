package com.aau.p3.performancedashboard.security.jwt;

import java.security.Key;
import java.util.Date;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import com.aau.p3.performancedashboard.security.services.UserDetailsImpl;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {
  private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

  @Value("${performancedashboard.app.jwtSecret}")
  private String jwtSecret;

  @Value("${performancedashboard.app.jwtExpirationMs}")
  private int jwtExpirationMs;

  @Value("${performancedashboard.app.jwtCookieName}")
  private String jwtCookie;

  /**
   * Retrieves the JWT (JSON Web Token) from the cookies in the HTTP request.
   *
   * @param request the HttpServletRequest object representing the HTTP request
   * @return the JWT as a String, or null if no JWT cookie is found
   */
  public String getJwtFromCookies(HttpServletRequest request) {
    Cookie cookie = WebUtils.getCookie(request, jwtCookie);

    if (cookie == null) {
      logger.error("No JWT cookie found");
      return null;
    } else {
      logger.info("JWT cookie found");
      return cookie.getValue();
    }
  }

  /**
   * Generates a JWT cookie for the given user principal.
   *
   * @param userPrincipal the user principal for which to generate the JWT cookie
   * @return the generated JWT cookie
   */
  public ResponseCookie generateJwtCookie(UserDetailsImpl userPrincipal) {
    String jwt = generateTokenFromUserID(userPrincipal.getUsername());
    ResponseCookie cookie = ResponseCookie.from(jwtCookie, jwt).path("/api").maxAge(24 * 60 * 60).httpOnly(true)
        .build();
    return cookie;
  }

  /**
   * Returns a clean JWT cookie.
   *
   * @return the clean JWT cookie
   */
  public ResponseCookie getCleanJwtCookie() {
    ResponseCookie cookie = ResponseCookie.from(jwtCookie, null).path("/api").build();
    return cookie;
  }

  /**
   * Retrieves the user ID from a JWT token.
   *
   * @param token the JWT token
   * @return the user ID extracted from the token
   */
  public String getUserIDFromJwtToken(String token) {
    return Jwts.parserBuilder().setSigningKey(key()).build()
        .parseClaimsJws(token).getBody().getSubject();
  }

  /**
   * Returns the Key used for JWT signing and verification.
   *
   * @return the Key used for JWT signing and verification
   */
  private Key key() {
    return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
  }

  /**
   * Validates a JWT token.
   *
   * @param authToken The JWT token to validate.
   * @return true if the token is valid, false otherwise.
   */
  public boolean validateJwtToken(String authToken) {
    try {
      Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
      return true;
    } catch (MalformedJwtException e) {
      logger.error("Invalid JWT token: {}", e.getMessage());
    } catch (ExpiredJwtException e) {
      logger.error("JWT token is expired: {}", e.getMessage());
    } catch (UnsupportedJwtException e) {
      logger.error("JWT token is unsupported: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
      logger.error("JWT claims string is empty: {}", e.getMessage());
    }

    return false;
  }

  /**
   * Generates a JWT token from a user ID.
   *
   * @param id the user ID
   * @return the generated JWT token
   */
  public String generateTokenFromUserID(String id) {
    return Jwts.builder()
        .setSubject(id)
        .setIssuedAt(new Date())
        .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
        .signWith(key(), SignatureAlgorithm.HS256)
        .compact();
  }
}